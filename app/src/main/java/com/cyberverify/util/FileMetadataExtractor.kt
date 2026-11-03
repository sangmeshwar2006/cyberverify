package com.cyberverify.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.cyberverify.domain.model.MediaMetadata
import com.cyberverify.domain.model.MediaType

object FileMetadataExtractor {

    private val ALLOWED_IMAGE_MIME_TYPES = setOf(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp"
    )

    private val ALLOWED_AUDIO_MIME_TYPES = setOf(
        "audio/wav",
        "audio/x-wav",
        "audio/mpeg",
        "audio/mp3",
        "audio/mp4",
        "audio/x-m4a",
        "audio/m4a",
        "audio/aac"
    )

    fun extractMetadata(context: Context, uri: Uri): MediaMetadata? {
        val contentResolver = context.contentResolver
        var fileName = "unknown_file"
        var fileSize = 0L

        runCatching {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIndex != -1 && !cursor.isNull(nameIndex)) {
                        fileName = cursor.getString(nameIndex)
                    }
                    if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
            }
        }

        var mimeType = contentResolver.getType(uri) ?: getMimeTypeFromUri(uri)
        if (mimeType == null) {
            mimeType = getMimeTypeFromFileName(fileName)
        }

        val mediaType = determineMediaType(mimeType, fileName) ?: return null

        var durationMs: Long? = null
        if (mediaType == MediaType.AUDIO) {
            durationMs = extractAudioDuration(context, uri)
        }

        return MediaMetadata(
            uri = uri.toString(),
            fileName = fileName,
            fileSize = fileSize,
            mimeType = mimeType ?: "application/octet-stream",
            mediaType = mediaType,
            durationMs = durationMs
        )
    }

    fun isAllowedImageType(mimeType: String?, fileName: String?): Boolean {
        if (mimeType != null && ALLOWED_IMAGE_MIME_TYPES.contains(mimeType.lowercase())) {
            return true
        }
        val ext = fileName?.substringAfterLast('.', "")?.lowercase() ?: ""
        return ext in listOf("jpg", "jpeg", "png", "webp")
    }

    fun isAllowedAudioType(mimeType: String?, fileName: String?): Boolean {
        if (mimeType != null && ALLOWED_AUDIO_MIME_TYPES.contains(mimeType.lowercase())) {
            return true
        }
        val ext = fileName?.substringAfterLast('.', "")?.lowercase() ?: ""
        return ext in listOf("wav", "mp3", "m4a", "aac")
    }

    fun determineMediaType(mimeType: String?, fileName: String?): MediaType? {
        return when {
            isAllowedImageType(mimeType, fileName) -> MediaType.IMAGE
            isAllowedAudioType(mimeType, fileName) -> MediaType.AUDIO
            else -> null
        }
    }

    private fun extractAudioDuration(context: Context, uri: Uri): Long? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationStr?.toLongOrNull()
        } catch (_: Exception) {
            null
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }
    }

    private fun getMimeTypeFromUri(uri: Uri): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return if (extension.isNotEmpty()) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        } else null
    }

    private fun getMimeTypeFromFileName(fileName: String): String? {
        val ext = fileName.substringAfterLast('.', "")
        return if (ext.isNotEmpty()) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.lowercase())
        } else null
    }
}
