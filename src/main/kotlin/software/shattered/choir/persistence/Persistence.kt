package software.shattered.choir.persistence

import software.shattered.choir.persistence.tasks.RunStrategy
import com.google.gson.Gson
import software.shattered.choir.attribute.Identified
import software.shattered.choir.attribute.NamespacedTypeKey
import software.shattered.choir.persistence.flatfile.AbstractJsonDataContainer
import software.shattered.choir.persistence.manager.DataContainerManager
import java.io.File

class Persistence(
    val gson: Gson,
    private val baseFolder: File,
    private val runStrategy: RunStrategy
) {

    private val containerManager: DataContainerManager =
        DataContainerManager()

    fun <T : Identified> addDataContainer(pluginKey: NamespacedTypeKey<T>, dataContainer: DataContainer<T>) {
        containerManager.addDataContainer(pluginKey, dataContainer)
    }

    fun <T : Identified> save(pluginTypeKey: NamespacedTypeKey<T>, value: T) {
        containerManager.save(pluginTypeKey, value)
    }

    fun <T : Identified> load(pluginTypeKey: NamespacedTypeKey<T>, id: String): T? {
        return containerManager.load(pluginTypeKey, id)
    }

    fun <T : Identified> delete(pluginTypeKey: NamespacedTypeKey<T>, id: String) {
        containerManager.delete(pluginTypeKey, id)
    }

    fun <T : Identified> newJsonBackend(pluginTypeKey: NamespacedTypeKey<T>, prefetch: Boolean = false) {
        containerManager.addDataContainer(pluginTypeKey, AbstractJsonDataContainer(File(baseFolder, pluginTypeKey.key), pluginTypeKey.type, gson, prefetch))
    }

    inline fun <reified T> loadJsonFileAs(file: File, noinline init: () -> T): T {
        return FileUtil.loadJsonFileAs(file, T::class.java, gson, init)
    }

    fun <T> loadJsonFileAs(file: File, clazz: Class<T>, init: () -> T): T {
        return FileUtil.loadJsonFileAs(file, clazz, gson, init)
    }

    inline fun <reified T> loadJsonFileAs(file: File): T? {
        return FileUtil.loadJsonFileAs(file, T::class.java, gson)
    }

    fun <T> loadJsonFileAs(file: File, clazz: Class<T>): T? {
        return FileUtil.loadJsonFileAs(file, clazz, gson)
    }

    fun <T> saveJsonFileAs(file: File, value: T) {
        FileUtil.saveJsonFileAs(file, value, gson, runStrategy)
    }

    inline fun <reified T> loadYamlFileAs(file: File, noinline init: () -> T): T {
        return FileUtil.loadYamlFileAs(file, T::class.java, gson, init)
    }

    fun <T> loadYamlFileAs(file: File, clazz: Class<T>, init: (() -> T)): T {
        return FileUtil.loadYamlFileAs(file, clazz, gson, init)
    }

    inline fun <reified T> loadYamlFileAs(file: File): T? {
        return FileUtil.loadYamlFileAs(file, T::class.java, gson)
    }

    fun <T> loadYamlFileAs(file: File, clazz: Class<T>): T? {
        return FileUtil.loadYamlFileAs(file, clazz, gson)
    }

    fun <T> saveYamlFileAs(file: File, value: T) {
        FileUtil.saveYamlFileAs(file, value, gson, runStrategy)
    }
}