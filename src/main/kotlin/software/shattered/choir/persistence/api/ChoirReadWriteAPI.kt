package software.shattered.choir.persistence.api

import software.shattered.choir.attribute.Identified

interface ChoirReadWriteAPI<T : Identified> {
    fun load(id: String): T?
    fun save(value: T)
    fun delete(id: String)
}