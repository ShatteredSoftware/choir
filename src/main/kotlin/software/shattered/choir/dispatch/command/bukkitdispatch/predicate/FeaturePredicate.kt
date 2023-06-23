package software.shattered.choir.dispatch.command.bukkitdispatch.predicate

import software.shattered.choir.datastore.GenericDataStore
import software.shattered.choir.dispatch.command.bukkitdispatch.context.BukkitCommandContext
import software.shattered.choir.dispatch.predicate.DispatchPredicate
import software.shattered.choir.dispatch.predicate.PredicateResult
import software.shattered.choir.api.Feature

class FeaturePredicate(val feature: Feature) : DispatchPredicate<BukkitCommandContext> {
    override fun check(state: BukkitCommandContext, debug: Boolean): PredicateResult {
        state.debugLog("predicate.feature.start", { unwrap(feature) }, state.getLocale())
        if (state.canUse(feature)) {
            state.debugLog("predicate.feature.pass", { unwrap(feature) }, state.getLocale())
            return PredicateResult(passed = true)
        }
        state.debugLog("predicate.feature.fail", { unwrap(feature) }, state.getLocale())
        return PredicateResult(passed = false, unwrap(feature))
    }

    override val failureMessageId = "feature-no-permission"

    private fun unwrap(feature: Feature) = GenericDataStore.of(
        "name" to feature.name,
        "key" to feature.key,
        "description" to feature.description,
        "permission" to feature.permission
    )
}