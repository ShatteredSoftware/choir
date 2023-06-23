package software.shattered.choir.storage.player

import software.shattered.choir.attribute.NamespacedKey
import software.shattered.choir.wrapper.ChoirPlayer
import software.shattered.choir.extensions.addSafe
import software.shattered.choir.extensions.getSafe
import software.shattered.choir.persistence.tasks.RunStrategy

class CachedPlayerDataStore(val baseStore: PlayerDataStore, val runStrategy: RunStrategy) :
    PlayerDataStore {
    private val cache: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()
    private val queued: MutableList<Pair<Pair<ChoirPlayer, NamespacedKey>, Any>> = mutableListOf()

    override fun <T : Any> save(player: ChoirPlayer, key: NamespacedKey, value: T) {
        cache.addSafe(key.key, player.id, value)
        queued.add(player to key to value)
    }

    override fun <T : Any> load(player: ChoirPlayer, key: NamespacedKey, clazz: Class<T>): T? {
        val value =
            cache.getSafe(key.key) { mutableMapOf() }[player.id] ?: baseStore.load(player, key, clazz) ?: return null
        if (clazz.isInstance(value)) {
            @Suppress("UNCHECKED_CAST")
            val checked = value as T
            cache.addSafe(key.key, player.id, checked)
            return checked
        }
        return null
    }

    fun flush() {
        runStrategy.execute {
            queued.forEach {
                val (pk, value) = it
                val (player, key) = pk
                baseStore.save(player, key, value)
            }
        }
    }
}