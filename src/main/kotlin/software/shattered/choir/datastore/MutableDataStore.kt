package software.shattered.choir.datastore

interface MutableDataStore : DataStore {

    /**
     * Adds or replaces a value in this container. Null values are ignored.
     */
    fun <T : Any> put(id: String, value: T?)

    /**
     * Adds a value to this container if the given key doesn't already exist.
     *
     * @param id The ID to compare against.
     * @param value The value to possibly insert.
     */
    fun <T : Any> putIfAbsent(id: String, value: T)

    fun remove(id: String)

    /**
     * Removes all values from this container.
     */
    fun clear()

    operator fun set(s: String, value: Any) {
        this.put(s, value)
    }

    fun pullFrom(other: DataStore) {
        other.keys.forEach {
            this.put(it, other.getUnsafe(it))
        }
    }
}

/**
 * Useful for doing multiple operations/fetches from the container. Provides the class for you, and provides a nicer
 * interface.
 *
 * @param T The class to mask.
 * @return A masked data container that can only retrieve the given class.
 */
inline fun <reified Underlying : MutableDataStore, reified T : Any> Underlying.masked(): MaskedDataContainer<Underlying, T> {
    return MaskedDataContainer(T::class.java, this)
}
