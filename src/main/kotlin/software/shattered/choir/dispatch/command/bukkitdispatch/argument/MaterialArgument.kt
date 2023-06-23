package software.shattered.choir.dispatch.command.bukkitdispatch.argument

import software.shattered.choir.dispatch.argument.impl.primitive.ChoiceArgument
import software.shattered.choir.dispatch.context.CommandContext
import org.bukkit.Material

class MaterialArgument(name: String) : ChoiceArgument<CommandContext, Material>(
    name, Material::getMaterial, { Material.values().map { it.name.lowercase() } }, "usage.material", Material.AIR, false
)