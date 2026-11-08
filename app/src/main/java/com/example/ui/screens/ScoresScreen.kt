package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
    onRefreshFixturesAndSpreads: (Int) -> Unit = {},
    onRefreshFixtures: (Int) -> Unit = onRefreshFixturesAndSpreads,
    onRefreshSpreads: (Int) -> Unit = onRefreshFixturesAndSpreads,
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

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("week_scores_refresh_card"),
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
                            text = "WEEK $currentWeek COMMISSIONER CONTROLS",
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
                                .testTag("scores_refresh_fixtures_btn")
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
                                .testTag("scores_refresh_spreads_btn")
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
                }
            }
        }

        // Live Weekly Standings pill preview
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

    // Save FAB (saves picks, spreads, and results)
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
            // Unanimous Loss Highlight Banner
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

            // Header with line and tap to edit score - tight gap for date, time, and location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${game.gameDate} • ${game.kickoffTime}" + if (game.tvNetwork.isNotBlank()) " • ${game.tvNetwork}" else "",
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
                    // Edit Spread Button
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

                    // Edit Score Button
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

            // Scoreboard view: THE AWAY TEAM FIRST (Top Row)
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

            // Scoreboard view: THE HOME TEAM SECOND (Bottom Row)
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

            // Result explanation banner (larger with white text)
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

            // Friend Picks & Outcomes with Winning Player Highlights
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
                                    when (outcome) {
                                        StatsCalculator.PickOutcome.WIN -> Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Win",
                                            tint = TurfGreen,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        StatsCalculator.PickOutcome.LOSS -> Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Loss",
                                            tint = if (allPlayersWrong) Color(0xFFEF4444) else CoralRed,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        StatsCalculator.PickOutcome.PUSH -> Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Push",
                                            tint = StadiumGold,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        else -> {}
                                    }
                                }
                                if (game.isFinal && outcome == StatsCalculator.PickOutcome.WIN) {
                                    Text(
                                        text = "WIN",
                                        color = TurfGreen,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp
                                    )
                                } else if (game.isFinal && outcome == StatsCalculator.PickOutcome.LOSS) {
                                    Text(
                                        text = if (allPlayersWrong) "WRONG" else "LOSS",
                                        color = if (allPlayersWrong) Color(0xFFEF4444) else CoralRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = "No pick",
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
    isFinal: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF182238),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22304E)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                TeamBadge(teamCode = teamShort, size = 30)
                Spacer(modifier = Modifier.width(8.dp))
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
                            fontSize = 14.sp
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (spreadText.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StadiumGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = spreadText,
                            color = StadiumGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Text(
                        text = if (isFinal && score != null) score.toString() else "-",
                        color = if (isFinal && score != null) Color.White else Color(0xFF64748B),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditScoreDialog(
    game: NflGame,
    onDismiss: () -> Unit,
    onSave: (Int?, Int?, Boolean) -> Unit
) {
    var awayScoreText by remember { mutableStateOf(game.awayScore?.toString() ?: "") }
    var homeScoreText by remember { mutableStateOf(game.homeScore?.toString() ?: "") }
    var isFinal by remember { mutableStateOf(game.isFinal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${game.awayTeamShort} @ ${game.homeTeamShort}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Spread: ${game.lineSummary}",
                    color = StadiumGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = awayScoreText,
                        onValueChange = { awayScoreText = it },
                        label = { Text("${game.awayTeamShort} Score") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TurfGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = homeScoreText,
                        onValueChange = { homeScoreText = it },
                        label = { Text("${game.homeTeamShort} Score") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TurfGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Quick score steppers
                Text(
                    text = "Quick Football Points:",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (pts in listOf(3, 6, 7)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E2844),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val current = homeScoreText.toIntOrNull() ?: 0
                                    homeScoreText = (current + pts).toString()
                                }
                        ) {
                            Text(
                                text = "+$pts ${game.homeTeamShort}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                // Final Game Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mark Game as FINAL",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = isFinal,
                        onCheckedChange = { isFinal = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TurfGreen,
                            checkedTrackColor = TurfGreen.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val away = awayScoreText.toIntOrNull()
                    val home = homeScoreText.toIntOrNull()
                    onSave(away, home, if (away != null && home != null) isFinal else false)
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
