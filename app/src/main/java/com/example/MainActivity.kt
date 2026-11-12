package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.firebase.FirestoreSyncService
import com.example.ui.components.PrivateLeagueGateScreen
import com.example.ui.screens.HistoricalStatsScreen
import com.example.ui.screens.LeagueSyncScreen
import com.example.ui.screens.PicksScreen
import com.example.ui.screens.ScoresScreen
import com.example.ui.screens.WeeklyStandingsScreen
import com.example.ui.theme.SpreadRivalsTheme
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.PickEmViewModel

class MainActivity : ComponentActivity() {
    private val syncComplete = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val firestoreSyncService = FirestoreSyncService(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firestoreSyncService.syncFromFirestore()
            } catch (e: Exception) {
                Log.d("SYNC", "ERROR: $e")
            }
            syncComplete.value = true
        }

        setContent {
            SpreadRivalsTheme {
                val synced by syncComplete.collectAsState()
                if (synced) {
                    MainApp()
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun MainApp(viewModel: PickEmViewModel = viewModel()) {
    val isLeagueUnlocked by viewModel.isLeagueUnlocked.collectAsStateWithLifecycle()
    val leaguePasscode by viewModel.leaguePasscode.collectAsStateWithLifecycle()
    val markEmail by viewModel.markEmail.collectAsStateWithLifecycle()
    val conorEmail by viewModel.conorEmail.collectAsStateWithLifecycle()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentWeek by viewModel.currentWeek.collectAsStateWithLifecycle()
    val availableWeeks by viewModel.availableWeeks.collectAsStateWithLifecycle()
    val players by viewModel.players.collectAsStateWithLifecycle()
    val selectedPlayerId by viewModel.selectedPlayerId.collectAsStateWithLifecycle()
    val currentWeekGames by viewModel.currentWeekGames.collectAsStateWithLifecycle()
    val currentWeekPicks by viewModel.currentWeekPicks.collectAsStateWithLifecycle()
    val currentWeeklySummary by viewModel.currentWeeklySummary.collectAsStateWithLifecycle()
    val allWeeklySummaries by viewModel.allWeeklySummaries.collectAsStateWithLifecycle()
    val overallLeaderboard by viewModel.overallLeaderboard.collectAsStateWithLifecycle()
    val headToHeadList by viewModel.headToHeadList.collectAsStateWithLifecycle()
    val lockStatus by viewModel.currentWeekLockStatus.collectAsStateWithLifecycle()
    val allWeeklyPrizes by viewModel.allWeeklyPrizes.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    if (!isLeagueUnlocked) {
        PrivateLeagueGateScreen(
            currentUser = currentUser,
            onUserSignedIn = { user -> viewModel.onUserSignedIn(user) },
            onUnlockWithPasscode = { code -> viewModel.unlockWithPasscode(code) }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = StadiumNavy900,
            snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F1728),
                contentColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.PICKS,
                    onClick = { viewModel.selectTab(AppTab.PICKS) },
                    icon = { Icon(Icons.Default.SportsFootball, contentDescription = "Picks") },
                    label = { Text("Picks", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StadiumNavy900,
                        selectedTextColor = TurfGreen,
                        indicatorColor = TurfGreen,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_tab_picks")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.SCORES,
                    onClick = { viewModel.selectTab(AppTab.SCORES) },
                    icon = { Icon(Icons.Default.SportsScore, contentDescription = "Scores") },
                    label = { Text("Scores", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StadiumNavy900,
                        selectedTextColor = TurfGreen,
                        indicatorColor = TurfGreen,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_tab_scores")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.WEEKLY,
                    onClick = { viewModel.selectTab(AppTab.WEEKLY) },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Weekly") },
                    label = { Text("Weekly", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StadiumNavy900,
                        selectedTextColor = StadiumGold,
                        indicatorColor = StadiumGold,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_tab_weekly")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.STATS,
                    onClick = { viewModel.selectTab(AppTab.STATS) },
                    icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Stats") },
                    label = { Text("Stats", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StadiumNavy900,
                        selectedTextColor = TurfGreen,
                        indicatorColor = TurfGreen,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_tab_stats")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.LEAGUE,
                    onClick = { viewModel.selectTab(AppTab.LEAGUE) },
                    icon = { Icon(Icons.Default.Group, contentDescription = "League") },
                    label = { Text("League", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StadiumNavy900,
                        selectedTextColor = TurfGreen,
                        indicatorColor = TurfGreen,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_tab_league")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.PICKS -> {
                    PicksScreen(
                        currentWeek = currentWeek,
                        availableWeeks = availableWeeks,
                        players = players,
                        selectedPlayerId = selectedPlayerId,
                        games = currentWeekGames,
                        picks = currentWeekPicks,
                        isWeekLocked = lockStatus.isLocked,
                        lockDeadlineDesc = lockStatus.lockDeadlineDescription,
                        onSelectWeek = { viewModel.selectWeek(it) },
                        onSelectPlayer = { viewModel.selectPlayer(it) },
                        onMakePick = { gameId, teamShort -> viewModel.makePick(gameId, teamShort) },
                        onMakePlayerPick = { gameId, playerId, teamShort -> viewModel.makePickForPlayer(gameId, playerId, teamShort) },
                        onRefreshFixtures = { week -> viewModel.refreshFixtures(week) },
                        onRefreshSpreads = { week -> viewModel.refreshSpreads(week) },
                        onRefreshSpreadsWithProvider = { week, provider -> viewModel.refreshSpreads(week, provider) },
                        onUpdateSpread = { gameId, newSpread, sportsbook -> viewModel.updateSpread(gameId, newSpread, sportsbook) },
                        onAddGame = { week, away, home, spread, time, date, tv, venue, sportsbook ->
                            viewModel.addGame(week, away, home, spread, time, date, tv, venue, sportsbook)
                        },
                        onToggleWeekSpreadLock = { week -> viewModel.toggleWeekSpreadLock(week) },
                        onSaveAll = { viewModel.saveAllChanges() }
                    )
                }

                AppTab.SCORES -> {
                    ScoresScreen(
                        currentWeek = currentWeek,
                        availableWeeks = availableWeeks,
                        players = players,
                        games = currentWeekGames,
                        picks = currentWeekPicks,
                        weeklySummary = currentWeeklySummary,
                        onSelectWeek = { viewModel.selectWeek(it) },
                        onUpdateScore = { gameId, away, home, final -> viewModel.updateScore(gameId, away, home, final) },
                        onRefreshFixtures = { week -> viewModel.refreshFixtures(week) },
                        onRefreshSpreads = { week -> viewModel.refreshSpreads(week) },
                        onUpdateSpread = { gameId, newSpread, sportsbook -> viewModel.updateSpread(gameId, newSpread, sportsbook) },
                        onClearScores = { week -> viewModel.clearWeekScores(week) },
                        onResetWeekFixtures = { week -> viewModel.deleteGamesForWeek(week) },
                        onSaveAll = { viewModel.saveAllChanges() }
                    )
                }

                AppTab.WEEKLY -> {
                    WeeklyStandingsScreen(
                        currentWeek = currentWeek,
                        availableWeeks = availableWeeks,
                        players = players,
                        games = currentWeekGames,
                        picks = currentWeekPicks,
                        weeklySummary = currentWeeklySummary,
                        allWeeklySummaries = allWeeklySummaries,
                        weeklyPrizes = allWeeklyPrizes,
                        onSelectWeek = { viewModel.selectWeek(it) }
                    )
                }

                AppTab.STATS -> {
                    HistoricalStatsScreen(
                        leaderboard = overallLeaderboard,
                        headToHeadList = headToHeadList,
                        getTrendsForPlayer = { playerId -> viewModel.getPlayerTrends(playerId) },
                        getTeamPickStatsForPlayer = { playerId -> viewModel.getPlayerTeamPickStats(playerId) }
                    )
                }

                AppTab.LEAGUE -> {
                    LeagueSyncScreen(
                        players = players,
                        currentUser = currentUser,
                        currentWeek = currentWeek,
                        isWeekLocked = lockStatus.isLocked,
                        isSyncing = isSyncing,
                        leaguePasscode = leaguePasscode,
                        markEmail = markEmail,
                        conorEmail = conorEmail,
                        onUpdateAllowedEmails = { mark, conor -> viewModel.updateAllowedEmails(mark, conor) },
                        onUpdatePasscode = { code -> viewModel.updateLeaguePasscode(code) },
                        onLockLeague = { viewModel.lockLeague() },
                        onSignInSuccess = { user -> viewModel.onUserSignedIn(user) },
                        onSignOut = { viewModel.onUserSignedOut() },
                        onToggleSpreadLock = { week -> viewModel.toggleWeekSpreadLock(week) },
                        onSyncGoogleSheetUrl = { url -> viewModel.syncGoogleSheetUrl(url) },
                        onGetGroupChatSummary = { week -> viewModel.generateChatSummary(week) },
                        onAddPlayer = { name, emoji, color -> viewModel.addPlayer(name, emoji, color) },
                        onUpdatePlayer = { player -> viewModel.updatePlayer(player) },
                        onDeletePlayer = { id -> viewModel.deletePlayer(id) },
                        onExportCsv = { viewModel.exportCsv() },
                        onImportCsv = { csv -> viewModel.importCsv(csv) },
                        onResetWeekFixtures = { week -> viewModel.deleteGamesForWeek(week) },
                        onShowMessage = { msg ->
                            // Handled via ViewModel
                        }
                    )
                }
            }
        }
    }
}
}
