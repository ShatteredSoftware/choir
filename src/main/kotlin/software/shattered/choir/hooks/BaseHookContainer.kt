package software.shattered.choir.hooks

import java.io.Closeable

open class BaseHookContainer(
    private val parent: HookContainer? = null
) : HookContainer, MutableHookContainer, Closeable {

    private val instances: MutableMap<Class<*>, Any> = mutableMapOf()
    private val initializers: MutableMap<Class<*>, () -> Any?> = mutableMapOf()

    override fun <T : Any> bind(cls: Class<T>, inst: T) {
        if (cls.isInstance(inst)) {
            instances[cls] = inst
            bindSupers(inst.javaClass, inst)
        }
        else {
            throw Error("${inst.javaClass} is not assignable to $cls")
        }
    }

    override fun <T : Any> bindFactory(cls: Class<T>, init: (container: HookContainer) -> T?) {
        initializers[cls] = { init(this) }
    }

    override fun <T : Any> get(cls: Class<T>): T {
        val inst = instances[cls]
        if (inst != null) {
            @Suppress("UNCHECKED_CAST")
            return inst as T
        }

        @Suppress("UNCHECKED_CAST")
        val value = initializers[cls]?.invoke() as? T

        return if (value != null) {
            bind(cls, value)
            return value
        } else {
            parent?.get(cls) ?: throw Error("Class $cls not found in container")
        }
    }

    private fun <T : Any> bindSupers(cls: Class<T>, value: T) {
        var curr: Class<in T> = cls
        while (curr !in instances && curr != Object::class.java) {
            instances[curr.superclass] = value
            curr.interfaces.forEach {
                if (it !in instances) {
                    instances[it] = value
                }
            }
            curr = curr.superclass
        }
    }

    fun inChild(run: MutableHookContainer.() -> Unit) {
        BaseHookContainer(this).use {
            run()
        }
    }

    fun immutable(): HookContainer {
        return this
    }

    override fun close() {
        this.instances.clear()
        this.initializers.clear()
    }
}