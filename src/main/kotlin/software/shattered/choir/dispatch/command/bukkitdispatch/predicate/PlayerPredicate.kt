package software.shattered.choir.dispatch.command.bukkitdispatch.predicate

import software.shattered.choir.dispatch.command.bukkitdispatch.context.BukkitCommandContext
import software.shattered.choir.dispatch.predicate.DispatchPredicate
import software.shattered.choir.dispatch.predicate.PredicateResult
import org.bukkit.entity.Player

object PlayerPredicate : DispatchPredicate<BukkitCommandContext> {
    override fun check(state: BukkitCommandContext, debug: Boolean): PredicateResult {
        state.debugLog("predicate.player.start", locale = state.getLocale())
        if (state.sender is Player) {
            state.debugLog("predicate.player.pass", locale = state.getLocale())
            return PredicateResult(passed = true)
        }
        state.debugLog("predicate.player.fail", locale = state.getLocale())
        return PredicateResult(passed = false)
    }

    override val failureMessageId = "no-console"
}