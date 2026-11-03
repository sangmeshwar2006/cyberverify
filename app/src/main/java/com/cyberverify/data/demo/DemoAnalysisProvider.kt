package com.cyberverify.data.demo

import com.cyberverify.domain.model.Indicator
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import com.cyberverify.domain.model.SuspiciousSegment
import kotlinx.coroutines.delay
import java.util.UUID

object DemoAnalysisProvider {

    suspend fun generateAnalysis(
        fileName: String,
        mediaType: MediaType,
        mediaUri: String? = null,
        simulateDelayMs: Long = 1200L
    ): ScanReport {
        if (simulateDelayMs > 0) {
            delay(simulateDelayMs)
        }

        val nameLower = fileName.lowercase()
        val isGrey = nameLower.contains("grey") || nameLower.contains("gray") ||
                nameLower.contains("unknown") || nameLower.contains("inconclusive") ||
                nameLower.contains("blur") || nameLower.contains("corrupt")
        val isRed = nameLower.contains("fake") || nameLower.contains("ai") ||
                nameLower.contains("deepfake") || nameLower.contains("synth") ||
                nameLower.contains("clone") || nameLower.contains("gen") ||
                nameLower.contains("manipulated")
        val isYellow = nameLower.contains("suspicious") || nameLower.contains("edit") ||
                nameLower.contains("mod") || nameLower.contains("tamper") ||
                nameLower.contains("sample")

        val riskLevel = when {
            isGrey -> RiskLevel.GREY
            isRed -> RiskLevel.RED
            isYellow -> RiskLevel.YELLOW
            else -> RiskLevel.GREEN
        }

        return when (mediaType) {
            MediaType.IMAGE -> generateImageReport(fileName, mediaUri, riskLevel)
            MediaType.AUDIO -> generateAudioReport(fileName, mediaUri, riskLevel)
        }
    }

    private fun generateImageReport(
        fileName: String,
        mediaUri: String?,
        riskLevel: RiskLevel
    ): ScanReport {
        val reportId = "RPT-IMG-" + UUID.nameUUIDFromBytes(fileName.toByteArray()).toString().take(8).uppercase()
        val timestamp = System.currentTimeMillis()

        return when (riskLevel) {
            RiskLevel.RED -> ScanReport(
                id = reportId,
                fileName = fileName,
                mediaType = MediaType.IMAGE,
                timestamp = timestamp,
                riskLevel = RiskLevel.RED,
                confidenceScore = 0.948f,
                summaryExplanation = "High-confidence manipulated image detected. Forensic examination reveals pixel grid irregularities, compression inconsistencies, lighting and shadow mismatches, and synthetic EXIF software signatures.",
                technicalFindings = listOf(
                    "Pixel Grid Irregularities: High-frequency spatial lattice and latent grid anomalies inconsistent with natural optical sensor capture.",
                    "Compression Inconsistencies: Quantization noise and Error Level Analysis (ELA) show non-uniform block compression mixing.",
                    "Lighting & Shadow Mismatches: Directional illumination vector conflicts between subject contours and background ambient light source.",
                    "EXIF Software Signatures: Missing physical camera sensor manufacturer tags; software metadata tag indicates digital canvas or AI generator export."
                ),
                recommendations = listOf(
                    "Do NOT trust or disseminate this image as authentic evidence.",
                    "Cross-verify source provenance with official government press releases or verified news outlets.",
                    "Report suspicious manipulated media to cybercrime.gov.in."
                ),
                indicators = listOf(
                    Indicator("Pixel Grid Irregularities", "Spatial latent grid and frequency domain anomalies detected", true, 96.2f),
                    Indicator("EXIF Software Signatures", "Camera hardware profile missing; digital export software signature found", true, 91.0f),
                    Indicator("Lighting & Shadow Mismatches", "Illumination vectors inconsistent across subject and background", true, 94.5f),
                    Indicator("Compression Inconsistencies", "Error Level Analysis reveals multi-pass block mismatch", true, 88.0f)
                ),
                heatmapUri = mediaUri,
                mediaUri = mediaUri,
                modelName = "CyberVerify VisionGuard Pro v2.4 (Diffusion & Pattern Detector)",
                metadataInfo = mapOf(
                    "File Name" to fileName,
                    "Dimensions" to "2048 x 2048 px",
                    "Format" to "JPEG Image",
                    "Color Space" to "sRGB (Uncalibrated)",
                    "EXIF Camera Model" to "Not Present (Digital Canvas)",
                    "Software" to "Digital Canvas / AI Generator Export"
                )
            )
            RiskLevel.YELLOW -> ScanReport(
                id = reportId,
                fileName = fileName,
                mediaType = MediaType.IMAGE,
                timestamp = timestamp,
                riskLevel = RiskLevel.YELLOW,
                confidenceScore = 0.734f,
                summaryExplanation = "Potential media alteration detected. Analysis indicates localized digital retouching, minor compression inconsistencies, and editing software modification tags.",
                technicalFindings = listOf(
                    "Compression Inconsistencies: Localized Error Level Analysis (ELA) discrepancy detected in regional JPEG compression blocks.",
                    "Lighting & Shadow Mismatches: Minor shadow softening around modified subject region.",
                    "EXIF Software Signatures: File contains editing software modification timestamp.",
                    "Pixel Grid Irregularities: Minor resampling artifacts along resized edges."
                ),
                recommendations = listOf(
                    "Exercise caution before relying on or sharing this image.",
                    "Request original uncompressed raw file or direct camera export for verification."
                ),
                indicators = listOf(
                    Indicator("Compression Inconsistencies", "Non-uniform compression levels across image blocks", true, 76.5f),
                    Indicator("EXIF Software Signatures", "Software modification timestamp detected in EXIF data", true, 72.0f),
                    Indicator("Pixel Grid Irregularities", "Minor resampling detected along image boundaries", true, 61.0f),
                    Indicator("Sensor Noise Consistency", "Sensor noise floor mostly consistent across canvas", false, 35.0f)
                ),
                heatmapUri = mediaUri,
                mediaUri = mediaUri,
                modelName = "CyberVerify VisionGuard Pro v2.4 (Diffusion & GAN Detector)",
                metadataInfo = mapOf(
                    "File Name" to fileName,
                    "Dimensions" to "1920 x 1080 px",
                    "Format" to "JPEG Image",
                    "Color Space" to "sRGB",
                    "EXIF Camera Model" to "Samsung Galaxy S23",
                    "Software" to "Adobe Photoshop 2024"
                )
            )
            RiskLevel.GREEN -> ScanReport(
                id = reportId,
                fileName = fileName,
                mediaType = MediaType.IMAGE,
                timestamp = timestamp,
                riskLevel = RiskLevel.GREEN,
                confidenceScore = 0.965f,
                summaryExplanation = "Authentic physical photograph verified. Forensic analysis confirms genuine optical sensor noise, uniform pixel grid, consistent lighting, and valid hardware EXIF signatures.",
                technicalFindings = listOf(
                    "Pixel Grid Irregularities: None. Uniform CMOS sensor pixel response verified across entire canvas.",
                    "Compression Inconsistencies: None. Consistent single-pass camera encoding detected.",
                    "Lighting & Shadow Mismatches: None. Natural optical lighting and shadow falloff verified.",
                    "EXIF Software Signatures: Genuine camera hardware manufacturer and optical parameters (ISO 200, f/2.0) verified."
                ),
                recommendations = listOf(
                    "Media appears authentic and safe for verification records.",
                    "Store hash digest for future tamper evidence tracking."
                ),
                indicators = listOf(
                    Indicator("Pixel Grid Uniformity", "Natural CMOS sensor pixel response verified", false, 12.0f),
                    Indicator("Lighting & Shadow Consistency", "Optical illumination matches physical environment", false, 8.5f),
                    Indicator("EXIF Software Signatures", "Genuine camera firmware signature and timestamp verified", false, 5.0f),
                    Indicator("Compression Consistency", "Single-pass camera encoding verified", false, 4.0f)
                ),
                heatmapUri = mediaUri,
                mediaUri = mediaUri,
                modelName = "CyberVerify VisionGuard Pro v2.4 (Diffusion & GAN Detector)",
                metadataInfo = mapOf(
                    "File Name" to fileName,
                    "Dimensions" to "4032 x 3024 px",
                    "Format" to "JPEG Image",
                    "Color Space" to "Display P3",
                    "EXIF Camera Model" to "Apple iPhone 15 Pro",
                    "Software" to "iOS 17.4 Camera App"
                )
            )
            RiskLevel.GREY -> ScanReport(
                id = reportId,
                fileName = fileName,
                mediaType = MediaType.IMAGE,
                timestamp = timestamp,
                riskLevel = RiskLevel.GREY,
                confidenceScore = 0.420f,
                summaryExplanation = "Unable to determine authenticity. Input media exhibits severe compression, low resolution, or conflicting forensic indicators preventing conclusive analysis.",
                technicalFindings = listOf(
                    "Pixel Grid Irregularities: Unresolvable due to heavy pixelation and blur.",
                    "Compression Inconsistencies: Multi-pass re-compression artifacts obscure quantization noise floor.",
                    "Lighting & Shadow Mismatches: Ambiguous illumination due to low contrast.",
                    "EXIF Software Signatures: Missing due to messaging app metadata stripping."
                ),
                recommendations = listOf(
                    "Obtain a higher-resolution or uncompressed original file for definitive verification.",
                    "Do not rely solely on automated results for low-quality or heavily compressed media."
                ),
                indicators = listOf(
                    Indicator("Resolution Adequacy", "Resolution below optimal forensic analysis threshold", true, 48.0f),
                    Indicator("Compression Grid Integrity", "Multi-pass re-compression artifacts obscure noise floor", true, 45.0f)
                ),
                heatmapUri = mediaUri,
                mediaUri = mediaUri,
                modelName = "CyberVerify VisionGuard Pro v2.4 (Diffusion & GAN Detector)",
                metadataInfo = mapOf(
                    "File Name" to fileName,
                    "Dimensions" to "480 x 360 px",
                    "Format" to "WEBP Image",
                    "Status" to "Inconclusive / Heavy Compression"
                )
            )
        }
    }

    private fun generateAudioReport(
        fileName: String,
        mediaUri: String?,
        riskLevel: RiskLevel
    ): ScanReport {
        val reportId = "RPT-AUD-" + UUID.nameUUIDFromBytes(fileName.toByteArray()).toString().take(8).uppercase()
        val timestamp = System.currentTimeMillis()

        return when (riskLevel) {
            RiskLevel.RED -> ScanReport(
                id = reportId,
                fileName = fileName,
                mediaType = MediaType.AUDIO,
                timestamp = timestamp,
                riskLevel = RiskLevel.RED,
                confidenceScore = 0.952f,
                summaryExplanation = "Voice clone / speech synthesis detected. Spectrographic analysis confirms synthetic frequency cutoffs, unnatural pitch modulation, spectral continuity anomalies, and specific flagged audio segments.",
                technicalFindings = listOf(
                    "Synthetic Frequency Cutoffs: Sharp high-frequency rolloff and bandwidth truncation above 16kHz typical of neural text-to-speech models.",
                    "Unnatural Pitch Modulation: Rigid harmonic alignment and lack of physiological fundamental frequency (F0) micro-jitters.",
                    "Spectral Continuity Anomalies: Phase inconsistencies and abrupt background noise drops between phonetic transitions.",
                    "Audio Segment Timestamps: Flagged synthesized voice segments identified at 00:03 - 00:08 and 00:14 - 00:19."
                ),
                recommendations = listOf(
                    "DO NOT comply with financial or sensitive instructions received in this voice recording.",
                    "Verify caller identity via out-of-band communication (official phone landline).",
                    "Report voice cloning financial scams to National Cyber Crime Reporting Portal (cybercrime.gov.in)."
                ),
                indicators = listOf(
                    Indicator("Synthetic Frequency Cutoffs", "Sharp bandwidth truncation above 16kHz detected", true, 97.0f),
                    Indicator("Unnatural Pitch Modulation", "Rigid harmonic spacing and missing micro-jitters", true, 93.5f),
                    Indicator("Spectral Continuity Anomalies", "Phase discontinuities and abrupt noise floor shifts", true, 91.2f),
                    Indicator("Suspicious Segment Timestamps", "Multiple synthesized vocal intervals located", true, 95.0f)
                ),
                heatmapUri = mediaUri,
                mediaUri = mediaUri,
                modelName = "CyberVerify AcousticTrace Net v3.1 (Voice Verification Detector)",
                metadataInfo = mapOf(
                    "File Name" to fileName,
                    "Duration" to "00:48",
                    "Channels" to "1 (Mono)",
                    "Sample Rate" to "24000 Hz",
                    "Bitrate" to "128 kbps",
                    "Codec" to "AAC LC"
                ),
                suspiciousSegments = listOf(
                    SuspiciousSegment(3200L, 8500L, "Synthetic Voice Segment (00:03 - 00:08)", RiskLevel.RED),
                    SuspiciousSegment(14200L, 19800L, "Pitch Modulation Anomaly (00:14 - 00:19)", RiskLevel.RED)
                )
            )
            RiskLevel.YELLOW -> ScanReport(
                id = reportId,
                fileName = fileName,
                mediaType = MediaType.AUDIO,
                timestamp = timestamp,
                riskLevel = RiskLevel.YELLOW,
                confidenceScore = 0.710f,
                summaryExplanation = "Audio editing / splicing suspected. Spectrographic analysis reveals localized spectral continuity anomalies and splice boundary phase shifts.",
                technicalFindings = listOf(
                    "Synthetic Frequency Cutoffs: Minor high-frequency attenuation at splice joints.",
                    "Unnatural Pitch Modulation: Normal vocal tract with isolated unnatural pitch jump at transition point.",
                    "Spectral Continuity Anomalies: Sudden background room ambience drop at 00:12.",
                    "Audio Segment Timestamps: Possible edit cut located at 00:12 - 00:15."
                ),
                recommendations = listOf(
                    "Re-verify context of recording with original source speaker.",
                    "Check for missing audio cuts or unexpected pauses in dialogue."
                ),
                indicators = listOf(
                    Indicator("Spectral Continuity Anomalies", "Phase shift and background drop detected at transition", true, 74.0f),
                    Indicator("Audio Segment Timestamps", "Edit boundary located at 00:12 - 00:15", true, 68.0f),
                    Indicator("Vocal Acoustics", "Vocal tract length and formant resonance appear human", false, 28.0f)
                ),
                heatmapUri = mediaUri,
                mediaUri = mediaUri,
                modelName = "CyberVerify AcousticTrace Net v3.1 (Voice Verification Detector)",
                metadataInfo = mapOf(
                    "File Name" to fileName,
                    "Duration" to "01:15",
                    "Channels" to "2 (Stereo)",
                    "Sample Rate" to "44100 Hz",
                    "Bitrate" to "256 kbps",
                    "Codec" to "MP3"
                ),
                suspiciousSegments = listOf(
                    SuspiciousSegment(12000L, 15500L, "Edit Cut / Ambient Shift (00:12 - 00:15)", RiskLevel.YELLOW)
                )
            )
            RiskLevel.GREEN -> ScanReport(
                id = reportId,
                fileName = fileName,
                mediaType = MediaType.AUDIO,
                timestamp = timestamp,
                riskLevel = RiskLevel.GREEN,
                confidenceScore = 0.971f,
                summaryExplanation = "Authentic human acoustic voice verified. Spectrogram exhibits continuous spectral continuity, natural pitch micro-modulation, full frequency bandwidth, and genuine vocal micro-tremors.",
                technicalFindings = listOf(
                    "Synthetic Frequency Cutoffs: None. Full natural frequency spectrum up to 24kHz verified.",
                    "Unnatural Pitch Modulation: None. Natural fundamental frequency (F0) micro-jitters and glottal pulses present.",
                    "Spectral Continuity Anomalies: None. Unbroken ambient room acoustics throughout recording.",
                    "Audio Segment Timestamps: Continuous authentic speech across entire recording duration (00:00 - 02:10)."
                ),
                recommendations = listOf(
                    "Audio recording verified authentic.",
                    "Safe for verification and administrative archives."
                ),
                indicators = listOf(
                    Indicator("Unnatural Pitch Modulation", "Natural pitch micro-jitters and glottal pulses present", false, 6.0f),
                    Indicator("Spectral Continuity Anomalies", "Continuous ambient acoustics and phase alignment", false, 4.0f),
                    Indicator("Synthetic Frequency Cutoffs", "Full natural frequency bandwidth verified", false, 3.0f)
                ),
                heatmapUri = mediaUri,
                mediaUri = mediaUri,
                modelName = "CyberVerify AcousticTrace Net v3.1 (Voice Verification Detector)",
                metadataInfo = mapOf(
                    "File Name" to fileName,
                    "Duration" to "02:10",
                    "Channels" to "1 (Mono)",
                    "Sample Rate" to "48000 Hz",
                    "Bitrate" to "320 kbps",
                    "Codec" to "PCM WAV"
                )
            )
            RiskLevel.GREY -> ScanReport(
                id = reportId,
                fileName = fileName,
                mediaType = MediaType.AUDIO,
                timestamp = timestamp,
                riskLevel = RiskLevel.GREY,
                confidenceScore = 0.380f,
                summaryExplanation = "Unable to determine voice authenticity. Extreme background noise, low audio sampling rate, or heavy GSM telephony compression prevents conclusive spectral analysis.",
                technicalFindings = listOf(
                    "Synthetic Frequency Cutoffs: Telephony bandwidth (8kHz) truncates high frequencies.",
                    "Unnatural Pitch Modulation: Unresolvable due to high noise floor.",
                    "Spectral Continuity Anomalies: Masked by cellular transmission compression.",
                    "Audio Segment Timestamps: Inconclusive across entire duration."
                ),
                recommendations = listOf(
                    "Provide a clean, uncompressed microphone recording for full spectral evaluation.",
                    "Avoid using cellular phone call recordings if high-precision voice verification is required."
                ),
                indicators = listOf(
                    Indicator("Acoustic Bandwidth Floor", "Audio sample rate insufficient for deep vocal analysis", true, 42.0f)
                ),
                heatmapUri = mediaUri,
                mediaUri = mediaUri,
                modelName = "CyberVerify AcousticTrace Net v3.1 (Voice Verification Detector)",
                metadataInfo = mapOf(
                    "File Name" to fileName,
                    "Duration" to "00:22",
                    "Channels" to "1 (Mono)",
                    "Sample Rate" to "8000 Hz (GSM Telephony)",
                    "Status" to "Inconclusive / Low Bandwidth"
                )
            )
        }
    }
}
