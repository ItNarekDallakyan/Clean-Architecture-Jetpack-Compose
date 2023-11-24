package org.ithd.common.ext

import android.util.Log
import org.ithd.model.video.VideoInputItem

fun Float.toUpdatedIndex(videoInputs: List<VideoInputItem>): Int {
    var sumDurations = 0f
    for (i in videoInputs.indices) {
        sumDurations += videoInputs[i].seconds
        if (this < sumDurations) {
            return i
        }
    }
    return 0
}

fun Float.normalizePosition(videoInputs: List<VideoInputItem>): Float {
    var sumOfDurations = 0f
    val index = toUpdatedIndex(videoInputs)
    for (i in 0 until index) {
        sumOfDurations += videoInputs[i].seconds
    }

    val result = if (index == 0) this else this - sumOfDurations

    Log.i("wpfowpekf", "normalizePosition = $result")

    return result
}