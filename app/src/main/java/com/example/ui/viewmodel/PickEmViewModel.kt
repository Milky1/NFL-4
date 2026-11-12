package com.example.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.firebase.FirestoreSyncService
import com.example.data.model.NflGame
import com.example.data.model.Player
import com.example.data.model.SpreadPick
import com.example.data.model.WeekSpreadLockStatus
import com.example.data.repository.PickEmRepository
import com.example.data.repository.SpreadsheetExportImport
import com.example.data.repository.WebSyncService
import com.example.data.stats.HeadToHeadRecord
import com.example.data.stats.PlayerRecord
import com.example.data.stats.StatsCalculator
import com.example.data.stats.TeamPickStat
import com.example.data.stats.WeeklyPrizeInfo
import com.example.data.stats.WeeklyTrendPoint
import com.example.data.stats.WeeklyWinnerSummary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.core.content.edit

enum class AppTab(val title: String) {
    PICKS("Picks"),
    SCORES("Scores"),
    WEEKLY("Weekly Standings"),
    STATS("Historical Stats"),
    LEAGUE("League & Sync")
}

class PickEmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PickEmRepository(AppDatabase.getDatabase(application))
    private val firestoreSyncService = FirestoreSyncService(application)
    private val privacyPrefs = application.getSharedPreferences("spread_rivals_privacy", Context.MODE_PRIVATE)

    private val _leaguePasscode = MutableStateFlow(
        privacyPrefs.getString("league_passcode", "RIVALS-CMC") ?: "RIVALS-CMC"
    )
    val leaguePasscode: StateFlow<String> = _leaguePasscode.asStateFlow()

    private val _markEmail = MutableStateFlow(
        privacyPrefs.getString("mark_email", "") ?: ""
    )
    val markEmail: StateFlow<String> = _markEmail.asStateFlow()

    private val _conorEmail = MutableStateFlow(
        privacyPrefs.getString("conor_email", "") ?: ""
    )
    val conorEmail: StateFlow<String> = _conorEmail.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    // League is unlocked if previously unlocked on this device, or if user is signed in as Clive or whitelisted email
    private val _isLeagueUnlocked = MutableStateFlow(
        privacyPrefs.getBoolean("league_unlocked", false) ||
        (Firebase.auth.currentUser?.email?.equals("clivecleary@gmail.com", ignoreCase = true) == true)
    )
    val isLeagueUnlocked: StateFlow<Boolean> = _isLeagueUnlocked.asStateFlow()

    private val _currentWeek = MutableStateFlow(9)
    val currentWeek: StateFlow<Int> = _currentWeek.asStateFlow()

    private val _selectedPlayerId = MutableStateFlow<Long?>(null)
    val selectedPlayerId: StateFlow<Long?> = _selectedPlayerId.asStateFlow()

    private val _currentTab = MutableStateFlow(AppTab.PICKS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // 24-hour spread lock set: historical weeks 1-8 are locked
    private val _lockedWeeks = MutableStateFlow<Set<Int>>((1..8).toSet())
    val lockedWeeks: StateFlow<Set<Int>> = _lockedWeeks.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    val players: StateFlow<List<Player>> = repository.allPlayers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGames: StateFlow<List<NflGame>> = repository.allGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPicks: StateFlow<List<SpreadPick>> = repository.allPicks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived: available weeks
    val availableWeeks: StateFlow<List<Int>> = allGames.combine(_currentWeek) { games, _ ->
        val existing = games.map { it.weekNumber }.distinct().sorted()
        if (existing.isEmpty()) (1..17).toList() else existing
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), (1..17).toList())

    // Derived: games for selected week
    val currentWeekGames: StateFlow<List<NflGame>> = combine(allGames, _currentWeek) { games, week ->
        games.filter { it.weekNumber == week }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived: picks for selected week
    val currentWeekPicks: StateFlow<List<SpreadPick>> = combine(allPicks, _currentWeek) { picks, week ->
        picks.filter { it.weekNumber == week }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived: 24h Spread Lock status for current week
    val currentWeekLockStatus: StateFlow<WeekSpreadLockStatus> = combine(
        _currentWeek,
        currentWeekGames,
        _lockedWeeks
    ) { week, games, lockedSet ->
        val isLocked = lockedSet.contains(week) || games.all { it.isFinal }
        val firstGame = games.minByOrNull { it.id }
        val firstGameInfo = if (firstGame != null) "${firstGame.gameDate} ${firstGame.kickoffTime}" else "Thu 8:15 PM ET"
        val deadline = when (week) {
            10 -> "Wed, Nov 6 at 8:15 PM ET (24h before kickoff)"
            1, 2, 3, 4, 5 -> "Locked 24h prior to kickoff (Official final lines)"
            else -> "Locks 24h before the first game of Week $week"
        }
        WeekSpreadLockStatus(
            weekNumber = week,
            isLocked = isLocked,
            lockDeadlineDescription = deadline,
            firstGameTime = firstGameInfo,
            sportsbook = "DraftKings"
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        WeekSpreadLockStatus(10, true, "Wed, Nov 6 at 8:15 PM ET (24h before kickoff)", "Thu 8:15 PM ET", "DraftKings")
    )

    // Derived: Weekly Summary for current week
    val currentWeeklySummary: StateFlow<WeeklyWinnerSummary?> = combine(
        currentWeekGames,
        players,
        currentWeekPicks,
        _currentWeek
    ) { games, playerList, picks, week ->
        if (playerList.isEmpty()) null
        else StatsCalculator.calculateWeeklySummary(week, games, playerList, picks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Derived: All Weekly Summaries
    val allWeeklySummaries: StateFlow<List<WeeklyWinnerSummary>> = combine(
        allGames,
        players,
        allPicks
    ) { games, playerList, picks ->
        if (playerList.isEmpty()) emptyList()
        else StatsCalculator.calculateAllWeeklySummaries(games, playerList, picks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived: Weekly Prize & Rollover Pool Info (€5 base, roll to €10+ on draw)
    val allWeeklyPrizes: StateFlow<Map<Int, WeeklyPrizeInfo>> = allWeeklySummaries.map { summaries ->
        StatsCalculator.calculateWeeklyPrizes(summaries)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Derived: Overall Leaderboard
    val overallLeaderboard: StateFlow<List<PlayerRecord>> = combine(
        allGames,
        players,
        allPicks
    ) { games, playerList, picks ->
        if (playerList.isEmpty()) emptyList()
        else StatsCalculator.calculateOverallLeaderboard(games, playerList, picks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived: Head-to-Head matchups
    val headToHeadList: StateFlow<List<HeadToHeadRecord>> = combine(
        allGames,
        players,
        allPicks
    ) { games, playerList, picks ->
        val results = mutableListOf<HeadToHeadRecord>()
        for (i in playerList.indices) {
            for (j in (i + 1) until playerList.size) {
                results.add(StatsCalculator.calculateHeadToHead(playerList[i], playerList[j], games, picks))
            }
        }
        results
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureInitialDataLoaded()
            val mostRecentWeek = repository.getMostRecentActiveWeek()
            _currentWeek.value = mostRecentWeek
        }

// Games Sync
// Games Sync (inside init)
        viewModelScope.launch {
            firestoreSyncService.observeCloudGames().collect { cloudGames ->
                Log.d("FirestoreSync", "Received ${cloudGames.size} games from Firestore")
                for (game in cloudGames) {

                    // Find the local game from memory
                    val localGame = allGames.value.find { it.id == game.id }

                    // If the game doesn't exist locally at all, or if its score/data changed, update it
                    if (localGame == null || localGame != game) {
                        repository.updateGameScore(
                            gameId = game.id,
                            awayScore = game.awayScore, // These will work if 'game' is your NflGame model
                            homeScore = game.homeScore,
                            isFinal = game.isFinal
                        )
                        Log.d("FirestoreSync", "Updated score for gameId: ${game.id}")
                    }
                }
            }
        }


        // Picks Sync
        viewModelScope.launch {
            firestoreSyncService.observeCloudPicks().collect { cloudPicks ->
                Log.d("FirestoreSync", "Received ${cloudPicks.size} picks from Firestore")
                for (cp in cloudPicks) {

                    // Check against your current in-memory picks list to see if it already matches
                    val existingPick = allPicks.value.find {
                        it.gameId == cp.gameId && it.playerId == cp.playerId
                    }

                    // Only update Room if the pick is new or the selected team is different
                    if (existingPick == null || existingPick.pickedTeamShort != cp.pickedTeamShort) {
                        repository.savePick(cp.gameId, cp.playerId, cp.weekNumber, cp.pickedTeamShort)
                        Log.d("FirestoreSync", "Saved pick for gameId: ${cp.gameId}, player: ${cp.playerId}, team: ${cp.pickedTeamShort}")
                    }
                }
            }
        }
    }

    fun onUserSignedIn(user: FirebaseUser) {
        _currentUser.value = user
        val email = user.email ?: ""
        val isClive = email.equals("clivecleary@gmail.com", ignoreCase = true)
        val isMark = email.equals(_markEmail.value, ignoreCase = true) && _markEmail.value.isNotBlank()
        val isConor = email.equals(_conorEmail.value, ignoreCase = true) && _conorEmail.value.isNotBlank()

        if (isClive || isMark || isConor) {
            _isLeagueUnlocked.value = true
            privacyPrefs.edit().putBoolean("league_unlocked", true).apply()
        }

        viewModelScope.launch {
            val assignedPlayer = when {
                isClive -> "Clive"
                isMark -> "Mark"
                isConor -> "Conor"
                else -> user.displayName ?: "Rival"
            }
            firestoreSyncService.saveUserProfile(assignedPlayer)
            for (game in currentWeekGames.value) {
                firestoreSyncService.uploadGame(game)
            }
            _snackbarMessage.value = "☁️ Signed in as $assignedPlayer ($email)! Private league verified."
        }
    }

    fun onUserSignedOut() {
        _currentUser.value = null
        _snackbarMessage.value = "Signed out of Firebase cloud sync"
    }

    @SuppressLint("UseKtx")
    fun unlockWithPasscode(inputCode: String): Boolean {
        val trimmed = inputCode.trim()
        val currentCode = _leaguePasscode.value.trim()
        if (trimmed.equals(currentCode, ignoreCase = true) || trimmed.equals("RIVALS-CMC", ignoreCase = true)) {
            _isLeagueUnlocked.value = true
            privacyPrefs.edit { putBoolean("league_unlocked", true) }
            _snackbarMessage.value = "🔓 Private league unlocked! Welcome to Clive, Mark & Conor's Pick'em."
            return true
        } else {
            _snackbarMessage.value = "❌ Incorrect league passcode. Ask Clive for the invite code."
            return false
        }
    }

    fun lockLeague() {
        _isLeagueUnlocked.value = false
        privacyPrefs.edit { putBoolean("league_unlocked", false) }
        _snackbarMessage.value = "🔒 League locked. Private access enabled."
    }

    @SuppressLint("UseKtx")
    fun updateAllowedEmails(mark: String, conor: String) {
        val m = mark.trim().lowercase()
        val c = conor.trim().lowercase()
        _markEmail.value = m
        _conorEmail.value = c
        privacyPrefs.edit { putString("mark_email", m).putString("conor_email", c) }

        viewModelScope.launch {
            if (m.isNotBlank()) firestoreSyncService.addEmailToWhitelist(m, "player_mark")
            if (c.isNotBlank()) firestoreSyncService.addEmailToWhitelist(c, "player_conor")
            _snackbarMessage.value = "✅ Mark & Conor's emails saved and synced to cloud whitelist!"
        }
    }

    fun updateLeaguePasscode(newCode: String) {
        val code = newCode.trim().ifBlank { "RIVALS-CMC" }
        _leaguePasscode.value = code
        privacyPrefs.edit { putString("league_passcode", code)}
        _snackbarMessage.value = "Passcode updated to $code"
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectWeek(week: Int) {
        _currentWeek.value = week
    }

    fun selectPlayer(playerId: Long) {
        _selectedPlayerId.value = playerId
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun toggleWeekSpreadLock(week: Int) {
        val current = _lockedWeeks.value.toMutableSet()
        if (current.contains(week)) {
            current.remove(week)
            _snackbarMessage.value = "Week $week DraftKings spreads unlocked for commissioner edit"
        } else {
            current.add(week)
            _snackbarMessage.value = "🔒 Week $week DraftKings spreads locked (Frozen 24h prior to kickoff)"
        }
        _lockedWeeks.value = current
    }

    fun makePick(gameId: Long, teamShort: String) {
        val currentPlayers = players.value
        val targetPlayerId = _selectedPlayerId.value ?: currentPlayers.firstOrNull()?.id ?: return
        val targetPlayer = currentPlayers.find { it.id == targetPlayerId }

        viewModelScope.launch {
            repository.savePick(
                gameId = gameId,
                playerId = targetPlayerId,
                weekNumber = _currentWeek.value,
                pickedTeamShort = teamShort
            )
            val pick = SpreadPick(
                gameId = gameId,
                playerId = targetPlayerId,
                weekNumber = _currentWeek.value,
                pickedTeamShort = teamShort
            )
            firestoreSyncService.uploadPick(pick, targetPlayer?.name ?: "Player")
        }
    }

    fun makePickForPlayer(gameId: Long, playerId: Long, teamShort: String) {
        val targetPlayer = players.value.find { it.id == playerId }
        viewModelScope.launch {
            repository.savePick(
                gameId = gameId,
                playerId = playerId,
                weekNumber = _currentWeek.value,
                pickedTeamShort = teamShort
            )
            if (firestoreSyncService.isUserSignedIn()) {
                val pick = SpreadPick(
                    gameId = gameId,
                    playerId = playerId,
                    weekNumber = _currentWeek.value,
                    pickedTeamShort = teamShort
                )
                firestoreSyncService.uploadPick(pick, targetPlayer?.name ?: "Player")
            }
        }
    }

    fun refreshFixtures(week: Int) {
        if (week < 9) {
            _snackbarMessage.value = "Fixtures can only be refreshed on current and future weeks (Week 9+)."
            return
        }
        viewModelScope.launch {
            val count = repository.refreshFixtures(week)
            _snackbarMessage.value = "🔄 Week $week fixtures refreshed ($count games). Away team first, Home team second!"
        }
    }

    fun saveAllChanges() {
        viewModelScope.launch {
            for (game in currentWeekGames.value) {
                val updates = mapOf(
                    "awayScore" to (game.awayScore ?: -1),
                    "homeScore" to (game.homeScore ?: -1),
                    "isFinal" to game.isFinal,
                    "homeSpread" to game.homeSpread
                )
                firestoreSyncService.updateGameDocument(game.id, updates)
            }
            for (pick in currentWeekPicks.value) {
                val player = players.value.find { it.id == pick.playerId }
                firestoreSyncService.uploadPick(pick, player?.name ?: "Player")
            }
            _snackbarMessage.value = "✅ Picks, scores, spreads, and results saved & synced to Firebase successfully!"
        }
    }

    fun refreshSpreads(week: Int, provider: String = "DraftKings") {
        viewModelScope.launch {
            val count = repository.refreshSpreads(week, provider)
            _snackbarMessage.value = "📊 Week $week spreads updated from $provider ($count games)."
        }
    }

    fun refreshFixturesAndSpreads(week: Int) {
        refreshFixtures(week)
        refreshSpreads(week)
    }

    fun updateScore(gameId: Long, awayScore: Int?, homeScore: Int?, isFinal: Boolean) {
        viewModelScope.launch {
            repository.updateGameScore(gameId, awayScore, homeScore, isFinal)
            if (firestoreSyncService.isUserSignedIn()) {
                val updates = mapOf(
                    "awayScore" to (awayScore ?: -1),
                    "homeScore" to (homeScore ?: -1),
                    "isFinal" to isFinal
                )
                firestoreSyncService.updateGameDocument(gameId, updates)
            }
        }
    }

    fun updateSpread(gameId: Long, newSpread: Double, sportsbook: String = "DraftKings") {
        val week = _currentWeek.value
        val isLocked = _lockedWeeks.value.contains(week)
        if (isLocked) {
            _snackbarMessage.value = "🔒 Spreads for Week $week are locked 24h prior to kickoff. Unlock in League settings to override."
            return
        }
        viewModelScope.launch {
            repository.updateGameSpread(gameId, newSpread, "DraftKings")
            if (firestoreSyncService.isUserSignedIn()) {
                val updates = mapOf(
                    "homeSpread" to newSpread,
                    "sportsbookProvider" to "DraftKings"
                )
                firestoreSyncService.updateGameDocument(gameId, updates)
            }
            _snackbarMessage.value = "DraftKings spread updated: ${if (newSpread > 0) "+$newSpread" else "$newSpread"}"
        }
    }

    fun addGame(
        weekNumber: Int,
        awayTeamShort: String,
        homeTeamShort: String,
        homeSpread: Double,
        kickoffTime: String,
        gameDate: String = "Sunday, Nov 10",
        tvNetwork: String = "CBS",
        venue: String = "",
        sportsbookProvider: String = "DraftKings"
    ) {
        viewModelScope.launch {
            repository.addCustomGame(
                weekNumber,
                awayTeamShort,
                homeTeamShort,
                homeSpread,
                kickoffTime,
                gameDate,
                tvNetwork,
                venue,
                "DraftKings"
            )
            _snackbarMessage.value = "DraftKings fixture added to Week $weekNumber"
        }
    }

    fun deleteGame(gameId: Long) {
        viewModelScope.launch {
            repository.deleteGame(gameId)
            _snackbarMessage.value = "Game removed"
        }
    }

    fun addPlayer(name: String, emoji: String, colorHex: Long) {
        viewModelScope.launch {
            repository.addPlayer(name, emoji, colorHex)
            _snackbarMessage.value = "Friend '$name' added to league!"
        }
    }

    fun updatePlayer(player: Player) {
        viewModelScope.launch {
            repository.updatePlayer(player)
            _snackbarMessage.value = "Player '${player.name}' updated"
        }
    }

    fun deletePlayer(playerId: Long) {
        viewModelScope.launch {
            repository.deletePlayer(playerId)
            if (_selectedPlayerId.value == playerId) {
                _selectedPlayerId.value = null
            }
            _snackbarMessage.value = "Player removed"
        }
    }

    fun getPlayerTrends(playerId: Long): List<WeeklyTrendPoint> {
        return StatsCalculator.calculateWeeklyTrends(playerId, allGames.value, allPicks.value)
    }

    fun getPlayerTeamPickStats(playerId: Long): List<TeamPickStat> {
        return StatsCalculator.calculateTeamPickStats(playerId, allGames.value, allPicks.value)
    }

    fun exportCsv(): String {
        return SpreadsheetExportImport.exportToCsv(allGames.value, players.value, allPicks.value)
    }

    fun importCsv(csvContent: String) {
        viewModelScope.launch {
            try {
                val parsed = SpreadsheetExportImport.parseCsvGames(csvContent)
                if (parsed.isEmpty()) {
                    _snackbarMessage.value = "No valid games or picks found in CSV"
                    return@launch
                }
                repository.importParsedRows(parsed)
                _snackbarMessage.value = "Imported ${parsed.size} games and picks successfully!"
            } catch (e: Exception) {
                _snackbarMessage.value = "Failed to import CSV: ${e.message}"
            }
        }
    }

    fun syncGoogleSheetUrl(url: String) {
        if (url.isBlank()) {
            _snackbarMessage.value = "Please enter a valid Google Sheets URL"
            return
        }
        viewModelScope.launch {
            _isSyncing.value = true
            val result = WebSyncService.fetchCsvFromUrl(url)
            _isSyncing.value = false
            result.onSuccess { csvText ->
                try {
                    val parsed = SpreadsheetExportImport.parseCsvGames(csvText)
                    if (parsed.isEmpty()) {
                        _snackbarMessage.value = "Spreadsheet loaded, but no valid game rows found."
                    } else {
                        repository.importParsedRows(parsed)
                        _snackbarMessage.value = "✅ Live sync complete! Imported ${parsed.size} fixtures and picks."
                    }
                } catch (e: Exception) {
                    _snackbarMessage.value = "Error parsing spreadsheet CSV: ${e.message}"
                }
            }.onFailure { err ->
                _snackbarMessage.value = "Sync failed: ${err.message}"
            }
        }
    }

    fun generateChatSummary(week: Int): String {
        val games = allGames.value.filter { it.weekNumber == week }
        val playerList = players.value
        val weekPicks = allPicks.value.filter { it.weekNumber == week }
        val lock = _lockedWeeks.value.contains(week)

        val sb = StringBuilder()
        sb.append("🏈 SPREAD RIVALS — WEEK $week PICKS\n")
        sb.append("📊 Source: DraftKings (Authoritative Spread)\n")
        sb.append(if (lock) "🔒 Status: Spreads LOCKED (Set 24h prior to kickoff)\n\n" else "⏱️ Status: Spreads Open\n\n")

        for (player in playerList) {
            sb.append("${player.avatarEmoji} ${player.name}:\n")
            val pPicks = weekPicks.filter { it.playerId == player.id }
            if (pPicks.isEmpty()) {
                sb.append("  (No picks submitted yet)\n")
            } else {
                for (pick in pPicks) {
                    val game = games.find { it.id == pick.gameId }
                    if (game != null) {
                        val spread = if (pick.pickedTeamShort.equals(game.homeTeamShort, ignoreCase = true)) {
                            game.formattedHomeSpread()
                        } else {
                            game.formattedAwaySpread()
                        }
                        val resultStr = if (game.isFinal) {
                            val cov = game.getCoveringTeamShort()
                            if (cov == "PUSH") " [PUSH]" else if (cov.equals(pick.pickedTeamShort, ignoreCase = true)) " [✅ COVER]" else " [❌ LOSS]"
                        } else ""
                        sb.append("  • ${pick.pickedTeamShort} ($spread)$resultStr\n")
                    }
                }
            }
            sb.append("\n")
        }

        val leaderboard = overallLeaderboard.value
        if (leaderboard.isNotEmpty()) {
            sb.append("🏆 Current Season Standings:\n")
            leaderboard.forEachIndexed { idx, rec ->
                sb.append("${idx + 1}. ${rec.player.name}: ${rec.wins}-${rec.losses}-${rec.pushes} (${rec.winPercentageFormatted}%)\n")
            }
        }

        return sb.toString()
    }

    fun resetCurrentWeekFixtures() {
        val week = _currentWeek.value
        viewModelScope.launch {
            repository.deleteGamesForWeek(week)
            _snackbarMessage.value = "🗑️ Week $week fixtures cleared. Ready for fresh CSV sync!"
        }
    }

    fun simulateWeekScores(week: Int) {
        viewModelScope.launch {
            val games = allGames.value.filter { it.weekNumber == week }
            for (game in games) {
                val base = 17 + Random.nextInt(14)
                val spreadEffect = (-game.homeSpread).toInt()
                val homeScore = (base + (spreadEffect / 2) + Random.nextInt(-7, 8)).coerceAtLeast(3)
                val awayScore = (base - (spreadEffect / 2) + Random.nextInt(-7, 8)).coerceAtLeast(3)
                repository.updateGameScore(game.id, awayScore, homeScore, isFinal = true)
            }
            _snackbarMessage.value = "Scores generated for Week $week!"
        }
    }

    fun clearWeekScores(week: Int) {
        viewModelScope.launch {
            val games = allGames.value.filter { it.weekNumber == week }
            for (game in games) {
                repository.updateGameScore(game.id, null, null, isFinal = false)
            }
            _snackbarMessage.value = "Scores reset for Week $week"
        }
    }

    fun deleteGamesForWeek(week: Int) {
        viewModelScope.launch {
            repository.deleteGamesForWeek(week)
            _snackbarMessage.value = "Week $week fixtures reset"
        }
    }
}
