package software.shattered.choir.dispatch.action

import software.shattered.choir.attribute.Identified

interface DispatchAction<in StateType> : Identified {
    fun run(state: StateType, debug: Boolean = false)
}