package software.shattered.choir.polyfill

import org.bukkit.Bukkit
import software.shattered.choir.ChoirPlugin
import software.shattered.choir.data.MinecraftVersion
import software.shattered.choir.api.Feature
import software.shattered.choir.persistence.util.ChoirMethods.getMethod
import java.io.File
import java.lang.IllegalArgumentException
import java.net.URL
import java.net.URLClassLoader
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.logging.Level

class Dependency(
    group: String,
    val artifact: String,
    version: String,
    baseUrl: String = "https://repo1.maven.org/maven2/",
    private val minVersion: MinecraftVersion? = null,
    private val maxVersion: MinecraftVersion? = null,
    @Transient
    val connectionModifier: (connection: URLConnection) -> URLConnection = { it },
) {
    val name: String = "$group:$artifact:$version"

    private val webUrl: String by lazy {
        baseUrl +
                (if (baseUrl.endsWith("/")) {
                    ""
                } else "/") +
                name.replace(":", "/").replace(".", "/") + "/" + artifact + "-" + version + ".jar"
    }

    private val filePath: String by lazy {
        name.replace(":", File.separator).replace(".", File.separator) + File.separator + artifact + "-" + version + ".jar"
    }

    companion object {
        val libraryFolder: File by lazy {
            File(Bukkit.getWorldContainer(), "libraries")
        }

        val plugin: software.shattered.choir.ChoirPlugin by lazy {
            software.shattered.choir.ChoirPlugin.get()
        }

        fun fromString(source: String): Dependency {
            val parts = source.split(":", limit = 3)
            if (parts.size != 3) {
                throw IllegalArgumentException("Source must be a 3-part string separated by colons")
            }
            val (group, artifact, version) = parts
            return Dependency(group, artifact, version)
        }
    }

    fun load() {
        if (minVersion != null && !MinecraftVersion.ServerVersion.newerThan(minVersion) && MinecraftVersion.ServerVersion != minVersion) {
            plugin.logger.info("Skipping dependency $name due to old server version; Requires > $minVersion")
        }
        if (maxVersion != null && MinecraftVersion.ServerVersion.newerThan(maxVersion)) {
            plugin.logger.info("Skipping dependency $name due to new server version; Requires < $maxVersion")
        }

        try {
            val file = File(libraryFolder, filePath)
            if (!file.exists()) {
                file.parentFile.mkdirs()

                val url = URL(webUrl)
                val connection = connectionModifier(url.openConnection())
                connection.getInputStream().use {
                    Files.copy(it, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
            }

            val url = file.toURI().toURL()
            val classLoader = plugin.javaClass.classLoader

            getMethod(URLClassLoader::class.java, "addURL", URL::class.java) {
                it.invoke(classLoader, url)
            }
            plugin.featureManager.add(Feature(plugin.key(artifact), "Library $artifact", true, "Library located at $name", "", true, 0))
        } catch (ex: Exception) {
            plugin.logger.log(Level.SEVERE, "Failed to load library $name; some functionality may not work or cause further errors. Caused by:", ex)
            plugin.featureManager.add(Feature(plugin.key(artifact), "Library $artifact", true, "Library located at $name", "", false, 0))
        }
    }

    override fun toString(): String {
        return name;
    }
}