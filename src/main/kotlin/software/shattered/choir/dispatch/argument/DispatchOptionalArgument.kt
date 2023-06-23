package software.shattered.choir.dispatch.argument

import software.shattered.choir.dispatch.context.CommandContext

interface DispatchOptionalArgument<StateType : CommandContext, T> :
    DispatchArgument<StateType, T> {
    fun default(state: StateType): T?
}