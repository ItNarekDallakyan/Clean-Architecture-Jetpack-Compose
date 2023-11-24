package org.ithd.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.ithd.common.helper.TimelinePointsHelper
import org.ithd.editor.component.Touchpad
import org.ithd.editor.player.VideoPlayerComponent
import org.ithd.ui.component.RuleComponent

@Composable
fun EditorRoute(
    editorViewModel: EditorViewModel,
    touchpadViewModel: TouchpadViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    val selectedVideos by editorViewModel.selectedVideos.collectAsState()
    val translationXState by touchpadViewModel.translationXState.collectAsState()
    var onBackgroundMode by remember { mutableStateOf(false) }
    var needToFullSize by rememberSaveable { mutableStateOf(false) }
    val rootContainerFraction by remember(needToFullSize) {
        mutableStateOf(if (needToFullSize) 1f else 0.5f)
    }
    // Pinch state properties
    val currentScale by touchpadViewModel.currentScaleState.collectAsState()
    // Time line
    val timelinePoints by touchpadViewModel.timelinePoints.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            onBackgroundMode =
                event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(fraction = rootContainerFraction)
        ) {
            VideoPlayerComponent(
                modifier = Modifier.fillMaxSize(),
                videoInputs = selectedVideos,
                onBackgroundMode = onBackgroundMode,
                onFullScreen = { needToFullScreen, needToRotate ->
                    needToFullSize = needToFullScreen
                },
                onBack = {
                    onBack()
                }
            )
        }


        Touchpad(
            modifier = Modifier.fillMaxSize(),
            touchpadViewModel = touchpadViewModel,
            selectedVideoList = selectedVideos,
            onTransformCanceled = {
                touchpadViewModel.onTransformableCanceled()
            }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer(
                        scaleX = currentScale,
                        translationX = translationXState
                    )
                    .drawWithContent {
                        drawRect(
                            topLeft = Offset.Zero,
                            color = Color.Cyan,
                            size = Size(1f, 300f)
                        )
                        timelinePoints.forEach { pointItem ->
                            drawOval(
                                topLeft = Offset(pointItem.x, 10f),
                                color = Color.Black,
                                size = Size(
                                    width = TimelinePointsHelper.calculateRulePointSize(currentScale),
                                    height = 10f
                                )
                            )
                        }
                    }
            ) {

            }
            RuleComponent(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.Center)
            )
        }
    }
}