package org.ithd.editor.player

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.dynamite.DynamiteModule
import org.ithd.common.ext.findActivity
import org.ithd.common.ext.getTimeString
import org.ithd.common.ext.needToRotate
import org.ithd.common.ext.normalizePosition
import org.ithd.common.ext.requestToLandscapeMode
import org.ithd.common.ext.requestToPortraitMode
import org.ithd.common.ext.toUpdatedIndex
import org.ithd.model.video.VideoInputItem
import org.ithd.ui.icon.EditorIcons.FullScreenExitIcon
import org.ithd.ui.icon.EditorIcons.FullScreenIcon
import org.ithd.ui.icon.EditorIcons.PauseIcon
import org.ithd.ui.icon.EditorIcons.PlayIcon
import org.ithd.ui.icon.EditorIcons.ReplayIcon
import org.ithd.ui.theme.EditorTypography

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayerComponent(
    modifier: Modifier = Modifier,
    videoInputs: List<VideoInputItem>,
    onBackgroundMode: Boolean,
    onFullScreen: (needToFullScreen: Boolean, needToRotate: Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var playerManager by remember { mutableStateOf<PlayerManager?>(null) }
    var needToFullScreen by rememberSaveable { mutableStateOf(false) }
    var isComplete by rememberSaveable { mutableStateOf(false) }
    var needToRotate by remember { mutableStateOf(false) }
    var progressValue by remember { mutableStateOf(0f) }
    var progressMax by remember { mutableStateOf(1f) }
    val totalDuration = videoInputs.sumOf { it.seconds }
    var storedPosition by remember { mutableStateOf(0L) }
    var storedIndex by remember { mutableStateOf(0) }

    playerManager?.progressChangeListener = { position ->
        val index =
            if (playerManager?.getCurrentItemIndex() == -1) 0 else playerManager?.playerView?.player?.currentMediaItemIndex
        if (storedIndex != index) {
            storedIndex = index ?: 0
            storedPosition = 0
        }
        if (index == 0) {
            progressValue = position.toFloat()
        } else {
            val needUpdate = storedPosition != position
            if (needUpdate) {

                Log.i("wpfowpekf", "position = $position")
                Log.i("wpfowpekf", "storedPosition = $storedPosition")
                Log.i("wpfowpekf", "progressValue = $progressValue")

                progressValue += position - storedPosition

                Log.i("wpfowpekf", "progressValue result = $progressValue")
            }
            if (storedPosition != position) {
                storedPosition = position
            }
        }

        progressMax = (totalDuration).toFloat()
        isComplete = progressValue >= progressMax
    }

    if (onBackgroundMode)  {
        playerManager?.pause()
    }

    when {
        needToFullScreen && needToRotate -> {
            context.findActivity().requestToLandscapeMode()
            playerManager?.playerView?.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        !needToFullScreen -> {
            context.findActivity().requestToPortraitMode()
            playerManager?.playerView?.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                600
            )
        }
        isComplete -> {
            playerManager?.pause()
        }
    }

    BackHandler {
        if (needToFullScreen) {
            needToFullScreen = false
            onFullScreen(needToFullScreen, needToRotate)
        } else {
            playerManager?.release()
            onBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = modifier
                .padding(bottom = if (needToRotate) 90.dp else 40.dp),
            factory = { context ->
                val listener = object : PlayerManager.Listener {
                    override fun onQueuePositionChanged(previousIndex: Int, newIndex: Int) {
                        Log.i("spofkw", "previousIndex = $previousIndex")
                        Log.i("spofkw", "newIndex = $newIndex")
                    }

                    override fun onUnsupportedTrack(trackType: Int) {
                        Log.i("spofkw", "trackType = $trackType")
                        // fixme propagate it
                        // R.string.error_unsupported_track
                    }

                    override fun onPlayerError(errorBundle: Bundle?) {
                        // fixme propagate bundle
                        Log.i("spofkw", "trackType = $errorBundle")
                    }
                }
                CustomPlayerView(context).apply {
                    playerManager = createCastContext(context) { errorCause ->
                        when (errorCause) {
                            is DynamiteModule.LoadingException -> {
                                // fixme propagate it
                                // R.string.cast_context_error
                            }

                            else -> {
                                // fixme propagate it
                                // R.string.error_unsupported_track
                            }
                        }
                    }?.let {
                        PlayerManager(
                            context = context,
                            listener = listener,
                            playerView = this,
                            castContext = it
                        )
                    }

                    videoInputs.forEach {
                        Log.i("pwefkwepfk","fps = ${it.fps}")
                        playerManager?.addItem(MediaItem.fromUri(it.uri))
                    }
                    playerManager?.selectQueueItem(0)
                    playerManager?.play()
                }
            }
        )
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            if (needToFullScreen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(MaterialTheme.colorScheme.inverseSurface)
                ) {
                    Slider(
                        value = progressValue,
                        valueRange = 0f..progressMax,
                        colors = SliderDefaults.colors(),
                        onValueChange = { currentPosition ->
                            Log.i("wpfowpekf", "currentPosition from slider = $currentPosition")
                            playerManager?.pause()
                            val updatedIndex = currentPosition.toUpdatedIndex(videoInputs)
                            val normalizeCurrentPosition =
                                currentPosition.normalizePosition(videoInputs)
                            progressValue = normalizeCurrentPosition
                            storedIndex = updatedIndex
                            storedPosition = normalizeCurrentPosition.toLong()
                            playerManager?.setCurrentPosition(
                                updatedIndex,
                                normalizeCurrentPosition
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .background(MaterialTheme.colorScheme.outline),
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(
                        text = progressValue.toLong().getTimeString(),
                        style = EditorTypography.bodyLarge
                    )

                    Text(
                        text = " | ",
                        style = EditorTypography.bodyLarge
                    )
                    Text(
                        text = totalDuration.getTimeString(),
                        style = EditorTypography.bodyLarge
                    )
                }

                Icon(
                    modifier = Modifier
                        .size(35.dp)
                        .align(Alignment.Center)
                        .clickable {
                            if (isComplete) {
                                isComplete = false
                                playerManager?.play()
                                playerManager?.selectQueueItem(0)
                            } else {
                                if (playerManager?.playerView?.player?.isPlaying == true) {
                                    playerManager?.pause()
                                } else {
                                    playerManager?.play()
                                }
                            }
                        },
                    imageVector = if (isComplete)
                        ReplayIcon
                    else if (playerManager?.playerView?.player?.isPlaying == true)
                        PauseIcon
                    else
                        PlayIcon,
                    contentDescription = "browse_compose_icon"
                )
                Icon(
                    modifier = Modifier
                        .size(35.dp)
                        .align(Alignment.CenterEnd)
                        .clickable {
                            val currentVideoIndex = playerManager?.getCurrentItemIndex()
                            if (currentVideoIndex != null && currentVideoIndex != -1) {
                                needToRotate =
                                    context.needToRotate(videoInputs[currentVideoIndex].uri)
                            }
                            needToFullScreen = !needToFullScreen
                            onFullScreen(needToFullScreen, needToRotate)
                        },
                    imageVector = if (needToFullScreen) FullScreenExitIcon else FullScreenIcon,
                    contentDescription = "browse_compose_icon"
                )
            }
        }
    }
}

fun createCastContext(
    context: Context,
    error: (Throwable) -> Unit,
): CastContext? {
    try {
        // todo migrate
        return CastContext.getSharedInstance(context)
    } catch (e: Throwable) {
        val cause = e.cause
        Log.i("spofkw", "error = $e")
        while (cause != null) {
            if (cause is DynamiteModule.LoadingException) {
                error.invoke(cause)
            } else {
                error.invoke(cause)
            }
        }
    }
    return null
}