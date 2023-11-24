package org.ithd.common.ext

import android.app.Activity
import android.content.pm.ActivityInfo

fun Activity?.requestToLandscapeMode() {
    this?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Activity?.requestToPortraitMode() {
    this?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}