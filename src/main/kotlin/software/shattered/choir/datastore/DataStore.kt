package software.shattered.choir.datastore

import software.shattered.choir.attribute.NamespacedTypeKey
import software.shattered.choir.util.Util.reflectiveTypeAssertion


interface DataStore {
    companion object {
        fun stringify(store: DataStore): Map<String, String> {
            val map = mutableMapOf<String, String>()
            store.keys.forEach {
                map[it] = store.getUnsafe(it).toString()
            }
            return map
        }
    }

    val keys: Set<String>

    /**
     * Pulls a value from this container if it exists and is of the given type, or returns `null` otherwise.
     *
     * @param id The ID to look up.
     * @param cl The class to check for.
     * @return The element contained in this container if it exists and is of the given type, or `null` otherwise.
     */
    fun <T : Any> get(id: String, cl: Class<T>): T?
    fun <T : Any> get(pluginTypeKey: NamespacedTypeKey<T>): T?

    /**
     * Pulls a value from this container if it exists and is of the same type as the default, or returns the default
     * otherwise.
     *
     * @param id The ID to look up.
     * @param def The default value.
     * @return The element contained in this container if it exists and is of the given type, or the default value
     * otherwise.
     */
    fun <T : Any> getOrDef(id: String, def: T): T

    /**
     * Pulls a value from this container regardless of its class.
     *
     * @param id The ID to look up.
     * @return The element contained in this container if it exists.
     */
    fun getUnsafe(id: String): Any?

    /**
     * Reduces this to a map of one class; any values that are not of the given type are not included in the new map.
     *
     * @param cl The class to filter this down to.
     * @return A map of only the given class.
     */
    fun <T : Any> asMapOf(cl: Class<T>): Map<String, T> {
        val map = mutableMapOf<String, T>()
        keys.forEach {
            val raw = getUnsafe(it) ?: return@forEach
            val v = reflectiveTypeAssertion(raw, cl) ?: return@forEach
            map[it] = v
        }
        return map.toMap()
    }

    fun merge(other: DataStore): DataStore {
        val newStore = GenericDataStore()
        this.keys.forEach {
            newStore.put(it, this.getUnsafe(it))
        }
        other.keys.forEach {
            newStore.put(it, other.getUnsafe(it))
        }
        return newStore
    }
}

/**
 * Infers the class parameter from a provided generic.
 *
 * @see [DataStore.get]
 */
inline fun <reified T : Any> DataStore.get(id: String): T? {
    return get(id, T::class.java)
}