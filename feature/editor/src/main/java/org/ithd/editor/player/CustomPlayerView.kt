package org.ithd.editor.player

import android.content.Context
import androidx.media3.ui.PlayerView

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class CustomPlayerView(context: Context) : PlayerView(context) {

    init {

        hideController()
        useController = false


        // this.useController = false

//        this.setShowNextButton(false)
//        this.setShowPreviousButton(false)
//        this.setShowFastForwardButton(false)
//        this.setShowRewindButton(false)

    }

}