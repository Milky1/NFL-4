package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NflTeams
import com.example.data.model.Player
import com.example.data.stats.PlayerRecord
import com.example.data.stats.TeamPickStat
import com.example.ui.theme.CoralRed
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen

/**
 * Visualizes the percentage of times a player has picked a particular NFL team against the spread,
 * along with their ATS win-loss-push record and win percentage when backing that franchise.
 */
@Composable
fun TeamPickTendencyView(
    leaderboard: List<PlayerRecord>,
    getTeamPickStatsForPlayer: (Long) -> List<TeamPickStat>,
    modifier: Modifier = Modifier
) {
    if (leaderboard.isEmpty()) return

    var selectedPlayerId by remember { mutableStateOf(leaderboard.first().player.id) }
    val activeRecord = leaderboard.find { it.player.id == selectedPlayerId } ?: leaderboard.first()
    val activePlayer = activeRecord.player

    val teamStats = remember(selectedPlayerId) {
        getTeamPickStatsForPlayer(selectedPlayerId)
    }

    val mostPicked = teamStats.maxByOrNull { it.timesPicked }
    val bestRecord = teamStats.filter { it.wins + it.losses >= 2 }.maxByOrNull { it.winPercentage }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("team_pick_tendency_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StadiumGold.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = StadiumGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TEAM PICK FREQUENCY & LOYALTY",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Percentage of picks backed by franchise & ATS success",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Player Switcher Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leaderboard.forEach { rec ->
                    val isSelected = rec.player.id == selectedPlayerId
                    val pColor = Color(rec.player.avatarColorHex)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) pColor else Color(0xFF1A2338),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) pColor else Color(0xFF263350)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPlayerId = rec.player.id }
                            .testTag("team_tendency_player_${rec.player.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = (if (isSelected) StadiumNavy900 else Color(rec.player.avatarColorHex)).copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) StadiumNavy900 else Color(rec.player.avatarColorHex)),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = rec.player.symbol,
                                        color = if (isSelected) StadiumNavy900 else Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = if (rec.player.symbol.length > 1) 9.sp else 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = rec.player.name,
                                color = if (isSelected) StadiumNavy900 else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Highlight Quick Cards (Favorite team & Best ROI team)
            if (teamStats.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Most Backed Team Card
                    if (mostPicked != null) {
                        val nflTeam = NflTeams.getByCode(mostPicked.teamCode)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0C1322),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color(0xFFF43F5E),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "MOST BACKED",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TeamBadge(teamCode = mostPicked.teamCode, size = 28)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = nflTeam.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${mostPicked.timesPicked} picks (${mostPicked.formattedPickPct})",
                                            color = TurfGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Best Record Team Card
                    if (bestRecord != null) {
                        val nflTeam = NflTeams.getByCode(bestRecord.teamCode)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0C1322),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = null,
                                        tint = TurfGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "BEST WIN RATE",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TeamBadge(teamCode = bestRecord.teamCode, size = 28)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = nflTeam.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${bestRecord.formattedRecord} (${bestRecord.formattedWinPct})",
                                            color = StadiumGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Breakdown List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOP 5 BACKED TEAMS (${activePlayer.name})",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Top 5 franchises",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val top5TeamStats = teamStats.take(5)

            if (top5TeamStats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No picks recorded for ${activePlayer.name} yet.",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val maxPicks = top5TeamStats.maxOfOrNull { it.timesPicked } ?: 1

                    top5TeamStats.forEach { stat ->
                        val nflTeam = NflTeams.getByCode(stat.teamCode)
                        val relativeProgress = (stat.timesPicked.toFloat() / maxPicks.toFloat()).coerceIn(0.05f, 1f)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0C1322),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Team Info
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        TeamBadge(teamCode = stat.teamCode, size = 32)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = nflTeam.fullName,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${stat.timesPicked} picks (${stat.formattedPickPct} of ${activePlayer.name}'s picks)",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    // Win Rate & ATS Record Pill
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (stat.winPercentage >= 50f) TurfGreen.copy(alpha = 0.15f) else Color(0xFF2B1D24),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (stat.winPercentage >= 50f) TurfGreen.copy(alpha = 0.5f) else Color(0xFF6E2835)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = stat.formattedRecord,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = stat.formattedWinPct,
                                                color = if (stat.winPercentage >= 50f) TurfGreen else CoralRed,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Proportional Pick Frequency Progress Bar
                                LinearProgressIndicator(
                                    progress = { relativeProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = nflTeam.primaryColor,
                                    trackColor = Color(0xFF1E2B45)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cross-Rival Loyalty Summary Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A2338),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "FRANCHISE LOYALTY COMPARISON",
                        color = StadiumGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    leaderboard.forEach { rec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(rec.player.avatarColorHex).copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(rec.player.avatarColorHex)),
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = rec.player.symbol,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = if (rec.player.symbol.length > 1) 8.sp else 10.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = rec.player.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Most picked: ",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                                TeamBadge(teamCode = rec.favoriteTeam, size = 20)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = rec.favoriteTeam,
                                    color = Color(rec.player.avatarColorHex),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
