package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spread_picks",
    indices = [
        Index(value = ["gameId", "playerId"], unique = true),
        Index(value = ["weekNumber"])
    ]
)
data class SpreadPick(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long,
    val playerId: Long,
    val weekNumber: Int,
    val pickedTeamShort: String, // e.g. "KC" or "BAL"
    val timestamp: Long = System.currentTimeMillis(),
    val firstPickTeamShort: String = pickedTeamShort // Stored first pick: highlighted if changed
) {
    val isChangedFromFirstPick: Boolean
        get() = firstPickTeamShort.isNotBlank() && !firstPickTeamShort.equals(pickedTeamShort, ignoreCase = true)
}
