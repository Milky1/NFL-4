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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.material3.MenuAnchorType
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
import kotlin.let

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

            // 3 Horizontal Refresh Buttons
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
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
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
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

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

    editingGameForSpread?.let { game ->
        EditSpreadDialog(
            game = game,
            availableGames = games, // <-- Pass games here
            onDismiss = { editingGameForSpread = null },
            onSaveSpread = { gameId, newSpread, sportsbook ->
                onUpdateSpread(gameId, newSpread, sportsbook)
                editingGameForSpread = null
            }
        )
    }

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
                            val pickedTeam = playerPick?.pickedTeamShort

                            val playerOutcome = if (game.isFinal && pickedTeam != null) {
                                val coverResult = game.getAtsResult()
                                when {
                                    coverResult == AtsResult.PUSH -> "PUSH"
                                    coverResult == AtsResult.HOME_COVERED && pickedTeam.equals(game.homeTeamShort, ignoreCase = true) -> "WON"
                                    coverResult == AtsResult.AWAY_COVERED && pickedTeam.equals(game.awayTeamShort, ignoreCase = true) -> "WON"
                                    coverResult == AtsResult.PENDING -> "PENDING"
                                    else -> "LOST"
                                }
                            } else null

                            val outcomeBg = when (playerOutcome) {
                                "WON" -> Color(0xFF0F766E)
                                "LOST" -> Color(0xFF7F1D1D)
                                "PUSH" -> Color(0xFF78350F)
                                else -> Color(0xFF222B40)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF131D31))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = player.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { onMakePlayerPick(player.id, game.awayTeamShort) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (pickedTeam.equals(game.awayTeamShort, ignoreCase = true)) Color(0xFF38BDF8) else Color(0xFF1E2B45),
                                            contentColor = if (pickedTeam.equals(game.awayTeamShort, ignoreCase = true)) Color(0xFF0F172A) else Color.White
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text(text = game.awayTeamShort, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { onMakePlayerPick(player.id, game.homeTeamShort) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (pickedTeam.equals(game.homeTeamShort, ignoreCase = true)) StadiumGold else Color(0xFF1E2B45),
                                            contentColor = if (pickedTeam.equals(game.homeTeamShort, ignoreCase = true)) StadiumNavy900 else Color.White
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text(text = game.homeTeamShort, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    if (playerOutcome != null) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = outcomeBg
                                        ) {
                                            Text(
                                                text = playerOutcome,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
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
    onPick: () -> Unit
) {
    val pickedPlayers = players.filter { player -> allPicksForTeam.any { it.playerId == player.id } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isPickedByActive) Color(0xFF1E2B45) else Color(0xFF0F172A))
            .border(
                width = if (isPickedByActive) 1.5.dp else 1.dp,
                color = if (isPickedByActive) StadiumGold else Color(0xFF1E2B45),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onPick() }
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = roleColor.copy(alpha = 0.2f),
                modifier = Modifier.width(42.dp)
            ) {
                Text(
                    text = roleLabel,
                    color = roleColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            TeamBadge(teamShort = teamShort, modifier = Modifier.size(28.dp))

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = teamName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (pickedPlayers.isNotEmpty()) {
                    Text(
                        text = "Picked by: ${pickedPlayers.joinToString(", ") { it.name }}",
                        color = StadiumGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (score != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1B2844)
                ) {
                    Text(
                        text = score.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF222B40)
            ) {
                Text(
                    text = spreadText,
                    color = StadiumGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isPickedByActive) StadiumGold else Color(0xFF1E2B45)),
                contentAlignment = Alignment.Center
            ) {
                if (isPickedByActive) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Picked",
                        tint = StadiumNavy900,
                        modifier = Modifier.size(14.dp)
                    )
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
    var spreadText by remember { mutableStateOf(game.homeSpread.toString()) }
    var sportsbook by remember { mutableStateOf(game.sportsbookProvider.ifBlank { "DraftKings" }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Spread (${game.awayTeamShort} @ ${game.homeTeamShort})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = spreadText,
                    onValueChange = { spreadText = it },
                    label = { Text("${game.homeTeamShort} Spread") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = sportsbook,
                    onValueChange = { sportsbook = it },
                    label = { Text("Sportsbook") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = spreadText.toDoubleOrNull() ?: game.homeSpread
                    onSaveSpread(game.id, parsed, sportsbook)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TurfGreen, contentColor = StadiumNavy900)
            ) {
                Text("Save Line", fontWeight = FontWeight.Bold)
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
    var awayTeam by remember { mutableStateOf("") }
    var homeTeam by remember { mutableStateOf("") }
    var spreadText by remember { mutableStateOf("-3.5") }
    var kickoffTime by remember { mutableStateOf("1:00 PM") }
    var gameDate by remember { mutableStateOf("Sunday") }
    var tvNetwork by remember { mutableStateOf("CBS") }
    var venue by remember { mutableStateOf("") }
    var sportsbook by remember { mutableStateOf("DraftKings") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Week $currentWeek Fixture",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = awayTeam,
                        onValueChange = { awayTeam = it.uppercase() },
                        label = { Text("Away (e.g. KC)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = homeTeam,
                        onValueChange = { homeTeam = it.uppercase() },
                        label = { Text("Home (e.g. BUF)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = spreadText,
                        onValueChange = { spreadText = it },
                        label = { Text("Home Spread") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = kickoffTime,
                        onValueChange = { kickoffTime = it },
                        label = { Text("Kickoff") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gameDate,
                        onValueChange = { gameDate = it },
                        label = { Text("Day / Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = tvNetwork,
                        onValueChange = { tvNetwork = it },
                        label = { Text("TV Network") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Stadium / Venue") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (awayTeam.isNotBlank() && homeTeam.isNotBlank()) {
                        val parsedSpread = spreadText.toDoubleOrNull() ?: 0.0
                        onAddGame(
                            currentWeek,
                            awayTeam.trim(),
                            homeTeam.trim(),
                            parsedSpread,
                            kickoffTime.trim(),
                            gameDate.trim(),
                            tvNetwork.trim(),
                            venue.trim(),
                            sportsbook.trim()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TurfGreen, contentColor = StadiumNavy900)
            ) {
                Text("Add Game", fontWeight = FontWeight.Bold)
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