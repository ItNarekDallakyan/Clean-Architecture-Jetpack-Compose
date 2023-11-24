package org.ithd.model.video

import android.net.Uri

data class VideoInputItem(
    val videoName: String? = null,
    val videoType: String? = null,
    val videoSize: String? = null,
    val seconds: Long,
    val fps: Int,
    val uri: Uri
)