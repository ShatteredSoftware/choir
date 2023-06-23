package software.shattered.choir.storage.player

import com.google.gson.Gson
import org.bukkit.plugin.java.JavaPlugin
import software.shattered.choir.attribute.NamespacedKey
import software.shattered.choir.wrapper.ChoirPlayer
import software.shattered.choir.persistence.tasks.RunStrategy
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class JsonPlayerDataStore(val gson: Gson, val runStrategy: RunStrategy, val plugin: JavaPlugin) : PlayerDataStore {
    override fun <T : Any> save(player: ChoirPlayer, key: NamespacedKey, value: T) {
        val baseFolder = File(plugin.dataFolder, "playerdata")
        baseFolder.mkdirs()

        val folder = File(baseFolder, player.id)
        folder.mkdirs()

        val file = File(folder, "${key.key}.json")

        runStrategy.execute {
            FileWriter(file).use {
                it.write(gson.toJson(value))
            }
        }
    }

    override fun <T : Any> load(player: ChoirPlayer, key: NamespacedKey, clazz: Class<T>): T? {
        val baseFolder = File(plugin.dataFolder, "playerdata")
        baseFolder.mkdirs()

        val folder = File(baseFolder, player.id)
        folder.mkdirs()

        val file = File(folder, "${key.key}.json")

        if (!file.exists()) {
            return null
        }

        FileReader(file).use {
            return gson.fromJson(it.readText(), clazz)
        }
    }
}