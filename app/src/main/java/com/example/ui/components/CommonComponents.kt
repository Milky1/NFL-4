package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.NflTeam
import com.example.data.model.NflTeams
import com.example.data.model.Player
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumGoldLight
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen

@Composable
fun HeroBanner(
    currentWeek: Int,
    totalPlayers: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_stadium_banner_1793799904905),
                contentDescription = "NFL Stadium",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // High contrast gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x660A0F1D),
                                Color(0xDD0A0F1D),
                                Color(0xFF0A0F1D)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TurfGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TurfGreen),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SportsFootball,
                                    contentDescription = null,
                                    tint = TurfGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SPREAD RIVALS",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = StadiumGold.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = StadiumGoldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WEEK $currentWeek",
                                color = StadiumGoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Beat the spread. Brag forever.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x3310B981)
                    ) {
                        Text(
                            text = "$totalPlayers Rivals Active",
                            color = TurfGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamHelmet(
    team: NflTeam,
    modifier: Modifier = Modifier,
    size: Int = 36
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier
            .size(size.dp)
            .testTag("helmet_${team.code}")
    ) {
        val w = this.size.width
        val h = this.size.height

        // Background circle
        drawCircle(
            color = Color(0xFF131D31),
            radius = w * 0.48f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
        )
        drawCircle(
            color = team.secondaryColor.copy(alpha = 0.6f),
            radius = w * 0.48f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = (w * 0.04f).coerceAtLeast(1.5f))
        )

        // Draw helmet shell (facing right)
        val shellPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.24f, h * 0.68f)
            cubicTo(
                w * 0.16f, h * 0.52f,
                w * 0.18f, h * 0.22f,
                w * 0.48f, h * 0.18f
            )
            cubicTo(
                w * 0.65f, h * 0.18f,
                w * 0.74f, h * 0.28f,
                w * 0.76f, h * 0.40f
            )
            lineTo(w * 0.62f, h * 0.45f)
            lineTo(w * 0.55f, h * 0.62f)
            lineTo(w * 0.36f, h * 0.68f)
            close()
        }
        drawPath(shellPath, color = team.primaryColor)

        // Helmet crown stripe
        val stripePath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.25f, h * 0.65f)
            cubicTo(
                w * 0.18f, h * 0.48f,
                w * 0.22f, h * 0.22f,
                w * 0.50f, h * 0.20f
            )
            cubicTo(
                w * 0.64f, h * 0.20f,
                w * 0.72f, h * 0.28f,
                w * 0.75f, h * 0.38f
            )
        }
        drawPath(
            stripePath,
            color = team.secondaryColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = (w * 0.07f).coerceAtLeast(2f),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )

        // Ear hole
        drawCircle(
            color = Color(0xFF0A0F1D),
            radius = (w * 0.05f).coerceAtLeast(1.5f),
            center = androidx.compose.ui.geometry.Offset(w * 0.44f, h * 0.56f)
        )

        // Facemask
        val maskColor = if (team.secondaryColor.value == 0xFF000000.toULong() || team.secondaryColor == Color.Black) {
            Color(0xFFCBD5E1)
        } else {
            team.secondaryColor
        }

        val maskTop = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.72f, h * 0.40f)
            lineTo(w * 0.84f, h * 0.44f)
            lineTo(w * 0.82f, h * 0.68f)
            lineTo(w * 0.54f, h * 0.64f)
        }
        drawPath(
            maskTop,
            color = maskColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = (w * 0.055f).coerceAtLeast(1.5f),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )

        // Middle horizontal bar
        drawLine(
            color = maskColor,
            start = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.52f),
            end = androidx.compose.ui.geometry.Offset(w * 0.83f, h * 0.53f),
            strokeWidth = (w * 0.05f).coerceAtLeast(1.5f),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Lower horizontal bar
        drawLine(
            color = maskColor,
            start = androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.60f),
            end = androidx.compose.ui.geometry.Offset(w * 0.82f, h * 0.61f),
            strokeWidth = (w * 0.05f).coerceAtLeast(1.5f),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Vertical strut
        drawLine(
            color = maskColor,
            start = androidx.compose.ui.geometry.Offset(w * 0.74f, h * 0.44f),
            end = androidx.compose.ui.geometry.Offset(w * 0.72f, h * 0.66f),
            strokeWidth = (w * 0.045f).coerceAtLeast(1.5f),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
fun TeamBadge(
    teamCode: String,
    modifier: Modifier = Modifier,
    size: Int = 36,
    showCode: Boolean = false // User requested: "Use the helmet for each team instead of letters."
) {
    val team = NflTeams.getByCode(teamCode)
    TeamHelmet(
        team = team,
        modifier = modifier.testTag("team_badge_$teamCode"),
        size = size
    )
}

@Composable
fun PlayerBadge(
    player: Player,
    modifier: Modifier = Modifier,
    size: Int = 26
) {
    Surface(
        shape = CircleShape,
        color = Color(player.avatarColorHex).copy(alpha = 0.25f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(player.avatarColorHex)),
        modifier = modifier
            .size(size.dp)
            .testTag("player_badge_${player.id}")
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = player.symbol,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = if (player.symbol.length > 1) (size * 0.38f).sp else (size * 0.46f).sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WeekSelector(
    availableWeeks: List<Int>,
    selectedWeek: Int,
    onSelectWeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    androidx.compose.runtime.LaunchedEffect(selectedWeek) {
        val index = availableWeeks.indexOf(selectedWeek)
        if (index >= 0) {
            listState.animateScrollToItem(index.coerceAtLeast(0))
        }
    }

    androidx.compose.foundation.lazy.LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 140.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(availableWeeks.size) { idx ->
            val week = availableWeeks[idx]
            val isSelected = week == selectedWeek
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) TurfGreen else Color(0xFF1B2844),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) TurfGreen else Color(0xFF2B3C63)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectWeek(week) }
                    .testTag("week_pill_$week")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (week <= 18) "Wk $week" else "Playoffs",
                        color = if (isSelected) StadiumNavy900 else Color(0xFFE2E8F0),
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerSelectorBar(
    players: List<Player>,
    selectedPlayerId: Long?,
    onSelectPlayer: (Long) -> Unit,
    modifier: Modifier = Modifier,
    getPicksCount: ((Long) -> Pair<Int, Int>)? = null // Pair(picked, totalGames)
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "SELECT ACTIVE PICKER",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (player in players) {
                val isSelected = player.id == selectedPlayerId
                val counts = getPicksCount?.invoke(player.id)

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFF24365C) else Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (isSelected) Color(player.avatarColorHex) else Color(0xFF1E2B45)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectPlayer(player.id) }
                        .testTag("player_selector_${player.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PlayerBadge(player = player, size = 22)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = player.name,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (counts != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${counts.first}/${counts.second}",
                                color = if (counts.first == counts.second && counts.second > 0) TurfGreen else Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
