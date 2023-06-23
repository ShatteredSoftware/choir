package software.shattered.choir.math.geometry

interface Spatial<AreaType> {
    /**
     * @return true if check fully belongs to an area determined by this shape
     */
    fun contains(check: AreaType): Boolean

    /**
     * @return true if at least one point of check can be found within this shape
     */
    fun intersects(check: AreaType): Boolean
}