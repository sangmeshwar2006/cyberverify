package com.cyberverify.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberverify.data.repository.ScanRepository
import com.cyberverify.domain.model.MediaMetadata
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.ScanReport
import com.cyberverify.util.AudioPlaybackManager
import com.cyberverify.util.AudioPlayerState
import com.cyberverify.util.FileMetadataExtractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScanAnalysisState {
    object Idle : ScanAnalysisState
    object Analyzing : ScanAnalysisState
    data class Success(val report: ScanReport) : ScanAnalysisState
    data class Error(val message: String) : ScanAnalysisState
}

class ScanViewModel(
    private val repository: ScanRepository,
    audioPlaybackManagerParam: AudioPlaybackManager? = null
) : ViewModel() {

    val audioPlaybackManager: AudioPlaybackManager =
        audioPlaybackManagerParam ?: AudioPlaybackManager(viewModelScope)

    val audioPlayerState: StateFlow<AudioPlayerState> = audioPlaybackManager.playerState

    private val _activeTab = MutableStateFlow(MediaType.IMAGE)
    val activeTab: StateFlow<MediaType> = _activeTab.asStateFlow()

    private val _selectedMediaUri = MutableStateFlow<String?>(null)
    val selectedMediaUri: StateFlow<String?> = _selectedMediaUri.asStateFlow()

    private val _selectedFileName = MutableStateFlow<String?>(null)
    val selectedFileName: StateFlow<String?> = _selectedFileName.asStateFlow()

    private val _selectedMetadata = MutableStateFlow<MediaMetadata?>(null)
    val selectedMetadata: StateFlow<MediaMetadata?> = _selectedMetadata.asStateFlow()

    private val _analysisState = MutableStateFlow<ScanAnalysisState>(ScanAnalysisState.Idle)
    val analysisState: StateFlow<ScanAnalysisState> = _analysisState.asStateFlow()

    private val _isDemoMode = MutableStateFlow(true)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    fun setActiveTab(mediaType: MediaType) {
        _activeTab.value = mediaType
        clearSelectedMedia()
    }

    fun selectMedia(context: Context, uri: Uri) {
        val metadata = FileMetadataExtractor.extractMetadata(context, uri)
        if (metadata == null) {
            _analysisState.value = ScanAnalysisState.Error(
                "Unsupported file format. Please select JPEG, PNG, WEBP image or WAV, MP3, M4A audio."
            )
            return
        }

        selectMediaWithMetadata(context, metadata)
    }

    fun selectMediaWithMetadata(context: Context?, metadata: MediaMetadata) {
        audioPlaybackManager.release()
        _selectedMediaUri.value = metadata.uri
        _selectedFileName.value = metadata.fileName
        _selectedMetadata.value = metadata
        _activeTab.value = metadata.mediaType
        _analysisState.value = ScanAnalysisState.Idle

        if (metadata.mediaType == MediaType.AUDIO && context != null) {
            runCatching {
                audioPlaybackManager.loadMedia(context, Uri.parse(metadata.uri))
            }
        }
    }

    fun selectMedia(uri: String, fileName: String, mediaType: MediaType = _activeTab.value) {
        audioPlaybackManager.release()
        _selectedMediaUri.value = uri
        _selectedFileName.value = fileName
        _activeTab.value = mediaType
        _analysisState.value = ScanAnalysisState.Idle

        val estimatedMime = when (mediaType) {
            MediaType.IMAGE -> "image/jpeg"
            MediaType.AUDIO -> "audio/mpeg"
        }
        _selectedMetadata.value = MediaMetadata(
            uri = uri,
            fileName = fileName,
            fileSize = 0L,
            mimeType = estimatedMime,
            mediaType = mediaType
        )
    }

    fun clearSelectedMedia() {
        audioPlaybackManager.release()
        _selectedMediaUri.value = null
        _selectedFileName.value = null
        _selectedMetadata.value = null
        _analysisState.value = ScanAnalysisState.Idle
    }

    fun toggleDemoMode(enabled: Boolean) {
        _isDemoMode.value = enabled
    }

    fun startAnalysis() {
        val uri = _selectedMediaUri.value ?: return
        val name = _selectedFileName.value ?: "media_file_${System.currentTimeMillis()}"
        val type = _activeTab.value

        audioPlaybackManager.pause()
        _analysisState.value = ScanAnalysisState.Analyzing

        viewModelScope.launch {
            val result = when (type) {
                MediaType.IMAGE -> repository.analyzeImage(
                    fileUri = uri,
                    fileName = name,
                    isDemoMode = _isDemoMode.value
                )
                MediaType.AUDIO -> repository.analyzeAudio(
                    fileUri = uri,
                    fileName = name,
                    isDemoMode = _isDemoMode.value
                )
            }

            result.fold(
                onSuccess = { report ->
                    _analysisState.value = ScanAnalysisState.Success(report)
                },
                onFailure = { throwable ->
                    _analysisState.value = ScanAnalysisState.Error(
                        throwable.localizedMessage ?: "Analysis failed. Please try again."
                    )
                }
            )
        }
    }

    fun resetAnalysisState() {
        _analysisState.value = ScanAnalysisState.Idle
    }

    suspend fun getScanReportById(id: String): ScanReport? {
        return repository.getScanReportById(id)
    }

    override fun onCleared() {
        audioPlaybackManager.release()
        super.onCleared()
    }
}
