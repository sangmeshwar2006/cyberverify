package com.cyberverify

import com.cyberverify.data.repository.GeminiRepository
import com.cyberverify.domain.model.ChatSender
import com.cyberverify.presentation.viewmodel.ChatbotViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatbotViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ChatbotViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatbotViewModel(FakeOfflineGeminiRepository())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialMessagesState() {
        val messages = viewModel.messages.value
        assertTrue(messages.isNotEmpty())
        assertEquals(ChatSender.BOT, messages.first().sender)
        assertFalse(viewModel.isLiveMode.value)
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
    fun `sendMessage with live gemini repository returns live response`() {
        val liveRepo = FakeLiveGeminiRepository("Live Gemini Response from Cloud")
        val liveViewModel = ChatbotViewModel(liveRepo)

        assertTrue(liveViewModel.isLiveMode.value)
        liveViewModel.sendMessage("Explain quantum cryptography")

        val messages = liveViewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertEquals("Live Gemini Response from Cloud", lastMessage.text)
    }

    @Test
    fun `sendMessage with live gemini repository returning null falls back smoothly`() {
        val failingLiveRepo = FakeLiveGeminiRepository(responseToReturn = null)
        val liveViewModel = ChatbotViewModel(failingLiveRepo)

        assertTrue(liveViewModel.isLiveMode.value)
        liveViewModel.sendMessage("What does Red risk mean?")

        val messages = liveViewModel.messages.value
        val lastMessage = messages.last()

        assertEquals(ChatSender.BOT, lastMessage.sender)
        assertTrue(lastMessage.text.contains("High Risk"))
    }

    @Test
    fun `setApiKey updates live mode state`() {
        val repo = FakeMutableGeminiRepository()
        val mutableViewModel = ChatbotViewModel(repo)

        assertFalse(mutableViewModel.isLiveMode.value)

        mutableViewModel.setApiKey("test_api_key_123")
        assertTrue(mutableViewModel.isLiveMode.value)
    }
}

private class FakeOfflineGeminiRepository : GeminiRepository {
    override fun isAvailable(): Boolean = false
    override fun updateApiKey(apiKey: String) {}
    override suspend fun generateResponse(prompt: String): String? = null
}

private class FakeLiveGeminiRepository(
    private val responseToReturn: String?
) : GeminiRepository {
    override fun isAvailable(): Boolean = true
    override fun updateApiKey(apiKey: String) {}
    override suspend fun generateResponse(prompt: String): String? = responseToReturn
}

private class FakeMutableGeminiRepository : GeminiRepository {
    private var key: String = ""
    override fun isAvailable(): Boolean = key.isNotBlank()
    override fun updateApiKey(apiKey: String) {
        key = apiKey
    }
    override suspend fun generateResponse(prompt: String): String? = if (isAvailable()) "Response for $prompt" else null
}
