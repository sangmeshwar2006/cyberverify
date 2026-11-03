package com.cyberverify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberverify.data.repository.ScanRepository
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeDashboardUiState(
    val recentScans: List<ScanReport> = emptyList(),
    val totalScans: Int = 0,
    val threatsDetected: Int = 0,
    val authenticMediaCount: Int = 0,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: ScanRepository
) : ViewModel() {

    val uiState: StateFlow<HomeDashboardUiState> = repository.getScanHistory()
        .map { scans ->
            HomeDashboardUiState(
                recentScans = scans.take(5),
                totalScans = scans.size,
                threatsDetected = scans.count { it.riskLevel == RiskLevel.RED || it.riskLevel == RiskLevel.YELLOW },
                authenticMediaCount = scans.count { it.riskLevel == RiskLevel.GREEN },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeDashboardUiState(isLoading = true)
        )

    fun deleteScan(id: String) {
        viewModelScope.launch {
            repository.deleteScan(id)
        }
    }
}
