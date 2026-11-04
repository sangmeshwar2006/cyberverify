package com.cyberverify.data.repository

import com.cyberverify.data.remote.GeminiApiService

interface GeminiRepository {
    fun isAvailable(): Boolean
    fun updateApiKey(apiKey: String)
    suspend fun generateResponse(prompt: String): String?
}

class GeminiRepositoryImpl(
    private val apiService: GeminiApiService = GeminiApiService()
) : GeminiRepository {

    override fun isAvailable(): Boolean {
        return apiService.isApiKeyAvailable()
    }

    override fun updateApiKey(apiKey: String) {
        apiService.updateApiKey(apiKey)
    }

    override suspend fun generateResponse(prompt: String): String? {
        return apiService.generateResponse(prompt)
    }
}
