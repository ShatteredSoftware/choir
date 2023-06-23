package software.shattered.choir.storage.location

import software.shattered.choir.math.vector.Vector3Like
import org.bukkit.Location

@JvmInline
value class WrappedLocation(val location: Location) : Vector3Like {
    override val x: Number get() = location.x
    override val y: Number get() = location.y
    override val z: Number get() = location.z
}