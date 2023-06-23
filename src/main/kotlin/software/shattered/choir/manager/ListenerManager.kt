package software.shattered.choir.manager

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class ListenerManager(val plugin: Plugin) {
    private val listeners = mutableListOf<Listener>()
    private var listening = true

    fun <T : Event> on(fn: (e: T) -> Unit): Listener {
        val listener = object : Listener {
            @Suppress("unused")
            fun onEvent(e: T) {
                if (listening) {
                    fn(e)
                }
            }
        }
        this.listeners += listener
        if (this.listening) {
            this.plugin.server.pluginManager.registerEvents(listener, plugin)
        }
        return listener
    }

    fun unlisten() {
        this.listeners.forEach(HandlerList::unregisterAll)
        this.listening = false
    }

    fun listen() {
        this.listeners.forEach {
            this.plugin.server.pluginManager.registerEvents(it, plugin)
        }
        this.listening = true
    }
}