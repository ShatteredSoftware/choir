package software.shattered.choir.api

import software.shattered.choir.attribute.Identified
import software.shattered.choir.attribute.NamespacedKey

class Feature(
    val key: NamespacedKey,
    val name: String,
    val defaultEnabled: Boolean,
    val description: String,
    val permission: String,
    val enabled: Boolean = true,

    /**
     * Cooldown in milliseconds.
     */
    val cooldown: Int = 10
) : Identified {
    override val id: String
        get() = key.toString()
}