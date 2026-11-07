package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.stats.HeadToHeadRecord
import com.example.data.stats.PlayerRecord
import com.example.data.stats.TeamPickStat
import com.example.data.stats.WeeklyTrendPoint
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumGoldLight
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen

/**
 * Data model for a row in the Yearly Total Spreadsheet
 */
data class YearlySpreadsheetRow(
    val year: String,
    val clive: Int,
    val mark: Int,
    val conor: Int,
    val isLiveYear: Boolean = false,
    val isOverride: Boolean = false
) {
    val winnerName: String
        get() = when {
            clive > mark && clive > conor -> "Clive"
            mark > clive && mark > conor -> "Mark"
            conor > clive && conor > mark -> "Conor"
            clive == mark && clive > conor -> "Clive & Mark"
            clive == conor && clive > mark -> "Clive & Conor"
            mark == conor && mark > clive -> "Mark & Conor"
            else -> "3-Way Tie"
        }

    val maxScore: Int get() = maxOf(clive, maxOf(mark, conor))
}

@Composable
fun HistoricalStatsScreen(
    leaderboard: List<PlayerRecord>,
    headToHeadList: List<HeadToHeadRecord> = emptyList(),
    getTrendsForPlayer: (Long) -> List<WeeklyTrendPoint> = { emptyList() },
    getTeamPickStatsForPlayer: (Long) -> List<TeamPickStat> = { emptyList() },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yearly_totals_overrides_v1", Context.MODE_PRIVATE) }

    // State for manual override of 2026
    var isOverrideActive by remember {
        mutableStateOf(prefs.getBoolean("is_override_active_2026", false))
    }
    var overrideClive by remember {
        mutableIntStateOf(prefs.getInt("override_clive_2026", 68))
    }
    var overrideMark by remember {
        mutableIntStateOf(prefs.getInt("override_mark_2026", 64))
    }
    var overrideConor by remember {
        mutableIntStateOf(prefs.getInt("override_conor_2026", 62))
    }

    var showOverrideDialog by remember { mutableStateOf(false) }

    // Dynamic 2026 calculation from weekly totals year-to-date and onwards
    val cliveLive = leaderboard.find { it.player.name.equals("clive", ignoreCase = true) }
    val markLive = leaderboard.find { it.player.name.equals("mark", ignoreCase = true) }
    val conorLive = leaderboard.find { it.player.name.equals("conor", ignoreCase = true) }

    val cliveCalculated2026 = cliveLive?.wins ?: 0
    val markCalculated2026 = markLive?.wins ?: 0
    val conorCalculated2026 = conorLive?.wins ?: 0

    // Effective 2026 values (either manual override or dynamic calculation)
    val clive2026 = if (isOverrideActive) overrideClive else cliveCalculated2026
    val mark2026 = if (isOverrideActive) overrideMark else markCalculated2026
    val conor2026 = if (isOverrideActive) overrideConor else conorCalculated2026

    // Historical records exactly from the attached Yearly Total spreadsheet:
    // 2025: Clive 127, Mark 130, Conor 127
    // 2024: Clive 130, Mark 140, Conor 156
    // 2023: Clive 126, Mark 113, Conor 129
    // 2022: Clive 127, Mark 116, Conor 125
    // 2021: Clive 135, Mark 141, Conor 131
    // 2020: Clive 130, Mark 137, Conor 112
    // 2019: Clive 110, Mark 130, Conor 131
    // 2018: Clive 125, Mark 121, Conor 120
    val historicalRows = remember {
        listOf(
            YearlySpreadsheetRow(year = "2025", clive = 127, mark = 130, conor = 127),
            YearlySpreadsheetRow(year = "2024", clive = 130, mark = 140, conor = 156),
            YearlySpreadsheetRow(year = "2023", clive = 126, mark = 113, conor = 129),
            YearlySpreadsheetRow(year = "2022", clive = 127, mark = 116, conor = 125),
            YearlySpreadsheetRow(year = "2021", clive = 135, mark = 141, conor = 131),
            YearlySpreadsheetRow(year = "2020", clive = 130, mark = 137, conor = 112),
            YearlySpreadsheetRow(year = "2019", clive = 110, mark = 130, conor = 131),
            YearlySpreadsheetRow(year = "2018", clive = 125, mark = 121, conor = 120)
        )
    }

    val row2026 = YearlySpreadsheetRow(
        year = "2026",
        clive = clive2026,
        mark = mark2026,
        conor = conor2026,
        isLiveYear = true,
        isOverride = isOverrideActive
    )

    // Calculate career totals across 2018–2026
    val cliveHistoricalSum = historicalRows.sumOf { it.clive }
    val markHistoricalSum = historicalRows.sumOf { it.mark }
    val conorHistoricalSum = historicalRows.sumOf { it.conor }

    val cliveCareerTotal = cliveHistoricalSum + clive2026
    val markCareerTotal = markHistoricalSum + mark2026
    val conorCareerTotal = conorHistoricalSum + conor2026

    val maxCareerTotal = maxOf(cliveCareerTotal, maxOf(markCareerTotal, conorCareerTotal))
    val careerLeader = when {
        cliveCareerTotal > markCareerTotal && cliveCareerTotal > conorCareerTotal -> "Clive"
        markCareerTotal > cliveCareerTotal && markCareerTotal > conorCareerTotal -> "Mark"
        conorCareerTotal > cliveCareerTotal && conorCareerTotal > markCareerTotal -> "Conor"
        else -> "Tied"
    }

    // Season Championships Count (historical 2018-2025)
    var cliveTitles = 0
    var markTitles = 0
    var conorTitles = 0
    historicalRows.forEach { r ->
        when {
            r.clive > r.mark && r.clive > r.conor -> cliveTitles++
            r.mark > r.clive && r.mark > r.conor -> markTitles++
            r.conor > r.clive && r.conor > r.mark -> conorTitles++
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==================== CASH MOVEMENT DASHBOARD (ABOVE YEARLY TOTAL SPREADSHEET) ====================
        item {
            Spacer(modifier = Modifier.height(6.dp))
            WeeklyCashDashboard()
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Main Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StadiumGold.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumGold.copy(alpha = 0.4f)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = "Spreadsheet",
                                tint = StadiumGoldLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "YEARLY TOTAL SPREADSHEET",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "2018 – 2026 Season Records",
                            color = StadiumGoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Override Action Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isOverrideActive) StadiumGold else Color(0xFF1E2F52),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isOverrideActive) StadiumGold else Color(0xFF38BDF8)),
                    modifier = Modifier
                        .clickable { showOverrideDialog = true }
                        .testTag("manual_override_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isOverrideActive) Icons.Default.Edit else Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = if (isOverrideActive) StadiumNavy900 else Color(0xFF38BDF8),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isOverrideActive) "Override Active" else "Override 2026",
                            color = if (isOverrideActive) StadiumNavy900 else Color(0xFF38BDF8),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Status banner explaining calculation and override state
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isOverrideActive) Color(0x33FFB300) else Color(0x2600E676),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isOverrideActive) StadiumGold.copy(alpha = 0.6f) else TurfGreen.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOverrideActive) Icons.Default.Edit else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isOverrideActive) StadiumGoldLight else TurfGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOverrideActive) {
                                "2026: Manual Override Active ($clive2026 - $mark2026 - $conor2026)"
                            } else {
                                "2026: Auto-Calculated Live ($clive2026 - $mark2026 - $conor2026)"
                            },
                            color = if (isOverrideActive) StadiumGoldLight else TurfGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (isOverrideActive) {
                        Text(
                            text = "Reset to YTD",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                isOverrideActive = false
                                prefs.edit().putBoolean("is_override_active_2026", false).apply()
                            }
                        )
                    }
                }
            }
        }

        // ==================== THE YEARLY TOTAL SPREADSHEET CARD ====================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("yearly_totals_spreadsheet_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E2B45))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header Description
                    Text(
                        text = "TOTAL CORRECT PREDICTIONS PER PERSON (2018–2026)",
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Column Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1728), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YEAR",
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1.0f)
                        )
                        Text(
                            text = "CLIVE [C]",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.0f)
                        )
                        Text(
                            text = "MARK [M]",
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.0f)
                        )
                        Text(
                            text = "CONOR [CO]",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.0f)
                        )
                        Text(
                            text = "WINNER",
                            color = StadiumGoldLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1.1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 2026 ROW (Clickable to edit/override)
                    SpreadsheetDataRow(
                        row = row2026,
                        onClick = { showOverrideDialog = true }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 2025 down to 2018 (from spreadsheet attachment)
                    historicalRows.forEach { row ->
                        SpreadsheetDataRow(row = row)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = Color(0xFF1E2B45), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // CAREER TOTAL ROW
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StadiumGold.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, StadiumGold.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("row_year_total")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.0f)) {
                                Text(
                                    text = "TOTAL",
                                    color = StadiumGoldLight,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "All Years",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 9.sp
                                )
                            }

                            // Clive Career Total
                            Text(
                                text = "$cliveCareerTotal" + if (cliveCareerTotal == maxCareerTotal) " 👑" else "",
                                color = if (cliveCareerTotal == maxCareerTotal) StadiumGoldLight else Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1.0f)
                            )

                            // Mark Career Total
                            Text(
                                text = "$markCareerTotal" + if (markCareerTotal == maxCareerTotal) " 👑" else "",
                                color = if (markCareerTotal == maxCareerTotal) StadiumGoldLight else Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1.0f)
                            )

                            // Conor Career Total
                            Text(
                                text = "$conorCareerTotal" + if (conorCareerTotal == maxCareerTotal) " 👑" else "",
                                color = if (conorCareerTotal == maxCareerTotal) StadiumGoldLight else Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1.0f)
                            )

                            // Overall Career Leader
                            Row(
                                modifier = Modifier.weight(1.1f),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$careerLeader 🏆",
                                    color = StadiumGoldLight,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==================== CAREER SUMMARY CARDS (Clive, Mark, Conor) ====================
        item {
            Text(
                text = "PLAYER CAREER PROFILES",
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Clive Card
                PlayerCareerCard(
                    name = "Clive",
                    symbol = "C",
                    colorHex = 0xFF00E676,
                    careerTotal = cliveCareerTotal,
                    titlesCount = cliveTitles,
                    score2026 = clive2026,
                    isCareerLeader = cliveCareerTotal == maxCareerTotal,
                    modifier = Modifier.weight(1f)
                )

                // Mark Card
                PlayerCareerCard(
                    name = "Mark",
                    symbol = "M",
                    colorHex = 0xFFFFB300,
                    careerTotal = markCareerTotal,
                    titlesCount = markTitles,
                    score2026 = mark2026,
                    isCareerLeader = markCareerTotal == maxCareerTotal,
                    modifier = Modifier.weight(1f)
                )

                // Conor Card
                PlayerCareerCard(
                    name = "Conor",
                    symbol = "CO",
                    colorHex = 0xFF38BDF8,
                    careerTotal = conorCareerTotal,
                    titlesCount = conorTitles,
                    score2026 = conor2026,
                    isCareerLeader = conorCareerTotal == maxCareerTotal,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // ==================== MANUAL OVERRIDE DIALOG ====================
    if (showOverrideDialog) {
        ManualOverride2026Dialog(
            currentClive = clive2026,
            currentMark = mark2026,
            currentConor = conor2026,
            liveClive = cliveCalculated2026,
            liveMark = markCalculated2026,
            liveConor = conorCalculated2026,
            isOverrideActive = isOverrideActive,
            onDismiss = { showOverrideDialog = false },
            onSaveOverride = { c, m, co ->
                overrideClive = c
                overrideMark = m
                overrideConor = co
                isOverrideActive = true
                prefs.edit()
                    .putBoolean("is_override_active_2026", true)
                    .putInt("override_clive_2026", c)
                    .putInt("override_mark_2026", m)
                    .putInt("override_conor_2026", co)
                    .apply()
                showOverrideDialog = false
            },
            onResetToCalculated = {
                isOverrideActive = false
                prefs.edit().putBoolean("is_override_active_2026", false).apply()
                showOverrideDialog = false
            }
        )
    }
}

/**
 * A single row inside the Yearly Total Spreadsheet
 */
@Composable
fun SpreadsheetDataRow(
    row: YearlySpreadsheetRow,
    onClick: (() -> Unit)? = null
) {
    val isLive = row.isLiveYear
    val maxScore = row.maxScore

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = when {
            isLive && row.isOverride -> Color(0xFF262013)
            isLive -> Color(0xFF142E28)
            else -> Color(0xFF0F1728)
        },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
                isLive && row.isOverride -> StadiumGold.copy(alpha = 0.6f)
                isLive -> TurfGreen.copy(alpha = 0.5f)
                else -> Color(0xFF1E2B45)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("row_year_${row.year}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Year Column
            Row(
                modifier = Modifier.weight(1.0f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = row.year,
                    color = if (isLive) (if (row.isOverride) StadiumGoldLight else TurfGreen) else Color.White,
                    fontWeight = if (isLive) FontWeight.Black else FontWeight.Bold,
                    fontSize = 12.sp
                )
                if (isLive) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (row.isOverride) "✏️" else "⚡",
                        fontSize = 10.sp
                    )
                }
            }

            // Clive Column
            Text(
                text = "${row.clive}" + if (row.clive == maxScore && (!isLive || row.clive > 0)) " 👑" else "",
                color = if (row.clive == maxScore && (!isLive || row.clive > 0)) StadiumGoldLight else Color(0xFFE2E8F0),
                fontWeight = if (row.clive == maxScore) FontWeight.Black else FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1.0f)
            )

            // Mark Column
            Text(
                text = "${row.mark}" + if (row.mark == maxScore && (!isLive || row.mark > 0)) " 👑" else "",
                color = if (row.mark == maxScore && (!isLive || row.mark > 0)) StadiumGoldLight else Color(0xFFE2E8F0),
                fontWeight = if (row.mark == maxScore) FontWeight.Black else FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1.0f)
            )

            // Conor Column
            Text(
                text = "${row.conor}" + if (row.conor == maxScore && (!isLive || row.conor > 0)) " 👑" else "",
                color = if (row.conor == maxScore && (!isLive || row.conor > 0)) StadiumGoldLight else Color(0xFFE2E8F0),
                fontWeight = if (row.conor == maxScore) FontWeight.Black else FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1.0f)
            )

            // Winner Column
            Text(
                text = if (isLive && maxScore == 0) "In Progress" else "${row.winnerName} 👑",
                color = if (isLive) Color(0xFF38BDF8) else StadiumGoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1.1f)
            )
        }
    }
}

/**
 * Player career profile card below the spreadsheet
 */
@Composable
fun PlayerCareerCard(
    name: String,
    symbol: String,
    colorHex: Long,
    careerTotal: Int,
    titlesCount: Int,
    score2026: Int,
    isCareerLeader: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCareerLeader) Color(0xFF1E2D4A) else Color(0xFF131D31)
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isCareerLeader) 1.5.dp else 1.dp,
            if (isCareerLeader) StadiumGold else Color(0xFF1E2B45)
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color(colorHex).copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(colorHex)),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = symbol,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = if (symbol.length > 1) 12.sp else 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$careerTotal",
                color = if (isCareerLeader) StadiumGoldLight else Color(colorHex),
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
            Text(
                text = "Career Wins",
                color = Color(0xFF94A3B8),
                fontSize = 9.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = StadiumGold.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "$titlesCount Titles 👑",
                    color = StadiumGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "2026: $score2026",
                color = Color(0xFFCBD5E1),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Interactive dialog allowing manual override of 2026 totals
 */
@Composable
fun ManualOverride2026Dialog(
    currentClive: Int,
    currentMark: Int,
    currentConor: Int,
    liveClive: Int,
    liveMark: Int,
    liveConor: Int,
    isOverrideActive: Boolean,
    onDismiss: () -> Unit,
    onSaveOverride: (Int, Int, Int) -> Unit,
    onResetToCalculated: () -> Unit
) {
    var cliveInput by remember { mutableStateOf(currentClive.toString()) }
    var markInput by remember { mutableStateOf(currentMark.toString()) }
    var conorInput by remember { mutableStateOf(currentConor.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = StadiumGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Manual Override (2026)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Override the live calculated weekly totals with custom numbers from your spreadsheet for 2026.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                // Live calculated reference badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F1728),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "LIVE CALCULATED TOTALS (YTD):",
                            color = TurfGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Clive: $liveClive  •  Mark: $liveMark  •  Conor: $liveConor",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Clive input
                OutlinedTextField(
                    value = cliveInput,
                    onValueChange = { cliveInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Clive 2026 Score [C]") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TurfGreen,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("override_input_clive")
                )

                // Mark input
                OutlinedTextField(
                    value = markInput,
                    onValueChange = { markInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Mark 2026 Score [M]") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StadiumGold,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("override_input_mark")
                )

                // Conor input
                OutlinedTextField(
                    value = conorInput,
                    onValueChange = { conorInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Conor 2026 Score [CO]") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("override_input_conor")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val c = cliveInput.toIntOrNull() ?: currentClive
                    val m = markInput.toIntOrNull() ?: currentMark
                    val co = conorInput.toIntOrNull() ?: currentConor
                    onSaveOverride(c, m, co)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TurfGreen, contentColor = StadiumNavy900),
                modifier = Modifier.testTag("confirm_override_btn")
            ) {
                Text("Save Override", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (isOverrideActive) {
                    TextButton(onClick = onResetToCalculated) {
                        Text("Reset to YTD", color = Color(0xFF38BDF8))
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        },
        containerColor = Color(0xFF131D31)
    )
}
