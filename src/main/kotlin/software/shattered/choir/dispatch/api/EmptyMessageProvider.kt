package software.shattered.choir.dispatch.api

import java.util.*

class EmptyMessageProvider : MessageProvider {
    override fun get(id: String, data: Map<String, Any>, locale: Locale): String {
        return ""
    }
}