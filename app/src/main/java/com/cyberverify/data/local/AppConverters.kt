package com.cyberverify.data.local

import com.cyberverify.domain.model.Indicator
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import com.cyberverify.domain.model.SuspiciousSegment
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object AppConverters {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)

    private val indicatorListType = Types.newParameterizedType(List::class.java, Indicator::class.java)
    private val indicatorListAdapter = moshi.adapter<List<Indicator>>(indicatorListType)

    private val stringMapType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
    private val stringMapAdapter = moshi.adapter<Map<String, String>>(stringMapType)

    private val segmentListType = Types.newParameterizedType(List::class.java, SuspiciousSegment::class.java)
    private val segmentListAdapter = moshi.adapter<List<SuspiciousSegment>>(segmentListType)

    fun String.toStringList(): List<String> {
        if (this.isBlank()) return emptyList()
        return runCatching { stringListAdapter.fromJson(this) ?: emptyList() }
            .getOrElse { this.split("||").filter { it.isNotBlank() } }
    }

    fun List<String>.stringListToJson(): String {
        return stringListAdapter.toJson(this)
    }

    fun String.toIndicatorList(): List<Indicator> {
        if (this.isBlank()) return emptyList()
        return runCatching { indicatorListAdapter.fromJson(this) ?: emptyList() }
            .getOrDefault(emptyList())
    }

    fun List<Indicator>.indicatorListToJson(): String {
        return indicatorListAdapter.toJson(this)
    }

    fun String.toStringMap(): Map<String, String> {
        if (this.isBlank()) return emptyMap()
        return runCatching { stringMapAdapter.fromJson(this) ?: emptyMap() }
            .getOrDefault(emptyMap())
    }

    fun Map<String, String>.stringMapToJson(): String {
        return stringMapAdapter.toJson(this)
    }

    fun String.toSegmentList(): List<SuspiciousSegment> {
        if (this.isBlank()) return emptyList()
        return runCatching { segmentListAdapter.fromJson(this) ?: emptyList() }
            .getOrDefault(emptyList())
    }

    fun List<SuspiciousSegment>.segmentListToJson(): String {
        return segmentListAdapter.toJson(this)
    }

    fun ScanEntity.toDomainModel(): ScanReport {
        return ScanReport(
            id = id,
            fileName = fileName,
            mediaType = MediaType.fromString(fileType),
            timestamp = date,
            riskLevel = RiskLevel.fromString(resultClassification),
            confidenceScore = confidenceScore,
            summaryExplanation = summaryExplanation,
            technicalFindings = technicalFindings.toStringList(),
            recommendations = recommendations.toStringList(),
            indicators = indicatorsJson.toIndicatorList(),
            heatmapUri = heatmapUri,
            mediaUri = mediaUri,
            modelName = modelName,
            metadataInfo = metadataInfoJson.toStringMap(),
            suspiciousSegments = suspiciousSegmentsJson.toSegmentList()
        )
    }

    fun ScanReport.toEntity(): ScanEntity {
        return ScanEntity(
            id = id,
            fileName = fileName,
            fileType = mediaType.name,
            date = timestamp,
            resultClassification = riskLevel.name,
            confidenceScore = confidenceScore,
            summaryExplanation = summaryExplanation,
            technicalFindings = technicalFindings.stringListToJson(),
            recommendations = recommendations.stringListToJson(),
            heatmapUri = heatmapUri,
            mediaUri = mediaUri,
            indicatorsJson = indicators.indicatorListToJson(),
            modelName = modelName,
            metadataInfoJson = metadataInfo.stringMapToJson(),
            suspiciousSegmentsJson = suspiciousSegments.segmentListToJson()
        )
    }
}
