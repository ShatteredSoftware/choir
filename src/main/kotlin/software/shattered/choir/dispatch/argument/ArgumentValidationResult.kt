package software.shattered.choir.dispatch.argument

import software.shattered.choir.datastore.GenericDataStore
import software.shattered.choir.datastore.MutableDataStore

data class ArgumentValidationResult<T>(
    val success: Boolean = false,
    val result: T? = null,
    val faliureMessageId: String = "invalid-input",
    val data: MutableDataStore = GenericDataStore()
)