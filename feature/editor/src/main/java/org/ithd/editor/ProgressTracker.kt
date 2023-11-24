package org.ithd.editor

import android.os.Handler
import android.os.Looper
import androidx.media3.common.Player

class ProgressTracker(
    private val player: Player?,
    private val positionListener: (position: Long) -> Unit
) : Runnable {
    private var handler: Handler? = null

    init {
        handler = Handler(Looper.getMainLooper())
        handler?.post(this)
    }

    override fun run() {
        if (player == null) return
        val position = player.currentPosition
        positionListener.invoke(position)
        handler?.postDelayed(this, 0L)
    }

    fun purgeHandler() {
        handler?.removeCallbacks(this)
    }
    fun cancelHandler() {
        handler?.removeCallbacks(this)
    }
}