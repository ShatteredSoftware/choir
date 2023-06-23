package software.shattered.choir.hooks

interface HookContainer {
    fun <T : Any> get(cls: Class<T>): T
}