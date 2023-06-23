package software.shattered.choir.manager

import com.google.gson.Gson
import org.bukkit.entity.Player
import software.shattered.choir.ChoirPlugin
import software.shattered.choir.api.PluginTypeKey
import software.shattered.choir.storage.player.ChoirPlayer
import software.shattered.choir.attribute.Identified
import software.shattered.choir.persistence.Persistence
import java.util.UUID

class PlayerManager(
    val plugin: software.shattered.choir.ChoirPlugin,
    private val persistence: Persistence,
    private val prefetch: Set<PluginTypeKey<Identified>> = setOf()
) {
    private val internalOnlinePlayers: MutableMap<UUID, ChoirPlayer> = mutableMapOf()
    private val internalOnlinePlayersByName: MutableMap<String, ChoirPlayer> = mutableMapOf()

    val onlinePlayers: Map<UUID, ChoirPlayer>
        get() = internalOnlinePlayers.toMap()

    val onlinePlayersByName: Map<String, ChoirPlayer>
        get() = internalOnlinePlayersByName.toMap()

    fun join(player: Player) {
        val choirPlayer = ChoirPlayer(player)
        internalOnlinePlayers[player.uniqueId] = choirPlayer
        internalOnlinePlayersByName[player.name] = choirPlayer
        prefetch.forEach {
            val data = persistence.load(it, choirPlayer.id) ?: return@forEach
            choirPlayer.data[it.toString()] = data
        }
    }

    fun leave(player: Player) {
        val corePlayer = internalOnlinePlayers[player.uniqueId] ?: return
        internalOnlinePlayers.remove(player.uniqueId)
        internalOnlinePlayersByName.remove(player.name)
        prefetch.forEach {
            val data = corePlayer.data.get(it.toString(), it.type) ?: return@forEach
            persistence.save(it, data) // TODO this is not what we want this to do
        }
    }
}