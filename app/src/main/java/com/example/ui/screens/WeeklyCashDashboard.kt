package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralRed
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumGoldLight
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen

/**
 * Weekly cash movement record directly corresponding to the 2026 Season spreadsheet.
 */
data class WeeklyCashRow(
    val weekNumber: Int,
    val cliveWins: Int,
    val markWins: Int,
    val conorWins: Int,
    val cliveCash: Int,
    val markCash: Int,
    val conorCash: Int,
    val isFinal: Boolean = true
) {
    val outcomeDescription: String
        get() = when {
            !isFinal -> "Active / Pending"
            cliveCash > 0 -> "Clive Won (+€$cliveCash)"
            markCash > 0 -> "Mark Won (+€$markCash)"
            conorCash > 0 -> "Conor Won (+€$conorCash)"
            cliveWins == markWins && cliveWins > conorWins -> "Tie (C & M)"
            cliveWins == conorWins && cliveWins > markWins -> "Tie (C & CO)"
            markWins == conorWins && markWins > cliveWins -> "Tie (M & CO)"
            else -> "Push (No Cash)"
        }
}

@Composable
fun WeeklyCashDashboard(
    modifier: Modifier = Modifier
) {
    var showAllWeeks by remember { mutableStateOf(false) }

    // Official data from the attached 2026 spreadsheet:
    // Week 1: (8,9,9) -> 0, 0, 0
    // Week 2: (9,8,5) -> 20, -10, -10
    // Week 3: (7,11,4) -> -5, 10, -5
    // Week 4: (8,10,7) -> -5, 10, -5
    // Week 5: (3,7,8) -> -5, -5, 10
    // Week 6: (6,8,8) -> 0, 0, 0
    // Week 7: (7,7,9) -> -10, -10, 20
    // Week 8: (9,5,6) -> 10, -5, -5
    // Profit/Loss Week 1-9: Clive: +5, Mark: -10, Conor: +5
    val allWeeks = remember {
        listOf(
            WeeklyCashRow(weekNumber = 1, cliveWins = 8, markWins = 9, conorWins = 9, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = true),
            WeeklyCashRow(weekNumber = 2, cliveWins = 9, markWins = 8, conorWins = 5, cliveCash = 20, markCash = -10, conorCash = -10, isFinal = true),
            WeeklyCashRow(weekNumber = 3, cliveWins = 7, markWins = 11, conorWins = 4, cliveCash = -5, markCash = 10, conorCash = -5, isFinal = true),
            WeeklyCashRow(weekNumber = 4, cliveWins = 8, markWins = 10, conorWins = 7, cliveCash = -5, markCash = 10, conorCash = -5, isFinal = true),
            WeeklyCashRow(weekNumber = 5, cliveWins = 3, markWins = 7, conorWins = 8, cliveCash = -5, markCash = -5, conorCash = 10, isFinal = true),
            WeeklyCashRow(weekNumber = 6, cliveWins = 6, markWins = 8, conorWins = 8, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = true),
            WeeklyCashRow(weekNumber = 7, cliveWins = 7, markWins = 7, conorWins = 9, cliveCash = -10, markCash = -10, conorCash = 20, isFinal = true),
            WeeklyCashRow(weekNumber = 8, cliveWins = 9, markWins = 5, conorWins = 6, cliveCash = 10, markCash = -5, conorCash = -5, isFinal = true),
            WeeklyCashRow(weekNumber = 9, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false),
            WeeklyCashRow(weekNumber = 10, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false),
            WeeklyCashRow(weekNumber = 11, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false),
            WeeklyCashRow(weekNumber = 12, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false),
            WeeklyCashRow(weekNumber = 13, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false),
            WeeklyCashRow(weekNumber = 14, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false),
            WeeklyCashRow(weekNumber = 15, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false),
            WeeklyCashRow(weekNumber = 16, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false),
            WeeklyCashRow(weekNumber = 17, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false),
            WeeklyCashRow(weekNumber = 18, cliveWins = 0, markWins = 0, conorWins = 0, cliveCash = 0, markCash = 0, conorCash = 0, isFinal = false)
        )
    }

    val completedWeeks = remember(allWeeks) { allWeeks.filter { it.isFinal } }
    val displayedWeeks = if (showAllWeeks) allWeeks else completedWeeks

    val cliveTotalCash = completedWeeks.sumOf { it.cliveCash } // +5
    val markTotalCash = completedWeeks.sumOf { it.markCash }   // -10
    val conorTotalCash = completedWeeks.sumOf { it.conorCash } // +5

    val cliveWeeksWon = completedWeeks.count { it.cliveCash > 0 }
    val markWeeksWon = completedWeeks.count { it.markCash > 0 }
    val conorWeeksWon = completedWeeks.count { it.conorCash > 0 }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_cash_movement_dashboard"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF10192D)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E2D4A))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Dashboard Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = TurfGreen.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TurfGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "€",
                                color = TurfGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "WEEKLY CASH MOVEMENT",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "2026 Season Stakes & Weekly Payouts",
                            color = TurfGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }


            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==================== 3 PLAYER CASH CARDS ====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Clive Card
                PlayerCashCard(
                    name = "Clive",
                    symbol = "C",
                    avatarColorHex = 0xFF00E676,
                    cashBalance = cliveTotalCash,
                    weeksWon = cliveWeeksWon,
                    modifier = Modifier.weight(1f)
                )

                // Mark Card
                PlayerCashCard(
                    name = "Mark",
                    symbol = "M",
                    avatarColorHex = 0xFFFFB300,
                    cashBalance = markTotalCash,
                    weeksWon = markWeeksWon,
                    modifier = Modifier.weight(1f)
                )

                // Conor Card
                PlayerCashCard(
                    name = "Conor",
                    symbol = "CO",
                    avatarColorHex = 0xFF38BDF8,
                    cashBalance = conorTotalCash,
                    weeksWon = conorWeeksWon,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==================== SEASON HALVES PROFIT/LOSS SPLIT ====================
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0C1322),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B44)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "PROFIT / LOSS BY SEASON HALF",
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Row for Week 1-9
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weeks 1–9",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CashSplitChip(label = "Clive", amount = 5, color = TurfGreen)
                            CashSplitChip(label = "Mark", amount = -10, color = CoralRed)
                            CashSplitChip(label = "Conor", amount = 5, color = TurfGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = Color(0xFF1A263D), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Row for Week 10-18
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weeks 10–18",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Scheduled (€0 / €0 / €0)",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==================== WEEKLY CASH LEDGER TABLE ====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WEEK-BY-WEEK CASH BREAKDOWN",
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1A253C),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B3A5A)),
                    modifier = Modifier.clickable { showAllWeeks = !showAllWeeks }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showAllWeeks) "Showing All 18 Wks" else "Showing Weeks 1–8",
                            color = StadiumGoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = if (showAllWeeks) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = StadiumGoldLight,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Table Column Headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C1322), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WEEK",
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    modifier = Modifier.weight(0.9f)
                )
                Text(
                    text = "CLIVE",
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1.0f)
                )
                Text(
                    text = "MARK",
                    color = Color(0xFFFFB300),
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1.0f)
                )
                Text(
                    text = "CONOR",
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1.0f)
                )
                Text(
                    text = "PAYOUT",
                    color = StadiumGoldLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Table Rows
            displayedWeeks.forEach { row ->
                WeeklyCashTableRow(row = row)
                Spacer(modifier = Modifier.height(3.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Color(0xFF1E2B45), thickness = 1.dp)
            Spacer(modifier = Modifier.height(6.dp))

            // ==================== CASH OVERALL ROW ====================
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = TurfGreen.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, TurfGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.9f)) {
                        Text(
                            text = "TOTAL",
                            color = TurfGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Net Cash",
                            color = Color(0xFF94A3B8),
                            fontSize = 8.sp
                        )
                    }

                    Text(
                        text = formatCash(cliveTotalCash),
                        color = if (cliveTotalCash >= 0) TurfGreen else CoralRed,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.0f)
                    )

                    Text(
                        text = formatCash(markTotalCash),
                        color = if (markTotalCash >= 0) TurfGreen else CoralRed,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.0f)
                    )

                    Text(
                        text = formatCash(conorTotalCash),
                        color = if (conorTotalCash >= 0) TurfGreen else CoralRed,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.0f)
                    )

                    Text(
                        text = "Net €0 ⚖️",
                        color = TurfGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerCashCard(
    name: String,
    symbol: String,
    avatarColorHex: Long,
    cashBalance: Int,
    weeksWon: Int,
    modifier: Modifier = Modifier
) {
    val isProfit = cashBalance > 0
    val isLoss = cashBalance < 0
    val cardBg = when {
        isProfit -> Color(0xFF0D251A)
        isLoss -> Color(0xFF261017)
        else -> Color(0xFF131D31)
    }
    val cardBorder = when {
        isProfit -> TurfGreen.copy(alpha = 0.5f)
        isLoss -> CoralRed.copy(alpha = 0.5f)
        else -> Color(0xFF223352)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(avatarColorHex).copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(avatarColorHex)),
                    modifier = Modifier.size(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = symbol,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatCash(cashBalance),
                color = when {
                    isProfit -> TurfGreen
                    isLoss -> CoralRed
                    else -> Color(0xFF94A3B8)
                },
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$weeksWon Wks Won",
                color = StadiumGoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun CashSplitChip(
    label: String,
    amount: Int,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = "$label: ${formatCash(amount)}",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun WeeklyCashTableRow(
    row: WeeklyCashRow,
    modifier: Modifier = Modifier
) {
    val rowBg = if (row.isFinal) Color(0xFF141F36) else Color(0xFF0F1728)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = rowBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (row.isFinal) Color(0xFF1F2E4F) else Color(0xFF18243C)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Week Column + scores pill
            Column(modifier = Modifier.weight(0.9f)) {
                Text(
                    text = "Week ${row.weekNumber}",
                    color = if (row.isFinal) Color.White else Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                if (row.isFinal) {
                    Text(
                        text = "${row.cliveWins}-${row.markWins}-${row.conorWins}",
                        color = Color(0xFF94A3B8),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Clive Column
            CashCell(amount = row.cliveCash, isFinal = row.isFinal, modifier = Modifier.weight(1.0f))

            // Mark Column
            CashCell(amount = row.markCash, isFinal = row.isFinal, modifier = Modifier.weight(1.0f))

            // Conor Column
            CashCell(amount = row.conorCash, isFinal = row.isFinal, modifier = Modifier.weight(1.0f))

            // Payout / Outcome description
            Text(
                text = row.outcomeDescription,
                color = when {
                    !row.isFinal -> Color(0xFF64748B)
                    row.cliveCash > 0 || row.markCash > 0 || row.conorCash > 0 -> StadiumGoldLight
                    else -> Color(0xFF94A3B8)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1.2f)
            )
        }
    }
}

@Composable
private fun CashCell(
    amount: Int,
    isFinal: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (!isFinal) {
            Text(
                text = "-",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            val isPos = amount > 0
            val isNeg = amount < 0
            val chipBg = when {
                isPos -> TurfGreen.copy(alpha = 0.2f)
                isNeg -> CoralRed.copy(alpha = 0.2f)
                else -> Color(0xFF1E2B44)
            }
            val textColor = when {
                isPos -> TurfGreen
                isNeg -> CoralRed
                else -> Color(0xFF94A3B8)
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = chipBg
            ) {
                Text(
                    text = formatCash(amount),
                    color = textColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

private fun formatCash(amount: Int): String {
    return when {
        amount > 0 -> "+€$amount"
        amount < 0 -> "-€${kotlin.math.abs(amount)}"
        else -> "€0"
    }
}
