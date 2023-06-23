package software.shattered.choir.tasks

import org.bukkit.plugin.java.JavaPlugin
import software.shattered.choir.persistence.tasks.RunStrategy

class AsyncBukkitRunStrategy(private val plugin: JavaPlugin) : RunStrategy() {
    override fun execute(runnable: Runnable) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, runnable)
    }
}