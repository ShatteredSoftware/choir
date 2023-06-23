package software.shattered.choir.math.geometry

class RTree<ItemType : Spatial<*>, V>() {
    data class Entry<K : Spatial<*>, V>(val key: K, val value: V, val node: Node<K, V>)
    data class Node<K : Spatial<*>, V>(val level: Int, val entries: List<Entry<K, V>>);

    private var root: Node<ItemType, V>? = null;

    fun find(area: ItemType) {

    }
}