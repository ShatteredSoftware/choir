package software.shattered.choir.manager

import org.bukkit.entity.Player
import software.shattered.choir.api.Feature

class FeatureManager {
    private val map = mutableMapOf<String, Feature>()

    fun get(id: String): Feature? {
        return map[id]
    }

    fun add(feature: Feature) {
        map[feature.id] = feature
    }

    fun canUse(player: Player, id: String): Boolean {
        val feature = get(id) ?: return false
        return feature.enabled && (feature.defaultEnabled || player.hasPermission(feature.permission))
    }

    fun canUse(player: Player, feature: Feature): Boolean {
        return feature.enabled && (feature.defaultEnabled || player.hasPermission(feature.permission))
    }
}