package software.shattered.choir.polyfill

import org.bukkit.configuration.file.YamlConfiguration
import software.shattered.choir.ChoirPlugin
import software.shattered.choir.data.MinecraftVersion
import software.shattered.choir.api.Feature
import software.shattered.choir.persistence.util.ChoirFiles

object LibraryLoader {
    fun loadLibraries(
        plugin: software.shattered.choir.ChoirPlugin,
        key: String = "libraries",
        filename: String = "plugin.yml",
    ): Set<String> {
        val pluginYaml = YamlConfiguration()
        pluginYaml.loadFromString(
            ChoirFiles.readInternalFileLines(filename, plugin.impl, plugin.logger)
                .joinToString("\n")
        )

        val libraries = pluginYaml.getStringList(key).map(Dependency::fromString)
        if (MinecraftVersion.ServerVersion.newerThan(MinecraftVersion.Legacy)) {
            // Assume spigot loaded things properly
            libraries.forEach {
                plugin.featureManager.add(Feature(plugin.key(it.artifact), "Library ${it.artifact}", true, "Library located at ${it.name}", "", true, 0))
            }
        }
        else {
            // Load the libraries ourselves
            libraries.forEach(Dependency::load)
        }
        return libraries.map(Dependency::name).toSet()
    }
}