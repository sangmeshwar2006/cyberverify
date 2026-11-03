package com.cyberverify.domain.model

data class SuspiciousSegment(
    val startTimeMs: Long,
    val endTimeMs: Long,
    val label: String,
    val severity: RiskLevel = RiskLevel.YELLOW
)
