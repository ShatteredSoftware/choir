package software.shattered.choir.dispatch.command.bukkitdispatch.argument

import software.shattered.choir.storage.player.ChoirPlayer
import software.shattered.choir.dispatch.argument.impl.primitive.ChoiceArgument
import software.shattered.choir.dispatch.command.bukkitdispatch.context.BukkitCommandContext
import org.bukkit.Bukkit
import software.shattered.choir.ChoirPlugin

open class OnlinePlayerArgument(name: String) : ChoiceArgument<BukkitCommandContext, ChoirPlayer>(
    name,
    { software.shattered.choir.ChoirPlugin.get().getPlayer(it) },
    { Bukkit.getOnlinePlayers().map { it.name } },
    "usage.player"
)