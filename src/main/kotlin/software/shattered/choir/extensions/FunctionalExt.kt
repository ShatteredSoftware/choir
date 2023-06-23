package software.shattered.choir.extensions

fun <T> T.tee(f: (T) -> Unit): T {
    f(this)
    return this
}