package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AtsResult {
    HOME_COVERED,
    AWAY_COVERED,
    PUSH,
    PENDING
}

@Entity(tableName = "nfl_games")
data class NflGame(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weekNumber: Int,
    val seasonYear: Int = 2026,
    val awayTeam: String,
    val awayTeamShort: String,
    val homeTeam: String,
    val homeTeamShort: String,
    /**
     * Standard spread from Home team's perspective.
     * e.g. -3.5 means Home is favored by 3.5 points.
     * +4.5 means Away is favored by 4.5 points (Home gets +4.5).
     */
    val homeSpread: Double,
    val kickoffTime: String = "Sun 1:00 PM",
    val gameDate: String = "Sunday, Nov 10",
    val tvNetwork: String = "CBS",
    val venue: String = "",
    val sportsbookProvider: String = "DraftKings",
    val awayScore: Int? = null,
    val homeScore: Int? = null,
    val isFinal: Boolean = false
) {
    /**
     * Determines which team covered the spread:
     * (homeScore - awayScore) + homeSpread
     */
    fun getAtsResult(): AtsResult {
        if (!isFinal || homeScore == null || awayScore == null) {
            return AtsResult.PENDING
        }
        val diff = (homeScore - awayScore) + homeSpread
        return when {
            diff > 0.0001 -> AtsResult.HOME_COVERED
            diff < -0.0001 -> AtsResult.AWAY_COVERED
            else -> AtsResult.PUSH
        }
    }

    fun getCoveringTeamShort(): String? {
        return when (getAtsResult()) {
            AtsResult.HOME_COVERED -> homeTeamShort
            AtsResult.AWAY_COVERED -> awayTeamShort
            AtsResult.PUSH -> "PUSH"
            AtsResult.PENDING -> null
        }
    }

    /**
     * Identifies the favored team based on standard spread convention.
     * homeSpread < 0 -> Home is favorite.
     * homeSpread > 0 -> Away is favorite.
     */
    val favoriteTeamShort: String
        get() = if (homeSpread > 0) awayTeamShort else homeTeamShort

    val favoriteSpreadFormatted: String
        get() {
            val mag = kotlin.math.abs(homeSpread)
            if (mag == 0.0) return "PK"
            val magStr = if (mag % 1.0 == 0.0) "${mag.toLong()}" else "$mag"
            return "-$magStr"
        }

    val lineSummary: String
        get() = "$favoriteTeamShort $favoriteSpreadFormatted"

    /**
     * Formats spread string for Home team: Only show spread for the favorite (e.g. "-3.5", "-10").
     * Returns empty string if Home is the underdog.
     */
    fun formattedHomeSpread(): String {
        return if (homeSpread < 0) favoriteSpreadFormatted else ""
    }

    /**
     * Formats spread string for Away team: Only show spread for the favorite (e.g. "-3.5", "-10").
     * Returns empty string if Away is the underdog.
     */
    fun formattedAwaySpread(): String {
        return if (homeSpread > 0) favoriteSpreadFormatted else ""
    }

    val fullFixtureInfo: String
        get() = "$gameDate • $kickoffTime" + if (tvNetwork.isNotBlank()) " • $tvNetwork" else ""
}
