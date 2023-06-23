package software.shattered.choir.datastore

class MaskedDataContainer<Underlying : MutableDataStore, T : Any>(
    private val cl: Class<T>,
    private val dataStore: Underlying
) {
    operator fun get(id: String): T? {
        return dataStore.get(id, cl)
    }

    operator fun set(id: String, value: T) {
        dataStore.put(id, value)
    }

    fun unmasked(): Underlying {
        return dataStore
    }
}