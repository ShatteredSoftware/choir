package software.shattered.choir.dispatch.util

object StringUtil {
    fun <T : MutableCollection<String>> copyPartialMatches(token: String, originals: Iterable<String>, collection: T): T {
        for (string in originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string)
            }
        }
        return collection
    }

    fun startsWithIgnoreCase(string: String, prefix: String): Boolean {
        return if (string.length < prefix.length) {
            false
        } else string.regionMatches(0, prefix, 0, prefix.length, ignoreCase = true)
    }

}