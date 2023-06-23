package software.shattered.choir.api

import org.bukkit.NamespacedKey as BukkitKey
import org.bukkit.plugin.Plugin
import software.shattered.choir.attribute.NamespacedKey
import java.util.*

open class PluginKey(val plugin: Plugin, override val key: String = UUID.randomUUID().toString()) :
    NamespacedKey {
    override val namespace: String = plugin.name.lowercase()

    override fun toString(): String {
        return stringify()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NamespacedKey && other !is BukkitKey) {
            return false
        }
        return this === other || this.toString() == other.toString()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }
}