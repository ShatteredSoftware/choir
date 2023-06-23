package software.shattered.choir.persistence

import software.shattered.choir.wrapper.ChoirPlayer
import software.shattered.choir.attribute.Identified
import software.shattered.choir.extensions.tee

interface PlayerDataContainer<T : Identified> {
    fun init() {}
    fun save(player: ChoirPlayer, value: T)
    fun load(player: ChoirPlayer): T?
    fun delete(player: ChoirPlayer)
    fun load(player: ChoirPlayer, init: () -> T): T {
        return load(player).let { result ->
            if (result != null) return result
            else init().tee {
                save(player, it)
            }
        }
    }

    fun flush() {}
    fun invalidate() {}
    fun isDirty(): Boolean { return true }
}