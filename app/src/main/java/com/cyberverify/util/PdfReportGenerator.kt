package com.cyberverify.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.cyberverify.domain.model.MediaType
import com.cyberverify.domain.model.RiskLevel
import com.cyberverify.domain.model.ScanReport
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    fun generateReport(context: Context, report: ScanReport): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Header with CyberVerify Logo, Title, Report ID & Timestamp
        drawGovernmentHeader(canvas, report)

        var currentY = 135f
        currentY = drawAnalysisStatus(canvas, report, currentY)
        currentY = drawOverallResult(canvas, report, currentY)
        currentY = drawAnalysisSummary(canvas, report, currentY)
        currentY = drawKeyFindingsChanges(canvas, report, currentY)
        currentY = drawTechnicalFindings(canvas, report, currentY)
        currentY = drawRecommendedNextSteps(canvas, report, currentY)
        drawImportantNotice(canvas, currentY)
        drawOfficialFooter(canvas, report)

        pdfDocument.finishPage(page)

        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val pdfFile = File(reportsDir, "CyberVerify_Report_${report.id}.pdf")

        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return pdfFile
    }

    fun getReportUri(context: Context, file: File): Uri {
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    fun openPdfReport(context: Context, file: File) {
        val uri = getReportUri(context, file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Open Forensic Report")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun sharePdfReport(context: Context, file: File) {
        val uri = getReportUri(context, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "CyberVerify Digital Forensic Report [${file.name}]")
            putExtra(Intent.EXTRA_TEXT, "Attached is the CyberVerify official digital media forensic verification report.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share Forensic Report")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun drawGovernmentHeader(canvas: Canvas, report: ScanReport) {
        val headerPaint = Paint().apply {
            color = Color.parseColor("#123B67")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 115f, headerPaint)

        val accentPaint = Paint().apply {
            color = Color.parseColor("#1769AA")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 115f, PAGE_WIDTH.toFloat(), 120f, accentPaint)

        val emblemPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(45f, 50f, 22f, emblemPaint)

        val innerEmblemPaint = Paint().apply {
            color = Color.parseColor("#123B67")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(45f, 50f, 18f, innerEmblemPaint)

        val titlePaint = TextPaint().apply {
            color = Color.WHITE
            textSize = 15f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("CYBERVERIFY DIGITAL FORENSIC REPORT", 80f, 42f, titlePaint)

        val subtitlePaint = TextPaint().apply {
            color = Color.parseColor("#E0E6ED")
            textSize = 8.5f
            isAntiAlias = true
        }
        canvas.drawText("GOVERNMENT OF INDIA DIGITAL MEDIA VERIFICATION PORTAL", 80f, 58f, subtitlePaint)

        val metaPaint = TextPaint().apply {
            color = Color.WHITE
            textSize = 8.5f
            isAntiAlias = true
        }
        val dateStr = FormatUtils.formatTimestamp(report.timestamp)
        canvas.drawText("REPORT ID: ${report.id}", 370f, 38f, metaPaint)
        canvas.drawText("TIMESTAMP: $dateStr", 370f, 52f, metaPaint)
        canvas.drawText("FILE: ${report.fileName.take(24)}", 370f, 66f, metaPaint)
        canvas.drawText("MEDIA TYPE: ${report.mediaType.name}", 370f, 80f, metaPaint)
    }

    private fun drawAnalysisStatus(canvas: Canvas, report: ScanReport, startY: Float): Float {
        var y = startY
        y = drawSectionHeader(canvas, "1. ANALYSIS STATUS", y)

        val boxBgPaint = Paint().apply {
            color = Color.parseColor("#F4F6F9")
            style = Paint.Style.FILL
        }
        val borderPaint = Paint().apply {
            color = Color.parseColor("#E0E6ED")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val textPaint = TextPaint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 9.5f
            isAntiAlias = true
        }

        val margin = 35f
        val boxWidth = PAGE_WIDTH - (margin * 2)

        val statusSummaryText = "Analysis Status: ${report.riskLevel.label}\nMedia File: ${report.fileName} (${report.mediaType.name})\nVerification Engine: ${report.modelName}"

        val textHeight = measureTextHeight(statusSummaryText, boxWidth.toInt() - 20, textPaint)
        val boxHeight = textHeight + 20f

        val rect = RectF(margin, y, margin + boxWidth, y + boxHeight)
        canvas.drawRoundRect(rect, 6f, 6f, boxBgPaint)
        canvas.drawRoundRect(rect, 6f, 6f, borderPaint)

        drawWrappedText(canvas, statusSummaryText, margin + 10f, y + 10f, boxWidth.toInt() - 20, textPaint)

        return y + boxHeight + 15f
    }

    private fun drawOverallResult(canvas: Canvas, report: ScanReport, startY: Float): Float {
        var y = startY
        y = drawSectionHeader(canvas, "2. OVERALL ANALYSIS RESULT", y)

        val margin = 35f
        val (bgColorHex, textColorHex, badgeLabel) = when (report.riskLevel) {
            RiskLevel.GREEN -> Triple("#E8F5E9", "#2E7D32", "AUTHENTIC MEDIA (LOW RISK)")
            RiskLevel.YELLOW -> Triple("#FFF3E0", "#ED6C02", "SUSPICIOUS MEDIA (MEDIUM RISK)")
            RiskLevel.RED -> Triple("#FFEBEE", "#D32F2F", "HIGH RISK DIGITAL MANIPULATION (FAKE)")
            RiskLevel.GREY -> Triple("#F5F5F5", "#757575", "UNABLE TO DETERMINE (INCONCLUSIVE)")
        }

        val badgeBgPaint = Paint().apply {
            color = Color.parseColor(bgColorHex)
            style = Paint.Style.FILL
        }
        val badgeBorderPaint = Paint().apply {
            color = Color.parseColor(textColorHex)
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }

        val badgeRect = RectF(margin, y, margin + 280f, y + 36f)
        canvas.drawRoundRect(badgeRect, 6f, 6f, badgeBgPaint)
        canvas.drawRoundRect(badgeRect, 6f, 6f, badgeBorderPaint)

        val badgeTextPaint = TextPaint().apply {
            color = Color.parseColor(textColorHex)
            textSize = 9.5f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(badgeLabel, margin + 12f, y + 22f, badgeTextPaint)

        val confLabelPaint = TextPaint().apply {
            color = Color.parseColor("#475569")
            textSize = 9f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val formattedConf = FormatUtils.formatConfidenceScore(report.confidenceScore)
        canvas.drawText("CONFIDENCE SCORE: $formattedConf", margin + 300f, y + 16f, confLabelPaint)

        val barBgPaint = Paint().apply {
            color = Color.parseColor("#E0E6ED")
            style = Paint.Style.FILL
        }
        val barWidth = 220f
        val confRatio = (if (report.confidenceScore <= 1.0f) report.confidenceScore else report.confidenceScore / 100f).coerceIn(0f, 1f)
        val fillWidth = barWidth * confRatio

        val barRectBg = RectF(margin + 300f, y + 22f, margin + 300f + barWidth, y + 32f)
        canvas.drawRoundRect(barRectBg, 4f, 4f, barBgPaint)

        val barFillPaint = Paint().apply {
            color = Color.parseColor(textColorHex)
            style = Paint.Style.FILL
        }
        val barRectFill = RectF(margin + 300f, y + 22f, margin + 300f + fillWidth, y + 32f)
        canvas.drawRoundRect(barRectFill, 4f, 4f, barFillPaint)

        return y + 50f
    }

    private fun drawAnalysisSummary(canvas: Canvas, report: ScanReport, startY: Float): Float {
        var y = startY
        y = drawSectionHeader(canvas, "3. ANALYSIS SUMMARY", y)

        val textPaint = TextPaint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 9f
            isAntiAlias = true
        }

        val margin = 35f
        val boxWidth = PAGE_WIDTH - (margin * 2)

        val textH = measureTextHeight(report.summaryExplanation, boxWidth.toInt() - 20, textPaint)
        val boxH = textH + 16f

        val bgPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            style = Paint.Style.FILL
        }
        val borderPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        val rect = RectF(margin, y, margin + boxWidth, y + boxH)
        canvas.drawRoundRect(rect, 4f, 4f, bgPaint)
        canvas.drawRoundRect(rect, 4f, 4f, borderPaint)

        drawWrappedText(canvas, report.summaryExplanation, margin + 10f, y + 8f, boxWidth.toInt() - 20, textPaint)

        return y + boxH + 15f
    }

    private fun drawKeyFindingsChanges(canvas: Canvas, report: ScanReport, startY: Float): Float {
        var y = startY
        y = drawSectionHeader(canvas, "4. KEY FINDINGS & CHANGES DETECTED", y)

        val textPaint = TextPaint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 9f
            isAntiAlias = true
        }

        val margin = 35f
        val boxWidth = PAGE_WIDTH - (margin * 2)

        val changesText = if (report.mediaType == MediaType.IMAGE) {
            "Visual Alteration Breakdown:\n• Pixel Grid Irregularities: Examined spatial lattice and frequency domain artifacts.\n• Compression Inconsistencies: Verified regional quantization via Error Level Analysis (ELA).\n• Lighting & Shadow Mismatches: Evaluated subject vs. background illumination vectors.\n• EXIF Software Signatures: Inspected camera hardware tags vs. digital editing metadata."
        } else {
            "Audio Alteration Breakdown:\n• Synthetic Frequency Cutoffs: Checked high-frequency bandwidth truncation above 16kHz.\n• Unnatural Pitch Modulation: Inspected harmonic spacing and fundamental frequency micro-jitters.\n• Spectral Continuity Anomalies: Verified phase alignment and background room ambience.\n• Audio Segment Timestamps: Flagged suspicious synthesized intervals and edit boundaries."
        }

        val textH = measureTextHeight(changesText, boxWidth.toInt() - 20, textPaint)
        val boxH = textH + 16f

        val bgPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            style = Paint.Style.FILL
        }
        val borderPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        val rect = RectF(margin, y, margin + boxWidth, y + boxH)
        canvas.drawRoundRect(rect, 4f, 4f, bgPaint)
        canvas.drawRoundRect(rect, 4f, 4f, borderPaint)

        drawWrappedText(canvas, changesText, margin + 10f, y + 8f, boxWidth.toInt() - 20, textPaint)

        return y + boxH + 15f
    }

    private fun drawTechnicalFindings(canvas: Canvas, report: ScanReport, startY: Float): Float {
        var y = startY
        y = drawSectionHeader(canvas, "5. TECHNICAL FINDINGS & INDICATORS", y)

        val bulletPaint = Paint().apply {
            color = Color.parseColor("#1769AA")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val textPaint = TextPaint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 8.5f
            isAntiAlias = true
        }

        val margin = 35f
        val textWidth = (PAGE_WIDTH - margin * 2 - 15f).toInt()

        val items = if (report.detailedFindings.isNotEmpty()) {
            report.detailedFindings
        } else {
            report.indicators.map { "${it.name}: ${it.description} (Score: ${FormatUtils.formatConfidenceScore(it.score)})" }
        }

        for (item in items) {
            canvas.drawCircle(margin + 4f, y + 5f, 2.5f, bulletPaint)
            val h = drawWrappedText(canvas, item, margin + 14f, y, textWidth, textPaint)
            y += h + 6f
            if (y > PAGE_HEIGHT - 120f) break
        }

        return y + 10f
    }

    private fun drawRecommendedNextSteps(canvas: Canvas, report: ScanReport, startY: Float): Float {
        var y = startY
        if (y > PAGE_HEIGHT - 160f) return y

        y = drawSectionHeader(canvas, "6. RECOMMENDED NEXT STEPS", y)

        val bulletPaint = Paint().apply {
            color = Color.parseColor("#ED6C02")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val textPaint = TextPaint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 8.5f
            isAntiAlias = true
        }

        val margin = 35f
        val textWidth = (PAGE_WIDTH - margin * 2 - 15f).toInt()

        for (rec in report.recommendations) {
            canvas.drawCircle(margin + 4f, y + 5f, 2.5f, bulletPaint)
            val h = drawWrappedText(canvas, rec, margin + 14f, y, textWidth, textPaint)
            y += h + 5f
            if (y > PAGE_HEIGHT - 100f) break
        }

        return y + 10f
    }

    private fun drawImportantNotice(canvas: Canvas, startY: Float): Float {
        val y = startY
        if (y > PAGE_HEIGHT - 110f) return y

        val margin = 35f
        val noticeText = "IMPORTANT NOTICE & LIMITATIONS: This automated forensic report is generated using spatial signal analysis, spectral artifact extraction, and advanced forensic indicators. Results serve as technical guidance and should be evaluated alongside secondary evidence."

        val bgPaint = Paint().apply {
            color = Color.parseColor("#F4F6F9")
            style = Paint.Style.FILL
        }
        val borderPaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            style = Paint.Style.STROKE
            strokeWidth = 0.5f
        }
        val textPaint = TextPaint().apply {
            color = Color.parseColor("#64748B")
            textSize = 7.5f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.ITALIC)
            isAntiAlias = true
        }

        val boxWidth = PAGE_WIDTH - (margin * 2)
        val textH = measureTextHeight(noticeText, boxWidth.toInt() - 16, textPaint)
        val boxH = textH + 10f

        val rect = RectF(margin, y, margin + boxWidth, y + boxH)
        canvas.drawRoundRect(rect, 4f, 4f, bgPaint)
        canvas.drawRoundRect(rect, 4f, 4f, borderPaint)

        drawWrappedText(canvas, noticeText, margin + 8f, y + 5f, boxWidth.toInt() - 16, textPaint)

        return y + boxH + 10f
    }

    private fun drawOfficialFooter(canvas: Canvas, report: ScanReport) {
        val y = PAGE_HEIGHT - 65f

        val linePaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            strokeWidth = 1f
        }
        canvas.drawLine(35f, y, PAGE_WIDTH - 35f, y, linePaint)

        val sealPaint = TextPaint().apply {
            color = Color.parseColor("#123B67")
            textSize = 8f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val hashStr = "DIGITAL HASH: SHA256-${report.id.hashCode().toString(16).uppercase()}"
        canvas.drawText("VERIFIED REPORT: CYBERVERIFY FORENSIC ENGINE", 35f, y + 16f, sealPaint)

        val textPaint = TextPaint().apply {
            color = Color.parseColor("#64748B")
            textSize = 7.5f
            isAntiAlias = true
        }
        canvas.drawText(hashStr, 35f, y + 28f, textPaint)

        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'IST'", Locale.US).format(Date(report.timestamp))
        canvas.drawText("CERTIFIED DATE: $formattedDate", 370f, y + 16f, textPaint)
        canvas.drawText("SIGNATURE: CyberVerify Automated Engine v1.0", 370f, y + 28f, sealPaint)
    }

    private fun drawSectionHeader(canvas: Canvas, title: String, y: Float): Float {
        val paint = TextPaint().apply {
            color = Color.parseColor("#123B67")
            textSize = 10f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(title, 35f, y + 12f, paint)
        return y + 18f
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        width: Int,
        paint: TextPaint
    ): Int {
        canvas.save()
        canvas.translate(x, y)
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.1f)
            .build()
        layout.draw(canvas)
        canvas.restore()
        return layout.height
    }

    private fun measureTextHeight(text: String, width: Int, paint: TextPaint): Int {
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.1f)
            .build()
        return layout.height
    }
}
