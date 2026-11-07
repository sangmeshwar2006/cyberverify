package com.cyberverify.data.repository

import com.cyberverify.data.demo.DemoAnalysisProvider
import com.cyberverify.data.local.AppConverters.toDomainModel
import com.cyberverify.data.local.AppConverters.toEntity
import com.cyberverify.data.local.ScanDao
import com.cyberverify.data.remote.AnalysisApiService
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ScanRepository(
    private val scanDao: ScanDao,
    private val apiService: AnalysisApiService? = null,
    private val firestoreRepository: FirestoreRepository? = null
) {
    private val scope = CoroutineScope(Dispatchers.IO)

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
                runCatching {
                    DemoAnalysisProvider.generateAnalysis(fileName, MediaType.IMAGE, fileUri)
                }.getOrElse {
                    DemoAnalysisProvider.generateAnalysis(fileName, MediaType.IMAGE, fileUri)
                }
            }

            scanDao.insertScan(report.toEntity())
            syncToFirestore(report)
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
            syncToFirestore(report)
            report
        }
    }

    suspend fun saveScanReport(report: ScanReport) {
        scanDao.insertScan(report.toEntity())
        syncToFirestore(report)
    }

    suspend fun deleteScan(id: String) {
        scanDao.deleteScanById(id)
        runCatching {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null && firestoreRepository != null) {
                scope.launch {
                    firestoreRepository.deleteScanReport(currentUser.uid, id)
                }
            }
        }
    }

    suspend fun clearHistory() {
        scanDao.clearAllScans()
        runCatching {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null && firestoreRepository != null) {
                scope.launch {
                    firestoreRepository.clearHistory(currentUser.uid)
                }
            }
        }
    }

    private fun syncToFirestore(report: ScanReport) {
        runCatching {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null && firestoreRepository != null) {
                scope.launch {
                    firestoreRepository.saveScanReport(currentUser.uid, report)
                }
            }
        }
    }
}
