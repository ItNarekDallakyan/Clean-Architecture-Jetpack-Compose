package org.ithd.common.ext

import android.graphics.Color
import android.util.Log
import org.ithd.common.constant.timeSectionWidth
import org.ithd.model.video.VideoInputItem
import java.util.Random

fun Float.createTimeLinePointList(
    currentValue: Pair<Int, MutableList<Float>>, selectedVideoCount: Int
): Pair<Int, MutableList<Float>> {
    val result = mutableListOf<Float>()
    val countOfSections = this.calculateCountOfRulePoint(currentValue, selectedVideoCount)
    val perX = 1f / countOfSections
    repeat(countOfSections) {
        val lastElement = result.lastOrNull() ?: 0f
        val element = perX + lastElement
        result.add(element)
    }
    return Pair(countOfSections, result)
}

fun Float.calculateCountOfRulePoint(
    currentValue: Pair<Int, MutableList<Float>>, selectedVideoCount: Int
): Int {
    val countOfRulePoint = (this / timeSectionWidth).toInt()
    val perX = 1f / currentValue.first
    val distancePoints = this * perX
    Log.i("refrfrefr--->>>", "distancePoints: $distancePoints")
    if (currentValue.first == 0) {
        return countOfRulePoint
    }
    return currentValue.first



    if (distancePoints > timeSectionWidth.plus(timeSectionWidth.div(2)) || distancePoints < timeSectionWidth
    ) {
        Log.i("dcdcde--->>>", "countOfRulePoint")
        return countOfRulePoint
    }
    Log.i("dcdcde--->>>", "currentValue.first")
    return currentValue.first
}

fun randomColor(): Int {
    val random = Random()
    val red: Int = 30 + random.nextInt(200)
    val green: Int = 30 + random.nextInt(200)
    val blue: Int = 30 + random.nextInt(200)
    return Color.rgb(red, green, blue)
}