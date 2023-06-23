package software.shattered.choir.manager

import software.shattered.choir.api.PluginTypeKey
import software.shattered.choir.attribute.Identified
import software.shattered.choir.persistence.DataContainer

class DataContainerManager {
    private val dataContainers: MutableMap<PluginTypeKey<*>, DataContainer<*>> = mutableMapOf()

    fun <T : Identified> addDataContainer(pluginKey: PluginTypeKey<T>, dataContainer: DataContainer<T>) {
        dataContainers[pluginKey] = dataContainer
    }

    private fun <T : Identified> getContainer(pluginTypeKey: PluginTypeKey<T>): DataContainer<T>? {
        @Suppress("UNCHECKED_CAST") // I promise they match
        return dataContainers[pluginTypeKey] as DataContainer<T>?
    }

    fun <T : Identified> save(pluginTypeKey: PluginTypeKey<T>, value: T) {
        val container = getContainer(pluginTypeKey) ?: return
        container.save(value)
    }

    fun <T : Identified> load(pluginTypeKey: PluginTypeKey<T>, id: String): T? {
        val container = getContainer(pluginTypeKey) ?: return null
        val result = container.load(id) ?: return null
        if (pluginTypeKey.type.isInstance(result)) {
            return result
        }
        return null
    }

    fun <T : Identified> delete(pluginTypeKey: PluginTypeKey<T>, id: String) {
        val container = getContainer(pluginTypeKey) ?: return
        container.delete(id)
    }
}