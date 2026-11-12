package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.NflGame
import com.example.data.model.NflTeams
import com.example.data.model.Player
import com.example.data.model.SpreadPick
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PickEmRepository(private val database: AppDatabase) {

    private val playerDao = database.playerDao()
    private val nflGameDao = database.nflGameDao()
    private val spreadPickDao = database.spreadPickDao()

    val allPlayers: Flow<List<Player>> = playerDao.getAllPlayers()
    val allGames: Flow<List<NflGame>> = nflGameDao.getAllGames()
    val allPicks: Flow<List<SpreadPick>> = spreadPickDao.getAllPicks()

    fun getGamesForWeek(weekNumber: Int): Flow<List<NflGame>> = nflGameDao.getGamesForWeek(weekNumber)
    fun getPicksForWeek(weekNumber: Int): Flow<List<SpreadPick>> = spreadPickDao.getPicksForWeek(weekNumber)

    suspend fun ensureInitialDataLoaded() {
        // Ensure Clive, Mark, and Conor have 'C', 'M', 'CO'
        playerDao.insertAll(DefaultNflData.initialPlayers)

        val allExisting = nflGameDao.getAllGames().first()
        val hasCorrectWeek1 = allExisting.any { it.weekNumber == 1 && it.awayTeamShort == "NE" && it.homeTeamShort == "SEA" && it.homeSpread == -3.5 }
        val hasCorrectWeek2 = allExisting.any { it.weekNumber == 2 && it.awayTeamShort == "DET" && it.homeTeamShort == "BUF" && it.homeSpread == -4.5 }
        val hasCorrectWeek2Car = allExisting.any { it.weekNumber == 2 && it.awayTeamShort == "CAR" && it.homeTeamShort == "ATL" && it.homeSpread == 2.5 }
        val hasCorrectWeek3 = allExisting.any { it.weekNumber == 3 && it.awayTeamShort == "ATL" && it.homeTeamShort == "GB" && it.homeSpread == -5.5 }
        val hasCorrectWeek3Car = allExisting.any { it.weekNumber == 3 && it.awayTeamShort == "CAR" && it.homeTeamShort == "CLE" && it.homeSpread == 2.5 }
        val hasCorrectWeek4 = allExisting.any { it.weekNumber == 4 && it.awayTeamShort == "PIT" && it.homeTeamShort == "CLE" && it.homeSpread == 2.5 }
        val hasCorrectWeek4Ind = allExisting.any { it.weekNumber == 4 && it.awayTeamShort == "IND" && it.homeTeamShort == "WAS" && it.homeSpread == 3.5 }
        val hasCorrectWeek5 = allExisting.any { it.weekNumber == 5 && it.awayTeamShort == "TB" && it.homeTeamShort == "DAL" && it.homeSpread == -8.5 }
        val hasCorrectWeek5Phi = allExisting.any { it.weekNumber == 5 && it.awayTeamShort == "PHI" && it.homeTeamShort == "JAX" && it.homeSpread == -7.5 }
        val hasCorrectWeek6 = allExisting.any { it.weekNumber == 6 && it.awayTeamShort == "SEA" && it.homeTeamShort == "DEN" && it.homeSpread == 2.5 }
        val hasCorrectWeek6Hou = allExisting.any { it.weekNumber == 6 && it.awayTeamShort == "HOU" && it.homeTeamShort == "JAX" && it.homeSpread == -2.5 }
        val hasCorrectWeek7 = allExisting.any { it.weekNumber == 7 && it.awayTeamShort == "NE" && it.homeTeamShort == "CHI" && it.homeSpread == -3.0 }
        val hasCorrectWeek7Pit = allExisting.any { it.weekNumber == 7 && it.awayTeamShort == "PIT" && it.homeTeamShort == "NO" && it.homeSpread == 3.0 }
        val hasCorrectWeek8 = allExisting.any { it.weekNumber == 8 && it.awayTeamShort == "CAR" && it.homeTeamShort == "GB" && it.homeSpread == -3.5 }
        val hasCorrectWeek8Dal = allExisting.any { it.weekNumber == 8 && it.awayTeamShort == "ARI" && it.homeTeamShort == "DAL" && it.homeSpread == -7.0 }
        val hasCorrectWeek9 = allExisting.any { it.weekNumber == 9 && it.awayTeamShort == "JAX" && it.homeTeamShort == "BAL" && it.homeSpread == 10.0 }
        val hasCorrectWeek9Cin = allExisting.any { it.weekNumber == 9 && it.awayTeamShort == "CIN" && it.homeTeamShort == "ATL" && it.homeSpread == 3.0 }
        val has17Weeks = allExisting.map { it.weekNumber }.distinct().size >= 17
        val week9Picks = spreadPickDao.getPicksForWeek(9).first()
        val hasCleanWeek9 = week9Picks.isEmpty()
        val week2Picks = spreadPickDao.getPicksForWeek(2).first()
        val hasUpdatedWeek2Picks = week2Picks.size == 48
        val week3Picks = spreadPickDao.getPicksForWeek(3).first()
        val hasUpdatedWeek3Picks = week3Picks.size == 48
        val week4Picks = spreadPickDao.getPicksForWeek(4).first()
        val hasUpdatedWeek4Picks = week4Picks.size == 48
        val week5Picks = spreadPickDao.getPicksForWeek(5).first()
        val hasUpdatedWeek5Picks = week5Picks.size == 45
        val week6Picks = spreadPickDao.getPicksForWeek(6).first()
        val hasUpdatedWeek6Picks = week6Picks.size == 42
        val week7Picks = spreadPickDao.getPicksForWeek(7).first()
        val hasUpdatedWeek7Picks = week7Picks.size == 42
        val week8Picks = spreadPickDao.getPicksForWeek(8).first()
        val hasUpdatedWeek8Picks = week8Picks.size == 42

        val hasFullSlates = allExisting.size >= 200 && hasCorrectWeek1 && hasCorrectWeek2 && hasCorrectWeek2Car && hasCorrectWeek3 && hasCorrectWeek3Car && hasCorrectWeek4 && hasCorrectWeek4Ind && hasCorrectWeek5 && hasCorrectWeek5Phi && hasCorrectWeek6 && hasCorrectWeek6Hou && hasCorrectWeek7 && hasCorrectWeek7Pit && hasCorrectWeek8 && hasCorrectWeek8Dal && hasCorrectWeek9 && hasCorrectWeek9Cin && has17Weeks && hasCleanWeek9 && hasUpdatedWeek2Picks && hasUpdatedWeek3Picks && hasUpdatedWeek4Picks && hasUpdatedWeek5Picks && hasUpdatedWeek6Picks && hasUpdatedWeek7Picks && hasUpdatedWeek8Picks

        if (!hasFullSlates) {
            spreadPickDao.deleteAllPicks()
            nflGameDao.deleteAllGames()

            val sampleGames = DefaultNflData.getSampleGames()
            nflGameDao.insertAll(sampleGames)
            val insertedGames = nflGameDao.getAllGames().first()
            val samplePicks = DefaultNflData.getSamplePicks(insertedGames)
            spreadPickDao.insertAll(samplePicks)
        }
    }

    suspend fun getMostRecentActiveWeek(): Int {
        val games = nflGameDao.getAllGames().first()
        // Most recent week is the first week containing unfinalized games, or Week 9
        val firstPendingWeek = games.filter { !it.isFinal }.minOfOrNull { it.weekNumber }
        return firstPendingWeek ?: 9
    }

    suspend fun refreshFixtures(weekNumber: Int): Int {
        val freshFixtures = DefaultNflData.getFreshWeekFixtures(weekNumber)
        if (freshFixtures.isEmpty()) return 0

        val existingGames = nflGameDao.getAllGames().first().filter { it.weekNumber == weekNumber }
        if (existingGames.isEmpty()) {
            nflGameDao.insertAll(freshFixtures)
            val updatedGames = nflGameDao.getAllGames().first()
            val samplePicks = DefaultNflData.getSamplePicks(updatedGames).filter { it.weekNumber == weekNumber }
            spreadPickDao.insertAll(samplePicks)
            return freshFixtures.size
        } else {
            for (fresh in freshFixtures) {
                val match = existingGames.find {
                    it.awayTeamShort.equals(fresh.awayTeamShort, ignoreCase = true) &&
                    it.homeTeamShort.equals(fresh.homeTeamShort, ignoreCase = true)
                }
                if (match != null) {
                    nflGameDao.updateFixtureDetails(match.id, fresh.gameDate, fresh.kickoffTime, fresh.tvNetwork, fresh.venue)
                } else {
                    nflGameDao.insert(fresh)
                }
            }
            return existingGames.size
        }
    }

    suspend fun refreshSpreads(weekNumber: Int, provider: String = "DraftKings"): Int {
        val freshSpreads = DefaultNflData.getSpreadsForProvider(weekNumber, provider)
        val existingGames = nflGameDao.getAllGames().first().filter { it.weekNumber == weekNumber }
        var updatedCount = 0

        for (game in existingGames) {
            val newSpread = freshSpreads[Pair(game.awayTeamShort, game.homeTeamShort)]
            if (newSpread != null) {
                nflGameDao.updateSpreadWithSportsbook(game.id, newSpread, provider)
                updatedCount++
            }
        }
        return updatedCount
    }

    // Backwards compatibility aliases
    suspend fun populateOrRefreshWeekFixtures(weekNumber: Int): Int = refreshFixtures(weekNumber)

    suspend fun savePick(gameId: Long, playerId: Long, weekNumber: Int, pickedTeamShort: String) {
        val existing = spreadPickDao.getPick(gameId, playerId)
        val initialFirstPick = existing?.firstPickTeamShort?.ifBlank { null } ?: existing?.pickedTeamShort?.ifBlank { null } ?: pickedTeamShort.uppercase()
        val pick = SpreadPick(
            id = existing?.id ?: 0L,
            gameId = gameId,
            playerId = playerId,
            weekNumber = weekNumber,
            pickedTeamShort = pickedTeamShort.uppercase(),
            timestamp = System.currentTimeMillis(),
            firstPickTeamShort = initialFirstPick
        )
        spreadPickDao.insertOrUpdate(pick)
    }

    suspend fun updateGameScore(gameId: Long, awayScore: Int?, homeScore: Int?, isFinal: Boolean) {
        nflGameDao.updateScore(gameId, awayScore, homeScore, isFinal)
    }

    suspend fun updateGameSpread(gameId: Long, newSpread: Double, sportsbook: String = "DraftKings") {
        nflGameDao.updateSpreadWithSportsbook(gameId, newSpread, sportsbook)
    }

    suspend fun updateFixtureDetails(
        gameId: Long,
        gameDate: String,
        kickoffTime: String,
        tvNetwork: String,
        venue: String
    ) {
        nflGameDao.updateFixtureDetails(gameId, gameDate, kickoffTime, tvNetwork, venue)
    }

    suspend fun addCustomGame(
        weekNumber: Int,
        awayTeamShort: String,
        homeTeamShort: String,
        homeSpread: Double,
        kickoffTime: String,
        gameDate: String = "Sunday",
        tvNetwork: String = "CBS",
        venue: String = "",
        sportsbookProvider: String = "DraftKings"
    ): Long {
        val awayTeam = NflTeams.getByCode(awayTeamShort)
        val homeTeam = NflTeams.getByCode(homeTeamShort)
        val game = NflGame(
            weekNumber = weekNumber,
            awayTeam = awayTeam.fullName,
            awayTeamShort = awayTeamShort.uppercase(),
            homeTeam = homeTeam.fullName,
            homeTeamShort = homeTeamShort.uppercase(),
            homeSpread = homeSpread,
            kickoffTime = kickoffTime,
            gameDate = gameDate,
            tvNetwork = tvNetwork,
            venue = venue,
            sportsbookProvider = sportsbookProvider,
            isFinal = false
        )
        return nflGameDao.insert(game)
    }

    suspend fun deleteGame(gameId: Long) {
        spreadPickDao.deletePicksForGame(gameId)
        nflGameDao.deleteGame(gameId)
    }

    suspend fun addPlayer(name: String, emoji: String, colorHex: Long): Long {
        val currentPlayers = allPlayers.first()
        val player = Player(
            name = name.trim(),
            avatarEmoji = emoji.ifBlank { "🏈" },
            avatarColorHex = colorHex,
            orderIndex = currentPlayers.size
        )
        return playerDao.insert(player)
    }

    suspend fun updatePlayer(player: Player) {
        playerDao.update(player)
    }

    suspend fun deletePlayer(playerId: Long) {
        playerDao.deleteById(playerId)
    }

    suspend fun resetToSampleData() {
        val games = allGames.first()
        for (g in games) {
            spreadPickDao.deletePicksForGame(g.id)
            nflGameDao.deleteGame(g.id)
        }
        val players = allPlayers.first()
        for (p in players) {
            playerDao.deleteById(p.id)
        }
        playerDao.insertAll(DefaultNflData.initialPlayers)
        val sampleGames = DefaultNflData.getSampleGames()
        nflGameDao.insertAll(sampleGames)
        val samplePicks = DefaultNflData.getSamplePicks(sampleGames)
        spreadPickDao.insertAll(samplePicks)
    }

    suspend fun deleteGamesForWeek(weekNumber: Int) {
        val weekGames = nflGameDao.getGamesForWeek(weekNumber).first()
        for (g in weekGames) {
            spreadPickDao.deletePicksForGame(g.id)
        }
        nflGameDao.deleteGamesForWeek(weekNumber)
    }

    suspend fun importParsedRows(rows: List<SpreadsheetExportImport.ParsedRow>) {
        val currentPlayers = allPlayers.first().toMutableList()
        val playerMap = currentPlayers.associateBy { it.name.lowercase() }.toMutableMap()

        for (row in rows) {
            for (playerName in row.playerPicks.keys) {
                if (!playerMap.containsKey(playerName.lowercase())) {
                    val newPlayer = Player(
                        name = playerName,
                        avatarColorHex = 0xFFFFB300,
                        avatarEmoji = "⭐",
                        orderIndex = currentPlayers.size
                    )
                    val newId = playerDao.insert(newPlayer)
                    val saved = newPlayer.copy(id = newId)
                    currentPlayers.add(saved)
                    playerMap[playerName.lowercase()] = saved
                }
            }
        }

        for (row in rows) {
            val awayTeam = NflTeams.getByCode(row.awayShort)
            val homeTeam = NflTeams.getByCode(row.homeShort)
            val game = NflGame(
                weekNumber = row.week,
                awayTeam = awayTeam.fullName,
                awayTeamShort = row.awayShort.uppercase(),
                homeTeam = homeTeam.fullName,
                homeTeamShort = row.homeShort.uppercase(),
                homeSpread = row.homeSpread,
                sportsbookProvider = row.sportsbookProvider,
                gameDate = row.gameDate,
                kickoffTime = row.kickoffTime,
                tvNetwork = row.tvNetwork,
                awayScore = row.awayScore,
                homeScore = row.homeScore,
                isFinal = row.isFinal
            )
            val gameId = nflGameDao.insert(game)

            for ((playerName, teamShort) in row.playerPicks) {
                val player = playerMap[playerName.lowercase()] ?: continue
                val pick = SpreadPick(
                    gameId = gameId,
                    playerId = player.id,
                    weekNumber = row.week,
                    pickedTeamShort = teamShort.uppercase()
                )
                spreadPickDao.insertOrUpdate(pick)
            }
        }
    }
}
