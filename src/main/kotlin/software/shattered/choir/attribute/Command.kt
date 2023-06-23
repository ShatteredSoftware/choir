package software.shattered.choir.attribute

interface Command<T> {
    fun apply(state: T)
    fun unapply(state: T)
}