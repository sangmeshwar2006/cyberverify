package com.cyberverify.domain.model

enum class RiskLevel(val label: String, val hexColor: String) {
    GREEN("No strong warning signs detected", "#2E7D32"),
    YELLOW("Some suspicious characteristics found", "#ED6C02"),
    RED("Strong warning signs detected", "#D32F2F"),
    GREY("Unable to determine", "#757575");

    companion object {
        fun fromString(value: String?): RiskLevel {
            if (value.isNullOrBlank()) return GREY
            val upper = value.trim().uppercase()
            runCatching { return valueOf(upper) }
            return when {
                upper.contains("SAFE") || upper.contains("AUTHENTIC") || upper.contains("LOW RISK") -> GREEN
                upper.contains("SUSPICIOUS") || upper.contains("MEDIUM") || upper.contains("MODERATE") -> YELLOW
                upper.contains("FAKE") || upper.contains("DEEPFAKE") || upper.contains("HIGH RISK") -> RED
                upper.contains("GREY") || upper.contains("GRAY") || upper.contains("INCONCLUSIVE") || upper.contains("UNKNOWN") -> GREY
                else -> GREY
            }
        }
    }
}
