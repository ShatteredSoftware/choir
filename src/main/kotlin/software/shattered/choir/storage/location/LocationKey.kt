package software.shattered.choir.storage.location

import software.shattered.choir.math.vector.Vector3Like

data class LocationKey<T>(override val x: Int, override val y: Int, override val z: Int, val data: T) : Vector3Like