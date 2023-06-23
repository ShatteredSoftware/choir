package software.shattered.choir.math

import software.shattered.choir.math.geometry.Spatial
import software.shattered.choir.math.vector.MutableVector2
import software.shattered.choir.math.vector.Vector2
import software.shattered.choir.math.vector.Vector3

class Rectangle<T : Number>(
    val origin: MutableVector2<T>,
    val delta: MutableVector2<T>,
    val context: software.shattered.choir.math.context.NumericContext<T>
): Spatial<Rectangle<*>> {
    val x get() = origin.x
    val y get() = origin.y
    val w get() = delta.x
    val h get() = delta.y

    val points: List<Vector2<T>> by lazy {
        with (context) {
            listOf(
                Vector2(context, x, y),
                Vector2(context, x + w, y),
                Vector2(context, x, y + h),
                Vector2(context, x + w, y + h),
            )
        }
    }

    fun getDiagonalX(): T = context.add(origin.x, delta.x)

    fun getDiagonalY(): T = context.add(origin.y, delta.y)

    /**
     * Assumes a normalized vector. Returns a vector where the x and y coordinates are normalized to the rectangle. An x
     * equal to the rectangle's x (in other words, on the left side of the rectangle) will result in an x component of 0,
     * and an x on the right side of the rectangle will result in an x component of 1. Similar rules apply to y
     * coordinates. Points outside the rectangle will result in components scaled to the size of this rectangle.
     */
    fun toLocal(point: Vector2<out Number>): Vector2<Double> {
        return ((point - origin) * delta.inverse()).toDouble()
    }

    fun contains(point: Vector2<*>): Boolean {
        val (px, py) = point
        val (ox, oy) = origin
        with (context) {
            return px > ox && px < getDiagonalX()
                    && py > oy && py < getDiagonalY()
        }
    }

    override fun contains(cuboid: Rectangle<*>): Boolean {
        return cuboid.points.all(this::contains);
    }

    override fun intersects(cuboid: Rectangle<*>): Boolean {
        return cuboid.points.any(this::contains)
    }

    /**
     * Returns a new rectangle that ensures positive width/height; negative width and height will result in a transformed
     * rectangle that matches the original by offsetting the origin.
     */
    fun normalized(): Rectangle<T> {
        with(context) {
            val ox = if (delta.x < 0) {
                origin.x - delta.x
            } else origin.x
            val oy = if (delta.y < 0) {
                origin.y - delta.y
            } else origin.y
            val dx = abs(delta.x)
            val dy = abs(delta.y)
            return Rectangle(
                MutableVector2(context, ox, oy),
                MutableVector2(context, dx, dy),
                context
            )
        }
    }
}