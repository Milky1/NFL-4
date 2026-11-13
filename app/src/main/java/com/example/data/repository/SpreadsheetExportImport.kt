package com.example.data.repository

import com.example.data.model.NflGame
import com.example.data.model.Player
import com.example.data.model.SpreadPick

object SpreadsheetExportImport {

    /**
     * Generates CSV matching standard spreadsheet format with sportsbook and fixtures:
     * Week, Away, Home, HomeSpread, Sportsbook, Date, Time, TV, AwayScore, HomeScore, IsFinal, [Player Picks...], CoveringTeam
     */
    fun exportToCsv(
        games: List<NflGame>,
        players: List<Player>,
        picks: List<SpreadPick>
    ): String {
        val sb = StringBuilder()
        val sortedPlayers = players.sortedBy { it.orderIndex }

        // Header
        sb.append("Week,Away,Home,HomeSpread,Sportsbook,Date,Time,TV,AwayScore,HomeScore,IsFinal")
        for (player in sortedPlayers) {
            sb.append(",").append(escapeCsv(player.name))
        }
        sb.append(",CoveringTeam\n")

        val picksMap = picks.associateBy { "${it.gameId}_${it.playerId}" }

        val sortedGames = games.sortedWith(compareBy({ it.weekNumber }, { it.id }))
        for (game in sortedGames) {
            sb.append(game.weekNumber).append(",")
            sb.append(escapeCsv(game.awayTeamShort)).append(",")
            sb.append(escapeCsv(game.homeTeamShort)).append(",")
            sb.append(game.homeSpread).append(",")
            sb.append(escapeCsv(game.sportsbookProvider)).append(",")
            sb.append(escapeCsv(game.gameDate)).append(",")
            sb.append(escapeCsv(game.kickoffTime)).append(",")
            sb.append(escapeCsv(game.tvNetwork)).append(",")
            sb.append(game.awayScore?.toString() ?: "").append(",")
            sb.append(game.homeScore?.toString() ?: "").append(",")
            sb.append(if (game.isFinal) "TRUE" else "FALSE")

            for (player in sortedPlayers) {
                val pick = picksMap["${game.id}_${player.id}"]
                sb.append(",").append(pick?.pickedTeamShort ?: "")
            }

            val covering = game.getCoveringTeamShort() ?: ""
            sb.append(",").append(covering).append("\n")
        }

        return sb.toString()
    }

    /**
     * Parses standard CSV rows to extract games or picks with flexible column layout.
     */
    fun parseCsvGames(csvContent: String): List<ParsedRow> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.size <= 1) return emptyList()

        val header = parseCsvLine(lines[0])
        val results = mutableListOf<ParsedRow>()

        val hasSportsbook = header.any { it.equals("Sportsbook", ignoreCase = true) }
        val isScheduleFormat = header.any { it.equals("KickoffTime", ignoreCase = true) || it.equals("GameDate", ignoreCase = true) }

        val sbIdx = header.indexOfFirst { it.equals("Sportsbook", ignoreCase = true) }
        val dateIdx = header.indexOfFirst { it.equals("Date", ignoreCase = true) || it.equals("GameDate", ignoreCase = true) }
        val timeIdx = header.indexOfFirst { it.equals("Time", ignoreCase = true) || it.equals("KickoffTime", ignoreCase = true) }
        val tvIdx = header.indexOfFirst { it.equals("TV", ignoreCase = true) }

        for (i in 1 until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue
            val parts = parseCsvLine(line)
            if (parts.size >= 4) {
                try {
                    val week = parts[0].toIntOrNull() ?: 1
                    val away = teamNameToShort(parts[1].trim())
                    val home = teamNameToShort(parts[2].trim())
                    val spread = parts[3].toDoubleOrNull() ?: 0.0

                    var sportsbook = "DraftKings"
                    var date = "Sunday"
                    var time = "1:00 PM ET"
                    var tv = "CBS"

                    var awayScore: Int? = null
                    var homeScore: Int? = null
                    var isFinal = false
                    val playerPicks = mutableMapOf<String, String>()

                    if (isScheduleFormat && !hasSportsbook) {
                        // 7-column schedule CSV: Week,AwayTeam,HomeTeam,HomeSpread,KickoffTime,GameDate,Venue
                        time = if (timeIdx >= 0) parts.getOrNull(timeIdx)?.ifBlank { "1:00 PM ET" } ?: "1:00 PM ET" else "1:00 PM ET"
                        date = if (dateIdx >= 0) parts.getOrNull(dateIdx)?.ifBlank { "Sunday" } ?: "Sunday" else "Sunday"
                    } else if (hasSportsbook && sbIdx >= 0 && parts.size > sbIdx) {
                        sportsbook = parts.getOrNull(sbIdx)?.ifBlank { "DraftKings" } ?: "DraftKings"
                        date = if (dateIdx >= 0) parts.getOrNull(dateIdx)?.ifBlank { "Sunday" } ?: "Sunday" else "Sunday"
                        time = if (timeIdx >= 0) parts.getOrNull(timeIdx)?.ifBlank { "1:00 PM ET" } ?: "1:00 PM ET" else "1:00 PM ET"
                        tv = if (tvIdx >= 0) parts.getOrNull(tvIdx)?.ifBlank { "CBS" } ?: "CBS" else "CBS"

                        val awayScoreIdx = if (tvIdx >= 0) tvIdx + 1 else 8
                        awayScore = parts.getOrNull(awayScoreIdx)?.toIntOrNull()
                        homeScore = parts.getOrNull(awayScoreIdx + 1)?.toIntOrNull()
                        isFinal = parts.getOrNull(awayScoreIdx + 2)?.equals("TRUE", ignoreCase = true) ?: (awayScore != null && homeScore != null)

                        for (col in (awayScoreIdx + 3) until parts.size) {
                            val colName = header.getOrNull(col) ?: continue
                            if (colName.equals("CoveringTeam", ignoreCase = true)) continue
                            val pickVal = parts[col].trim()
                            if (pickVal.isNotEmpty()) {
                                playerPicks[colName] = pickVal
                            }
                        }
                    } else {
                        // Standard compact format
                        awayScore = parts.getOrNull(4)?.toIntOrNull()
                        homeScore = parts.getOrNull(5)?.toIntOrNull()
                        isFinal = parts.getOrNull(6)?.equals("TRUE", ignoreCase = true) ?: (awayScore != null && homeScore != null)

                        for (col in 7 until parts.size) {
                            val colName = header.getOrNull(col) ?: continue
                            if (colName.equals("CoveringTeam", ignoreCase = true)) continue
                            val pickVal = parts[col].trim()
                            if (pickVal.isNotEmpty()) {
                                playerPicks[colName] = pickVal
                            }
                        }
                    }

                    results.add(
                        ParsedRow(
                            week = week,
                            awayShort = away,
                            homeShort = home,
                            homeSpread = spread,
                            sportsbookProvider = sportsbook,
                            gameDate = date,
                            kickoffTime = time,
                            tvNetwork = tv,
                            awayScore = awayScore,
                            homeScore = homeScore,
                            isFinal = isFinal,
                            playerPicks = playerPicks
                        )
                    )
                } catch (_: Exception) {
                    // ignore malformed row
                }
            }
        }
        return results
    }

    private fun teamNameToShort(input: String): String {
        val cleaned = input.trim()
        if (cleaned.length <= 3) return cleaned.uppercase()
        return when (cleaned.lowercase()) {
            "arizona cardinals" -> "ARI"
            "atlanta falcons" -> "ATL"
            "baltimore ravens" -> "BAL"
            "buffalo bills" -> "BUF"
            "carolina panthers" -> "CAR"
            "chicago bears" -> "CHI"
            "cincinnati bengals" -> "CIN"
            "cleveland browns" -> "CLE"
            "dallas cowboys" -> "DAL"
            "denver broncos" -> "DEN"
            "detroit lions" -> "DET"
            "green bay packers" -> "GB"
            "houston texans" -> "HOU"
            "indianapolis colts" -> "IND"
            "jacksonville jaguars" -> "JAX"
            "kansas city chiefs" -> "KC"
            "las vegas raiders" -> "LV"
            "los angeles chargers" -> "LAC"
            "los angeles rams" -> "LAR"
            "miami dolphins" -> "MIA"
            "minnesota vikings" -> "MIN"
            "new england patriots" -> "NE"
            "new orleans saints" -> "NO"
            "new york giants" -> "NYG"
            "new york jets" -> "NYJ"
            "philadelphia eagles" -> "PHI"
            "pittsburgh steelers" -> "PIT"
            "san francisco 49ers" -> "SF"
            "seattle seahawks" -> "SEA"
            "tampa bay buccaneers" -> "TB"
            "tennessee titans" -> "TEN"
            "washington commanders" -> "WAS"
            else -> cleaned.uppercase()
        }
    }

    private fun escapeCsv(str: String): String {
        return if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            "\"" + str.replace("\"", "\"\"") + "\""
        } else {
            str
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        var inQuotes = false
        val sb = StringBuilder()
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    sb.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim())
                sb.clear()
            } else {
                sb.append(c)
            }
            i++
        }
        tokens.add(sb.toString().trim())
        return tokens
    }

    data class ParsedRow(
        val week: Int,
        val awayShort: String,
        val homeShort: String,
        val homeSpread: Double,
        val sportsbookProvider: String = "DraftKings",
        val gameDate: String = "Sunday",
        val kickoffTime: String = "1:00 PM ET",
        val tvNetwork: String = "CBS",
        val awayScore: Int?,
        val homeScore: Int?,
        val isFinal: Boolean,
        val playerPicks: Map<String, String> // playerName -> teamShort
    )
}
