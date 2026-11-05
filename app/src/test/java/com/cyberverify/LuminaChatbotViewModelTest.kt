package com.cyberverify

import com.cyberverify.presentation.viewmodel.LuminaChatbotViewModel
import com.cyberverify.presentation.viewmodel.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LuminaChatbotViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LuminaChatbotViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LuminaChatbotViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains welcome message`() {
        val messages = viewModel.messages.value
        assertEquals(1, messages.size)
        assertEquals(Role.MODEL, messages[0].role)
        assertTrue(messages[0].content.contains("Lumina"))
    }

    @Test
    fun `sendMessage with keyword populates user and model responses`() = runTest {
        viewModel.sendMessage("Tell me about image scan")

        // Advance dispatcher past coroutine delays
        testDispatcher.scheduler.advanceUntilIdle()

        val messages = viewModel.messages.value
        assertEquals(3, messages.size)
        assertEquals(Role.USER, messages[1].role)
        assertEquals("Tell me about image scan", messages[1].content)
        assertEquals(Role.MODEL, messages[2].role)
        assertTrue(messages[2].content.contains("Image Scan Forensics"))
        assertFalse(viewModel.isTyping.value)
    }

    @Test
    fun `clearChat resets messages to initial greeting`() = runTest {
        viewModel.sendMessage("Voice verification")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.messages.value.size > 1)

        viewModel.clearChat()

        val messages = viewModel.messages.value
        assertEquals(1, messages.size)
        assertTrue(messages[0].content.contains("Chat session reset"))
    }
}
