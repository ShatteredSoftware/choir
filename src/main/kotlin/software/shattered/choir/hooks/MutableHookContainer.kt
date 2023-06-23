package software.shattered.choir.hooks

interface MutableHookContainer {
    fun <T : Any> bind(cls: Class<T>, inst: T)
    fun <T : Any> bindFactory(cls: Class<T>, init: (container: HookContainer) -> T?)
}