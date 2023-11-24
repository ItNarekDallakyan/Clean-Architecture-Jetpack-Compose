package org.ithd.editor

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ithd.common.ext.toListVideoInputItem
import org.ithd.model.video.VideoInputItem
import javax.inject.Inject


@HiltViewModel
class EditorViewModel @Inject constructor(
    private val application: Application
) : AndroidViewModel(application) {

    private val _selectedVideos = MutableStateFlow<MutableList<VideoInputItem>>(mutableListOf())
    val selectedVideos = _selectedVideos.asStateFlow()

    fun setSelectedVideos(videos: MutableList<Uri>) {
        viewModelScope.launch {
            _selectedVideos.value = videos.toListVideoInputItem(application)
        }
    }
}