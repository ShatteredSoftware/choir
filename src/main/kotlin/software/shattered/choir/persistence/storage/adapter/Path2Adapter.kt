package software.shattered.choir.persistence.storage.adapter

import software.shattered.choir.math.context.DoubleContext
import software.shattered.choir.math.path.IdentifiedPath2
import software.shattered.choir.math.vector.Vector2
import software.shattered.choir.math.vector.Vector2Double
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class Path2Adapter : JsonDeserializer<IdentifiedPath2<*>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): IdentifiedPath2<*> {
        require(!(json == null || typeOfT == null || context == null)) { "Trying to deserialize null" }
        val obj = json.asJsonObject
        val id = obj.get("id").asString
        val arr = obj.get("points").asJsonArray
        val res = mutableListOf<Vector2Double>()
        for (item in arr) {
            res.add(context.deserialize(item, Vector2::class.java))
        }
        return IdentifiedPath2(id, res, DoubleContext)
    }
}
