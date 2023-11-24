package org.ithd.common.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun Context.screenWidthPixel(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun Context.needToRotate(mVideoUri: Uri): Boolean {
    return try {
        // Find Activity
        val activity = this.findActivity()
        //Create a new instance of MediaMetadataRetriever
        val retriever = MediaMetadataRetriever()
        //Declare the Bitmap
        //Set the video Uri as data source for MediaMetadataRetriever
        retriever.setDataSource(this, mVideoUri)
        //Get one "frame"/bitmap - * NOTE - no time was set, so the first available frame will be used
        val bmp: Bitmap = retriever.frameAtTime ?: return false
        //Get the bitmap width and height
        val videoWidth = bmp.width
        val videoHeight = bmp.height

        videoWidth > videoHeight
    } catch (ex: RuntimeException) {
        //error occurred
        Log.e("MediaMetadataRetriever", "- Failed to rotate the video")
        false
    }
}