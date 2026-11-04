package com.cyberverify.data.remote

import com.cyberverify.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiApiService(
    private var apiKey: String = BuildConfig.GEMINI_API_KEY,
    private val modelName: String = "gemini-1.5-flash"
) {

    private var generativeModel: GenerativeModel? = createGenerativeModel(apiKey)

    fun updateApiKey(newApiKey: String) {
        apiKey = newApiKey
        generativeModel = createGenerativeModel(newApiKey)
    }

    fun isApiKeyAvailable(): Boolean {
        return apiKey.isNotBlank() && apiKey != "null"
    }

    private fun createGenerativeModel(key: String): GenerativeModel? {
        return if (key.isNotBlank() && key != "null") {
            try {
                GenerativeModel(
                    modelName = modelName,
                    apiKey = key
                )
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    suspend fun generateResponse(userPrompt: String): String? = withContext(Dispatchers.IO) {
        val model = generativeModel ?: return@withContext null
        if (!isApiKeyAvailable()) return@withContext null

        val systemPrompt = """
            You are CyberVerify Assistant, an expert digital safety, forensic media verification, and cybersecurity AI assistant.
            You help users analyze media authenticity (images and audio), explain risk levels (Green/Yellow/Red/Grey), EXIF metadata, deepfake synthesis (GANs, voice cloning), error level analysis (ELA), privacy, and general cybersecurity/technology topics.
            Provide accurate, structured, concise, and helpful responses using markdown formatting.
        """.trimIndent()

        val fullPrompt = "$systemPrompt\n\nUser Question: $userPrompt"

        try {
            val response = model.generateContent(fullPrompt)
            response.text?.trim()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
