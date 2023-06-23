package software.shattered.choir.util

import java.util.logging.Logger

object Util {
    fun <T : Any> reflectiveTypeAssertion(value: Any, cl: Class<T>, logger: Logger? = null): T? {
        if (cl.isInstance(value)) {
            @Suppress("UNCHECKED_CAST") // I check this right above this, I promise.
            return value as T
        }
        if (logger != null) {
            val trace = Throwable().stackTrace
            logger.warning("Type assertion of $value to ${cl.name} failed: ${value.javaClass.name} cannot be assigned to ${cl.name}.\nStacktrace:\n$trace")
        }
        return null
    }
}