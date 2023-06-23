package software.shattered.choir.persistence

import software.shattered.choir.attribute.Identified


abstract class CachedDataContainer<T : Identified> :
    DataContainer<T> {
    protected val cache = mutableMapOf<String, T>()
    protected val deleted = mutableListOf<String>()
    private var dirty: Boolean = false

    abstract fun doLoad(id: String): T?

    abstract fun doDelete(id: String)

    abstract fun doSave(value: T)

    override fun getIds(): Set<String> {
        return cache.keys
    }

    override fun load(id: String): T? {
        if (cache.containsKey(id)) {
            return cache[id]
        }
        val value = doLoad(id)
        if (value != null) {
            cache[id] = value
        }
        return value
    }

    override fun save(value: T) {
        dirty = true
        cache[value.id] = value
    }

    override fun delete(id: String) {
        dirty = true
        cache.remove(id)
        deleted.add(id)
    }

    override fun flush() {
        if (dirty) {
            cache.values.forEach {
                doSave(it)
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