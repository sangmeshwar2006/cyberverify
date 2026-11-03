package com.cyberverify.util

object MediaPickerContracts {
    val IMAGE_MIME_TYPES = arrayOf("image/jpeg", "image/png", "image/webp")
    val AUDIO_MIME_TYPES = arrayOf("audio/wav", "audio/x-wav", "audio/mpeg", "audio/mp4", "audio/x-m4a", "audio/aac")

    const val IMAGE_GET_CONTENT_TYPE = "image/*"
    const val AUDIO_GET_CONTENT_TYPE = "audio/*"
}
