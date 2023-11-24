package org.ithd.common.ext

fun Long?.toUploadedVideoSizeInKB(): String {
    val value: Long = this ?: return "0 KB"
    return "${value / 1024} KB"
}

fun Long.getTimeString(): String {
    val buf = StringBuffer()
    val hours = (this / (1000 * 60 * 60)).toInt()
    val minutes = (this % (1000 * 60 * 60) / (1000 * 60)).toInt()
    val seconds = (this % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
    if (hours == 0) {
        buf
            .append(String.format("%02d", minutes))
            .append(":")
            .append(String.format("%02d", seconds))
    } else {
        buf
            .append(String.format("%02d", hours))
            .append(":")
            .append(String.format("%02d", minutes))
            .append(":")
            .append(String.format("%02d", seconds))
    }
    return buf.toString()
}