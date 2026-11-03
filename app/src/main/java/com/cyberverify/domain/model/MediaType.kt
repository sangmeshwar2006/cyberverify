package com.cyberverify.domain.model

enum class MediaType {
    IMAGE,
    AUDIO;

    companion object {
        fun fromString(value: String): MediaType {
            return runCatching { valueOf(value.uppercase()) }.getOrDefault(IMAGE)
        }
    }
}
