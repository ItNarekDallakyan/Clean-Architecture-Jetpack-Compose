package org.ithd.browse

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor() : ViewModel() {

    private val _selectedVideos = MutableStateFlow<List<Uri>>(emptyList())
    val selectedVideos = _selectedVideos.asStateFlow()

    fun setSelectedVideos(videos: List<Uri>) {
        viewModelScope.launch {
            _selectedVideos.value = videos
        }
    }
}