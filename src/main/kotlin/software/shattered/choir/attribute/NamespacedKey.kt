package software.shattered.choir.attribute

interface NamespacedKey {
    val namespace: String
    val key: String

    fun stringify(): String {
        return "$namespace:$key"
    }
}