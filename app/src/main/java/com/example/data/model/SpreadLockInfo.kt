package com.example.data.model

data class WeekSpreadLockStatus(
    val weekNumber: Int,
    val isLocked: Boolean,
    val lockDeadlineDescription: String,
    val firstGameTime: String,
    val sportsbook: String = "DraftKings"
)
