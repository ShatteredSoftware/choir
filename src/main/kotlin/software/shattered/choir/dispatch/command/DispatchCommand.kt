package software.shattered.choir.dispatch.command

import software.shattered.choir.datastore.GenericDataStore
import software.shattered.choir.datastore.MutableDataStore
import software.shattered.choir.attribute.Identified
import software.shattered.choir.dispatch.action.DispatchAction
import software.shattered.choir.dispatch.argument.ArgumentValidationResult
import software.shattered.choir.dispatch.argument.DispatchArgument
import software.shattered.choir.dispatch.argument.DispatchOptionalArgument
import software.shattered.choir.dispatch.context.CommandContext
import software.shattered.choir.dispatch.predicate.DispatchPredicate

/**
 * Order of operations:
 * * Validate predicates
 * * Validate arguments
 * * Look for children
 *   * If a child matches, pass off handling to that child
 * * Look for optional arguments
 * * Run the given action, passing in data from parsed arguments
 */
class DispatchCommand<StateType : CommandContext>(
    override val id: String,
    private val action: DispatchAction<StateType>,
    private val predicates: List<DispatchPredicate<StateType>> = emptyList(),
    private val arguments: List<DispatchArgument<StateType, *>> = emptyList(),
    private val optionalArguments: List<DispatchOptionalArgument<StateType, *>> = emptyList(),
    private val children: Map<String, DispatchCommand<StateType>> = emptyMap(),
) : Identified {
    companion object {
        fun <T : CommandContext> build(key: String, fn: DispatchCommandBuilder<T>.() -> Unit): DispatchCommand<T> {
            val builder = DispatchCommandBuilder<T>(key)
            fn(builder)
            return builder.build()
        }
    }

    private val argsByPosition: List<Pair<DispatchArgument<StateType, *>, Int>>
    private val requiredArgumentLength = arguments.fold(0) { sum, it -> sum + it.expectedArgs }

    init {
        val argsByPosition = mutableListOf<Pair<DispatchArgument<StateType, *>, Int>>()
        var counter = 0
        for (arg in arguments) {
            for (position in 0 until arg.expectedArgs) {
                argsByPosition.add(arg to counter)
            }
            counter += arg.expectedArgs
        }
        this.argsByPosition = argsByPosition
    }

    fun execute(
        state: StateType,
        args: List<String>,
        currentData: GenericDataStore = GenericDataStore(),
        debug: Boolean = false
    ) {
        if (failedPredicates(state, debug)) {
            return
        }

        state.debugLog(
            "dispatch.debug.length",
            { GenericDataStore.of("size" to args.size, "required" to requiredArgumentLength) }, state.getLocale()
        )
        if (isInvalidArgLength(args)) {
            logArgLengthFailure(state, currentData)
            return
        }

        val (success, lastIndex) = areArgsOk(state, args, currentData, debug)
        if (!success) {
            return
        }

        val child = getChildToRun(args, lastIndex)
        if (child != null) {
            // Pass off handling to children
            child.execute(state, args.slice((lastIndex + 1)..args.lastIndex), currentData)
            return
        }

        if (lastIndex < args.size) {
            checkOptionalArgs(state, args, currentData, lastIndex, debug)
        }

        currentData.pullFrom(state.data)
        state.data = currentData

        action.run(state)
    }

    fun complete(
        state: StateType,
        args: List<String>,
        currentState: GenericDataStore = GenericDataStore(),
        debug: Boolean = false
    ): List<String> {
        if (failedPredicates(state, debug)) {
            return emptyList()
        }

        if (args.size > requiredArgumentLength) {
            val child = children[args[requiredArgumentLength]]
            if (child != null) {
                return child.complete(state, args.subList(requiredArgumentLength, args.size), currentState, debug)
            }
        }

        if (args.size < argsByPosition.size) {
            val (currentArg, startingIndex) = argsByPosition[args.size]
            return currentArg.complete(args, startingIndex, state)
        }
        return emptyList()
    }

    private fun failedPredicates(state: StateType, debug: Boolean): Boolean {
        val results = predicates.map { it.check(state, debug) }
        val predicateResults = results.zip(predicates)
        val failures = predicateResults.filter { (result, _) -> !result.passed }
        if (failures.isNotEmpty()) {
            failures.forEach { (result, predicate) ->
                state.log(predicate.failureMessageId, result.data, state.getLocale())
            }
            return true
        }
        return false
    }

    private fun isInvalidArgLength(args: List<String>): Boolean {
        if (args.size < requiredArgumentLength) {
            return true
        }
        return false
    }

    private fun logArgLengthFailure(state: StateType, data: MutableDataStore) {
        val usage = arguments.fold(StringBuilder()) { builder, arg ->
            builder.append(state.messageProvider.get(arg.usageId, emptyMap(), state.getLocale()))
        }
        data["usage"] = usage
        state.log("command.usage", data, state.getLocale())
    }

    private fun areArgsOk(
        state: StateType,
        args: List<String>,
        data: MutableDataStore,
        debug: Boolean
    ): Pair<Boolean, Int> {
        var currendIndex = 0
        val failures = mutableListOf<ArgumentValidationResult<*>>()
        for (arg in arguments) {
            state.debugLog(
                "dispatch.debug.argument.start",
                { GenericDataStore.of("arg" to arg.name) },
                state.getLocale()
            )

            val result = arg.validate(args, currendIndex, state)

            if (result.success) {
                state.debugLog(
                    "dispatch.debug.argument.pass",
                    { GenericDataStore.of("arg" to arg.name, "value" to (result.result?.toString() ?: "null")) },
                    state.getLocale()
                )
                data[arg.name] =
                    result.result ?: throw IllegalStateException("${arg.name} came back as null when successful")
            } else {
                state.debugLog(
                    "dispatch.debug.argument.fail",
                    { GenericDataStore.of("arg" to arg.name) },
                    state.getLocale()
                )
                failures.add(result)
            }

            currendIndex += arg.expectedArgs
        }

        if (failures.isNotEmpty()) {
            failures.forEach {
                state.log(it.faliureMessageId, it.data, state.getLocale())
            }
            return false to 0
        }
        return true to currendIndex
    }

    private fun getChildToRun(
        args: List<String>,
        lastIndex: Int,
    ): DispatchCommand<StateType>? {
        if (children.isNotEmpty()) {
            if (lastIndex >= args.size) {
                return null
            }
            return children[args[lastIndex]] ?: return null
        }
        return null
    }

    private fun checkOptionalArgs(
        state: StateType,
        args: List<String>,
        data: MutableDataStore,
        start: Int,
        debug: Boolean
    ) {
        val excludedArgs: MutableSet<DispatchArgument<*, *>> = mutableSetOf()
        var current = start
        optionalArguments.forEach { arg ->
            state.debugLog(
                "dispatch.debug.optional.start",
                { GenericDataStore.of("arg" to arg.name) },
                state.getLocale()
            )
            val result = arg.validate(args, current, state)
            if (result.success) {
                state.debugLog(
                    "dispatch.debug.optional.pass",
                    { GenericDataStore.of("arg" to arg.name, "value" to (result.result?.toString() ?: "null")) },
                    state.getLocale()
                )
                data[arg.name] =
                    result.result ?: throw IllegalStateException("${arg.name} came back as null when successful")
                excludedArgs += arg
                current += arg.expectedArgs
            }
            state.debugLog(
                "dispatch.debug.optional.fail",
                { GenericDataStore.of("arg" to arg.name) },
                state.getLocale()
            )
        }
    }
}
