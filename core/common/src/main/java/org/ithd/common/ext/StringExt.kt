package org.ithd.common.ext

fun String?.toVideoName(): String {
    val value: String = this ?: return ""
    return if (value.contains("."))
        value.substring(0, value.indexOf("."))
    else
        this
}

fun String?.toVideoType(): String {
    val value: String = this ?: return ""
    return if (value.contains("/"))
        value.substring(value.indexOf("/") + 1).uppercase()
    else if (value.contains("\\d".toRegex()))
        value.substring(0, value.indexOfFirst { it.isDigit() }).uppercase()
    else
        this
}