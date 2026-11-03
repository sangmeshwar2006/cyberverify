package com.cyberverify.domain.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val sender: ChatSender,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ChatSender {
    USER,
    BOT
}
