package com.cyberverify

import com.cyberverify.domain.model.ChatSender
import com.cyberverify.presentation.viewmodel.ChatbotViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatbotViewModelTest {

    private lateinit var viewModel: ChatbotViewModel

    @Before
    fun setUp() {
        viewModel = ChatbotViewModel()
    }

    @Test
    fun initialMessagesState() {
        val messages = viewModel.messages.value
        assertTrue(messages.isNotEmpty())
        assertEquals(ChatSender.BOT, messages.first().sender)
    }

    @Test
    fun `sendMessage adds user message and bot reply`() {
        val initialCount = viewModel.messages.value.size
        viewModel.sendMessage("How to scan images?")
        val newMessages = viewModel.messages.value

        assertEquals(initialCount + 2, newMessages.size)
        assertEquals(ChatSender.USER, newMessages[initialCount].sender)
        assertEquals("How to scan images?", newMessages[initialCount].text)
        assertEquals(ChatSender.BOT, newMessages[initialCount + 1].sender)
        assertTrue(newMessages[initialCount + 1].text.contains("How to Scan Media"))
    }

    @Test
    fun `sendMessage with risk query returns risk explanation`() {
        viewModel.sendMessage("What does Red risk mean?")
        val messages = viewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("High Risk"))
    }

    @Test
    fun `sendMessage with deepfake query returns deepfake explanation`() {
        viewModel.sendMessage("Tell me about visual deepfakes and GANs")
        val messages = viewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("Deepfakes & Visual Manipulation"))
    }

    @Test
    fun `sendMessage with voice clone query returns audio forensics explanation`() {
        viewModel.sendMessage("How do voice clones and audio deepfakes work?")
        val messages = viewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("Voice Clones & Audio Forensics"))
    }

    @Test
    fun `sendMessage with exif metadata query returns metadata explanation`() {
        viewModel.sendMessage("Explain EXIF metadata and GPS tags")
        val messages = viewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("Metadata & EXIF Data Forensics"))
    }

    @Test
    fun `sendMessage with image format query returns jpg png ela explanation`() {
        viewModel.sendMessage("What is the difference between JPG and PNG and ELA?")
        val messages = viewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("Image Formats & Compression Forensics"))
    }

    @Test
    fun `sendMessage with arbitrary question returns intelligent fallback response`() {
        viewModel.sendMessage("Tell me a random story about a blue bicycle")
        val messages = viewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("Gemini-Level Intelligence & Forensic Analysis"))
    }

    @Test
    fun `sendMessage with cybersecurity query returns cybersecurity response`() {
        viewModel.sendMessage("Explain ransomware and phishing threats")
        val messages = viewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("Cybersecurity & Threat Intelligence"))
    }

    @Test
    fun `sendMessage with programming query returns programming response`() {
        viewModel.sendMessage("How do Jetpack Compose and coroutines work?")
        val messages = viewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("Programming & Software Engineering"))
    }

    @Test
    fun `sendMessage with general knowledge query returns scientific response`() {
        viewModel.sendMessage("Tell me about artificial intelligence and transformers")
        val messages = viewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("General Knowledge & Scientific Intelligence"))
    }
}
