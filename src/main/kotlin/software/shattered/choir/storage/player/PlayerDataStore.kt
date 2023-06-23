package software.shattered.choir.storage.player

import software.shattered.choir.attribute.NamespacedKey
import software.shattered.choir.wrapper.ChoirPlayer

interface PlayerDataStore {
    fun <T : Any> save(player: ChoirPlayer, key: NamespacedKey, value: T)
    fun <T : Any> load(player: ChoirPlayer, key: NamespacedKey, clazz: Class<T>): T?
    fun <T : Any> load(player: ChoirPlayer, key: NamespacedKey, clazz: Class<T>, init: () -> T): T {
        return load(player, key, clazz) ?: init()
    }
}