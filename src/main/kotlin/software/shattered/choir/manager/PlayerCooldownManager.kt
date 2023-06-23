package software.shattered.choir.manager

import software.shattered.choir.api.Feature
import software.shattered.choir.storage.player.ChoirPlayer
import software.shattered.choir.extensions.getSafe

class PlayerCooldownManager(val currentTimeFunction: () -> Long = System::currentTimeMillis) {
    val map: MutableMap<String, MutableMap<String, Long>> = mutableMapOf()

    fun canUse(player: ChoirPlayer, feature: Feature): Boolean {
        val playerUses = map[player.id] ?: return true
        val lastUse = playerUses[feature.id] ?: return true
        return currentTimeFunction() + feature.cooldown < currentTimeFunction()
    }

    fun use(player: ChoirPlayer, feature: Feature) {
        val playerUses = map.getSafe(player.id, ::mutableMapOf)
        playerUses[feature.id] = currentTimeFunction()
    }

    fun reset(player: ChoirPlayer, feature: Feature) {
        val playerUses = map[player.id] ?: return
        playerUses.remove(feature.id)
    }
}