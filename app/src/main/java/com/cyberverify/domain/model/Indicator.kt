package com.cyberverify.domain.model

data class Indicator(
    val name: String,
    val description: String,
    val isSuspicious: Boolean,
    val score: Float
)
