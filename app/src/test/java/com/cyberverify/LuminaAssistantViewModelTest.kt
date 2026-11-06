package com.cyberverify

import com.cyberverify.presentation.viewmodel.LuminaAssistantViewModel
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
class LuminaAssistantViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LuminaAssistantViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LuminaAssistantViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains session with welcome message`() {
        val sessions = viewModel.sessions.value
        assertTrue(sessions.isNotEmpty())
        val activeSession = sessions.first()
        assertTrue(activeSession.messages.isNotEmpty())
        assertEquals(Role.MODEL, activeSession.messages[0].role)
        assertTrue(activeSession.messages[0].content.contains("Lumina"))
    }

    @Test
    fun `sendMessage populates user and model responses in active session`() = runTest {
        viewModel.sendMessage("Tell me about image scan")

        testDispatcher.scheduler.advanceUntilIdle()

        val activeSession = viewModel.sessions.value.find { it.id == viewModel.activeSessionId.value }
        assert(activeSession != null)
        val messages = activeSession!!.messages
        assertTrue(messages.size >= 3)
        assertEquals(Role.USER, messages[1].role)
        assertEquals("Tell me about image scan", messages[1].content)
        assertEquals(Role.MODEL, messages[2].role)
        assertTrue(messages[2].content.contains("Image Scan Forensics"))
        assertFalse(viewModel.isTyping.value)
    }

    @Test
    fun `createNewSession adds new session and sets as active`() = runTest {
        val initialCount = viewModel.sessions.value.size
        viewModel.createNewSession()
        val sessions = viewModel.sessions.value
        assertEquals(initialCount + 1, sessions.size)
    }
}
