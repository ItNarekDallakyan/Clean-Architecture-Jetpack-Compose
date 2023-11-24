package org.ithd.common.ext

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.provider.OpenableColumns
import org.ithd.model.video.VideoInputItem
import java.io.IOException


fun MutableList<Uri>.toListVideoInputItem(context: Context): MutableList<VideoInputItem> {
    return this.map { uri ->
        VideoInputItem(
            videoName = uri.getFileName(context),
            videoType = uri.getFileMimeType(context).toVideoType(),
            videoSize = uri.getFileSize(context).toUploadedVideoSizeInKB(),
            seconds = uri.durationSeconds(context),
            fps = uri.fps(context),
            uri = uri
        )
    }.toMutableList()
}

fun Uri.fps(context: Context): Int {
    val extractor = MediaExtractor()
    var frameRate = 24
    try {
        extractor.setDataSource(context, this, null)
        val numTracks = extractor.trackCount
        repeat(numTracks) { index ->
            val format = extractor.getTrackFormat(index)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                    frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        extractor.release()
    }
    return frameRate
}

fun Uri.durationSeconds(context: Context): Long {
    val extractor = MediaExtractor()
    var duration = 0L
    try {
        extractor.setDataSource(context, this, null)
        val numTracks = extractor.trackCount
        repeat(numTracks) { index ->
            val format = extractor.getTrackFormat(index)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                if (format.containsKey(MediaFormat.KEY_DURATION)) {
                    duration = format.getLong(MediaFormat.KEY_DURATION)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        extractor.release()
    }
    return duration.div(1000000)
}

fun Uri?.getFileName(context: Context): String? {/*
    * Get the file's content URI from the incoming Intent,
    * then query the server app to get the file's display name
    * and size.
    */
    this?.let { returnUri ->
        context.contentResolver.query(
            returnUri, null, null, null, null
        )
    }?.use { cursor ->
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        return cursor.getString(nameIndex)
    }
    return null
}

fun Uri?.getFileMimeType(context: Context): String? {/*
    * Get the file's content URI from the incoming Intent, then
    * get the file's MIME type
    */
    return this?.let { returnUri ->
        context.contentResolver.getType(returnUri)
    }
}

fun Uri?.getFileSize(context: Context): Long? {/*
    * Get the file's content URI from the incoming Intent,
    * then query the server app to get the file's display name
    * and size.
    */
    this?.let { returnUri ->
        context.contentResolver.query(
            returnUri, null, null, null, null
        )
    }?.use { cursor ->
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        return cursor.getLong(sizeIndex)
    }
    return null
}