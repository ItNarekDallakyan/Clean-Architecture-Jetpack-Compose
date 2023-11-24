package org.ithd.model.timeline

data class PointItem(
    var x: Float = 0f,
    val time: Float = 0f
) {

    fun nextPoint(nextPointX: Float): PointItem {
        val currentPointItem = this
        val nextPointItem = this.copy()
        this.x = nextPointItem.x.plus(nextPointX)
        return nextPointItem
    }
}

data class TimelinePoints(
    val points: List<PointItem>,
) {

}