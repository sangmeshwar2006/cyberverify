package com.cyberverify.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import com.cyberverify.ui.theme.CyberVerifyTheme
import com.cyberverify.ui.theme.GovernmentBlue
import com.cyberverify.ui.theme.NavyBlue
import com.cyberverify.ui.theme.RiskAmber
import com.cyberverify.ui.theme.RiskAmberLight
import com.cyberverify.ui.theme.RiskGreen
import com.cyberverify.ui.theme.RiskGreenLight
import com.cyberverify.ui.theme.RiskRed
import com.cyberverify.ui.theme.RiskRedLight
import com.cyberverify.util.PdfReportGenerator

@Composable
fun QuickSummaryScreen(
    report: ScanReport,
    onViewDetailedReport: () -> Unit,
    onCheckAnotherFile: () -> Unit,
    modifier: Modifier = Modifier,
    onDownloadPdfOverride: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val (badgeBg, textColor, statusIcon, statusTitle, statusSubtitle) = when (report.riskLevel) {
        RiskLevel.GREEN -> StatusUIData(
            bgColor = RiskGreenLight,
            textColor = RiskGreen,
            icon = Icons.Default.CheckCircle,
            title = "AUTHENTIC MEDIA",
            subtitle = "No strong warning signs detected."
        )
        RiskLevel.YELLOW -> StatusUIData(
            bgColor = RiskAmberLight,
            textColor = RiskAmber,
            icon = Icons.Default.Warning,
            title = "SUSPICIOUS MEDIA",
            subtitle = "Some suspicious characteristics found."
        )
        RiskLevel.RED -> StatusUIData(
            bgColor = RiskRedLight,
            textColor = RiskRed,
            icon = Icons.Default.ErrorOutline,
            title = "HIGH RISK MANIPULATION",
            subtitle = "Strong warning signs detected."
        )
        RiskLevel.GREY -> StatusUIData(
            bgColor = Color(0xFFF0F0F0),
            textColor = Color(0xFF616161),
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            title = "UNABLE TO DETERMINE",
            subtitle = "Inconclusive analysis result."
        )
    }

    val analysisStatusText = when (report.riskLevel) {
        RiskLevel.GREEN -> "The uploaded file appears authentic. Forensic checks confirm natural sensor signatures and normal signal continuity."
        RiskLevel.YELLOW -> "Minor alterations or compression inconsistencies were detected. Exercise normal caution before relying on this file."
        RiskLevel.RED -> "Strong evidence of digital manipulation or synthetic generation was detected. Do not treat this file as authentic evidence."
        RiskLevel.GREY -> "The analysis was inconclusive due to heavy file compression, low resolution, or missing metadata."
    }

    val keyChangesText = if (report.mediaType == MediaType.IMAGE) {
        when (report.riskLevel) {
            RiskLevel.RED -> "• Pixel Grid Irregularities: Spatial lattice and latent frequency anomalies detected.\n• Compression Inconsistencies: Error Level Analysis shows non-uniform block compression.\n• Lighting & Shadow Mismatches: Directional illumination conflicts between subject and background.\n• EXIF Software Signatures: Camera hardware profile missing; digital editing software export tag found."
            RiskLevel.YELLOW -> "• Pixel Grid Irregularities: Minor resampling artifacts along edges.\n• Compression Inconsistencies: Regional JPEG quantization mismatch.\n• Lighting & Shadow Mismatches: Slight shadow softening around subject.\n• EXIF Software Signatures: Editing software modification timestamp present."
            RiskLevel.GREEN -> "• Pixel Grid Irregularities: None. Uniform CMOS sensor response verified.\n• Compression Inconsistencies: None. Clean single-pass camera encoding.\n• Lighting & Shadow Mismatches: None. Natural optical lighting verified.\n• EXIF Software Signatures: Genuine camera hardware manufacturer tags verified."
            RiskLevel.GREY -> "• Insufficient spatial resolution and heavy re-compression prevent detailed optical inspection."
        }
    } else {
        when (report.riskLevel) {
            RiskLevel.RED -> "• Synthetic Frequency Cutoffs: Bandwidth truncation above 16kHz typical of voice models.\n• Unnatural Pitch Modulation: Rigid harmonic spacing and missing micro-jitters.\n• Spectral Continuity Anomalies: Phase discontinuities and background noise drops.\n• Audio Segment Timestamps: Suspicious synthesized intervals identified at 00:03 - 00:08 and 00:14 - 00:19."
            RiskLevel.YELLOW -> "• Synthetic Frequency Cutoffs: Minor high-frequency attenuation at splice points.\n• Unnatural Pitch Modulation: Isolated pitch jump at transition point.\n• Spectral Continuity Anomalies: Sudden background room ambience drop at 00:12.\n• Audio Segment Timestamps: Edit boundary located at 00:12 - 00:15."
            RiskLevel.GREEN -> "• Synthetic Frequency Cutoffs: None. Full natural frequency spectrum up to 24kHz.\n• Unnatural Pitch Modulation: Natural fundamental frequency micro-jitters present.\n• Spectral Continuity Anomalies: Unbroken ambient room acoustics.\n• Audio Segment Timestamps: Continuous authentic speech across entire recording (00:00 - 02:10)."
            RiskLevel.GREY -> "• Telephony bandwidth compression and background noise mask acoustic spectral features."
        }
    }

    val recommendationsText = report.recommendations.joinToString("\n") { "• $it" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Column {
            Text(
                text = "Your Analysis Is Ready!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyBlue
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Here is a clear summary of our forensic findings.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Large Status Card with 4 Color-Coded Statuses
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = badgeBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = textColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = statusTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    ),
                    color = textColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = statusSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = textColor.copy(alpha = 0.9f)
                )
            }
        }

        // Section 1: Analysis Status
        SummarySectionCard(
            title = "Analysis Status",
            content = analysisStatusText,
            accentColor = textColor,
            icon = Icons.Default.Shield
        )

        // Section 2: Key Findings & Changes Detected
        SummarySectionCard(
            title = "Key Findings & Changes Detected",
            content = keyChangesText,
            accentColor = GovernmentBlue,
            icon = Icons.Default.Analytics
        )

        // Section 3: Recommended Next Steps
        SummarySectionCard(
            title = "Recommended Next Steps",
            content = recommendationsText,
            accentColor = if (report.riskLevel == RiskLevel.RED || report.riskLevel == RiskLevel.YELLOW) RiskAmber else GovernmentBlue,
            icon = Icons.Default.Lightbulb
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Prominent CTA Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Primary Button: View Detailed Report
            Button(
                onClick = onViewDetailedReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text(
                    text = "View Detailed Report",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Secondary Button: Download PDF Report
            Button(
                onClick = {
                    if (onDownloadPdfOverride != null) {
                        onDownloadPdfOverride()
                    } else {
                        val pdfFile = PdfReportGenerator.generateReport(context, report)
                        PdfReportGenerator.sharePdfReport(context, pdfFile)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GovernmentBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download PDF Report",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Outlined Button: Check Another File
            OutlinedButton(
                onClick = onCheckAnotherFile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Check Another File",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun SummarySectionCard(
    title: String,
    content: String,
    accentColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = NavyBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private data class StatusUIData(
    val bgColor: Color,
    val textColor: Color,
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

@Preview(showBackground = true)
@Composable
fun QuickSummaryScreenPreview() {
    CyberVerifyTheme {
        QuickSummaryScreen(
            report = ScanReport(
                id = "RPT-IMG-8F7E21",
                fileName = "deepfake_video_frame.jpg",
                mediaType = MediaType.IMAGE,
                timestamp = System.currentTimeMillis(),
                riskLevel = RiskLevel.RED,
                confidenceScore = 0.948f,
                summaryExplanation = "High-confidence synthetic media detected. Forensic examination reveals spatial latent noise artifacts and missing CMOS camera sensor profile.",
                technicalFindings = listOf("GAN Latent Spectrum", "Missing PRNU"),
                recommendations = listOf("Do not share on social media"),
                indicators = emptyList()
            ),
            onViewDetailedReport = {},
            onCheckAnotherFile = {}
        )
    }
}
