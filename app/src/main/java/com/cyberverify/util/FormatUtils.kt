package com.cyberverify.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
        val size = bytes / Math.pow(1024.0, digitGroups.toDouble())
        return if (digitGroups == 0) {
            "$bytes B"
        } else {
            String.format(Locale.US, "%.1f %s", size, units[digitGroups])
        }
    }

    fun formatDuration(ms: Long): String {
        val secondsTotal = (ms / 1000).coerceAtLeast(0)
        val minutes = secondsTotal / 60
        val seconds = secondsTotal % 60
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, remainingMinutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", remainingMinutes, seconds)
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        if (timestamp <= 0) return "N/A"
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
        return sdf.format(Date(timestamp))
    }

    fun formatConfidenceScore(score: Float): String {
        val percentage = if (score <= 1.0f) score * 100f else score
        return String.format(Locale.US, "%.1f%%", percentage)
    }

    fun parseMarkdownToAnnotatedString(
        text: String,
        defaultColor: Color,
        boldColor: Color = defaultColor
    ): AnnotatedString {
        return buildAnnotatedString {
            val regex = Regex("(\\*\\*[^*]+\\*\\*)|(`[^`]+`)|([^*`]+)")
            val matches = regex.findAll(text)
            for (match in matches) {
                val group = match.value
                when {
                    group.startsWith("**") && group.endsWith("**") -> {
                        val inner = group.removeSurrounding("**")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = boldColor)) {
                            append(inner)
                        }
                    }
                    group.startsWith("`") && group.endsWith("`") -> {
                        val inner = group.removeSurrounding("`")
                        withStyle(SpanStyle(fontFamily = FontFamily.Monospace, color = boldColor)) {
                            append(inner)
                        }
                    }
                    else -> {
                        withStyle(SpanStyle(color = defaultColor)) {
                            append(group)
                        }
                    }
                }
            }
        }
    }
}
