package com.cyberverify.data.repository

import com.cyberverify.data.demo.DemoAnalysisProvider
import com.cyberverify.data.local.AppConverters.toDomainModel
import com.cyberverify.data.local.AppConverters.toEntity
import com.cyberverify.data.local.ScanDao
import com.cyberverify.data.remote.AnalysisApiService
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScanRepository(
    private val scanDao: ScanDao,
    private val apiService: AnalysisApiService? = null
) {

    fun getScanHistory(): Flow<List<ScanReport>> {
        return scanDao.getAllScans().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    fun getScansByRisk(riskLevel: RiskLevel): Flow<List<ScanReport>> {
        return scanDao.getScansByRisk(riskLevel.name).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    suspend fun getScanReportById(id: String): ScanReport? {
        return scanDao.getScanById(id)?.toDomainModel()
    }

    suspend fun analyzeImage(
        fileUri: String,
        fileName: String,
        isDemoMode: Boolean = true
    ): Result<ScanReport> {
        return runCatching {
            val report = if (isDemoMode || apiService == null) {
                DemoAnalysisProvider.generateAnalysis(
                    fileName = fileName,
                    mediaType = MediaType.IMAGE,
                    mediaUri = fileUri
                )
            } else {
                // If live API available, attempt network request; fallback to demo on failure
                runCatching {
                    // Place logic here when endpoint configured
                    DemoAnalysisProvider.generateAnalysis(fileName, MediaType.IMAGE, fileUri)
                }.getOrElse {
                    DemoAnalysisProvider.generateAnalysis(fileName, MediaType.IMAGE, fileUri)
                }
            }

            scanDao.insertScan(report.toEntity())
            report
        }
    }

    suspend fun analyzeAudio(
        fileUri: String,
        fileName: String,
        isDemoMode: Boolean = true
    ): Result<ScanReport> {
        return runCatching {
            val report = if (isDemoMode || apiService == null) {
                DemoAnalysisProvider.generateAnalysis(
                    fileName = fileName,
                    mediaType = MediaType.AUDIO,
                    mediaUri = fileUri
                )
            } else {
                runCatching {
                    DemoAnalysisProvider.generateAnalysis(fileName, MediaType.AUDIO, fileUri)
                }.getOrElse {
                    DemoAnalysisProvider.generateAnalysis(fileName, MediaType.AUDIO, fileUri)
                }
            }

            scanDao.insertScan(report.toEntity())
            report
        }
    }

    suspend fun saveScanReport(report: ScanReport) {
        scanDao.insertScan(report.toEntity())
    }

    suspend fun deleteScan(id: String) {
        scanDao.deleteScanById(id)
    }

    suspend fun clearHistory() {
        scanDao.clearAllScans()
    }
}
