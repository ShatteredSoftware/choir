package software.shattered.choir.persistence.flatfile

import com.google.gson.Gson
import software.shattered.choir.persistence.CachedPlayerDataContainer
import software.shattered.choir.attribute.Identified
import software.shattered.choir.attribute.NamespacedTypeKey
import software.shattered.choir.persistence.tasks.RunStrategy
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class JsonPlayerDataBackend<T : Identified>(
    val parent: File,
    val key: NamespacedTypeKey<T>,
    private val gson: Gson,
    private val runStrategy: RunStrategy
) :
    CachedPlayerDataContainer<T>() {

    val baseFolder = File(parent, "playerdata")

    override fun doSave(id: String, value: T) {
        runStrategy.execute {
            baseFolder.mkdirs()

            val folder = File(baseFolder, id)
            folder.mkdirs()

            val file = File(folder, "${this.key.key}.json")

            FileWriter(file).use {
                it.write(gson.toJson(value))
            }
        }
    }

    override fun doLoad(id: String): T? {
        baseFolder.mkdirs()

        val folder = File(baseFolder, id)
        folder.mkdirs()

        val file = File(folder, "${key.key}.json")

        if (!file.exists()) {
            return null
        }

        FileReader(file).use {
            return gson.fromJson(it.readText(), key.type)
        }
    }

    override fun doDelete(id: String) {
        runStrategy.execute {
            if (!baseFolder.exists()) {
                return@execute
            }

            val folder = File(baseFolder, id)
            if (!folder.exists()) {
                return@execute
            }

            val file = File(folder, "${key.key}.json")

            if (!file.exists()) {
                return@execute
            }

            file.delete()
        }
    }
}