package software.shattered.choir.storage.player

import software.shattered.choir.datastore.GenericDataStore
import org.bukkit.entity.Player
import software.shattered.choir.attribute.Identified
import java.util.Locale

class ChoirPlayer(
    @Transient val internalPlayer: Player,
    override val locale: Locale = Locale.US // TODO: implement in storage?
) : Identified, software.shattered.choir.wrapper.ChoirPlayer, Player by internalPlayer {

    override val username: String
        get() = internalPlayer.name

    override val pastUsernames: Set<String>
        get() = emptySet()

    override val id = internalPlayer.uniqueId.toString()
    val currentName = internalPlayer.name
    val knownUsernames = setOf(internalPlayer.name)

    @Transient
    val data = GenericDataStore()
}