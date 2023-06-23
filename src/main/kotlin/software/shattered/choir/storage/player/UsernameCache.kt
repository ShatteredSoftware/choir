package software.shattered.choir.storage.player

import software.shattered.choir.extensions.addSafe


class UsernameCache {
    val usernames: MutableMap<String, MutableSet<String>> = mutableMapOf()

    fun add(player: ChoirPlayer) {
        usernames.addSafe(player.currentName, player.id)
    }
}