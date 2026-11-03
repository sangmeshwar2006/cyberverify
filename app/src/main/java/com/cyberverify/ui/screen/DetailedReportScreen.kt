package com.cyberverify.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cyberverify.domain.model.Indicator
import com.cyberverify.domain.model.MediaMetadata
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import com.cyberverify.domain.model.SuspiciousSegment
import com.cyberverify.ui.theme.CyberVerifyTheme
import com.cyberverify.ui.theme.GovernmentBlue
import com.cyberverify.ui.theme.NavyBlue
import com.cyberverify.ui.theme.RiskAmber
import com.cyberverify.ui.theme.RiskAmberLight
import com.cyberverify.ui.theme.RiskGreen
import com.cyberverify.ui.theme.RiskGreenLight
import com.cyberverify.ui.theme.RiskRed
import com.cyberverify.ui.theme.RiskRedLight
import com.cyberverify.util.AudioPlayerState
import com.cyberverify.util.FormatUtils
import com.cyberverify.util.PdfReportGenerator

@Composable
fun DetailedReportScreen(
    report: ScanReport,
    onBackToSummary: () -> Unit,
    modifier: Modifier = Modifier,
    playerState: AudioPlayerState = AudioPlayerState(),
    onPlayPauseToggle: () -> Unit = {},
    onSeekTo: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val (badgeBg, textColor, statusIcon, statusTitle, statusSubtitle) = when (report.riskLevel) {
        RiskLevel.GREEN -> DetailedStatusData(
            bgColor = RiskGreenLight,
            textColor = RiskGreen,
            icon = Icons.Default.CheckCircle,
            title = "AUTHENTIC MEDIA",
            subtitle = "Low Risk • Authentic Signal Signature Verified"
        )
        RiskLevel.YELLOW -> DetailedStatusData(
            bgColor = RiskAmberLight,
            textColor = RiskAmber,
            icon = Icons.Default.Warning,
            title = "SUSPICIOUS MEDIA",
            subtitle = "Medium Risk • Localized Compression Anomalies Detected"
        )
        RiskLevel.RED -> DetailedStatusData(
            bgColor = RiskRedLight,
            textColor = RiskRed,
            icon = Icons.Default.ErrorOutline,
            title = "HIGH RISK MANIPULATION",
            subtitle = "High Risk • Digital Generative Pattern Detected"
        )
        RiskLevel.GREY -> DetailedStatusData(
            bgColor = Color(0xFFF0F0F0),
            textColor = Color(0xFF616161),
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            title = "UNABLE TO DETERMINE",
            subtitle = "Inconclusive • Signal Resolution Below Threshold"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Title & Back
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToSummary) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Summary",
                    tint = NavyBlue
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Detailed Forensic Report",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                )
                Text(
                    text = "Report ID: ${report.id}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // SECTION A: REPORT INFORMATION
        SectionCard(title = "SECTION A: REPORT INFORMATION", icon = Icons.Default.Info) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ReportInfoRow(label = "Report ID", value = report.id)
                ReportInfoRow(label = "Uploaded Filename", value = report.fileName)
                ReportInfoRow(label = "Media Type", value = report.mediaType.name)
                ReportInfoRow(label = "Analysis Date / Time", value = FormatUtils.formatTimestamp(report.timestamp))
                ReportInfoRow(label = "Analysis Status", value = "Completed & Forensically Certified")
            }
        }

        // SECTION B: OVERALL ANALYSIS RESULT
        SectionCard(title = "SECTION B: OVERALL ANALYSIS RESULT", icon = Icons.Default.Shield) {
            Column {
                // Classification Badge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = badgeBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = textColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = statusIcon,
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = statusTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = textColor
                            )
                            Text(
                                text = statusSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Confidence vs Fake Probability Distinction
                val formattedConf = FormatUtils.formatConfidenceScore(report.confidenceScore)
                val confRatio = (if (report.confidenceScore <= 1.0f) report.confidenceScore else report.confidenceScore / 100f).coerceIn(0f, 1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Algorithmic Confidence Score",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = NavyBlue
                    )
                    Text(
                        text = formattedConf,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { confRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = textColor,
                    trackColor = textColor.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Note: Model confidence measures algorithmic certainty in feature pattern matching, distinct from raw fake probability score.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // SECTION C: ANALYSIS SUMMARY
        SectionCard(title = "SECTION C: ANALYSIS SUMMARY", icon = Icons.Default.Analytics) {
            Text(
                text = report.summaryExplanation,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // SECTION D: KEY FINDINGS & CHANGES DETECTED
        SectionCard(title = "SECTION D: KEY FINDINGS & CHANGES DETECTED", icon = if (report.mediaType == MediaType.IMAGE) Icons.Default.Image else Icons.Default.Audiotrack) {
            if (report.mediaType == MediaType.IMAGE) {
                ImageEvidenceViewer(report = report)
            } else {
                AudioEvidenceViewer(
                    report = report,
                    playerState = playerState,
                    onPlayPauseToggle = onPlayPauseToggle,
                    onSeekTo = onSeekTo
                )
            }
        }

        // SECTION E: DETAILED FORENSIC INDICATORS
        SectionCard(title = "SECTION E: DETAILED FORENSIC INDICATORS", icon = Icons.Default.Warning) {
            if (report.indicators.isEmpty()) {
                Text(
                    text = "No anomaly flags detected.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    report.indicators.forEach { indicator ->
                        IndicatorDetailedItem(indicator = indicator)
                    }
                }
            }
        }

        // SECTION F: TECHNICAL ANALYSIS FINDINGS
        SectionCard(title = "SECTION F: TECHNICAL ANALYSIS FINDINGS", icon = Icons.Default.Memory) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Forensic Examiner Engine: ",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = NavyBlue
                    )
                    Text(
                        text = report.modelName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (report.detailedFindings.isNotEmpty()) {
                    Text(
                        text = "Technical Findings Summary:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = NavyBlue
                    )
                    report.detailedFindings.forEach { finding ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "• ",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = GovernmentBlue
                            )
                            Text(
                                text = finding,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (report.metadataInfo.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Extracted Metadata & Technical Parameters:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = NavyBlue
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            report.metadataInfo.forEach { (key, value) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = value,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION G: RECOMMENDED NEXT STEPS
        SectionCard(title = "SECTION G: RECOMMENDED NEXT STEPS", icon = Icons.Default.Lightbulb) {
            if (report.recommendations.isEmpty()) {
                Text(
                    text = "No special actions required.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    report.recommendations.forEach { recommendation ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "• ",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GovernmentBlue
                            )
                            Text(
                                text = recommendation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // SECTION H: IMPORTANT NOTICE & LIMITATIONS
        SectionCard(title = "SECTION H: IMPORTANT NOTICE & LIMITATIONS", icon = Icons.Default.Info) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Automated Forensic Notice & Disclaimer",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "This automated forensic report is generated using spatial signal analysis, spectral artifact extraction, and advanced forensic indicators. Results serve as technical guidance and should be evaluated alongside secondary physical evidence.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Bar Buttons: Download PDF, Share Report, Back to Summary
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val pdfFile = PdfReportGenerator.generateReport(context, report)
                        PdfReportGenerator.openPdfReport(context, pdfFile)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Download PDF",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = {
                        val pdfFile = PdfReportGenerator.generateReport(context, report)
                        PdfReportGenerator.sharePdfReport(context, pdfFile)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = GovernmentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share Report",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = GovernmentBlue
                    )
                }
            }

            OutlinedButton(
                onClick = onBackToSummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Back to Quick Summary",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GovernmentBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = NavyBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
private fun ReportInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ImageEvidenceViewer(
    report: ScanReport,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        offset += offsetChange
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Interactive Visual Alteration Inspection (Pixel Grid & Lighting Analysis):",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = NavyBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .background(Color.Black)
                .transformable(state = state),
            contentAlignment = Alignment.Center
        ) {
            val displayUri = report.heatmapUri ?: report.mediaUri ?: "content://sample"
            AsyncImage(
                model = displayUri,
                contentDescription = "Image Analysis Preview",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    ),
                contentScale = ContentScale.Fit
            )

            // Zoom Control Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scale = (scale - 0.5f).coerceAtLeast(1f)
                        if (scale == 1f) offset = Offset.Zero
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOut,
                        contentDescription = "Zoom Out",
                        tint = Color.White
                    )
                }

                Text(
                    text = "${(scale * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                    onClick = { scale = (scale + 0.5f).coerceAtMost(4f) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Zoom In",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Visual Alteration Breakdown:\n• Pixel Grid Irregularities: Checked for latent spatial grid anomalies.\n• Compression Inconsistencies: Verified via Error Level Analysis (ELA).\n• Lighting & Shadow Mismatches: Evaluated subject vs. background illumination vectors.\n• EXIF Software Signatures: Inspected camera hardware vs. digital editing metadata.",
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AudioEvidenceViewer(
    report: ScanReport,
    playerState: AudioPlayerState,
    onPlayPauseToggle: () -> Unit,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Interactive Audio Timeline & Alteration Breakdown:",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = NavyBlue
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Mini Audio Player Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledIconButton(
                            onClick = onPlayPauseToggle,
                            modifier = Modifier.size(44.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = GovernmentBlue)
                        ) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = report.fileName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                            Text(
                                text = "${playerState.formattedPosition} / ${if (playerState.durationMs > 0) playerState.formattedDuration else "00:48"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    AssistChip(
                        onClick = { },
                        label = { Text("Audio Signal", style = MaterialTheme.typography.labelSmall) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = playerState.progress,
                    onValueChange = { p ->
                        val target = (p * playerState.durationMs).toLong()
                        onSeekTo(target)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = GovernmentBlue,
                        activeTrackColor = GovernmentBlue
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Audio Alteration Breakdown:\n• Synthetic Frequency Cutoffs: Checked for high-frequency bandwidth truncation.\n• Unnatural Pitch Modulation: Inspected harmonic spacing and micro-jitters.\n• Spectral Continuity Anomalies: Verified phase alignment and background ambience.\n• Audio Segment Timestamps: Specific suspicious intervals flagged below.",
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Suspicious Segments Breakdown List
        if (report.suspiciousSegments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Flagged Audio Segments & Timestamps:",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = NavyBlue
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                report.suspiciousSegments.forEach { segment ->
                    SegmentRowItem(segment = segment)
                }
            }
        }
    }
}

@Composable
private fun SegmentRowItem(segment: SuspiciousSegment) {
    val startStr = FormatUtils.formatDuration(segment.startTimeMs)
    val endStr = FormatUtils.formatDuration(segment.endTimeMs)

    val (bg, textColor) = when (segment.severity) {
        RiskLevel.RED -> Pair(RiskRedLight, RiskRed)
        RiskLevel.YELLOW -> Pair(RiskAmberLight, RiskAmber)
        else -> Pair(RiskGreenLight, RiskGreen)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$startStr - $endStr",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = textColor
            )
            Text(
                text = segment.label,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(textColor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = segment.severity.name,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}

@Composable
private fun IndicatorDetailedItem(indicator: Indicator) {
    val (chipBg, chipTextColor, labelText) = if (indicator.isSuspicious) {
        Triple(RiskRedLight, RiskRed, "Suspicious Flag")
    } else {
        Triple(RiskGreenLight, RiskGreen, "Clean Signal")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = indicator.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = indicator.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(chipBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = chipTextColor
            )
        }
    }
}

private data class DetailedStatusData(
    val bgColor: Color,
    val textColor: Color,
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

@Preview(showBackground = true)
@Composable
fun DetailedReportScreenPreview() {
    CyberVerifyTheme {
        DetailedReportScreen(
            report = ScanReport(
                id = "RPT-IMG-8F7E21",
                fileName = "deepfake_video_frame.jpg",
                mediaType = MediaType.IMAGE,
                timestamp = System.currentTimeMillis(),
                riskLevel = RiskLevel.RED,
                confidenceScore = 0.948f,
                summaryExplanation = "High-confidence synthetic media detected. Forensic examination reveals spatial latent noise artifacts and missing CMOS camera sensor profile.",
                technicalFindings = listOf("GAN Latent Spectrum anomaly detected", "Missing PRNU sensor profile"),
                recommendations = listOf("Do not share on social media"),
                indicators = listOf(
                    Indicator("Frequency Domain Artifacts", "High frequency grid pattern present", true, 96.0f)
                ),
                modelName = "CyberVerify VisionGuard Pro v2.4",
                metadataInfo = mapOf("Resolution" to "1920x1080", "Software" to "Midjourney v6")
            ),
            onBackToSummary = {}
        )
    }
}
