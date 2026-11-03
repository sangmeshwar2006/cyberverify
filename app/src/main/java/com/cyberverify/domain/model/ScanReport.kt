package com.cyberverify.domain.model

data class ScanReport(
    val id: String,
    val fileName: String,
    val mediaType: MediaType,
    val timestamp: Long,
    val riskLevel: RiskLevel,
    val confidenceScore: Float,
    val summaryExplanation: String,
    val technicalFindings: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val indicators: List<Indicator> = emptyList(),
    val heatmapUri: String? = null,
    val mediaUri: String? = null,
    val modelName: String = "CyberVerify Multi-Modal Forensic Engine v1.0",
    val metadataInfo: Map<String, String> = emptyMap(),
    val suspiciousSegments: List<SuspiciousSegment> = emptyList(),
    val detailedFindingsList: List<String>? = null
) {
    val detailedFindings: List<String>
        get() = detailedFindingsList ?: technicalFindings
}
