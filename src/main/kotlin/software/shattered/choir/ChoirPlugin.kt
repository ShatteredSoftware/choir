package software.shattered.choir

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.sentry.Hub
import io.sentry.SentryOptions
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import software.shattered.choir.api.PluginKey
import software.shattered.choir.dispatch.command.bukkitdispatch.context.BukkitCommandContext
import software.shattered.choir.dispatch.command.DispatchCommand
import software.shattered.choir.dispatch.command.DispatchCommandBuilder
import software.shattered.choir.errors.ErrorLogger
import software.shattered.choir.manager.FeatureManager
import software.shattered.choir.manager.ListenerManager
import software.shattered.choir.manager.PlayerCooldownManager
import software.shattered.choir.manager.PlayerManager
import software.shattered.mini18n.*
import software.shattered.choir.net.UpdateChecker
import software.shattered.choir.polyfill.Dependency
import software.shattered.choir.polyfill.LibraryLoader
import software.shattered.choir.storage.player.ChoirPlayer
import software.shattered.choir.tasks.AsyncBukkitRunStrategy
import software.shattered.choir.persistence.Persistence
import software.shattered.choir.persistence.tasks.RunStrategy
import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.logging.Level
import java.util.stream.Collectors
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@Suppress("MemberVisibilityCanBePrivate", "LeakingThis", "unused", "UNUSED_PARAMETER")
abstract class ChoirPlugin(val impl: Class<out software.shattered.choir.ChoirPlugin>) : JavaPlugin() {

    /*
     *  API Stuff
     */
    fun getPlayer(id: String): ChoirPlayer? {
        return this.playerManager.onlinePlayers[UUID.fromString(id)] ?: this.playerManager.onlinePlayersByName[id]
    }

    fun <T : Event> on(fn: (T) -> Unit) {
        this.listenerManager.on(fn)
    }

    fun key(value: String): PluginKey {
        return PluginKey(this, value)
    }

    protected fun checkPlugin(pluginName: String): Boolean {
        return server.pluginManager.isPluginEnabled(pluginName)
    }

    protected inline fun <reified T : Plugin> withPlugin(pluginName: String, fn: T.() -> Unit) {
        val plugin = server.pluginManager.getPlugin(pluginName)
        if (plugin == null || plugin !is T || !plugin.isEnabled) {
            return
        }
        fn(plugin)
    }

    protected fun withFeature(id: String, fn: () -> Unit) {
        val feature = featureManager.get(id)
        if (feature != null && feature.enabled) {
            fn()
        }
    }

    protected fun command(key: PluginKey, fn: DispatchCommandBuilder<BukkitCommandContext>.() -> Unit) {
        DispatchCommand.build(key.toString(), fn)
    }

    open val defaultRunStrategy: RunStrategy = AsyncBukkitRunStrategy(this)
    open val defaultLocale: Locale = Locale.getDefault()

    /*
        Overrideable Stuff
     */
    protected open val bStatsId = 0
    protected open val spigotResourceId = 0
    protected open val sentryDsn: String? = null
    protected open val requiresPaper: Boolean = false
    protected open val dependencies: List<Dependency> = emptyList()

    var isUpdateAvailable = false
        private set

    var latestVersion: String? = null
        private set

    protected var hasPaper: Boolean = false
        private set
    protected var loaded = false
        private set

    lateinit var gson: Gson
    val cooldownManager = PlayerCooldownManager()
    val featureManager = FeatureManager()

    protected lateinit var playerManager: PlayerManager
    private val listenerManager: ListenerManager = ListenerManager(this)

    private val messageSet = MessageSet()
    protected val messageProcessorStore = MessageProcessorStore()

    protected lateinit var persistence: Persistence
    protected lateinit var choirPersistence: Persistence

    protected fun gsonSetup(gsonBuilder: GsonBuilder) {}
    protected var metrics: Metrics? = null
    protected var errorLogger: ErrorLogger? = null

    /**
     * Do any work that must be done before loading the config. Defaults to doing some operations such as loading libraries on
     * legacy versions, and extracting any files in the extract resource folder, but you are free to opt out of them.
     *
     * @throws Exception Any error that occurs.
     */
    @Throws(Exception::class)
    protected open fun load() {
        loadLibraries()

        extractResources()

        loadMessageSet()

        loadConfig()
    }

    protected open fun postEnable() {}
    protected open fun preDisable() {}
    protected open fun onFirstTick() {}
    protected open fun parseConfig(config: YamlConfiguration?) {}
    protected open fun initSentry(hub: Hub) {}

    /*
     * Internal functionality
     */

    override fun onLoad() {
        loaded = false
        try {
            checkPaper()
            initGson()

            persistence = Persistence(this.gson, this.dataFolder, this.defaultRunStrategy)
            choirPersistence = Persistence(this.gson, File(this.dataFolder.parentFile, "choir"), this.defaultRunStrategy)
            playerManager = PlayerManager(this, this.persistence)

            load()

            loaded = true
        } catch (t: Throwable) {
            logger.log(Level.SEVERE, "An error occurred while loading.", t)
            loaded = false
        }
    }

    override fun onEnable() {
        if (!loaded) {
            bigScaryMessage(
                "${description.name} cannot be enabled due to an error that\n" +
                        "occurred during the plugin loading phase."
            )
            isEnabled = false
            return
        }
        if (bStatsId != 0) {
            metrics = Metrics(this, bStatsId)
        }
        if (spigotResourceId != 0) {
            checkUpdates()
        }
        if (sentryDsn != null) {
            setupSentry()
        }

        on<PlayerJoinEvent> { playerManager.join(it.player) }
        on<PlayerQuitEvent> { playerManager.leave(it.player) }

        Bukkit.getScheduler().runTask(this, Runnable { onFirstTick() })
        postEnable()
        this.listenerManager.listen()
    }

    override fun onDisable() {
        this.listenerManager.unlisten()
        preDisable()
    }

    private fun checkUpdates() {
        val updateChecker = UpdateChecker(this, spigotResourceId)
        updateChecker.getVersion { version: String? ->
            isUpdateAvailable = if (version == null || description.version.startsWith(version)) {
                logger.info("You are up to date.")
                false
            } else {
                logger.info("Version $version is available.")
                true
            }
            latestVersion = version
        }
    }

    private fun setupSentry() {
        val options = SentryOptions()
        options.dsn = sentryDsn
        options.tracesSampleRate = 0.2

        val hub = Hub(options)
        hub.setExtra("plugin_version", this.description.version)
        hub.setExtra("bukkit_version", this.server.bukkitVersion)
        hub.setExtra("server_version", this.server.version)
        initSentry(hub)

        errorLogger = ErrorLogger(hub)
    }

    private fun loadLibraries() {
        // Run polyfill for old versions
        val loaded = LibraryLoader.loadLibraries(this)

        // Load all code-defined dependencies not already defined in plugin.yml
        dependencies.filter {
            !loaded.contains(it.name)
        }.forEach(Dependency::load)
    }

    private fun checkPaper() {
        hasPaper = try {
            Class.forName("com.destroystokyo.paper.VersionHistoryManager\$VersionData")
            true
        } catch (ex: Exception) {
            false
        }

        if (requiresPaper && !hasPaper) {
            bigScaryMessage {
                logger.severe(
                    "This plugin requires Paper or a fork of Paper. We are not able to find Paper. Disabling."
                )
                logger.severe("If you are using a fork of Paper, please contact the plugin authors:")
                description.authors.forEach {
                    logger.severe(" - $it")
                }
            }
            this.isEnabled = false
            this.loaded = false
            throw Exception("This plugin requires Paper or a fork of Paper. Disabling.")
        }
    }

    private fun initGson() {
        val gsonBuilder = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
        gsonSetup(gsonBuilder)
        this.gson = gsonBuilder.create()
    }

    private fun bigScaryMessage(s: String) {
        bigScaryMessage { logger.severe(s) }
    }

    private fun bigScaryMessage(f: () -> Unit) {
        logger.severe("============================================================")
        logger.severe("")
        logger.severe("")
        f()
        logger.severe("")
        logger.severe("")
        logger.severe("Get help:")
        logger.severe("https://discord.gg/zUbNX9t")
        logger.severe("")
        logger.severe("")
        logger.severe("============================================================")
    }

    private fun extractResources(
        exclude: Set<String> = setOf("plugin.yml"),
        prefix: String = "extract/"
    ) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        val jar = ZipFile(impl.protectionDomain.codeSource.location.path)
        val entries = jar.stream()
            .filter { f -> f.name.startsWith(prefix) }
            .filter { f -> !exclude.any { f.name.endsWith(it) } }
            .sorted(Comparator.comparing(ZipEntry::getName))
            .collect(Collectors.toList())

        for (entry in entries) {
            val dest = dataFolder.resolve(entry.name.substring(prefix.length))
            if (dest.exists()) {
                continue
            }
            if (entry.isDirectory) {
                Files.createDirectory(dest.toPath())
                continue
            }
            Files.copy(jar.getInputStream(entry), dest.toPath())
        }
    }

    private fun loadMessageSet() {
        val langFolder = File(dataFolder, "locale")
        langFolder.listFiles()?.forEach {
            if (it.extension == "yml" || it.extension == "yaml") {
                val language = it.nameWithoutExtension.split("-", limit = 2)[0]
                val locale = Locale(language)
                logger.info("Loaded locale information for $language (${locale.displayName})")

                val values = YamlConfiguration
                    .loadConfiguration(it)
                    .getValues(true).entries
                    .map { (key, value) -> key to value.toString() }
                messageSet.addAll(locale, values)
            }
        }
    }

    private fun loadConfig() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            if (getResource("config.yml") != null) {
                saveResource("config.yml", false)
            }
        }
        parseConfig(YamlConfiguration.loadConfiguration(configFile))
    }

    companion object {
        private var instance: software.shattered.choir.ChoirPlugin? = null

        fun get(): software.shattered.choir.ChoirPlugin {
            return software.shattered.choir.ChoirPlugin.Companion.instance ?: throw IllegalStateException("Accessing choir instance before initialization")
        }
    }

    init {
        software.shattered.choir.ChoirPlugin.Companion.instance = this
    }
}