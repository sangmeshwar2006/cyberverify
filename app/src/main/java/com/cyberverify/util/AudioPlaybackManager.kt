package com.cyberverify.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlaybackManager(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private var mediaPlayer: MediaPlayer? = null
    private var positionUpdateJob: Job? = null

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    fun loadMedia(context: Context, uri: Uri) {
        releasePlayer()

        try {
            val player = MediaPlayer().apply {
                setDataSource(context, uri)
                prepare()
            }

            val duration = player.duration.coerceAtLeast(0).toLong()
            mediaPlayer = player

            _playerState.value = AudioPlayerState(
                isPlaying = false,
                currentPositionMs = 0L,
                durationMs = duration,
                isLoaded = true,
                error = null
            )

            player.setOnCompletionListener {
                stopPositionPolling()
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    currentPositionMs = 0L
                )
            }

            player.setOnErrorListener { _, what, extra ->
                stopPositionPolling()
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    error = "Playback error occurred ($what, $extra)"
                )
                true
            }

        } catch (e: Exception) {
            _playerState.value = AudioPlayerState(
                isLoaded = false,
                error = e.localizedMessage ?: "Failed to load audio file"
            )
        }
    }

    fun play() {
        val player = mediaPlayer ?: return
        if (_playerState.value.isLoaded && !_playerState.value.isPlaying) {
            try {
                player.start()
                _playerState.value = _playerState.value.copy(isPlaying = true)
                startPositionPolling()
            } catch (e: Exception) {
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    error = e.localizedMessage ?: "Failed to start playback"
                )
            }
        }
    }

    fun pause() {
        val player = mediaPlayer ?: return
        if (_playerState.value.isPlaying) {
            try {
                player.pause()
                stopPositionPolling()
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    currentPositionMs = player.currentPosition.toLong()
                )
            } catch (e: Exception) {
                _playerState.value = _playerState.value.copy(
                    error = e.localizedMessage ?: "Failed to pause playback"
                )
            }
        }
    }

    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun seekTo(positionMs: Long) {
        val player = mediaPlayer ?: return
        if (_playerState.value.isLoaded) {
            try {
                val clampedPos = positionMs.coerceIn(0L, _playerState.value.durationMs).toInt()
                player.seekTo(clampedPos)
                _playerState.value = _playerState.value.copy(
                    currentPositionMs = clampedPos.toLong()
                )
            } catch (e: Exception) {
                _playerState.value = _playerState.value.copy(
                    error = e.localizedMessage ?: "Failed to seek"
                )
            }
        }
    }

    fun stop() {
        val player = mediaPlayer ?: return
        try {
            if (player.isPlaying) {
                player.stop()
            }
            stopPositionPolling()
            player.seekTo(0)
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                currentPositionMs = 0L
            )
        } catch (_: Exception) {
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                currentPositionMs = 0L
            )
        }
    }

    fun release() {
        releasePlayer()
    }

    private fun startPositionPolling() {
        stopPositionPolling()
        positionUpdateJob = coroutineScope.launch {
            while (isActive) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _playerState.value = _playerState.value.copy(
                            currentPositionMs = player.currentPosition.toLong()
                        )
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopPositionPolling() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    private fun releasePlayer() {
        stopPositionPolling()
        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {}
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        _playerState.value = AudioPlayerState()
    }
}
