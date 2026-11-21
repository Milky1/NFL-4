package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AtsResult
import com.example.data.model.NflGame
import com.example.data.model.Player
import com.example.data.model.SpreadPick
import com.example.data.stats.StatsCalculator
import com.example.data.stats.WeeklyWinnerSummary
import com.example.ui.components.TeamBadge
import com.example.ui.components.WeekSelector
import com.example.ui.theme.CoralRed
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen

@Composable
fun ScoresScreen(
    currentWeek: Int,
    availableWeeks: List<Int>,
    players: List<Player>,
    games: List<NflGame>,
    picks: List<SpreadPick>,
    weeklySummary: WeeklyWinnerSummary?,
    onSelectWeek: (Int) -> Unit,
    onUpdateScore: (Long, Int?, Int?, Boolean) -> Unit,
    onUpdateSpread: (Long, Double, String) -> Unit = { _, _, _ -> },
    onClearScores: (Int) -> Unit,
    onSaveAll: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var editingGameForScore by remember { mutableStateOf<NflGame?>(null) }
    var editingGameForSpread by remember { mutableStateOf<NflGame?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ENTER & UPDATE SCORES",
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1B2844),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3C62)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onClearScores(currentWeek) }
                                .testTag("clear_scores_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Clear",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                WeekSelector(
                    availableWeeks = availableWeeks,
                    selectedWeek = currentWeek,
                    onSelectWeek = onSelectWeek
                )
            }


            if (weeklySummary != null && weeklySummary.playerResults.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2844))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "WEEK $currentWeek STANDINGS",
                                color = StadiumGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (res in weeklySummary.playerResults) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = res.player.avatarEmoji, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = res.player.name,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = res.formattedRecord,
                                            color = if (res.wins > res.losses) TurfGreen else Color(0xFFCBD5E1),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "${res.winPercentage.toInt()}% ATS",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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
                        }
                    }
                }
            } else {
                items(games, key = { it.id }) { game ->
                    GameScoreCard(
                        game = game,
                        players = players,
                        picks = picks.filter { it.gameId == game.id },
                        onEditScore = { editingGameForScore = game },
                        onEditSpread = { editingGameForSpread = game }
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
                .testTag("scores_save_fab")
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

    editingGameForScore?.let { game ->
        EditScoreDialog(
            game = game,
            onDismiss = { editingGameForScore = null },
            onSave = { away, home, final ->
                onUpdateScore(game.id, away, home, final)
                editingGameForScore = null
            }
        )
    }

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
}

@Composable
fun GameScoreCard(
    game: NflGame,
    players: List<Player>,
    picks: List<SpreadPick>,
    onEditScore: () -> Unit,
    onEditSpread: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val atsResult = game.getAtsResult()
    val allPlayersWrong = StatsCalculator.areAllPlayersWrong(game, players, picks)

    val containerColor = if (allPlayersWrong) Color(0xFF280B16) else Color(0xFF131D31)
    val borderColor = if (allPlayersWrong) Color(0xFFEF4444) else Color(0xFF1E2B45)
    val borderWidth = if (allPlayersWrong) 2.dp else 1.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("score_card_${game.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(borderWidth, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (allPlayersWrong) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEF4444),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .testTag("all_players_wrong_scores_banner_${game.id}")
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
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${game.gameDate} ${game.kickoffTime}" + if (game.tvNetwork.isNotBlank()) " • ${game.tvNetwork}" else "",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (game.venue.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 1.dp)) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Venue",
                                tint = StadiumGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = game.venue,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E2844),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumGold.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEditSpread() }
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                            .testTag("score_card_edit_spread_${game.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = game.lineSummary,
                                color = StadiumGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Spread",
                                tint = StadiumGold,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E2844),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3C62)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEditScore() }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("enter_score_btn_${game.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Score",
                                tint = TurfGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (game.isFinal) "Edit Score" else "Enter Score",
                                color = TurfGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            ScoreboardTeamRow(
                roleLabel = "AWAY",
                roleColor = Color(0xFF38BDF8),
                teamShort = game.awayTeamShort,
                teamName = game.awayTeam,
                spreadText = game.formattedAwaySpread(),
                score = game.awayScore,
                isFinal = game.isFinal
            )

            Spacer(modifier = Modifier.height(6.dp))

            ScoreboardTeamRow(
                roleLabel = "HOME",
                roleColor = StadiumGold,
                teamShort = game.homeTeamShort,
                teamName = game.homeTeam,
                spreadText = game.formattedHomeSpread(),
                score = game.homeScore,
                isFinal = game.isFinal
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (game.isFinal && game.homeScore != null && game.awayScore != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (atsResult) {
                        AtsResult.HOME_COVERED -> Color(0xFF0F766E)
                        AtsResult.AWAY_COVERED -> Color(0xFF0F766E)
                        AtsResult.PUSH -> Color(0xFF854D0E)
                        AtsResult.PENDING -> Color(0xFF1E2844)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val text = when (atsResult) {
                            AtsResult.HOME_COVERED -> "${game.homeTeamShort} COVERED (${game.homeScore} - ${game.awayScore})"
                            AtsResult.AWAY_COVERED -> "${game.awayTeamShort} COVERED (${game.awayScore} - ${game.homeScore})"
                            AtsResult.PUSH -> "PUSH (${game.awayScore} - ${game.homeScore})"
                            AtsResult.PENDING -> "In Progress"
                        }
                        Text(
                            text = text,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (player in players) {
                    val pick = picks.find { it.playerId == player.id }
                    val outcome = StatsCalculator.evaluatePick(game, pick)

                    val outcomeBg = when {
                        !game.isFinal -> Color(0xFF1A233A)
                        outcome == StatsCalculator.PickOutcome.WIN -> Color(0xFF0F3E22)
                        outcome == StatsCalculator.PickOutcome.LOSS && allPlayersWrong -> Color(0xFF380C1A)
                        outcome == StatsCalculator.PickOutcome.LOSS -> Color(0xFF1E1420)
                        outcome == StatsCalculator.PickOutcome.PUSH -> StadiumGold.copy(alpha = 0.2f)
                        else -> Color(0xFF1A233A)
                    }

                    val outcomeBorder = when {
                        !game.isFinal -> Color(0xFF263554)
                        outcome == StatsCalculator.PickOutcome.WIN -> TurfGreen
                        outcome == StatsCalculator.PickOutcome.LOSS && allPlayersWrong -> Color(0xFFEF4444)
                        outcome == StatsCalculator.PickOutcome.LOSS -> CoralRed.copy(alpha = 0.5f)
                        outcome == StatsCalculator.PickOutcome.PUSH -> StadiumGold
                        else -> Color(0xFF263554)
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = outcomeBg,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, outcomeBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(player.avatarColorHex).copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(player.avatarColorHex)),
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = player.symbol,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = if (player.symbol.length > 1) 7.sp else 9.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = player.name,
                                    color = Color(player.avatarColorHex),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            if (pick != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = pick.pickedTeamShort,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when (outcome) {
                                            StatsCalculator.PickOutcome.WIN -> "✅ WON"
                                            StatsCalculator.PickOutcome.LOSS -> "❌ LOST"
                                            StatsCalculator.PickOutcome.PUSH -> "➖ PUSH"
                                            else -> ""
                                        },
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(
                                    text = "No Pick",
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreboardTeamRow(
    roleLabel: String,
    roleColor: Color,
    teamShort: String,
    teamName: String,
    spreadText: String,
    score: Int?,
    isFinal: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E2B45), RoundedCornerShape(12.dp))
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

            TeamBadge(teamCode = teamShort, size = 28)

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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScoreDialog(
    game: NflGame,
    onDismiss: () -> Unit,
    onSave: (Int?, Int?, Boolean) -> Unit
) {
    var awayScoreText by remember { mutableStateOf(game.awayScore?.toString() ?: "") }
    var homeScoreText by remember { mutableStateOf(game.homeScore?.toString() ?: "") }
    var isFinalState by remember { mutableStateOf(game.isFinal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Score (${game.awayTeamShort} @ ${game.homeTeamShort})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = awayScoreText,
                    onValueChange = { awayScoreText = it },
                    label = { Text("${game.awayTeamShort} Score") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = homeScoreText,
                    onValueChange = { homeScoreText = it },
                    label = { Text("${game.homeTeamShort} Score") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mark Game Final",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = isFinalState,
                        onCheckedChange = { isFinalState = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = TurfGreen)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val away = awayScoreText.toIntOrNull()
                    val home = homeScoreText.toIntOrNull()
                    onSave(away, home, isFinalState)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TurfGreen, contentColor = StadiumNavy900)
            ) {
                Text("Save Score", fontWeight = FontWeight.Bold)
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