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

    suspend fun ensureInitialDataLoaded(): List<NflGame> {
        playerDao.insertAll(DefaultNflData.initialPlayers)

        val allExisting = nflGameDao.getAllGames().first()

        if (allExisting.isEmpty()) {
            val sampleGames = DefaultNflData.getSampleGames().filter { it.weekNumber <= 8 }
            nflGameDao.insertAll(sampleGames)

            val insertedGames = nflGameDao.getAllGames().first()

            val samplePicks = DefaultNflData.getSamplePicks(insertedGames).filter { it.weekNumber <= 8 }
            spreadPickDao.insertAll(samplePicks)

            return insertedGames
        }

        return emptyList()
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
