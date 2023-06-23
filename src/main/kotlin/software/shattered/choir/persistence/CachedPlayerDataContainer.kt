package software.shattered.choir.persistence

import software.shattered.choir.wrapper.ChoirPlayer
import software.shattered.choir.attribute.Identified


abstract class CachedPlayerDataContainer<T : Identified> : PlayerDataContainer<T> {
    protected val cache = mutableMapOf<String, T>()
    protected val deleted = mutableListOf<String>()

    private var dirty: Boolean = false

    abstract fun doLoad(id: String): T?

    abstract fun doDelete(id: String)

    abstract fun doSave(id: String, value: T)

    override fun load(player: ChoirPlayer): T? {
        val id = player.id
        if (cache.containsKey(id)) {
            return cache[id]
        }
        val value = doLoad(id)
        if (value != null) {
            cache[id] = value
        }
        return value
    }

    override fun save(player: ChoirPlayer, value: T) {
        dirty = true
        val id = player.id
        cache[id] = value
    }

    override fun delete(player: ChoirPlayer) {
        dirty = true
        val id = player.id
        cache.remove(id)
        deleted.add(id)
    }

    override fun flush() {
        if (dirty) {
            cache.entries.forEach { (key, it) ->
                doSave(key, it)
            }
            deleted.forEach {
                doDelete(it)
            }
            deleted.clear()
        }
        dirty = false
    }

    override fun invalidate() {
        flush()
        cache.clear()
    }
}