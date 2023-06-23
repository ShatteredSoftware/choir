package software.shattered.choir.tasks

import org.bukkit.plugin.java.JavaPlugin
import software.shattered.choir.persistence.tasks.RunStrategy

class BukkitRunStrategy(private val plugin: JavaPlugin) : RunStrategy() {
    override fun execute(runnable: Runnable) {
        plugin.server.scheduler.runTask(plugin, runnable)
    }
}