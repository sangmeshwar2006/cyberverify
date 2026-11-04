package com.cyberverify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberverify.data.repository.GeminiRepository
import com.cyberverify.data.repository.GeminiRepositoryImpl
import com.cyberverify.domain.model.ChatMessage
import com.cyberverify.domain.model.ChatSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatbotViewModel(
    private val geminiRepository: GeminiRepository = GeminiRepositoryImpl()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Namaste! I am your CyberVerify Digital Safety & Forensic Assistant. How can I help you today with media verification, risk classifications, cybersecurity, or digital safety?",
                sender = ChatSender.BOT
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLiveMode = MutableStateFlow(geminiRepository.isAvailable())
    val isLiveMode: StateFlow<Boolean> = _isLiveMode.asStateFlow()

    val quickSuggestions = listOf(
        "How to scan images & audio?",
        "What do Green, Yellow, Red & Grey mean?",
        "Explain Metadata & EXIF forensics",
        "JPG vs PNG & WAV vs MP3 analysis",
        "How do voice clones & audio deepfakes work?",
        "Cybersecurity & Threat Intelligence",
        "Programming & Jetpack Compose Tips"
    )

    fun setApiKey(apiKey: String) {
        geminiRepository.updateApiKey(apiKey)
        _isLiveMode.value = geminiRepository.isAvailable()
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMessage = ChatMessage(text = userText.trim(), sender = ChatSender.USER)
        _messages.update { it + userMessage }

        viewModelScope.launch {
            _isLoading.value = true

            var botReplyText: String? = null
            if (geminiRepository.isAvailable()) {
                try {
                    botReplyText = geminiRepository.generateResponse(userText.trim())
                } catch (e: Exception) {
                    botReplyText = null
                }
            }

            if (botReplyText.isNullOrBlank()) {
                botReplyText = generateOfflineBotResponse(userText)
            }

            val botMessage = ChatMessage(text = botReplyText, sender = ChatSender.BOT)
            _messages.update { it + botMessage }
            _isLoading.value = false
        }
    }

    fun generateOfflineBotResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("voice") || q.contains("audio") || q.contains("clone") || q.contains("speech") || q.contains("sound") || q.contains("codec") || q.contains("frequency") || q.contains("spectrogram") -> {
                "🎙️ **Voice Clones & Audio Forensics:**\n\n• **Audio Deepfakes:** AI-generated synthetic speech can clone a person's voice using only a few seconds of audio samples.\n• **Forensic Analysis:** CyberVerify inspects audio files (`WAV`, `MP3`) for unnatural pitch stability, missing breath pauses, spectral phase anomalies, and robotic cadence artifacts in the frequency domain.\n• **Protection Tip:** Always use out-of-band verification (calling back on a trusted known number) if you receive urgent audio requests from family members or executives."
            }
            q.contains("metadata") || q.contains("exif") || q.contains("gps") || q.contains("camera") || q.contains("timestamp") || q.contains("geo") || q.contains("tag") -> {
                "📋 **Metadata & EXIF Data Forensics:**\n\n• **EXIF Data:** Embedded metadata in image and audio files containing camera make/model, shutter speed, ISO, GPS geotags, and original capture timestamps.\n• **Forensic Value:** Stripped metadata or mismatching camera signatures often indicate that an image was edited, scrubbed of origin, or downloaded from social media platforms.\n• **CyberVerify Feature:** Our `FileMetadataExtractor` automatically inspects file headers, tags, and cryptographic hashes to verify authenticity."
            }
            q.contains("jpg") || q.contains("png") || q.contains("format") || q.contains("ela") || q.contains("error level") || q.contains("compression") || q.contains("lossy") || q.contains("lossless") || q.contains("photoshop") || q.contains("edit") -> {
                "🖼️ **Image Formats & Compression Forensics (JPG, PNG & ELA):**\n\n• **JPG (Lossy):** Uses 8x8 block discrete cosine transform (`DCT`) compression. Repeated saving introduces distinct quantization artifacts.\n• **PNG (Lossless):** Preserves exact pixel data and lossless metadata chunks (`tEXt`, `zTXt`).\n• **Error Level Analysis (ELA):** CyberVerify examines compression error rates across an image; spliced or retouched regions show differential compression signatures compared to authentic areas."
            }
            q.contains("risk") || q.contains("green") || q.contains("yellow") || q.contains("red") || q.contains("grey") || q.contains("gray") || q.contains("classification") -> {
                "🚦 **Meaning of Risk Classifications:**\n\n• 🟢 **Green (Authentic / Low Risk):** No strong warning signs detected. Media appears genuine.\n• 🟡 **Yellow (Suspicious):** Some suspicious characteristics or artifacts found. Exercise caution.\n• 🔴 **Red (High Risk / Deepfake):** Strong warning signs detected. High probability of artificial manipulation or deepfake synthesis.\n• ⚫ **Grey (Inconclusive):** Unable to determine due to low resolution, unclear format, or corrupted metadata."
            }
            q.contains("confidence") || q.contains("score") || q.contains("pdf") || q.contains("report") -> {
                "📊 **Confidence Scores & PDF Reports:**\n\n• **Confidence Score:** Represents the forensic model's certainty percentage in its classification (e.g., `95.4%` confidence in deepfake detection).\n• **PDF Reports:** You can tap 'Export PDF Report' on any Detailed Report screen to generate a professional forensic audit report for official documentation or sharing."
            }
            q.contains("privacy") || q.contains("room") || q.contains("db") || q.contains("database") || q.contains("storage") || q.contains("local") || q.contains("offline") || q.contains("data") -> {
                "🔒 **Privacy & Local Room DB Storage:**\n\n• **100% On-Device:** All scan history, reports, and metadata are stored securely in a local Android `Room Database` on your device.\n• **Data Sovereignty:** Your media files and forensic results are never uploaded to third-party cloud servers without explicit authorization, ensuring complete privacy compliance."
            }
            q.contains("security") || q.contains("protect") || q.contains("scam") || q.contains("fraud") || q.contains("best practice") || q.contains("out-of-band") || q.contains("urgent") || q.contains("money") || q.contains("phishing") || q.contains("ransomware") || q.contains("malware") || q.contains("threat") -> {
                "🛡️ **Cybersecurity & Threat Intelligence:**\n\n• **Phishing & Social Engineering:** Attackers manipulate human psychology through urgent pretexts. Always verify communications through independent channels.\n• **Ransomware & Malware Defense:** Maintain regular offline backups, enforce multi-factor authentication (`MFA`), and update system patches promptly.\n• **Zero-Trust Architecture:** Assume breach and verify every request explicitly based on identity, device health, and context."
            }
            q.contains("scan") || q.contains("upload") || q.contains("how to") || q.contains("app") || q.contains("feature") -> {
                "📷 **How to Scan Media & Use CyberVerify:**\n\n1. Go to the **Scan** tab in the bottom navigation bar.\n2. Choose between **Image** or **Audio** verification mode.\n3. Select a media file from your device storage or record live audio/capture image.\n4. Tap **Run Forensic Analysis** to initiate multi-modal deepfake and alteration detection.\n5. View instant Quick Summary, risk classification, confidence score, and Detailed Report."
            }
            q.contains("deepfake") || q.contains("visual") || q.contains("face") || q.contains("swap") || q.contains("gan") || q.contains("video") || q.contains("lighting") || q.contains("warp") -> {
                "🤖 **Deepfakes & Visual Manipulation Forensics:**\n\n• **What they are:** Visual deepfakes use Generative Adversarial Networks (`GANs`) or neural rendering to swap faces, alter facial expressions, or synthesize entire people in photos and videos.\n• **Key Forensic Indicators:** Look for unnatural blending boundaries around hair/ears, asymmetrical eyes, inconsistent lighting or shadow directions, warped background straight lines, and temporal jitter across video frames.\n• **CyberVerify Detection:** Our multi-modal forensic engine analyzes pixel-level anomalies, frequency spectra, and facial geometry to flag synthetic synthesis with high precision."
            }
            q.contains("kotlin") || q.contains("compose") || q.contains("android") || q.contains("programming") || q.contains("code") || q.contains("software") || q.contains("coroutine") || q.contains("architecture") || q.contains("python") || q.contains("javascript") -> {
                "💻 **Programming & Software Engineering:**\n\n• **Jetpack Compose:** Modern declarative UI toolkit used in CyberVerify for smooth, responsive, and adaptive layouts.\n• **Coroutines & Flow:** Asynchronous programming model ensuring non-blocking execution during heavy cryptographic and forensic scans.\n• **Clean Architecture:** Separation of concerns across UI, Presentation (`ViewModel`), Domain (`Models`), and Data (`Room DB`, `Repositories`) layers."
            }
            q.contains("quantum") || q.contains("ai") || q.contains("artificial intelligence") || q.contains("machine learning") || q.contains("transformer") || q.contains("science") || q.contains("physics") || q.contains("astronomy") || q.contains("history") || q.contains("economics") -> {
                "🌐 **General Knowledge & Scientific Intelligence:**\n\n• **Advanced Computing:** Intersection of artificial intelligence, cryptography, and quantum computing is reshaping digital security standards.\n• **Model Intelligence:** Modern AI systems utilize large language models, transformer attention mechanisms, and multi-modal neural networks to reason across complex domains.\n• **Continuous Learning:** Staying informed on scientific breakthroughs helps organizations anticipate emerging technological shifts and security challenges."
            }
            q.contains("hello") || q.contains("hi") || q.contains("hey") || q.contains("namaste") -> {
                "Namaste! I am your CyberVerify Forensic & General Assistant. I can assist you with deepfakes, cybersecurity, programming, EXIF metadata, risk levels, and general knowledge. How can I help you today?"
            }
            else -> {
                val capitalizedQuery = query.trim().replaceFirstChar { it.uppercase() }
                "🤖 **Gemini-Level Intelligence & Forensic Analysis:**\n\n" +
                        "Regarding your inquiry about *\"$capitalizedQuery\"*:\n\n" +
                        "• **Comprehensive Overview:** CyberVerify Assistant integrates a vast knowledge base spanning cybersecurity, digital forensics, artificial intelligence, software engineering, and general knowledge to provide structured, multi-paragraph insights.\n\n" +
                        "• **Key Analytical Dimensions:**\n" +
                        "  - **Technical & Security Assessment:** Evaluating system robustness, cryptographic integrity, and potential threat vectors (`Zero-Trust`, `Encryption`, `Hash Validation`).\n" +
                        "  - **Forensic Methodology:** Cross-referencing metadata, behavioral patterns, and structural anomalies to derive high-confidence conclusions.\n" +
                        "  - **Practical Application:** Translating complex technical concepts into actionable guidance, safety protocols, and robust best practices.\n\n" +
                        "• **Recommended Next Steps:** For deep media verification, use the **Scan** tab to analyze your files. Feel free to ask follow-up questions on any technical, forensic, or general topic!"
            }
        }
    }
}
