package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Player
import com.example.data.stats.WeeklyWinnerSummary
import com.example.ui.theme.CoralRed
import com.example.ui.theme.StadiumGoldLight
import com.example.ui.theme.TurfGreen

data class WeeklyCashRow(
    val weekNumber: Int,
    val cliveWins: Int,
    val markWins: Int,
    val conorWins: Int,
    val cliveCash: Int,
    val markCash: Int,
    val conorCash: Int,
    val isFinal: Boolean
) {
    val outcomeDescription: String
        get() = when {
            !isFinal -> "Pending"
            cliveCash > 0 -> "Clive (+€$cliveCash)"
            markCash > 0 -> "Mark (+€$markCash)"
            conorCash > 0 -> "Conor (+€$conorCash)"
            cliveWins == markWins && cliveWins > conorWins -> "Tie (C & M) → Rollover"
            cliveWins == conorWins && cliveWins > markWins -> "Tie (C & CO) → Rollover"
            markWins == conorWins && markWins > cliveWins -> "Tie (M & CO) → Rollover"
            else -> "3-Way Tie → Rollover"
        }
}

private fun buildSeasonCashRowsFromSummaries(
    summaries: List<WeeklyWinnerSummary>,
    players: List<Player>,
    baseStake: Int = 5
): List<WeeklyCashRow> {
    val cliveId = players.firstOrNull { it.name.equals("Clive", ignoreCase = true) }?.id
    val markId = players.firstOrNull { it.name.equals("Mark", ignoreCase = true) }?.id
    val conorId = players.firstOrNull { it.name.equals("Conor", ignoreCase = true) }?.id

    val summaryByWeek = summaries.associateBy { it.weekNumber }
    var rolloverPerLoser = 0

    return (1..18).map { week ->
        val summary = summaryByWeek[week]
        val winsByPlayerId = summary?.playerResults?.associate { it.player.id to it.wins } ?: emptyMap()

        val cliveWins = cliveId?.let { winsByPlayerId[it] } ?: 0
        val markWins = markId?.let { winsByPlayerId[it] } ?: 0
        val conorWins = conorId?.let { winsByPlayerId[it] } ?: 0
        val isWeekFinal = summary?.isCompleted == true

        if (!isWeekFinal) {
            WeeklyCashRow(
                weekNumber = week,
                cliveWins = cliveWins,
                markWins = markWins,
                conorWins = conorWins,
                cliveCash = 0,
                markCash = 0,
                conorCash = 0,
                isFinal = false
            )
        } else {
            val topWins = maxOf(cliveWins, markWins, conorWins)
            val winners = buildList {
                if (cliveWins == topWins) add("CLIVE")
                if (markWins == topWins) add("MARK")
                if (conorWins == topWins) add("CONOR")
            }

            if (winners.size == 1) {
                val perLoserPayout = baseStake + rolloverPerLoser
                rolloverPerLoser = 0

                val winner = winners.first()
                WeeklyCashRow(
                    weekNumber = week,
                    cliveWins = cliveWins,
                    markWins = markWins,
                    conorWins = conorWins,
                    cliveCash = if (winner == "CLIVE") perLoserPayout * 2 else -perLoserPayout,
                    markCash = if (winner == "MARK") perLoserPayout * 2 else -perLoserPayout,
                    conorCash = if (winner == "CONOR") perLoserPayout * 2 else -perLoserPayout,
                    isFinal = true
                )
            } else {
                rolloverPerLoser += baseStake
                WeeklyCashRow(
                    weekNumber = week,
                    cliveWins = cliveWins,
                    markWins = markWins,
                    conorWins = conorWins,
                    cliveCash = 0,
                    markCash = 0,
                    conorCash = 0,
                    isFinal = true
                )
            }
        }
    }
}

@Composable
fun WeeklyCashDashboard(
    weeklySummaries: List<WeeklyWinnerSummary>,
    players: List<Player>,
    modifier: Modifier = Modifier
) {
    val allWeeks = remember(weeklySummaries, players) {
        buildSeasonCashRowsFromSummaries(
            summaries = weeklySummaries,
            players = players,
            baseStake = 5
        )
    }

    val completedWeeks = remember(allWeeks) { allWeeks.filter { it.isFinal } }

    val cliveTotalCash = completedWeeks.sumOf { it.cliveCash }
    val markTotalCash = completedWeeks.sumOf { it.markCash }
    val conorTotalCash = completedWeeks.sumOf { it.conorCash }

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
                            text = "€5/wk • Winner takes €5 each • Ties rollover",
                            color = TurfGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlayerCashCard(
                    name = "Clive",
                    symbol = "C",
                    avatarColorHex = 0xFF00E676,
                    cashBalance = cliveTotalCash,
                    weeksWon = cliveWeeksWon,
                    modifier = Modifier.weight(1f)
                )

                PlayerCashCard(
                    name = "Mark",
                    symbol = "M",
                    avatarColorHex = 0xFFFFB300,
                    cashBalance = markTotalCash,
                    weeksWon = markWeeksWon,
                    modifier = Modifier.weight(1f)
                )

                PlayerCashCard(
                    name = "Conor",
                    symbol = "CO",
                    avatarColorHex = 0xFF38BDF8,
                    cashBalance = conorTotalCash,
                    weeksWon = conorWeeksWon,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "WEEK-BY-WEEK CASH BREAKDOWN",
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            allWeeks.forEach { row ->
                WeeklyCashTableRow(row = row)
                Spacer(modifier = Modifier.height(3.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Color(0xFF1E2B45), thickness = 1.dp)
            Spacer(modifier = Modifier.height(6.dp))

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
            Column(modifier = Modifier.weight(0.9f)) {
                Text(
                    text = "Week ${row.weekNumber}",
                    color = if (row.isFinal) Color.White else Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = "${row.cliveWins}-${row.markWins}-${row.conorWins}",
                    color = if (row.isFinal) Color(0xFF94A3B8) else Color(0xFF475569),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            CashCell(amount = row.cliveCash, isFinal = row.isFinal, modifier = Modifier.weight(1.0f))
            CashCell(amount = row.markCash, isFinal = row.isFinal, modifier = Modifier.weight(1.0f))
            CashCell(amount = row.conorCash, isFinal = row.isFinal, modifier = Modifier.weight(1.0f))

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