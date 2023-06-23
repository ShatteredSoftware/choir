package software.shattered.choir.math

import software.shattered.choir.math.geometry.Spatial
import software.shattered.choir.math.vector.MutableVector3
import software.shattered.choir.math.vector.Vector3

data class Cuboid<T : Number>(
    val origin: MutableVector3<T>,
    val delta: MutableVector3<T>,
    val context: software.shattered.choir.math.context.NumericContext<T>
) : Spatial<software.shattered.choir.math.Cuboid<*>> {
    val x get() = origin.x
    val y get() = origin.y
    val z get() = origin.z
    val l get() = delta.x
    val h get() = delta.y
    val w get() = delta.z

    val points: List<Vector3<T>> by lazy {
        with (context) {
            listOf(
                Vector3(context, x, y, z),
                Vector3(context, x + l, y, z),
                Vector3(context, x, y + h, z),
                Vector3(context, x + l, y + h, z),
                Vector3(context, x, y, z + w),
                Vector3(context, x + l, y, z + w),
                Vector3(context, x, y + h, z + w),
                Vector3(context, x + l, y + h, z + w),
            )
        }
    }

    fun expand(dx: T, dy: T, dz: T) {
        delta.translate(dx, dy, dz)
    }

    fun translate(dx: T, dy: T, dz: T) {
        origin.translate(dx, dy, dz)
    }

    fun contains(point: Vector3<*>): Boolean {
        val (px, py, pz) = point
        val (ox, oy, oz) = origin
        with (context) {
            return px > ox && px < getDiagonalX()
                    && py > oy && py < getDiagonalY()
                    && pz > oz && pz < getDiagonalZ()
        }
    }

    override fun contains(cuboid: software.shattered.choir.math.Cuboid<*>): Boolean {
        return cuboid.points.all(this::contains);
    }

    override fun intersects(cuboid: software.shattered.choir.math.Cuboid<*>): Boolean {
        return cuboid.points.any(this::contains)
    }

    fun getDiagonalX(): T = context.add(origin.x, delta.x)

    fun getDiagonalY(): T = context.add(origin.y, delta.y)

    fun getDiagonalZ(): T = context.add(origin.z, delta.z)

    /**
     * Returns a new cuboid where the delta is guaranteed to be a positive vector by shifting the origin if needed.
     */
    fun normalized(): software.shattered.choir.math.Cuboid<T> {
        with (context) {
            val ox = if (delta.x < 0) {
                origin.x - delta.x
            } else origin.x
            val oy = if (delta.y < 0) {
                origin.y - delta.y
            } else origin.y
            val oz = if (delta.z < 0) {
                origin.z - delta.z
            } else origin.z
            val dx = abs(delta.x)
            val dy = abs(delta.y)
            val dz = abs(delta.z)
            return software.shattered.choir.math.Cuboid(
                MutableVector3(this@Cuboid.context, ox, oy, oz),
                MutableVector3(this@Cuboid.context, dx, dy, dz),
                this@Cuboid.context
            )
        }
    }
}