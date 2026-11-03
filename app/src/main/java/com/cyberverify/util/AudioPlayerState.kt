package com.cyberverify.util

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isLoaded: Boolean = false,
    val error: String? = null
) {
    val progress: Float
        get() = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val formattedPosition: String
        get() = FormatUtils.formatDuration(currentPositionMs)

    val formattedDuration: String
        get() = FormatUtils.formatDuration(durationMs)
}
