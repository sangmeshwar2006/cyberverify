package com.cyberverify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberverify.data.repository.ScanRepository
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val scans: List<ScanReport> = emptyList(),
    val searchQuery: String = "",
    val selectedRiskFilter: RiskLevel? = null,
    val selectedMediaTypeFilter: MediaType? = null,
    val isLoading: Boolean = false
)

class HistoryViewModel(
    private val repository: ScanRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedRiskFilter = MutableStateFlow<RiskLevel?>(null)
    val selectedRiskFilter: StateFlow<RiskLevel?> = _selectedRiskFilter.asStateFlow()

    private val _selectedMediaTypeFilter = MutableStateFlow<MediaType?>(null)
    val selectedMediaTypeFilter: StateFlow<MediaType?> = _selectedMediaTypeFilter.asStateFlow()

    val uiState: StateFlow<HistoryUiState> = combine(
        repository.getScanHistory(),
        _searchQuery,
        _selectedRiskFilter,
        _selectedMediaTypeFilter
    ) { allScans, query, riskFilter, mediaTypeFilter ->
        val filtered = allScans.filter { scan ->
            val matchesQuery = query.isBlank() ||
                    scan.fileName.contains(query, ignoreCase = true) ||
                    scan.summaryExplanation.contains(query, ignoreCase = true)

            val matchesRisk = riskFilter == null || scan.riskLevel == riskFilter
            val matchesMediaType = mediaTypeFilter == null || scan.mediaType == mediaTypeFilter

            matchesQuery && matchesRisk && matchesMediaType
        }

        HistoryUiState(
            scans = filtered,
            searchQuery = query,
            selectedRiskFilter = riskFilter,
            selectedMediaTypeFilter = mediaTypeFilter,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setRiskFilter(riskLevel: RiskLevel?) {
        _selectedRiskFilter.value = riskLevel
    }

    fun setMediaTypeFilter(mediaType: MediaType?) {
        _selectedMediaTypeFilter.value = mediaType
    }

    fun deleteScan(id: String) {
        viewModelScope.launch {
            repository.deleteScan(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    suspend fun getScanReportById(id: String): ScanReport? {
        return repository.getScanReportById(id)
    }
}
