package com.cyberverify.data.repository

import android.util.Log
import com.cyberverify.domain.model.ScanReport
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun saveScanReport(userId: String, report: ScanReport) {
        if (userId.isBlank()) return
        runCatching {
            firestore.collection("users")
                .document(userId)
                .collection("scans")
                .document(report.id)
                .set(report)
                .await()
        }.onFailure { e ->
            Log.e("FirestoreRepository", "Failed to save scan report to Firestore", e)
        }
    }

    suspend fun deleteScanReport(userId: String, scanId: String) {
        if (userId.isBlank()) return
        runCatching {
            firestore.collection("users")
                .document(userId)
                .collection("scans")
                .document(scanId)
                .delete()
                .await()
        }.onFailure { e ->
            Log.e("FirestoreRepository", "Failed to delete scan report from Firestore", e)
        }
    }

    suspend fun clearHistory(userId: String) {
        if (userId.isBlank()) return
        runCatching {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("scans")
                .get()
                .await()
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
        }.onFailure { e ->
            Log.e("FirestoreRepository", "Failed to clear scan history from Firestore", e)
        }
    }

    suspend fun fetchScans(userId: String): List<ScanReport> {
        if (userId.isBlank()) return emptyList()
        return runCatching {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("scans")
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(ScanReport::class.java) }
        }.getOrElse { e ->
            Log.e("FirestoreRepository", "Failed to fetch scan reports from Firestore", e)
            emptyList()
        }
    }
}
