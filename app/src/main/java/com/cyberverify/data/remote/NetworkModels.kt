package com.cyberverify.data.remote

import com.cyberverify.domain.model.Indicator
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport

data class IndicatorDto(
    val name: String,
    val description: String,
    val isSuspicious: Boolean,
    val score: Float
) {
    fun toDomain(): Indicator {
        return Indicator(
            name = name,
            description = description,
            isSuspicious = isSuspicious,
            score = score
        )
    }
}

data class AnalysisResponse(
    val reportId: String,
    val fileName: String,
    val fileType: String,
    val timestamp: Long,
    val riskLevel: String,
    val confidenceScore: Float,
    val summaryExplanation: String,
    val technicalFindings: List<String>,
    val recommendations: List<String>,
    val indicators: List<IndicatorDto>,
    val heatmapUri: String? = null
) {
    fun toDomain(mediaUri: String? = null): ScanReport {
        return ScanReport(
            id = reportId,
            fileName = fileName,
            mediaType = MediaType.fromString(fileType),
            timestamp = timestamp,
            riskLevel = RiskLevel.fromString(riskLevel),
            confidenceScore = confidenceScore,
            summaryExplanation = summaryExplanation,
            technicalFindings = technicalFindings,
            recommendations = recommendations,
            indicators = indicators.map { it.toDomain() },
            heatmapUri = heatmapUri,
            mediaUri = mediaUri
        )
    }
}
