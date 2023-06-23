package software.shattered.choir.math

object Math {
    fun rescale(value: Int, originalMin: Int, originalMax: Int, newMin: Int, newMax: Int) = (((value - originalMin).toDouble() / (originalMax - originalMin).toDouble()) * (newMax - newMin).toDouble() + newMin).toInt()
    fun rescale(value: Long, originalMin: Long, originalMax: Long, newMin: Long, newMax: Long) = (((value - originalMin).toDouble() / (originalMax - originalMin).toDouble()) * (newMax - newMin).toDouble() + newMin).toLong()
    fun rescale(value: Float, originalMin: Float, originalMax: Float, newMin: Float, newMax: Float) = (((value - originalMin).toDouble() / (originalMax - originalMin).toDouble()) * (newMax - newMin).toDouble() + newMin).toFloat()
    fun rescale(value: Double, originalMin: Double, originalMax: Double, newMin: Double, newMax: Double) = ((value - originalMin) / (originalMax - originalMin)) * (newMax - newMin) + newMin
}