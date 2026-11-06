package com.cyberverify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberverify.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val id: String,
    val role: Role,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Role {
    USER, MODEL
}

data class ChatSession(
    val id: String,
    val title: String,
    val messages: List<ChatMessage>,
    val updatedAt: Long = System.currentTimeMillis()
)

class LuminaAssistantViewModel : ViewModel() {

    private val _sessions = MutableStateFlow<List<ChatSession>>(
        listOf(
            ChatSession(
                id = "1",
                title = "Welcome to Lumina AI",
                messages = listOf(
                    ChatMessage(
                        id = "1",
                        role = Role.MODEL,
                        content = "Hello! I am **Lumina**, your advanced AI Forensic Assistant for **CyberVerify**.\n\n" +
                                "I am fully versed in all CyberVerify forensic capabilities, including:\n" +
                                "- **Image Scan**: Deepfake & face swap detection\n" +
                                "- **Voice Scan**: Audio synthetic clone & spectrogram analysis\n" +
                                "- **4 Risk Levels**: 🟢 GREEN, 🟡 YELLOW, 🔴 RED, ⚪ GREY\n" +
                                "- **Quick Summary & Detailed Reports**\n" +
                                "- **PDF Report Generator**\n" +
                                "- **Room DB Local Storage**\n\n" +
                                "How can I assist you with media verification today?"
                    )
                )
            )
        )
    )
    val sessions: StateFlow<List<ChatSession>> = _sessions.asStateFlow()

    private val _activeSessionId = MutableStateFlow<String?>("1")
    val activeSessionId: StateFlow<String?> = _activeSessionId.asStateFlow()

    private val _isSidebarOpen = MutableStateFlow(true)
    val isSidebarOpen: StateFlow<Boolean> = _isSidebarOpen.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val generativeModel: GenerativeModel? by lazy {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "YOUR_API_KEY_HERE") {
            try {
                GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = apiKey
                )
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    fun toggleSidebar() {
        _isSidebarOpen.value = !_isSidebarOpen.value
    }

    fun createNewSession() {
        val newSessionId = System.currentTimeMillis().toString()
        val newSession = ChatSession(
            id = newSessionId,
            title = "New Chat",
            messages = emptyList(),
            updatedAt = System.currentTimeMillis()
        )
        _sessions.update { listOf(newSession) + it }
        _activeSessionId.value = newSessionId
    }

    fun selectSession(sessionId: String) {
        _activeSessionId.value = sessionId
    }

    fun deleteSession(sessionId: String) {
        _sessions.update { currentList ->
            val updated = currentList.filter { it.id != sessionId }
            if (_activeSessionId.value == sessionId) {
                _activeSessionId.value = updated.firstOrNull()?.id
            }
            updated
        }
    }

    fun clearActiveChat() {
        val currentId = _activeSessionId.value ?: return
        _sessions.update { currentList ->
            currentList.map { session ->
                if (session.id == currentId) {
                    session.copy(
                        messages = listOf(
                            ChatMessage(
                                id = System.currentTimeMillis().toString(),
                                role = Role.MODEL,
                                content = "Chat session reset. I'm Lumina, your CyberVerify AI Forensic Assistant. What would you like to investigate?"
                            )
                        ),
                        updatedAt = System.currentTimeMillis()
                    )
                } else session
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        var currentId = _activeSessionId.value
        if (currentId == null || _sessions.value.none { it.id == currentId }) {
            val newId = System.currentTimeMillis().toString()
            val newSession = ChatSession(
                id = newId,
                title = if (content.length > 30) content.take(30) + "..." else content,
                messages = emptyList(),
                updatedAt = System.currentTimeMillis()
            )
            _sessions.update { listOf(newSession) + it }
            _activeSessionId.value = newId
            currentId = newId
        }

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            role = Role.USER,
            content = content
        )

        // Update session with user message and update title if it was "New Chat" or first message
        _sessions.update { currentList ->
            currentList.map { session ->
                if (session.id == currentId) {
                    val isFirst = session.messages.isEmpty()
                    val newTitle = if (isFirst || session.title == "New Chat" || session.title == "Welcome to Lumina AI") {
                        if (content.length > 30) content.take(30) + "..." else content
                    } else session.title
                    session.copy(
                        title = newTitle,
                        messages = session.messages + userMessage,
                        updatedAt = System.currentTimeMillis()
                    )
                } else session
            }
        }

        _isTyping.value = true

        viewModelScope.launch {
            val responseContent = generateHybridResponse(content)
            val botMessage = ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                role = Role.MODEL,
                content = responseContent
            )

            _sessions.update { currentList ->
                currentList.map { session ->
                    if (session.id == currentId) {
                        session.copy(
                            messages = session.messages + botMessage,
                            updatedAt = System.currentTimeMillis()
                        )
                    } else session
                }
            }
            _isTyping.value = false
        }
    }

    private suspend fun generateHybridResponse(query: String): String {
        // 1. Instantaneous local CyberVerify expert response
        val localResponse = getLocalKeywordResponse(query)
        if (localResponse != null) {
            delay(150)
            return localResponse
        }

        // 2. Live Gemini AI generation
        val model = generativeModel
        if (model != null) {
            try {
                val aiResponse = withContext(Dispatchers.IO) {
                    val promptWithContext = "You are Lumina, the advanced AI Forensic Assistant for CyberVerify, an on-device media forensics app detecting deepfakes (image face swaps, voice clones) with 4 risk levels (GREEN, YELLOW, RED, GREY), quick summaries, detailed reports, PDF generation, and Room DB local storage. Answer the following user question clearly with markdown formatting:\n\n$query"
                    val result = model.generateContent(promptWithContext)
                    result.text
                }
                if (!aiResponse.isNullOrBlank()) {
                    return aiResponse
                }
            } catch (e: Exception) {
                // Fallback on error
            }
        }

        // 3. Fallback response
        delay(250)
        return "I have analyzed your inquiry regarding **\"$query\"**. As Lumina, your CyberVerify AI Forensic Assistant, I am ready to help you investigate deepfakes, inspect image artifacts, analyze voice spectrograms, and review 4-tier risk assessments."
    }

    private fun getLocalKeywordResponse(query: String): String? {
        val q = query.lowercase()
        return when {
            q.contains("image") || q.contains("photo") || q.contains("face") || q.contains("deepfake") -> {
                "### CyberVerify Image Scan Forensics\n\n" +
                        "CyberVerify's **Image Verification Engine** specializes in detecting facial alterations, face swaps, GAN-generated artifacts, lighting inconsistencies, and pixel-level tampering.\n\n" +
                        "- **Detection Methods**: Inspects frequency domain anomalies, eye reflection consistency, facial landmark geometry, and compression artifact misalignment.\n" +
                        "- **Local Processing**: Fast, secure forensic analysis directly on device.\n" +
                        "- **Result**: Generates a confidence score and classifies the image into one of 4 risk levels."
            }
            q.contains("voice") || q.contains("audio") || q.contains("clone") || q.contains("speech") -> {
                "### CyberVerify Voice Scan Forensics\n\n" +
                        "The **Voice Verification Engine** detects synthetic speech, AI-generated voice clones, and audio splicing.\n\n" +
                        "- **Spectrogram Analysis**: Analyzes frequency spectrums, harmonics, pitch stability, and background noise floors.\n" +
                        "- **Segment Inspection**: Identifies suspicious audio segments and timestamps where anomalies occur.\n" +
                        "- **Interactive Player**: Built-in audio player with waveform visualization and timestamp scrubbing."
            }
            q.contains("risk") || q.contains("level") || q.contains("green") || q.contains("yellow") || q.contains("red") || q.contains("grey") -> {
                "### CyberVerify 4-Tier Risk Assessment Model\n\n" +
                        "CyberVerify categorizes all forensic scans into four distinct risk levels:\n\n" +
                        "1. **🟢 GREEN (Authentic)**: High confidence that the media is genuine and unmanipulated.\n" +
                        "2. **🟡 YELLOW (Suspicious)**: Minor anomalies or ambiguous artifacts detected. Caution advised before sharing.\n" +
                        "3. **🔴 RED (High Risk)**: Strong forensic evidence of deepfake generation, face swapping, or voice cloning.\n" +
                        "4. **⚪ GREY (Inconclusive)**: Unsupported format, corrupted file, or insufficient resolution for conclusive analysis."
            }
            q.contains("summary") || q.contains("quick") -> {
                "### Quick Summary Screen\n\n" +
                        "The **Quick Summary** provides an executive overview immediately following a scan:\n" +
                        "- Prominent Risk Level Badge & Verdict\n" +
                        "- Confidence Score percentage\n" +
                        "- File metadata (Filename, File size, Resolution / Duration, Timestamp)\n" +
                        "- Quick action buttons to view the Detailed Report or scan another file."
            }
            q.contains("detail") || q.contains("report") -> {
                "### Detailed Forensic Report\n\n" +
                        "The **Detailed Report** screen offers an exhaustive breakdown of the forensic investigation:\n" +
                        "- Key risk indicators with severity levels\n" +
                        "- Frame-by-frame image anomaly highlights\n" +
                        "- Audio spectrogram timeline breakdown for voice files\n" +
                        "- Cryptographic verification hash"
            }
            q.contains("pdf") || q.contains("export") || q.contains("download") -> {
                "### PDF Report Generator\n\n" +
                        "CyberVerify includes a built-in **PDF Report Generator** (`PdfReportGenerator.kt`):\n" +
                        "- Formats any scan report into a professional official forensic document.\n" +
                        "- Includes organization header, timestamp, metadata, risk score, and detailed indicators.\n" +
                        "- Perfect for official documentation, legal records, or sharing verified findings."
            }
            q.contains("room") || q.contains("database") || q.contains("storage") || q.contains("history") -> {
                "### Room Database Local Storage\n\n" +
                        "CyberVerify uses **Room DB** (`AppDatabase`, `ScanDao`, `ScanEntity`) for secure local storage:\n" +
                        "- 100% offline persistence of all past scan reports.\n" +
                        "- View scan history anytime in the History tab.\n" +
                        "- Ensures complete user data privacy without cloud syncing."
            }
            q.contains("security") || q.contains("privacy") || q.contains("auth") -> {
                "### CyberVerify Security Architecture\n\n" +
                        "- **Authentication**: Secure user login and profile management.\n" +
                        "- **Local Processing**: Forensic analysis designed with privacy-first principles.\n" +
                        "- **Data Integrity**: Cryptographic hashing and Room DB encryption safeguards.\n" +
                        "- **Compliance**: Designed for government, enterprise, and journalist verification standards."
            }
            else -> null
        }
    }
}
