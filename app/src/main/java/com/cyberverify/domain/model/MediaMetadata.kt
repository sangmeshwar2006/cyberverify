package com.cyberverify.domain.model

import com.cyberverify.util.FormatUtils

data class MediaMetadata(
    val uri: String,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val mediaType: MediaType,
    val durationMs: Long? = null
) {
    val formattedSize: String
        get() = FormatUtils.formatFileSize(fileSize)

    val formattedDuration: String?
        get() = durationMs?.let { FormatUtils.formatDuration(it) }
}
