package software.shattered.choir.manager

import software.shattered.choir.api.PluginTypeKey
import software.shattered.choir.persistence.PlayerDataContainer
import software.shattered.choir.storage.player.ChoirPlayer
import software.shattered.choir.attribute.Identified

class PlayerDataContainerManager {
    private val dataContainers: MutableMap<PluginTypeKey<*>, PlayerDataContainer<*>> = mutableMapOf()

    fun <T : Identified> addDataContainer(pluginKey: PluginTypeKey<T>, dataContainer: PlayerDataContainer<T>) {
        dataContainers[pluginKey] = dataContainer
    }

    private fun <T : Identified> getContainer(pluginTypeKey: PluginTypeKey<T>): PlayerDataContainer<T>? {
        @Suppress("UNCHECKED_CAST") // I promise they match
        return dataContainers[pluginTypeKey] as PlayerDataContainer<T>?
    }

    fun <T : Identified> save(player: ChoirPlayer, pluginTypeKey: PluginTypeKey<T>, value: T) {
        val container = getContainer(pluginTypeKey) ?: return
        container.save(player, value)
    }

    fun <T : Identified> load(player: ChoirPlayer, pluginTypeKey: PluginTypeKey<T>): T? {
        val container = getContainer(pluginTypeKey) ?: return null
        val result = container.load(player) ?: return null
        if (pluginTypeKey.type.isInstance(result)) {
            return result
        }
        return null
    }

    fun <T : Identified> load(player: ChoirPlayer, pluginTypeKey: PluginTypeKey<T>, init: () -> T): T {
        val container = getContainer(pluginTypeKey) ?: return init()
        val result = container.load(player, init)
        if (pluginTypeKey.type.isInstance(result)) {
            return result
        }
        return init()
    }

    fun <T : Identified> delete(player: ChoirPlayer, pluginTypeKey: PluginTypeKey<T>) {
        val container = getContainer(pluginTypeKey) ?: return
        container.delete(player)
    }
}
