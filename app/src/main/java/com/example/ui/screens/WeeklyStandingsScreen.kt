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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NflGame
import com.example.data.model.Player
import com.example.data.model.SpreadPick
import com.example.data.stats.StatsCalculator
import com.example.data.stats.WeeklyPrizeInfo
import com.example.data.stats.WeeklyWinnerSummary
import com.example.ui.components.TeamBadge
import com.example.ui.components.WeekSelector
import com.example.ui.theme.CoralRed
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumGoldLight
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen

@Composable
fun WeeklyStandingsScreen(
    currentWeek: Int,
    availableWeeks: List<Int>,
    players: List<Player>,
    games: List<NflGame>,
    picks: List<SpreadPick>,
    weeklySummary: WeeklyWinnerSummary?,
    allWeeklySummaries: List<WeeklyWinnerSummary>,
    weeklyPrizes: Map<Int, WeeklyPrizeInfo> = emptyMap(),
    onSelectWeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "WEEKLY CHAMPIONS & RESULTS",
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

        // Weekly Champion Banner
        item {
            if (weeklySummary != null && weeklySummary.winners.isNotEmpty()) {
                WeeklyChampionCard(summary = weeklySummary)
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Week $currentWeek Champion Not Decided",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Enter scores in the Scores tab to crown this week's winner!",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Weekly Leaderboard Table
        item {
            Text(
                text = "WEEK $currentWeek LEADERBOARD",
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
        }

        val sortedResults = weeklySummary?.playerResults?.sortedWith(
            compareByDescending<com.example.data.stats.WeeklyPlayerResult> { it.wins }
                .thenByDescending { it.winPercentage }
        ) ?: emptyList()

        items(sortedResults) { res ->
            val isWinner = weeklySummary?.winners?.any { it.id == res.player.id } == true
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_player_${res.player.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isWinner) Color(0xFF1E2C4A) else Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(
                    if (isWinner) 1.5.dp else 1.dp,
                    if (isWinner) StadiumGold else Color(0xFF1E2B45)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(res.player.avatarColorHex).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(res.player.avatarColorHex)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = res.player.symbol,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = if (res.player.symbol.length > 1) 14.sp else 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = res.player.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (isWinner) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "👑", fontSize = 14.sp)
                                }
                            }
                            Text(
                                text = "${res.totalDecided} decided • ${res.pending} pending",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = res.formattedRecord,
                            color = if (isWinner) StadiumGoldLight else TurfGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${res.winPercentage.toInt()}% ATS",
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Full Week Game-by-Game Breakdown
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "WEEK $currentWeek PICKS BREAKDOWN",
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
        }

        items(games) { game ->
            val gamePicks = picks.filter { it.gameId == game.id }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TeamBadge(teamCode = game.awayTeamShort, size = 24)
                            Text(
                                text = " @ ",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                            TeamBadge(teamCode = game.homeTeamShort, size = 24)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${game.awayTeamShort} vs ${game.homeTeamShort}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        if (game.isFinal && game.awayScore != null && game.homeScore != null) {
                            Text(
                                text = "${game.awayScore} - ${game.homeScore}",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        } else {
                            Text(
                                text = "Line: ${game.lineSummary}",
                                color = StadiumGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (player in players) {
                            val pick = gamePicks.find { it.playerId == player.id }
                            val outcome = StatsCalculator.evaluatePick(game, pick)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1B2844),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${player.name}: ${pick?.pickedTeamShort ?: "-"}",
                                        color = if (pick != null) Color.White else Color(0xFF64748B),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                    when (outcome) {
                                        StatsCalculator.PickOutcome.WIN -> Icon(Icons.Default.Check, contentDescription = "W", tint = TurfGreen, modifier = Modifier.size(13.dp))
                                        StatsCalculator.PickOutcome.LOSS -> Icon(Icons.Default.Close, contentDescription = "L", tint = CoralRed, modifier = Modifier.size(13.dp))
                                        StatsCalculator.PickOutcome.PUSH -> Icon(Icons.Default.Remove, contentDescription = "P", tint = StadiumGold, modifier = Modifier.size(13.dp))
                                        else -> {}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Season Weekly Roll of Champions
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "SEASON WEEKLY CHAMPIONS ROLL",
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
        }

        val completedSummaries = allWeeklySummaries.filter { it.winners.isNotEmpty() && it.playerResults.any { p -> p.totalDecided > 0 } }
        if (completedSummaries.isEmpty()) {
            item {
                Text(
                    text = "No completed weeks yet.",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp
                )
            }
        } else {
            items(completedSummaries) { summary ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectWeek(summary.weekNumber) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StadiumGold.copy(alpha = 0.2f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "W${summary.weekNumber}",
                                        color = StadiumGoldLight,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                val winnerNames = summary.winners.joinToString(" & ") { it.name }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = winnerNames,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "👑", fontSize = 13.sp)
                                }
                                Text(
                                    text = if (summary.isTie) "Tied Week" else "Outright Winner",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = summary.winningRecord,
                            color = TurfGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun WeeklyChampionCard(
    summary: WeeklyWinnerSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_champion_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2945)),
        border = androidx.compose.foundation.BorderStroke(2.dp, StadiumGold)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            StadiumGold.copy(alpha = 0.25f),
                            Color(0xFF1B2945)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = StadiumGold.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, StadiumGold),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = StadiumGoldLight,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (summary.isTie) "WEEK ${summary.weekNumber} CO-CHAMPIONS" else "WEEK ${summary.weekNumber} CHAMPION",
                    color = StadiumGoldLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                val names = summary.winners.joinToString(" & ") { "${it.name} [${it.symbol}]" }
                Text(
                    text = names,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                val topResult = summary.playerResults.find { it.player.id == summary.winners.firstOrNull()?.id }
                if (topResult != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x3300E676),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "${topResult.formattedRecord} (${topResult.winPercentage.toInt()}% ATS)",
                            color = TurfGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
