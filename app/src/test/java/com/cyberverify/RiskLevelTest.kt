package com.cyberverify

import com.cyberverify.domain.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class RiskLevelTest {

    @Test
    fun `fromString parses valid risk levels correctly`() {
        assertEquals(RiskLevel.GREEN, RiskLevel.fromString("GREEN"))
        assertEquals(RiskLevel.YELLOW, RiskLevel.fromString("YELLOW"))
        assertEquals(RiskLevel.RED, RiskLevel.fromString("RED"))
        assertEquals(RiskLevel.GREY, RiskLevel.fromString("GREY"))
    }

    @Test
    fun `fromString maps unknown or inconclusive strings to GREY`() {
        assertEquals(RiskLevel.GREY, RiskLevel.fromString("unknown"))
        assertEquals(RiskLevel.GREY, RiskLevel.fromString("inconclusive"))
        assertEquals(RiskLevel.GREY, RiskLevel.fromString("random_invalid_string"))
        assertEquals(RiskLevel.GREY, RiskLevel.fromString(null))
        assertEquals(RiskLevel.GREY, RiskLevel.fromString(""))
    }

    @Test
    fun `RiskLevel attributes match design specifications`() {
        assertEquals("Unable to determine", RiskLevel.GREY.label)
        assertEquals("#757575", RiskLevel.GREY.hexColor)
        assertEquals("#2E7D32", RiskLevel.GREEN.hexColor)
        assertEquals("#ED6C02", RiskLevel.YELLOW.hexColor)
        assertEquals("#D32F2F", RiskLevel.RED.hexColor)
    }
}
