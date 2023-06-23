package software.shattered.choir.storage.adapter

import software.shattered.choir.storage.location.LocationCache
import software.shattered.choir.storage.location.LocationKey
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.reflect.TypeToken
import software.shattered.choir.persistence.storage.adapter.NullSafeAdapter
import java.lang.reflect.Type

class LocationCacheAdapter : NullSafeAdapter<LocationCache<*>> {
    private val locationKeyType: TypeToken<LocationKey<String>> = object : TypeToken<LocationKey<String>>() {}

    override fun deserializeSafe(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocationCache<String> {
        val arr = json.asJsonArray
        val cache = LocationCache<String>()
        arr.forEach {
            val key = context.deserialize<LocationKey<String>>(it, locationKeyType.type)
            cache.add(key.x, key.y, key.z, key.data)
        }
        return cache
    }

    override fun serializeSafe(src: LocationCache<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src.getAllKeys())
    }
}