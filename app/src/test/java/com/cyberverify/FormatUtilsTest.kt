package com.cyberverify

import com.cyberverify.util.FormatUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatUtilsTest {

    @Test
    fun `formatFileSize formats bytes correctly`() {
        assertEquals("0 B", FormatUtils.formatFileSize(0))
        assertEquals("500 B", FormatUtils.formatFileSize(500))
        assertEquals("1.0 KB", FormatUtils.formatFileSize(1024))
        assertEquals("2.5 MB", FormatUtils.formatFileSize((2.5 * 1024 * 1024).toLong()))
    }

    @Test
    fun `formatDuration formats milliseconds correctly`() {
        assertEquals("00:00", FormatUtils.formatDuration(0))
        assertEquals("00:45", FormatUtils.formatDuration(45_000))
        assertEquals("02:15", FormatUtils.formatDuration(135_000))
        assertEquals("01:02:15", FormatUtils.formatDuration(3_735_000))
    }
}
