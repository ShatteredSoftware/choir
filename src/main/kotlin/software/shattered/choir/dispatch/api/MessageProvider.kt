package software.shattered.choir.dispatch.api

import java.util.Locale

interface MessageProvider {
    fun get(id: String, data: Map<String, Any> = emptyMap(), locale: Locale): String
}