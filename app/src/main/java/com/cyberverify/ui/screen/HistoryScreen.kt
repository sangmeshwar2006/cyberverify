package com.cyberverify.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import com.cyberverify.presentation.viewmodel.HistoryUiState
import com.cyberverify.presentation.viewmodel.HistoryViewModel
import com.cyberverify.ui.theme.CyberVerifyTheme
import com.cyberverify.ui.theme.GovernmentBlue
import com.cyberverify.ui.theme.NavyBlue
import com.cyberverify.ui.theme.RiskAmber
import com.cyberverify.ui.theme.RiskGreen
import com.cyberverify.ui.theme.RiskGreenLight
import com.cyberverify.ui.theme.RiskRed
import com.cyberverify.ui.theme.RiskRedLight
import com.cyberverify.util.FormatUtils

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel,
    onSelectReport: (ScanReport) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by historyViewModel.uiState.collectAsStateWithLifecycle()

    HistoryScreenContent(
        uiState = uiState,
        onSearchQueryChange = { historyViewModel.updateSearchQuery(it) },
        onRiskFilterSelect = { historyViewModel.setRiskFilter(it) },
        onMediaTypeFilterSelect = { historyViewModel.setMediaTypeFilter(it) },
        onDeleteScan = { historyViewModel.deleteScan(it) },
        onClearAll = { historyViewModel.clearAllHistory() },
        onSelectReport = onSelectReport,
        modifier = modifier
    )
}

@Composable
fun HistoryScreenContent(
    uiState: HistoryUiState,
    onSearchQueryChange: (String) -> Unit,
    onRiskFilterSelect: (RiskLevel?) -> Unit,
    onMediaTypeFilterSelect: (MediaType?) -> Unit,
    onDeleteScan: (String) -> Unit,
    onClearAll: () -> Unit,
    onSelectReport: (ScanReport) -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = NavyBlue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Forensic Scan Log",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = NavyBlue
                    )
                    Text(
                        text = "${uiState.scans.size} Reports Preserved",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (uiState.scans.isNotEmpty()) {
                IconButton(onClick = { showClearConfirmDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear All History",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search by filename or report summary...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips Row (All, Images, Audio, High Risk)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                val isAllSelected = uiState.selectedMediaTypeFilter == null && uiState.selectedRiskFilter == null
                FilterChip(
                    selected = isAllSelected,
                    onClick = {
                        onMediaTypeFilterSelect(null)
                        onRiskFilterSelect(null)
                    },
                    label = { Text("All Reports") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }

            item {
                val isImageSelected = uiState.selectedMediaTypeFilter == MediaType.IMAGE
                FilterChip(
                    selected = isImageSelected,
                    onClick = {
                        onMediaTypeFilterSelect(if (isImageSelected) null else MediaType.IMAGE)
                    },
                    label = { Text("Images") },
                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GovernmentBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }

            item {
                val isAudioSelected = uiState.selectedMediaTypeFilter == MediaType.AUDIO
                FilterChip(
                    selected = isAudioSelected,
                    onClick = {
                        onMediaTypeFilterSelect(if (isAudioSelected) null else MediaType.AUDIO)
                    },
                    label = { Text("Audio") },
                    leadingIcon = { Icon(Icons.Default.AudioFile, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF512DA8),
                        selectedLabelColor = Color.White
                    )
                )
            }

            item {
                val isHighRiskSelected = uiState.selectedRiskFilter == RiskLevel.RED
                FilterChip(
                    selected = isHighRiskSelected,
                    onClick = {
                        onRiskFilterSelect(if (isHighRiskSelected) null else RiskLevel.RED)
                    },
                    label = { Text("High Risk") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RiskRed,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // History List or Empty State
        if (uiState.scans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No forensic reports found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Try adjusting your search query or filter chips.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.scans, key = { it.id }) { report ->
                    HistoryItemCard(
                        report = report,
                        onClick = { onSelectReport(report) },
                        onDelete = { onDeleteScan(report.id) }
                    )
                }
            }
        }
    }

    // Clear Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All Forensic History?") },
            text = { Text("This will permanently delete all stored forensic scan reports from Room local database. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HistoryItemCard(
    report: ScanReport,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (report.mediaType == MediaType.IMAGE) Color(0xFFE3F2FD) else Color(0xFFEDE7F6),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (report.mediaType == MediaType.IMAGE) Icons.Default.Image else Icons.Default.AudioFile,
                        contentDescription = null,
                        tint = if (report.mediaType == MediaType.IMAGE) GovernmentBlue else Color(0xFF512DA8),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.fileName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = FormatUtils.formatTimestamp(report.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Score: ${FormatUtils.formatConfidenceScore(report.confidenceScore)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = GovernmentBlue
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Risk Badge
            val (bgColor, textColor, labelText) = when (report.riskLevel) {
                RiskLevel.GREEN -> Triple(RiskGreenLight, RiskGreen, "AUTHENTIC")
                RiskLevel.YELLOW -> Triple(Color(0xFFFFF3E0), RiskAmber, "SUSPICIOUS")
                RiskLevel.RED -> Triple(RiskRedLight, RiskRed, "HIGH RISK")
                RiskLevel.GREY -> Triple(Color(0xFFF0F0F0), Color(0xFF616161), "INCONCLUSIVE")
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = labelText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete scan report",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    CyberVerifyTheme {
        HistoryScreenContent(
            uiState = HistoryUiState(),
            onSearchQueryChange = {},
            onRiskFilterSelect = {},
            onMediaTypeFilterSelect = {},
            onDeleteScan = {},
            onClearAll = {},
            onSelectReport = {}
        )
    }
}
