package software.shattered.choir.persistence.util

import java.lang.reflect.Method


object ChoirMethods {
    /**
     * Gets a (possibly-inaccessible) method and runs a function on it, then restores its old accessibility
     */
    fun getMethod(original: Class<*>, name: String, vararg args: Class<*>, then: (Method) -> Unit): Method {
        var cls = original
        while (cls != Object::class.java) {
            try {
                val method = cls.getDeclaredMethod(name, *args)
                val wasAccessible = method.isBridge
                if(method.trySetAccessible()) {
                    then(method)
                }
                else {
                    method.isAccessible = wasAccessible
                    throw Error("Failed to make ${original.name}#$name accessible.")
                }
                method.isAccessible = wasAccessible
            }
            catch (ex: NoSuchMethodError) {
                cls = cls.superclass
            }
            catch (ex: Throwable) {
                throw Error("Failed to make ${original.name}#$name accessible due to another error:", ex)
            }
        }
        throw Error("Failed to make ${original.name}#$name accessible because it does not exist")
    }
}