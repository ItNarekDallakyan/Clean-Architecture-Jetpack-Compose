package org.ithd.editor.component

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.ithd.editor.TouchpadViewModel
import org.ithd.model.video.VideoInputItem

@Composable
fun Touchpad(
    modifier: Modifier = Modifier,
    touchpadViewModel: TouchpadViewModel,
    selectedVideoList: MutableList<VideoInputItem>,
    onTransformCanceled: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val state = rememberTransformableState { zoomChange, panChange, _ ->
        touchpadViewModel.onTransformableChanged(
            zoomChange = zoomChange, panChange = panChange
        )
    }

    remember(key1 = state.isTransformInProgress) {
        if (state.isTransformInProgress.not()) onTransformCanceled.invoke()
        mutableStateOf(Unit)
    }

    LaunchedEffect(key1 = selectedVideoList) {
        touchpadViewModel.computeTouchpad(selectedVideoList)
    }

    Box(
        modifier = modifier.transformable(state = state)
    ) {
        content()
    }
}