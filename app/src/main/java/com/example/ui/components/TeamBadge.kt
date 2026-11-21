package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NflTeams // Keep your exact NflTeams import here

@Composable
fun TeamBadge(
    teamCode: String,
    size: Int = 28,
    modifier: Modifier = Modifier
) {
    val logoRes = NflTeams.getLogoRes(teamCode)

    if (logoRes != null) {
        Box(
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "$teamCode logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        val team = NflTeams.teams.firstOrNull {
            it.code.equals(teamCode.trim(), ignoreCase = true)
        }
        Box(
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(team?.primaryColor ?: Color(0xFF1E293B))
                .border(
                    width = 1.5.dp,
                    color = team?.secondaryColor ?: Color(0xFF475569),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = teamCode.trim().take(3).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size * 0.34).sp
            )
        }
    }
}