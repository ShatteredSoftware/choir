package software.shattered.choir.dispatch.predicate

import software.shattered.choir.dispatch.context.CommandContext

interface DispatchPredicate<in StateType : CommandContext> {
    val failureMessageId: String

    fun check(state: StateType, debug: Boolean = false): PredicateResult
}