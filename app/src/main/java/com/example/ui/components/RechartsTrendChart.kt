package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Player
import com.example.data.stats.WeeklyTrendPoint
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen

enum class TrendMetricMode(val label: String, val shortDesc: String) {
    CUMULATIVE_SEASON("Season-Long (Cumulative)", "Tracks total season win rate as weeks progress"),
    WEEKLY_PERFORMANCE("Single Week Performance", "Shows individual week-by-week win percentages")
}

/**
 * Recharts-inspired multi-series chart component for visualizing season-long winning percentage trends.
 * Features:
 * - Multi-player trend line overlays (Clive, Mark, Conor) with custom brand colors
 * - 50.0% Break-Even dotted benchmark reference line (Recharts <ReferenceLine />)
 * - Interactive vertical cursor hairline and floating tooltip on tap (Recharts <Tooltip />)
 * - Toggle between Cumulative Season Trajectory and Single Week Performance
 * - Interactive filter legend (Recharts <Legend />)
 */
@Composable
fun RechartsTrendChart(
    players: List<Player>,
    getTrendsForPlayer: (Long) -> List<WeeklyTrendPoint>,
    modifier: Modifier = Modifier
) {
    var metricMode by remember { mutableStateOf(TrendMetricMode.CUMULATIVE_SEASON) }
    var selectedPlayerId by remember { mutableStateOf<Long?>(null) } // null = All Rivals comparison
    var activeWeekIndex by remember { mutableStateOf<Int?>(null) }

    // Map trends for all players
    val playerTrends = remember(players, metricMode) {
        players.associateWith { getTrendsForPlayer(it.id) }
    }

    // Determine the available weeks across all players
    val allWeeks = remember(playerTrends) {
        playerTrends.values.flatten().map { it.weekNumber }.distinct().sorted()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recharts_trend_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Recharts title and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TurfGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = "Trend Chart",
                                tint = TurfGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "WINNING % TRENDS",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Recharts-style multi-rival trend comparison",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                // Break-Even reference indicator pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1B2844),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(StadiumGold, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "50% Target",
                            color = StadiumGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Selector: Cumulative Season vs Single Week
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C1322), RoundedCornerShape(10.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TrendMetricMode.values().forEach { mode ->
                    val isSelected = metricMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF1E2B45) else Color.Transparent)
                            .clickable {
                                metricMode = mode
                                activeWeekIndex = null
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            color = if (isSelected) TurfGreen else Color(0xFF94A3B8),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recharts Legend: Interactive Player Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "All Rivals" Filter Chip
                val isAllSelected = selectedPlayerId == null
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isAllSelected) TurfGreen else Color(0xFF1A2338),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAllSelected) TurfGreen else Color(0xFF263350)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { selectedPlayerId = null }
                        .testTag("legend_filter_all")
                ) {
                    Text(
                        text = "● All Rivals",
                        color = if (isAllSelected) StadiumNavy900 else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Individual Player Pills
                players.forEach { player ->
                    val isPlayerSelected = selectedPlayerId == player.id
                    val pColor = Color(player.avatarColorHex)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isPlayerSelected) pColor else Color(0xFF1A2338),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPlayerSelected) pColor else pColor.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedPlayerId = player.id }
                            .testTag("legend_filter_${player.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(
                                        if (isPlayerSelected) StadiumNavy900 else pColor,
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = player.name,
                                color = if (isPlayerSelected) StadiumNavy900 else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (allWeeks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Complete Week games to view season trend chart.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                }
            } else {
                // Interactive Chart Canvas Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFF0C1322), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    RechartsCanvas(
                        allWeeks = allWeeks,
                        players = if (selectedPlayerId != null) players.filter { it.id == selectedPlayerId } else players,
                        playerTrends = playerTrends,
                        metricMode = metricMode,
                        activeWeekIndex = activeWeekIndex,
                        onSelectWeekIndex = { activeWeekIndex = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Recharts Floating Tooltip Card (Active on Tap)
                val currentTooltipWeek = if (activeWeekIndex != null && activeWeekIndex!! in allWeeks.indices) {
                    allWeeks[activeWeekIndex!!]
                } else null

                AnimatedVisibility(
                    visible = currentTooltipWeek != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (currentTooltipWeek != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E2B45),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TurfGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📍 WEEK $currentTooltipWeek DETAILS",
                                        color = TurfGreen,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = if (metricMode == TrendMetricMode.CUMULATIVE_SEASON) "Cumulative Season" else "Single Week",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    players.forEach { p ->
                                        val pts = playerTrends[p] ?: emptyList()
                                        val pt = pts.find { it.weekNumber == currentTooltipWeek }
                                        val pColor = Color(p.avatarColorHex)

                                        Column(
                                            modifier = Modifier
                                                .background(Color(0xFF131D31), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(pColor, CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = p.name,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            if (pt != null) {
                                                val pct = if (metricMode == TrendMetricMode.CUMULATIVE_SEASON) pt.cumulativeWinPct else pt.winPct
                                                val w = if (metricMode == TrendMetricMode.CUMULATIVE_SEASON) pt.cumulativeWins else pt.wins
                                                val l = if (metricMode == TrendMetricMode.CUMULATIVE_SEASON) pt.cumulativeLosses else pt.losses
                                                Text(
                                                    text = "%.1f%%".format(pct),
                                                    color = if (pct >= 50f) TurfGreen else Color(0xFFF87171),
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "$w-$l ATS",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 10.sp
                                                )
                                            } else {
                                                Text(
                                                    text = "N/A",
                                                    color = Color(0xFF64748B),
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

                if (currentTooltipWeek == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tap any week on the chart to view exact player records & win %",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RechartsCanvas(
    allWeeks: List<Int>,
    players: List<Player>,
    playerTrends: Map<Player, List<WeeklyTrendPoint>>,
    metricMode: TrendMetricMode,
    activeWeekIndex: Int?,
    onSelectWeekIndex: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalWeeks = allWeeks.size

    Canvas(
        modifier = modifier.pointerInput(allWeeks) {
            detectTapGestures { offset ->
                if (totalWeeks > 0) {
                    val paddingLeft = 32.dp.toPx()
                    val paddingRight = 16.dp.toPx()
                    val chartWidth = size.width - paddingLeft - paddingRight
                    val stepX = if (totalWeeks > 1) chartWidth / (totalWeeks - 1) else chartWidth / 2f
                    val relativeX = offset.x - paddingLeft
                    val nearestIndex = ((relativeX + (stepX / 2f)) / stepX).toInt().coerceIn(0, totalWeeks - 1)
                    onSelectWeekIndex(if (nearestIndex == activeWeekIndex) null else nearestIndex)
                }
            }
        }
    ) {
        val width = size.width
        val height = size.height

        val paddingLeft = 32.dp.toPx()
        val paddingRight = 16.dp.toPx()
        val paddingTop = 16.dp.toPx()
        val paddingBottom = 26.dp.toPx()

        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom

        // Draw horizontal grid lines: 100%, 75%, 50%, 25%, 0%
        val gridLevels = listOf(100f, 75f, 50f, 25f, 0f)
        gridLevels.forEach { level ->
            val y = paddingTop + (1f - (level / 100f)) * chartHeight
            val is50Benchmark = level == 50f

            if (is50Benchmark) {
                // Dashed reference line for 50% break-even
                drawLine(
                    color = StadiumGold.copy(alpha = 0.6f),
                    start = Offset(paddingLeft, y),
                    end = Offset(width - paddingRight, y),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            } else {
                // Subtle background grid lines
                drawLine(
                    color = Color(0xFF1E2B45),
                    start = Offset(paddingLeft, y),
                    end = Offset(width - paddingRight, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        if (totalWeeks == 0) return@Canvas

        val stepX = if (totalWeeks > 1) chartWidth / (totalWeeks - 1) else chartWidth / 2f

        // Draw vertical hairline indicator if a week is selected (Recharts Cursor)
        if (activeWeekIndex != null && activeWeekIndex in allWeeks.indices) {
            val cursorX = paddingLeft + (activeWeekIndex * stepX)
            drawLine(
                color = TurfGreen.copy(alpha = 0.5f),
                start = Offset(cursorX, paddingTop),
                end = Offset(cursorX, height - paddingBottom),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
            // Cursor base indicator circle
            drawCircle(
                color = TurfGreen,
                radius = 3.dp.toPx(),
                center = Offset(cursorX, height - paddingBottom)
            )
        }

        // Draw series lines for each active player
        players.forEach { player ->
            val points = playerTrends[player] ?: emptyList()
            val lineColor = Color(player.avatarColorHex)

            val curveOffsets = mutableListOf<Offset>()
            allWeeks.forEachIndexed { idx, weekNum ->
                val pt = points.find { it.weekNumber == weekNum }
                val pct = if (pt != null) {
                    if (metricMode == TrendMetricMode.CUMULATIVE_SEASON) pt.cumulativeWinPct else pt.winPct
                } else 50f

                val x = paddingLeft + (idx * stepX)
                val clampedPct = pct.coerceIn(0f, 100f)
                val y = paddingTop + (1f - (clampedPct / 100f)) * chartHeight
                curveOffsets.add(Offset(x, y))
            }

            if (curveOffsets.size >= 2) {
                val path = Path()
                path.moveTo(curveOffsets[0].x, curveOffsets[0].y)

                for (i in 0 until curveOffsets.size - 1) {
                    val p0 = curveOffsets[i]
                    val p1 = curveOffsets[i + 1]
                    // Smooth cubic bezier curve (Recharts curveMonotone)
                    val controlX1 = p0.x + (p1.x - p0.x) / 2f
                    val controlY1 = p0.y
                    val controlX2 = p0.x + (p1.x - p0.x) / 2f
                    val controlY2 = p1.y
                    path.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                }

                // If only 1 player selected, draw gradient area fill below line
                if (players.size == 1) {
                    val fillPath = Path()
                    fillPath.addPath(path)
                    fillPath.lineTo(curveOffsets.last().x, height - paddingBottom)
                    fillPath.lineTo(curveOffsets.first().x, height - paddingBottom)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.28f),
                                lineColor.copy(alpha = 0.0f)
                            ),
                            startY = paddingTop,
                            endY = height - paddingBottom
                        )
                    )
                }

                // Draw line stroke
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(
                        width = if (players.size == 1) 3.5.dp.toPx() else 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Draw data points (Recharts dot)
                curveOffsets.forEachIndexed { idx, offset ->
                    val isCursorWeek = idx == activeWeekIndex
                    val outerRadius = if (isCursorWeek) 6.dp.toPx() else 4.dp.toPx()
                    val innerRadius = if (isCursorWeek) 3.5.dp.toPx() else 2.dp.toPx()

                    drawCircle(color = Color(0xFF0C1322), radius = outerRadius, center = offset)
                    drawCircle(color = lineColor, radius = innerRadius, center = offset)
                }
            } else if (curveOffsets.size == 1) {
                drawCircle(color = lineColor, radius = 5.dp.toPx(), center = curveOffsets[0])
            }
        }
    }
}
