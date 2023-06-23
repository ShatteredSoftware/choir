package software.shattered.choir.dispatch.context

import software.shattered.choir.datastore.EmptyDataStore
import software.shattered.choir.datastore.DataStore
import software.shattered.choir.dispatch.api.MessageProvider
import java.util.*

abstract class CommandContext(val messageProvider: MessageProvider, var data: DataStore = EmptyDataStore, private val debug: Boolean = false) {
    protected abstract fun sendMessage(message: String)
    protected open fun sendDebugMessage(message: String) { sendMessage(message) }

    abstract fun getLocale(): Locale

    fun log(message: String, data: DataStore? = null, locale: Locale? = null) {
        sendMessage(messageProvider.get(message, data?.asMapOf(Any::class.java) ?: emptyMap(), locale ?: getLocale()))
    }

    fun debugLog(message: String, data: () -> DataStore? = { null }, locale: Locale? = null) {
        if (debug) {
            sendDebugMessage(messageProvider.get(message, data()?.let { DataStore.stringify(it) } ?: emptyMap(), locale ?: getLocale()))
        }
    }
}