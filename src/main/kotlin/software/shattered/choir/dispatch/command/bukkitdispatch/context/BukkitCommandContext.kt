package software.shattered.choir.dispatch.command.bukkitdispatch.context

import software.shattered.choir.storage.player.ChoirPlayer
import software.shattered.choir.dispatch.context.CommandContext
import software.shattered.choir.api.Feature
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import software.shattered.choir.ChoirPlugin
import software.shattered.choir.dispatch.api.MessageProvider
import java.util.*

class BukkitCommandContext(val sender: CommandSender, messageProvider: MessageProvider, private val miniMessage: MiniMessage): CommandContext(messageProvider) {
    companion object {
        private val serializer: BungeeComponentSerializer = BungeeComponentSerializer.get()
    }

    fun canUse(feature: Feature): Boolean {
        if (sender is Player) {
            return software.shattered.choir.ChoirPlugin.get().featureManager.canUse(getPlayer() ?: return false, feature)
        }
        if (sender is ConsoleCommandSender) {
            return feature.enabled
        }
        return false
    }

    override fun sendMessage(message: String) {
        val component = miniMessage.deserialize(message)
        sender.sendMessage(component)
    }

    override fun getLocale(): Locale {
        return getPlayer()?.locale ?: return Locale.US
    }

    fun getPlayer() : ChoirPlayer? {
        return if (sender is Player) {
            software.shattered.choir.ChoirPlugin.get().getPlayer(sender.uniqueId.toString())
        }
        else null
    }
}