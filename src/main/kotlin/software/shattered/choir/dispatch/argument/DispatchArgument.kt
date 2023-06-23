package software.shattered.choir.dispatch.argument

import software.shattered.choir.dispatch.context.CommandContext

interface DispatchArgument<in StateType : CommandContext, out T> {
    val expectedArgs: Int
    val name: String
    val usageId: String
    fun validate(arguments: List<String>, start: Int, state: StateType): ArgumentValidationResult<out T>
    fun complete(partialArguments: List<String>, start: Int, state: StateType): List<String>
}