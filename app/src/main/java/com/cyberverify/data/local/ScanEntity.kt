package com.cyberverify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey val id: String,
    val fileName: String,
    val fileType: String,
    val date: Long,
    val resultClassification: String,
    val confidenceScore: Float,
    val summaryExplanation: String,
    val technicalFindings: String,
    val recommendations: String,
    val heatmapUri: String? = null,
    val mediaUri: String? = null,
    val indicatorsJson: String = "[]",
    val modelName: String = "CyberVerify Multi-Modal Forensic Engine v1.0",
    val metadataInfoJson: String = "{}",
    val suspiciousSegmentsJson: String = "[]"
)
