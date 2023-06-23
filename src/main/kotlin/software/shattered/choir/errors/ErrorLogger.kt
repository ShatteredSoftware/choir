package software.shattered.choir.errors

import io.sentry.Hub




class ErrorLogger(private val hub: Hub) {
    private val handledExceptions: MutableSet<String> = mutableSetOf()

    fun handleError(throwable: Throwable) {
        if (handledExceptions.contains(throwable.message)) {
            return
        }
        hub.captureException(throwable)
        throwable.message?.let { handledExceptions.add(it) }
    }
}