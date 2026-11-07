package com.example.data.stats

import com.example.data.model.AtsResult
import com.example.data.model.NflGame
import com.example.data.model.Player
import com.example.data.model.SpreadPick

data class PlayerRecord(
    val player: Player,
    val wins: Int = 0,
    val losses: Int = 0,
    val pushes: Int = 0,
    val weeklyWins: Int = 0,
    val currentStreak: String = "-",
    val longestWinStreak: Int = 0,
    val favoriteTeam: String = "-",
    val favoritePickRate: Float = 0f, // percentage of time picking favorites
    val underdogPickRate: Float = 0f,
    val totalEarningsEuros: Int = 0
) {
    val totalDecided: Int get() = wins + losses
    val totalPicks: Int get() = wins + losses + pushes
    val winPercentage: Float get() = if (totalDecided > 0) (wins.toFloat() / totalDecided.toFloat()) * 100f else 0f
    val formattedRecord: String get() = "$wins-$losses" + if (pushes > 0) "-$pushes" else ""
    val formattedWinPct: String get() = "%.1f%%".format(winPercentage)
    val winPercentageFormatted: String get() = "%.1f".format(winPercentage)
    val formattedEarnings: String get() = "€$totalEarningsEuros"
}

data class WeeklyPlayerResult(
    val player: Player,
    val wins: Int,
    val losses: Int,
    val pushes: Int,
    val pending: Int
) {
    val totalDecided: Int get() = wins + losses
    val winPercentage: Float get() = if (totalDecided > 0) (wins.toFloat() / totalDecided.toFloat()) * 100f else 0f
    val formattedRecord: String get() = "$wins-$losses" + if (pushes > 0) "-$pushes" else ""
}

data class WeeklyWinnerSummary(
    val weekNumber: Int,
    val winners: List<Player>,
    val isTie: Boolean,
    val winningRecord: String,
    val playerResults: List<WeeklyPlayerResult>,
    val isCompleted: Boolean
)

data class WeeklyPrizeInfo(
    val weekNumber: Int,
    val basePrize: Int = 5,
    val rolloverFromPrevious: Int = 0,
    val totalPot: Int = 5,
    val isDrawRollover: Boolean = false,
    val outrightWinner: Player? = null,
    val payoutAmount: Int = 0
) {
    val potDescription: String
        get() = if (rolloverFromPrevious > 0) {
            "€$totalPot Pot (includes €$rolloverFromPrevious rollover from draw)"
        } else {
            "€$totalPot Prize"
        }
}

data class HeadToHeadRecord(
    val playerA: Player,
    val playerB: Player,
    val totalOppositePicks: Int,
    val playerAWins: Int,
    val playerBWins: Int,
    val pushes: Int
)

data class WeeklyTrendPoint(
    val weekNumber: Int,
    val winPct: Float,
    val wins: Int,
    val losses: Int,
    val cumulativeWinPct: Float = 0f,
    val cumulativeWins: Int = 0,
    val cumulativeLosses: Int = 0
)

data class TeamPickStat(
    val teamCode: String,
    val timesPicked: Int,
    val totalPlayerPicks: Int,
    val pickPercentage: Float,
    val wins: Int,
    val losses: Int,
    val pushes: Int,
    val winPercentage: Float
) {
    val formattedPickPct: String get() = "%.1f%%".format(pickPercentage)
    val formattedWinPct: String get() = "%.1f%%".format(winPercentage)
    val formattedRecord: String get() = "$wins-$losses" + if (pushes > 0) "-$pushes" else ""
}

object StatsCalculator {

    enum class PickOutcome {
        WIN,
        LOSS,
        PUSH,
        PENDING,
        NO_PICK
    }

    fun evaluatePick(game: NflGame, pick: SpreadPick?): PickOutcome {
        if (pick == null) return PickOutcome.NO_PICK
        val ats = game.getAtsResult()
        return when (ats) {
            AtsResult.PENDING -> PickOutcome.PENDING
            AtsResult.PUSH -> PickOutcome.PUSH
            AtsResult.HOME_COVERED -> if (pick.pickedTeamShort.equals(game.homeTeamShort, ignoreCase = true)) PickOutcome.WIN else PickOutcome.LOSS
            AtsResult.AWAY_COVERED -> if (pick.pickedTeamShort.equals(game.awayTeamShort, ignoreCase = true)) PickOutcome.WIN else PickOutcome.LOSS
        }
    }

    fun areAllPlayersWrong(game: NflGame, players: List<Player>, picks: List<SpreadPick>): Boolean {
        if (!game.isFinal || game.homeScore == null || game.awayScore == null) return false
        val gamePicks = picks.filter { it.gameId == game.id }
        if (gamePicks.isEmpty()) return false
        val outcomes = gamePicks.map { evaluatePick(game, it) }
        return outcomes.isNotEmpty() && outcomes.all { it == PickOutcome.LOSS }
    }

    fun getWinningPlayers(game: NflGame, players: List<Player>, picks: List<SpreadPick>): List<Player> {
        if (!game.isFinal) return emptyList()
        val gamePicks = picks.filter { it.gameId == game.id }
        return players.filter { player ->
            val pick = gamePicks.find { it.playerId == player.id }
            evaluatePick(game, pick) == PickOutcome.WIN
        }
    }

    fun calculateWeeklySummary(
        weekNumber: Int,
        games: List<NflGame>,
        players: List<Player>,
        picks: List<SpreadPick>
    ): WeeklyWinnerSummary {
        val weekGames = games.filter { it.weekNumber == weekNumber }
        val weekPicks = picks.filter { it.weekNumber == weekNumber }

        val playerResults = players.map { player ->
            var wins = 0
            var losses = 0
            var pushes = 0
            var pending = 0

            for (game in weekGames) {
                val pick = weekPicks.find { it.gameId == game.id && it.playerId == player.id }
                when (evaluatePick(game, pick)) {
                    PickOutcome.WIN -> wins++
                    PickOutcome.LOSS -> losses++
                    PickOutcome.PUSH -> pushes++
                    PickOutcome.PENDING, PickOutcome.NO_PICK -> pending++
                }
            }

            WeeklyPlayerResult(
                player = player,
                wins = wins,
                losses = losses,
                pushes = pushes,
                pending = pending
            )
        }

        val allGamesFinal = weekGames.isNotEmpty() && weekGames.all { it.isFinal }
        val maxWins = playerResults.maxOfOrNull { it.wins } ?: 0
        val topPlayers = if (maxWins > 0) playerResults.filter { it.wins == maxWins }.map { it.player } else emptyList()

        return WeeklyWinnerSummary(
            weekNumber = weekNumber,
            winners = topPlayers,
            isTie = topPlayers.size > 1,
            winningRecord = if (maxWins > 0) "$maxWins wins" else "No results",
            playerResults = playerResults,
            isCompleted = allGamesFinal
        )
    }

    fun calculateAllWeeklySummaries(
        games: List<NflGame>,
        players: List<Player>,
        picks: List<SpreadPick>
    ): List<WeeklyWinnerSummary> {
        val weekNumbers = games.map { it.weekNumber }.distinct().sorted()
        return weekNumbers.map { weekNum ->
            calculateWeeklySummary(weekNum, games, players, picks)
        }
    }

    fun calculateWeeklyPrizes(allSummaries: List<WeeklyWinnerSummary>): Map<Int, WeeklyPrizeInfo> {
        val result = mutableMapOf<Int, WeeklyPrizeInfo>()
        var accumulatedRollover = 0
        val sorted = allSummaries.sortedBy { it.weekNumber }

        for (summary in sorted) {
            val totalPot = 5 + accumulatedRollover
            val hasResults = summary.playerResults.any { it.wins > 0 || it.losses > 0 }

            if (!hasResults) {
                // Future / active open week (e.g. Week 9)
                result[summary.weekNumber] = WeeklyPrizeInfo(
                    weekNumber = summary.weekNumber,
                    basePrize = 5,
                    rolloverFromPrevious = accumulatedRollover,
                    totalPot = totalPot,
                    isDrawRollover = false,
                    outrightWinner = null,
                    payoutAmount = 0
                )
            } else if (summary.winners.size == 1) {
                // Outright Winner takes the pot!
                val winner = summary.winners.first()
                result[summary.weekNumber] = WeeklyPrizeInfo(
                    weekNumber = summary.weekNumber,
                    basePrize = 5,
                    rolloverFromPrevious = accumulatedRollover,
                    totalPot = totalPot,
                    isDrawRollover = false,
                    outrightWinner = winner,
                    payoutAmount = totalPot
                )
                accumulatedRollover = 0
            } else if (summary.winners.size > 1) {
                // Draw! €5 rolls over to next week (= €10, etc.)
                result[summary.weekNumber] = WeeklyPrizeInfo(
                    weekNumber = summary.weekNumber,
                    basePrize = 5,
                    rolloverFromPrevious = accumulatedRollover,
                    totalPot = totalPot,
                    isDrawRollover = true,
                    outrightWinner = null,
                    payoutAmount = 0
                )
                accumulatedRollover = totalPot
            } else {
                result[summary.weekNumber] = WeeklyPrizeInfo(
                    weekNumber = summary.weekNumber,
                    basePrize = 5,
                    rolloverFromPrevious = accumulatedRollover,
                    totalPot = totalPot,
                    isDrawRollover = false,
                    outrightWinner = null,
                    payoutAmount = 0
                )
            }
        }
        return result
    }

    fun calculateOverallLeaderboard(
        games: List<NflGame>,
        players: List<Player>,
        picks: List<SpreadPick>
    ): List<PlayerRecord> {
        val weeklySummaries = calculateAllWeeklySummaries(games, players, picks)
        val weeklyPrizes = calculateWeeklyPrizes(weeklySummaries)
        val gamesMap = games.associateBy { it.id }

        return players.map { player ->
            var wins = 0
            var losses = 0
            var pushes = 0
            var weeklyWinCount = 0

            // Count weekly championship crowns (only on weeks where games have concluded)
            for (summary in weeklySummaries) {
                if (summary.winners.any { it.id == player.id } && summary.playerResults.any { it.totalDecided > 0 }) {
                    weeklyWinCount++
                }
            }

            val playerEarnings = weeklyPrizes.values
                .filter { it.outrightWinner?.id == player.id }
                .sumOf { it.payoutAmount }

            // Streak tracking
            var currentStreakCount = 0
            var currentStreakType = ""
            var maxWinStreak = 0
            var rollingWinStreak = 0

            val playerPicks = picks.filter { it.playerId == player.id }
                .sortedBy { it.weekNumber }

            var favoritePicks = 0
            var underdogPicks = 0
            val teamPickCounts = mutableMapOf<String, Int>()

            for (pick in playerPicks) {
                val game = gamesMap[pick.gameId] ?: continue
                teamPickCounts[pick.pickedTeamShort] = (teamPickCounts[pick.pickedTeamShort] ?: 0) + 1

                // Is picked team favorite or underdog?
                // Home spread: if homeSpread < 0, Home is favorite. Away is underdog.
                val isHomeFav = game.homeSpread < 0
                val pickedHome = pick.pickedTeamShort.equals(game.homeTeamShort, ignoreCase = true)
                if ((pickedHome && isHomeFav) || (!pickedHome && !isHomeFav)) {
                    favoritePicks++
                } else {
                    underdogPicks++
                }

                when (evaluatePick(game, pick)) {
                    PickOutcome.WIN -> {
                        wins++
                        rollingWinStreak++
                        if (rollingWinStreak > maxWinStreak) maxWinStreak = rollingWinStreak
                        if (currentStreakType == "W") currentStreakCount++ else {
                            currentStreakType = "W"
                            currentStreakCount = 1
                        }
                    }
                    PickOutcome.LOSS -> {
                        losses++
                        rollingWinStreak = 0
                        if (currentStreakType == "L") currentStreakCount++ else {
                            currentStreakType = "L"
                            currentStreakCount = 1
                        }
                    }
                    PickOutcome.PUSH -> {
                        pushes++
                        // push does not break or add to win streak
                    }
                    else -> {}
                }
            }

            val totalPicksRecorded = favoritePicks + underdogPicks
            val favRate = if (totalPicksRecorded > 0) (favoritePicks.toFloat() / totalPicksRecorded) * 100f else 50f
            val dogRate = if (totalPicksRecorded > 0) (underdogPicks.toFloat() / totalPicksRecorded) * 100f else 50f

            val mostPickedTeam = teamPickCounts.maxByOrNull { it.value }?.key ?: "-"

            val streakString = if (currentStreakCount > 0) "$currentStreakCount$currentStreakType" else "-"

            PlayerRecord(
                player = player,
                wins = wins,
                losses = losses,
                pushes = pushes,
                weeklyWins = weeklyWinCount,
                currentStreak = streakString,
                longestWinStreak = maxWinStreak,
                favoriteTeam = mostPickedTeam,
                favoritePickRate = favRate,
                underdogPickRate = dogRate,
                totalEarningsEuros = playerEarnings
            )
        }.sortedWith(
            compareByDescending<PlayerRecord> { it.winPercentage }
                .thenByDescending { it.wins }
                .thenByDescending { it.weeklyWins }
        )
    }

    fun calculateWeeklyTrends(
        playerId: Long,
        games: List<NflGame>,
        picks: List<SpreadPick>
    ): List<WeeklyTrendPoint> {
        val weekNumbers = games.map { it.weekNumber }.distinct().sorted()
        val gamesMap = games.associateBy { it.id }
        val playerPicks = picks.filter { it.playerId == playerId }

        val trends = mutableListOf<WeeklyTrendPoint>()
        var cumWins = 0
        var cumLosses = 0

        for (week in weekNumbers) {
            val weekGames = games.filter { it.weekNumber == week && it.isFinal }
            if (weekGames.isEmpty()) continue

            val weekPicks = playerPicks.filter { it.weekNumber == week }
            var w = 0
            var l = 0

            for (game in weekGames) {
                val pick = weekPicks.find { it.gameId == game.id }
                when (evaluatePick(game, pick)) {
                    PickOutcome.WIN -> w++
                    PickOutcome.LOSS -> l++
                    else -> {}
                }
            }

            cumWins += w
            cumLosses += l
            val pct = if (w + l > 0) (w.toFloat() / (w + l).toFloat()) * 100f else 0f
            val cumPct = if (cumWins + cumLosses > 0) (cumWins.toFloat() / (cumWins + cumLosses).toFloat()) * 100f else 0f
            trends.add(
                WeeklyTrendPoint(
                    weekNumber = week,
                    winPct = pct,
                    wins = w,
                    losses = l,
                    cumulativeWinPct = cumPct,
                    cumulativeWins = cumWins,
                    cumulativeLosses = cumLosses
                )
            )
        }

        return trends
    }

    fun calculateTeamPickStats(
        playerId: Long,
        games: List<NflGame>,
        picks: List<SpreadPick>
    ): List<TeamPickStat> {
        val playerPicks = picks.filter { it.playerId == playerId }
        val totalPlayerPicks = playerPicks.size
        if (totalPlayerPicks == 0) return emptyList()

        val gamesMap = games.associateBy { it.id }
        val picksByTeam = playerPicks.groupBy { it.pickedTeamShort.uppercase() }

        return picksByTeam.map { (teamCode, teamPicks) ->
            var wins = 0
            var losses = 0
            var pushes = 0

            for (pick in teamPicks) {
                val game = gamesMap[pick.gameId] ?: continue
                if (!game.isFinal) continue
                when (evaluatePick(game, pick)) {
                    PickOutcome.WIN -> wins++
                    PickOutcome.LOSS -> losses++
                    PickOutcome.PUSH -> pushes++
                    else -> {}
                }
            }

            val decided = wins + losses
            val winPct = if (decided > 0) (wins.toFloat() / decided.toFloat()) * 100f else 0f
            val pickPct = (teamPicks.size.toFloat() / totalPlayerPicks.toFloat()) * 100f

            TeamPickStat(
                teamCode = teamCode,
                timesPicked = teamPicks.size,
                totalPlayerPicks = totalPlayerPicks,
                pickPercentage = pickPct,
                wins = wins,
                losses = losses,
                pushes = pushes,
                winPercentage = winPct
            )
        }.sortedWith(
            compareByDescending<TeamPickStat> { it.timesPicked }
                .thenByDescending { it.winPercentage }
                .thenBy { it.teamCode }
        )
    }

    fun calculateHeadToHead(
        playerA: Player,
        playerB: Player,
        games: List<NflGame>,
        picks: List<SpreadPick>
    ): HeadToHeadRecord {
        val gamesMap = games.associateBy { it.id }
        var oppositeCount = 0
        var aWins = 0
        var bWins = 0
        var pushes = 0

        val picksA = picks.filter { it.playerId == playerA.id }.associateBy { it.gameId }
        val picksB = picks.filter { it.playerId == playerB.id }.associateBy { it.gameId }

        for ((gameId, pickA) in picksA) {
            val pickB = picksB[gameId] ?: continue
            val game = gamesMap[gameId] ?: continue
            if (!game.isFinal) continue

            // Only compare when they disagreed!
            if (!pickA.pickedTeamShort.equals(pickB.pickedTeamShort, ignoreCase = true)) {
                oppositeCount++
                val outA = evaluatePick(game, pickA)
                val outB = evaluatePick(game, pickB)

                if (outA == PickOutcome.WIN) aWins++
                else if (outB == PickOutcome.WIN) bWins++
                else pushes++
            }
        }

        return HeadToHeadRecord(
            playerA = playerA,
            playerB = playerB,
            totalOppositePicks = oppositeCount,
            playerAWins = aWins,
            playerBWins = bWins,
            pushes = pushes
        )
    }
}
