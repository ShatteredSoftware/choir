package software.shattered.choir.persistence

import software.shattered.choir.persistence.api.ChoirReadWriteAPI
import software.shattered.choir.attribute.Identified

interface DataContainer<T : Identified> :
    ChoirReadWriteAPI<T> {
    fun getIds(): Set<String>
    fun invalidate() {}
    fun flush() {}
    fun isDirty(): Boolean { return true }
}