package software.shattered.choir.api

import org.bukkit.plugin.Plugin
import software.shattered.choir.attribute.NamespacedTypeKey

open class PluginTypeKey<T>(plugin: Plugin, override val type: Class<T>, override val key: String) :
    NamespacedTypeKey<T> {
    override val namespace: String = plugin.name.lowercase()

    override fun toString(): String {
        return stringify()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PluginTypeKey<*>) {
            return false
        }
        return this === other || this.toString() == other.toString()
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}