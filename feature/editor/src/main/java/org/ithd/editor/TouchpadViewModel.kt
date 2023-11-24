package org.ithd.editor

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.ithd.common.constant.pinchScaleDefault
import org.ithd.common.helper.TimelinePointsHelper
import org.ithd.model.timeline.PointItem
import org.ithd.model.video.VideoInputItem
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class TouchpadViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    // Current Scale
    private val _currentScaleState = MutableStateFlow(pinchScaleDefault)
    val currentScaleState = _currentScaleState.asStateFlow()

    // Maximum Scale
    private val _maximumScaleState = MutableStateFlow(0f)

    // Minimum Scale
    private val _minimumScaleState = MutableStateFlow(0f)

    // Is Pinching
    private val _isPinchingState = MutableStateFlow(false)

    // Translation X
    private val _translationXState = MutableStateFlow(0f)
    val translationXState = _translationXState.asStateFlow()

    // Timeline position
    private val _timelinePositionOfPercent = MutableStateFlow(0.0f)

    // Selected video list
    private var _selectedVideoList: MutableList<VideoInputItem> = mutableListOf()

    private val _timelinePoints = MutableStateFlow<List<PointItem>>(value = mutableListOf())
    val timelinePoints = _timelinePoints.asStateFlow()

    init {
        // Handling  pinch zoom state
        viewModelScope.launch {
            _currentScaleState.onEach {
                _timelinePoints.value = TimelinePointsHelper.createPoints(
                    selectedVideos = _selectedVideoList,
                    currentTimelinePoints = _timelinePoints.value,
                    currentScale = _currentScaleState.value
                )
            }.collect()
        }
    }

    private fun computeTranslationXInPinchingState(): Float? {
        val positionOfPercent = _timelinePositionOfPercent.value
        val currentPositionOfPercent = computeCurrentPositionOfPercent()
        if (currentPositionOfPercent != positionOfPercent) {
            val currentScale = _currentScaleState.value
            return if (positionOfPercent == 100f) {
                -currentScale
            } else {
                -((positionOfPercent * currentScale) / 100f)
            }
        }
        return null
    }

    private fun computePinchZoomScale(zoomChange: Float): Float? {
        val minimumScale = _minimumScaleState.value
        val maximumScale = _maximumScaleState.value
        val newScale = _currentScaleState.value.times(zoomChange)
        if (newScale in minimumScale..maximumScale && _currentScaleState.value != newScale) {
            return newScale
        } else if (newScale > maximumScale) {
            return maximumScale
        } else if (newScale < minimumScale) {
            return minimumScale
        }
        return null
    }

    fun onTransformableCanceled() {
        viewModelScope.launch {
            val currentScale = _currentScaleState.value
            val translationX = _translationXState.value
            if (translationX > 0) {
                _translationXState.value = 0f
            } else if (translationX < -currentScale) {
                _translationXState.value = -currentScale
            }
            _isPinchingState.value = false
            _timelinePositionOfPercent.value = computeCurrentPositionOfPercent()
        }
    }

    private fun handleTranslationChanges(panChange: Offset) {
        val currentScale = _currentScaleState.value
        val translationX = _translationXState.value + panChange.x
        if (translationX <= 0 && translationX >= -currentScale) {
            _translationXState.value = _translationXState.value + panChange.x
        } else if (translationX > 0) {
            _translationXState.value = 0f
        } else if (translationX < -currentScale) {
            _translationXState.value = -currentScale
        }
    }

    private fun computeCurrentPositionOfPercent(): Float {
        val translationX = abs(_translationXState.value)
        return ((translationX / _currentScaleState.value) * 100f)
    }

    fun onTransformableChanged(zoomChange: Float, panChange: Offset) {
        viewModelScope.launch {
            computePinchZoomScale(zoomChange)?.let { newPinchZoomScale ->
                _isPinchingState.value = true
                _currentScaleState.value = newPinchZoomScale
                computeTranslationXInPinchingState()?.let { newTranslationX ->
                    _translationXState.value = newTranslationX
                }
            } ?: run {
                if (_isPinchingState.value.not()) {
                    handleTranslationChanges(panChange)
                }
            }
        }
    }

    fun computeTouchpad(selectedVideoList: MutableList<VideoInputItem>) {
        viewModelScope.launch {
            _selectedVideoList = selectedVideoList
            // Compute initial scale
            _currentScaleState.value = TimelinePointsHelper.computeInitialScale(_selectedVideoList)
            _maximumScaleState.value = TimelinePointsHelper.computeMaximumScale(_selectedVideoList)
            _minimumScaleState.value = TimelinePointsHelper.computeMinimumScale(_selectedVideoList)
        }
    }
}