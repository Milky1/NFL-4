package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val avatarColorHex: Long = 0xFF00E676,
    val avatarEmoji: String = "C",
    val orderIndex: Int = 0
) {
    val symbol: String
        get() = when (name.trim().lowercase()) {
            "clive" -> "C"
            "mark" -> "M"
            "conor" -> "CO"
            else -> if (avatarEmoji.isNotBlank()) avatarEmoji else name.take(2).uppercase()
        }
}
