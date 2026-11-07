package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import com.example.data.stats.StatsCalculator
import com.example.data.model.AtsResult
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NflGame
import com.example.data.model.NflTeams
import com.example.data.model.Player
import com.example.data.model.SpreadPick
import com.example.ui.components.HeroBanner
import com.example.ui.components.PlayerSelectorBar
import com.example.ui.components.TeamBadge
import com.example.ui.components.WeekSelector
import com.example.ui.theme.CoralRed
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumGoldLight
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen

@Composable
fun PicksScreen(
    currentWeek: Int,
    availableWeeks: List<Int>,
    players: List<Player>,
    selectedPlayerId: Long?,
    games: List<NflGame>,
    picks: List<SpreadPick>,
    isWeekLocked: Boolean = false,
    lockDeadlineDesc: String = "",
    onSelectWeek: (Int) -> Unit,
    onSelectPlayer: (Long) -> Unit,
    onMakePick: (Long, String) -> Unit,
    onMakePlayerPick: (Long, Long, String) -> Unit = { _, _, _ -> },
    onRefreshFixturesAndSpreads: (Int) -> Unit = {},
    onRefreshFixtures: (Int) -> Unit = onRefreshFixturesAndSpreads,
    onRefreshSpreads: (Int) -> Unit = onRefreshFixturesAndSpreads,
    onRefreshSpreadsWithProvider: (Int, String) -> Unit = { week, _ -> onRefreshSpreads(week) },
    onUpdateSpread: (Long, Double, String) -> Unit,
    onAddGame: (Int, String, String, Double, String, String, String, String, String) -> Unit,
    onToggleWeekSpreadLock: (Int) -> Unit = {},
    onSaveAll: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activePlayer = players.find { it.id == selectedPlayerId } ?: players.firstOrNull()

    var selectedProvider by remember { mutableStateOf("DraftKings") }
    var editingGameForSpread by remember { mutableStateOf<NflGame?>(null) }
    var showAddGameDialog by remember { mutableStateOf(false) }
    var showLockedNoticeDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                HeroBanner(currentWeek = currentWeek, totalPlayers = players.size)
            }

            // 3 Horizontal Refresh Buttons: Refresh Fixtures, Refresh Spreads, Refresh Match Scores
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("week_refresh_controls_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "WEEK $currentWeek LIVE ACTIONS",
                                color = TurfGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Button 1: Refresh Fixtures
                            Button(
                                onClick = { onRefreshFixtures(currentWeek) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1A263E),
                                    contentColor = Color.White
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("refresh_fixtures_btn")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Fixtures",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Refresh\nFixtures",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF38BDF8),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 12.sp
                                    )
                                }
                            }

                            // Button 2: Refresh Spreads
                            Button(
                                onClick = { onRefreshSpreads(currentWeek) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF22283A),
                                    contentColor = Color.White
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumGold),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("refresh_spreads_btn")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = "Spreads",
                                        tint = StadiumGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Refresh\nSpreads",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = StadiumGold,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        // Lock Spreads Button
                        Button(
                            onClick = { onToggleWeekSpreadLock(currentWeek) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isWeekLocked) Color(0xFF1E2B45) else Color(0xFF22283A),
                                contentColor = StadiumGold
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumGold),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("toggle_week_lock_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = StadiumGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isWeekLocked) "🔒 Week $currentWeek Spreads Locked (Frozen)" else "🔓 Lock Week $currentWeek Spreads",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = StadiumGold
                                )
                            }
                        }


                    }
                }
            }

            item {
                Text(
                    text = "NFL SCHEDULE & DRAFTKINGS SPREADS",
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                WeekSelector(
                    availableWeeks = availableWeeks,
                    selectedWeek = currentWeek,
                    onSelectWeek = onSelectWeek
                )
            }

            item {
                PlayerSelectorBar(
                    players = players,
                    selectedPlayerId = activePlayer?.id,
                    onSelectPlayer = onSelectPlayer,
                    getPicksCount = { playerId ->
                        val pickedCount = picks.count { it.playerId == playerId && games.any { g -> g.id == it.gameId } }
                        Pair(pickedCount, games.size)
                    }
                )
            }

            if (games.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No games scheduled for Week $currentWeek",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap the + button below or 'Refresh Spreads' to populate matchups.",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(games, key = { it.id }) { game ->
                    GamePickCard(
                        game = game,
                        players = players,
                        activePlayer = activePlayer,
                        picks = picks.filter { it.gameId == game.id },
                        isLocked = isWeekLocked,
                        onPickTeam = { teamShort ->
                            onMakePick(game.id, teamShort)
                        },
                        onMakePlayerPick = { targetPlayerId, teamShort ->
                            onMakePlayerPick(game.id, targetPlayerId, teamShort)
                        },
                        onEditSpread = {
                            if (isWeekLocked) {
                                showLockedNoticeDialog = true
                            } else {
                                editingGameForSpread = game
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB & nav bar
            }
        }

        // Save FAB (saves picks, spreads, and results)
        FloatingActionButton(
            onClick = { onSaveAll() },
            containerColor = TurfGreen,
            contentColor = StadiumNavy900,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("save_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }

    // Spread Editing Dialog
    editingGameForSpread?.let { game ->
        EditSpreadDialog(
            game = game,
            availableGames = games,
            onDismiss = { editingGameForSpread = null },
            onSaveSpread = { gameId, newSpread, sportsbook ->
                onUpdateSpread(gameId, newSpread, sportsbook)
                editingGameForSpread = null
            }
        )
    }

    // Add Custom Game Dialog
    if (showAddGameDialog) {
        AddGameDialog(
            currentWeek = currentWeek,
            onDismiss = { showAddGameDialog = false },
            onAddGame = { week, away, home, spread, time, date, tv, venue, sportsbook ->
                onAddGame(week, away, home, spread, time, date, tv, venue, sportsbook)
                showAddGameDialog = false
            }
        )
    }



    // Spread locked notice modal
    if (showLockedNoticeDialog) {
        AlertDialog(
            onDismissRequest = { showLockedNoticeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = StadiumGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DraftKings Spread Locked",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Text(
                    text = "Per league rules, DraftKings point spreads freeze 24 hours prior to the first game each week (Wednesday evening before Thursday Night Football). Spreads cannot be changed after lock.\n\n(Commissioner can unlock in the League tab if emergency adjustments are needed).",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showLockedNoticeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TurfGreen, contentColor = StadiumNavy900)
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}

@Composable
fun GamePickCard(
    game: NflGame,
    players: List<Player>,
    activePlayer: Player?,
    picks: List<SpreadPick>,
    isLocked: Boolean = false,
    onPickTeam: (String) -> Unit,
    onMakePlayerPick: (Long, String) -> Unit = { _, _ -> },
    onEditSpread: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activePlayerPick = picks.find { it.playerId == activePlayer?.id }
    val allPlayersWrong = StatsCalculator.areAllPlayersWrong(game, players, picks)
    val atsResult = game.getAtsResult()

    val containerColor = if (allPlayersWrong) Color(0xFF280B16) else Color(0xFF131D31)
    val borderColor = if (allPlayersWrong) Color(0xFFEF4444) else Color(0xFF1E2B45)
    val borderWidth = if (allPlayersWrong) 2.dp else 1.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("game_card_${game.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Unanimous Loss Highlight Banner (if all players got the same game wrong)
            if (allPlayersWrong) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEF4444),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .testTag("all_players_wrong_banner_${game.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "💀 ALL 3 PLAYERS WRONG (0/3 ATS) — UNANIMOUS MISS!",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            } else if (game.isFinal && game.homeScore != null && game.awayScore != null) {
                // Game is final: ATS Result Banner (Larger with white text)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (atsResult == AtsResult.PUSH) Color(0xFF854D0E) else Color(0xFF0F766E),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val atsText = when (atsResult) {
                            AtsResult.HOME_COVERED -> "${game.homeTeamShort} COVERED (${game.homeScore} - ${game.awayScore})"
                            AtsResult.AWAY_COVERED -> "${game.awayTeamShort} COVERED (${game.awayScore} - ${game.homeScore})"
                            AtsResult.PUSH -> "PUSH (${game.awayScore} - ${game.homeScore})"
                            AtsResult.PENDING -> "In Progress"
                        }
                        Text(
                            text = atsText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Header: Fixture Date, Time, Network, and Sportsbook Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = game.fullFixtureInfo,
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (game.isFinal) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TurfGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "FINAL",
                                    color = TurfGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    if (game.venue.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Venue",
                                tint = StadiumGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = game.venue,
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Sportsbook Badge & Edit Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEditSpread() }
                        .background(Color(0xFF1B2844))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("edit_spread_${game.id}")
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF00C853).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = game.sportsbookProvider,
                            color = Color(0xFF69F0AE),
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = game.lineSummary,
                        color = StadiumGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.Edit,
                        contentDescription = if (isLocked) "Spread Locked" else "Edit Spread",
                        tint = if (isLocked) StadiumGold else Color(0xFF94A3B8),
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // THE AWAY TEAM FIRST (Top Row)
            TeamMatchupRow(
                roleLabel = "AWAY",
                roleColor = Color(0xFF38BDF8),
                teamShort = game.awayTeamShort,
                teamName = game.awayTeam,
                spreadText = game.formattedAwaySpread(),
                score = game.awayScore,
                isFinal = game.isFinal,
                isPickedByActive = activePlayerPick?.pickedTeamShort.equals(game.awayTeamShort, ignoreCase = true),
                activePlayer = activePlayer,
                allPicksForTeam = picks.filter { it.pickedTeamShort.equals(game.awayTeamShort, ignoreCase = true) },
                players = players,
                onPick = { onPickTeam(game.awayTeamShort) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // THE HOME TEAM SECOND (Bottom Row)
            TeamMatchupRow(
                roleLabel = "HOME",
                roleColor = StadiumGold,
                teamShort = game.homeTeamShort,
                teamName = game.homeTeam,
                spreadText = game.formattedHomeSpread(),
                score = game.homeScore,
                isFinal = game.isFinal,
                isPickedByActive = activePlayerPick?.pickedTeamShort.equals(game.homeTeamShort, ignoreCase = true),
                activePlayer = activePlayer,
                allPicksForTeam = picks.filter { it.pickedTeamShort.equals(game.homeTeamShort, ignoreCase = true) },
                players = players,
                onPick = { onPickTeam(game.homeTeamShort) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // EACH PLAYER'S PICKS & HIGHLIGHT OUTCOMES
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (allPlayersWrong) Color(0xFF1D0911) else Color(0xFF182238),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (allPlayersWrong) Color(0xFF8B1E32) else Color(0xFF22304E)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (game.isFinal) "PLAYER SPREAD OUTCOMES" else "EACH PLAYER'S PICKS",
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                        if (!game.isFinal) {
                            Text(
                                text = "Tap buttons below to set pick for each player",
                                color = Color(0xFF64748B),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (player in players) {
                            val playerPick = picks.find { it.playerId == player.id }
                            val outcome = StatsCalculator.evaluatePick(game, playerPick)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Player Name and Avatar
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(player.avatarColorHex).copy(alpha = 0.25f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(player.avatarColorHex)),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = player.symbol,
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = if (player.symbol.length > 1) 9.sp else 11.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = player.name,
                                        color = Color(player.avatarColorHex),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                if (game.isFinal) {
                                    // Highlight for each player who got it correct, and highlight all wrong
                                    when (outcome) {
                                        StatsCalculator.PickOutcome.WIN -> {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFF0F3E22),
                                                border = androidx.compose.foundation.BorderStroke(1.5.dp, TurfGreen)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Win",
                                                        tint = TurfGreen,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "${playerPick?.pickedTeamShort} • WIN",
                                                        color = TurfGreen,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                        StatsCalculator.PickOutcome.LOSS -> {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (allPlayersWrong) Color(0xFF380C1A) else Color(0xFF1E1420),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (allPlayersWrong) Color(0xFFEF4444) else CoralRed.copy(alpha = 0.5f)
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Loss",
                                                        tint = if (allPlayersWrong) Color(0xFFEF4444) else CoralRed,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "${playerPick?.pickedTeamShort ?: "None"} • LOSS",
                                                        color = if (allPlayersWrong) Color(0xFFEF4444) else CoralRed,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                        StatsCalculator.PickOutcome.PUSH -> {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = StadiumGold.copy(alpha = 0.2f),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumGold)
                                            ) {
                                                Text(
                                                    text = "${playerPick?.pickedTeamShort} • PUSH",
                                                    color = StadiumGold,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                        else -> {
                                            Text(
                                                text = "No Pick",
                                                color = Color(0xFF64748B),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                } else {
                                    // Game is NOT final: Interactive pick toggle buttons for each player
                                    Column(horizontalAlignment = Alignment.End) {
                                        // Highlight if pick changed from first pick
                                        if (playerPick != null && playerPick.isChangedFromFirstPick) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                                                modifier = Modifier.padding(bottom = 3.dp)
                                            ) {
                                                Text(
                                                    text = "⚠️ Changed from 1st pick: ${playerPick.firstPickTeamShort}",
                                                    color = Color(0xFFFBBF24),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            val pickedAway = playerPick?.pickedTeamShort.equals(game.awayTeamShort, ignoreCase = true)
                                            val pickedHome = playerPick?.pickedTeamShort.equals(game.homeTeamShort, ignoreCase = true)
                                            val isChangedPick = playerPick != null && playerPick.isChangedFromFirstPick

                                            // Away pick chip
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (pickedAway) {
                                                    if (isChangedPick) Color(0xFFB45309).copy(alpha = 0.4f) else Color(player.avatarColorHex).copy(alpha = 0.3f)
                                                } else Color(0xFF1E2844),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    if (pickedAway) 1.5.dp else 1.dp,
                                                    if (pickedAway) {
                                                        if (isChangedPick) Color(0xFFF59E0B) else Color(player.avatarColorHex)
                                                    } else Color(0xFF283858)
                                                ),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { onMakePlayerPick(player.id, game.awayTeamShort) }
                                                    .testTag("pick_${player.name}_${game.awayTeamShort}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "${game.awayTeamShort} ${game.formattedAwaySpread()}".trim(),
                                                        color = if (pickedAway) Color.White else Color(0xFF94A3B8),
                                                        fontWeight = if (pickedAway) FontWeight.Black else FontWeight.Medium,
                                                        fontSize = 11.sp
                                                    )
                                                    if (pickedAway && isChangedPick) {
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text("★", color = Color(0xFFFBBF24), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                            // Home pick chip
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (pickedHome) {
                                                    if (isChangedPick) Color(0xFFB45309).copy(alpha = 0.4f) else Color(player.avatarColorHex).copy(alpha = 0.3f)
                                                } else Color(0xFF1E2844),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    if (pickedHome) 1.5.dp else 1.dp,
                                                    if (pickedHome) {
                                                        if (isChangedPick) Color(0xFFF59E0B) else Color(player.avatarColorHex)
                                                    } else Color(0xFF283858)
                                                ),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { onMakePlayerPick(player.id, game.homeTeamShort) }
                                                    .testTag("pick_${player.name}_${game.homeTeamShort}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "${game.homeTeamShort} ${game.formattedHomeSpread()}".trim(),
                                                        color = if (pickedHome) Color.White else Color(0xFF94A3B8),
                                                        fontWeight = if (pickedHome) FontWeight.Black else FontWeight.Medium,
                                                        fontSize = 11.sp
                                                    )
                                                    if (pickedHome && isChangedPick) {
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text("★", color = Color(0xFFFBBF24), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamMatchupRow(
    roleLabel: String,
    roleColor: Color,
    teamShort: String,
    teamName: String,
    spreadText: String,
    score: Int?,
    isFinal: Boolean,
    isPickedByActive: Boolean,
    activePlayer: Player?,
    allPicksForTeam: List<SpreadPick>,
    players: List<Player>,
    onPick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = activePlayer?.let { Color(it.avatarColorHex) } ?: TurfGreen

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isPickedByActive) Color(0xFF1D2E50) else Color(0xFF182238),
        border = androidx.compose.foundation.BorderStroke(
            if (isPickedByActive) 2.dp else 1.dp,
            if (isPickedByActive) activeColor else Color(0xFF253350)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onPick() }
            .testTag("matchup_row_${teamShort}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Team identity with Away / Home badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                TeamBadge(teamCode = teamShort, size = 34)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = roleColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = roleLabel,
                                color = roleColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = teamShort,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = teamName,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right side: Spread & Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Friends who picked this team
                if (allPicksForTeam.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (pick in allPicksForTeam) {
                            val player = players.find { it.id == pick.playerId } ?: continue
                            Surface(
                                shape = CircleShape,
                                color = Color(player.avatarColorHex).copy(alpha = 0.3f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(player.avatarColorHex)),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = player.symbol,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = if (player.symbol.length > 1) 8.sp else 9.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Spread Badge (only for the favorite)
                if (spreadText.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StadiumGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = spreadText,
                            color = StadiumGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Final Game Score if concluded
                if (isFinal && score != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Text(
                            text = score.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSpreadDialog(
    game: NflGame,
    availableGames: List<NflGame> = emptyList(),
    onDismiss: () -> Unit,
    onSaveSpread: (Long, Double, String) -> Unit
) {
    var currentGame by remember(game) { mutableStateOf(game) }
    var gameExpanded by remember { mutableStateOf(false) }

    // Favorite: "AWAY", "HOME", "EVEN"
    val initialFav = when {
        currentGame.homeSpread > 0 -> "AWAY"
        currentGame.homeSpread < 0 -> "HOME"
        else -> "EVEN"
    }
    var selectedFavorite by remember(currentGame) { mutableStateOf(initialFav) }

    val initialPoints = when {
        currentGame.homeSpread != 0.0 -> String.format(java.util.Locale.US, "%.1f", kotlin.math.abs(currentGame.homeSpread))
        else -> "0.0"
    }
    var pointsText by remember(currentGame) { mutableStateOf(initialPoints) }
    var selectedSportsbook by remember(currentGame) { mutableStateOf(currentGame.sportsbookProvider) }
    var sportsbookExpanded by remember { mutableStateOf(false) }

    val sportsbooks = listOf("DraftKings", "FanDuel", "BetMGM", "Caesars", "ESPN BET", "Manual Entry")

    // Calculated homeSpread
    val calculatedHomeSpread = remember(selectedFavorite, pointsText) {
        val pts = pointsText.toDoubleOrNull() ?: 0.0
        val absPts = kotlin.math.abs(pts)
        when (selectedFavorite) {
            "AWAY" -> absPts
            "HOME" -> -absPts
            else -> 0.0
        }
    }

    val previewText = remember(currentGame, selectedFavorite, pointsText, selectedSportsbook) {
        val pts = pointsText.toDoubleOrNull() ?: 0.0
        val absPts = kotlin.math.abs(pts)
        val line = when (selectedFavorite) {
            "AWAY" -> "${currentGame.awayTeamShort} -${String.format(java.util.Locale.US, "%.1f", absPts)}"
            "HOME" -> "${currentGame.homeTeamShort} -${String.format(java.util.Locale.US, "%.1f", absPts)}"
            else -> "PICK'EM (0.0)"
        }
        "$line ($selectedSportsbook)"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = StadiumGold, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Spread / Manual Line",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (availableGames.size > 1) {
                    ExposedDropdownMenuBox(
                        expanded = gameExpanded,
                        onExpandedChange = { gameExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${currentGame.awayTeamShort} @ ${currentGame.homeTeamShort} (${currentGame.lineSummary})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Matchup") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gameExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TurfGreen,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = gameExpanded,
                            onDismissRequest = { gameExpanded = false }
                        ) {
                            availableGames.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text("${g.awayTeamShort} @ ${g.homeTeamShort} (${g.lineSummary})") },
                                    onClick = {
                                        currentGame = g
                                        gameExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "${currentGame.awayTeam} @ ${currentGame.homeTeam}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Who is favored?
                Text(
                    text = "WHO IS FAVORED?",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFavorite == "AWAY") TurfGreen else Color(0xFF1B2844),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedFavorite == "AWAY") TurfGreen else Color(0xFF334155)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFavorite = "AWAY" }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${currentGame.awayTeamShort} (-)",
                                color = if (selectedFavorite == "AWAY") StadiumNavy900 else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "AWAY",
                                color = if (selectedFavorite == "AWAY") StadiumNavy900 else Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFavorite == "HOME") TurfGreen else Color(0xFF1B2844),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedFavorite == "HOME") TurfGreen else Color(0xFF334155)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFavorite = "HOME" }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${currentGame.homeTeamShort} (-)",
                                color = if (selectedFavorite == "HOME") StadiumNavy900 else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "HOME",
                                color = if (selectedFavorite == "HOME") StadiumNavy900 else Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFavorite == "EVEN") StadiumGold else Color(0xFF1B2844),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedFavorite == "EVEN") StadiumGold else Color(0xFF334155)),
                        modifier = Modifier
                            .weight(0.8f)
                            .clickable {
                                selectedFavorite = "EVEN"
                                pointsText = "0.0"
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "EVEN",
                                color = if (selectedFavorite == "EVEN") StadiumNavy900 else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "PK (0.0)",
                                color = if (selectedFavorite == "EVEN") StadiumNavy900 else Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                if (selectedFavorite != "EVEN") {
                    OutlinedTextField(
                        value = pointsText,
                        onValueChange = { pointsText = it },
                        label = { Text("Spread Points (e.g. 10.0 or 3.5)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TurfGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("spread_points_input")
                    )
                }

                // Sportsbook Provider Dropdown
                ExposedDropdownMenuBox(
                    expanded = sportsbookExpanded,
                    onExpandedChange = { sportsbookExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSportsbook,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sportsbook / Provider") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sportsbookExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TurfGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = sportsbookExpanded,
                        onDismissRequest = { sportsbookExpanded = false }
                    ) {
                        sportsbooks.forEach { book ->
                            DropdownMenuItem(
                                text = { Text(book) },
                                onClick = {
                                    selectedSportsbook = book
                                    sportsbookExpanded = false
                                }
                            )
                        }
                    }
                }

                // Live Preview Banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StadiumGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "PREVIEW (ALWAYS MINUS NOTATION):",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = previewText,
                            color = StadiumGoldLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveSpread(currentGame.id, calculatedHomeSpread, selectedSportsbook)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TurfGreen, contentColor = StadiumNavy900),
                modifier = Modifier.testTag("save_spread_btn")
            ) {
                Text("Save Spread", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        },
        containerColor = Color(0xFF131D31)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameDialog(
    currentWeek: Int,
    onDismiss: () -> Unit,
    onAddGame: (Int, String, String, Double, String, String, String, String, String) -> Unit
) {
    val allTeams = NflTeams.teams
    var awayCode by remember { mutableStateOf("DAL") }
    var homeCode by remember { mutableStateOf("PHI") }
    var spreadText by remember { mutableStateOf("-3.5") }
    var selectedSportsbook by remember { mutableStateOf("DraftKings") }
    var gameDate by remember { mutableStateOf("Sun, Nov 10") }
    var kickoffTime by remember { mutableStateOf("1:00 PM ET") }
    var tvNetwork by remember { mutableStateOf("CBS") }
    var venue by remember { mutableStateOf("") }

    var awayExpanded by remember { mutableStateOf(false) }
    var homeExpanded by remember { mutableStateOf(false) }
    var sportsbookExpanded by remember { mutableStateOf(false) }

    val sportsbooks = listOf("DraftKings", "FanDuel", "BetMGM", "Caesars", "ESPN BET", "Consensus")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Fixture to Week $currentWeek",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Away Team Selector
                    ExposedDropdownMenuBox(
                        expanded = awayExpanded,
                        onExpandedChange = { awayExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${NflTeams.getByCode(awayCode).fullName} ($awayCode)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Away Team") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = awayExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TurfGreen,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = awayExpanded,
                            onDismissRequest = { awayExpanded = false }
                        ) {
                            allTeams.forEach { team ->
                                DropdownMenuItem(
                                    text = { Text("${team.fullName} (${team.code})") },
                                    onClick = {
                                        awayCode = team.code
                                        awayExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Home Team Selector
                    ExposedDropdownMenuBox(
                        expanded = homeExpanded,
                        onExpandedChange = { homeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${NflTeams.getByCode(homeCode).fullName} ($homeCode)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Home Team") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = homeExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TurfGreen,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = homeExpanded,
                            onDismissRequest = { homeExpanded = false }
                        ) {
                            allTeams.forEach { team ->
                                DropdownMenuItem(
                                    text = { Text("${team.fullName} (${team.code})") },
                                    onClick = {
                                        homeCode = team.code
                                        homeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Sportsbook Provider Selector
                    ExposedDropdownMenuBox(
                        expanded = sportsbookExpanded,
                        onExpandedChange = { sportsbookExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedSportsbook,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sportsbook Provider") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sportsbookExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TurfGreen,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = sportsbookExpanded,
                            onDismissRequest = { sportsbookExpanded = false }
                        ) {
                            sportsbooks.forEach { book ->
                                DropdownMenuItem(
                                    text = { Text(book) },
                                    onClick = {
                                        selectedSportsbook = book
                                        sportsbookExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Home Spread Input
                    OutlinedTextField(
                        value = spreadText,
                        onValueChange = { spreadText = it },
                        label = { Text("Home Spread (e.g. -3.5 or +2.5)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TurfGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // Fixture Date
                    OutlinedTextField(
                        value = gameDate,
                        onValueChange = { gameDate = it },
                        label = { Text("Fixture Date (e.g. Sun, Nov 10)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TurfGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // Kickoff Time
                    OutlinedTextField(
                        value = kickoffTime,
                        onValueChange = { kickoffTime = it },
                        label = { Text("Kickoff Time (e.g. 1:00 PM ET)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TurfGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // TV Broadcast Network
                    OutlinedTextField(
                        value = tvNetwork,
                        onValueChange = { tvNetwork = it },
                        label = { Text("TV Network (e.g. CBS, FOX, NBC, ESPN)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TurfGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // Stadium / Venue
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Stadium / Venue (Optional)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TurfGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val spread = spreadText.toDoubleOrNull() ?: -3.0
                    onAddGame(currentWeek, awayCode, homeCode, spread, kickoffTime, gameDate, tvNetwork, venue, selectedSportsbook)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TurfGreen, contentColor = StadiumNavy900)
            ) {
                Text("Add Fixture", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        },
        containerColor = Color(0xFF131D31)
    )
}
