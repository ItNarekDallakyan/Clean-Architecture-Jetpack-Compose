package org.ithd.common.helper

import org.ithd.common.constant.timeSectionWidth
import org.ithd.model.timeline.PointItem
import org.ithd.model.video.VideoInputItem

object TimelinePointsHelper {

    fun createPoints(
        selectedVideos: List<VideoInputItem>,
        currentTimelinePoints: List<PointItem>,
        currentScale: Float
    ): List<PointItem> {
        return if (currentTimelinePoints.isEmpty()) {
            createInitialPoints(currentScale)
        } else {
            handleTimelinePointsChange(
                selectedVideos = selectedVideos,
                currentTimelinePoints = currentTimelinePoints,
                currentScale = currentScale
            )
        }
    }

    fun nextPoint(nextPointX: Float, currentPointItem: PointItem): PointItem {
        return PointItem(
            x = nextPointX.plus(currentPointItem.x)
        )
    }

    private fun createInitialPoints(currentScale: Float): List<PointItem> {
        val result = ArrayList<PointItem>()
        val countOfRulePoint = (currentScale / timeSectionWidth).toInt()
        val perX = 1f / countOfRulePoint
        val countOfPoints = (currentScale / timeSectionWidth).toInt()
        repeat(countOfPoints) {
            val lastElement = result.firstOrNull() ?: PointItem()
            val element = nextPoint(nextPointX = perX, currentPointItem = lastElement)
            result.add(element)
        }
        return result
    }

    private fun handleTimelinePointsChange(
        selectedVideos: List<VideoInputItem>,
        currentTimelinePoints: List<PointItem>,
        currentScale: Float
    ): List<PointItem> {
        val result = ArrayList<PointItem>()
        val countOfRulePoint = (currentScale / timeSectionWidth).toInt()
        val perX = 1f / countOfRulePoint
        val distancePoints = currentScale * perX
        val sumOfSeconds = calculateSumOfSeconds(selectedVideos)
        val countOfPoints = (currentScale / timeSectionWidth).toInt()
        repeat(countOfPoints) {
            val lastElement = result.lastOrNull() ?: PointItem()
            val element = lastElement.nextPoint(nextPointX = perX)
            result.add(element)
        }
        return currentTimelinePoints
    }

    private fun calculateSumOfSeconds(selectedVideos: List<VideoInputItem>) =
        selectedVideos.sumOf { it.seconds }

    fun calculateRulePointSize(currentScale: Float) = (0.1f / currentScale) * 100

    fun computeInitialScale(
        selectedVideos: List<VideoInputItem>
    ): Float {
        val sumOfSeconds = selectedVideos.sumOf { it.seconds }
        return sumOfSeconds.times(timeSectionWidth)
    }

    fun computeMaximumScale(
        selectedVideos: List<VideoInputItem>
    ): Float {
        var maximumScale = 0.0f
        selectedVideos.forEach {
            val videoMaximumScale = it.seconds * it.fps * timeSectionWidth
            maximumScale = maximumScale.plus(videoMaximumScale)
        }
        return maximumScale
    }

    fun computeMinimumScale(
        selectedVideos: List<VideoInputItem>
    ): Float {
        return selectedVideos.size * timeSectionWidth
    }
}