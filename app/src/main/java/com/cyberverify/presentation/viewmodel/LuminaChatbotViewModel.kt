package com.cyberverify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val role: Role,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Role {
    USER, MODEL
}

class LuminaChatbotViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
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
                        "- **Room DB Local Storage**\n" +
                        "- **Enterprise Security Features**\n\n" +
                        "How can I assist you with media verification today?"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            role = Role.USER,
            content = prompt
        )
        _messages.update { it + userMsg }
        _isTyping.value = true

        viewModelScope.launch {
            delay(700) // Simulate AI reasoning
            val responseContent = generateLuminaResponse(prompt)
            val modelMsg = ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                role = Role.MODEL,
                content = responseContent
            )
            _messages.update { it + modelMsg }
            _isTyping.value = false
        }
    }

    fun clearChat() {
        _messages.value = listOf(
            ChatMessage(
                id = System.currentTimeMillis().toString(),
                role = Role.MODEL,
                content = "Chat session reset. I'm Lumina, your CyberVerify AI Forensic Assistant. What would you like to investigate?"
            )
        )
    }

    private fun generateLuminaResponse(query: String): String {
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
            else -> {
                "I have registered your inquiry regarding **\"$query\"**. As Lumina, your CyberVerify AI Forensic Assistant, I can guide you through our image and voice scan engines, explain the 4 risk levels (GREEN, YELLOW, RED, GREY), walk you through Quick Summaries and Detailed Reports, assist with PDF report generation, or explain our Room DB storage and security features.\n\nHow else can I help you?"
            }
        }
    }
}
