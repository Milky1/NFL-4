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
    val favoritePickRate: Float = 0f,
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
            val hasResults = summary.isCompleted || summary.playerResults.any { it.wins > 0 || it.losses > 0 }

            if (!hasResults) {
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

            for (summary in weeklySummaries) {
                if (summary.winners.any { it.id == player.id } && summary.playerResults.any { it.totalDecided > 0 }) {
                    weeklyWinCount++
                }
            }

            val playerEarnings = weeklyPrizes.values
                .filter { it.outrightWinner?.id == player.id }
                .sumOf { it.payoutAmount }

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
}