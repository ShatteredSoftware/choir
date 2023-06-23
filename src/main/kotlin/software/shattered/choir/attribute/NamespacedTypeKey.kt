package software.shattered.choir.attribute

interface NamespacedTypeKey<T> : NamespacedKey {
    val type: Class<T>

    override fun stringify(): String {
        return "$namespace:${type.name}:$key"
    }
}