package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.StadiumGold
import com.example.ui.theme.StadiumGoldLight
import com.example.ui.theme.StadiumNavy900
import com.example.ui.theme.TurfGreen
import com.google.firebase.auth.FirebaseUser

/**
 * Full-screen security gate ensuring only Clive, Mark, and Conor can access the app.
 * Allows instant access via:
 * 1. Google Sign-In with an authorized email (clivecleary@gmail.com, or whitelisted Mark/Conor emails)
 * 2. League Access Passcode (default "RIVALS-CMC")
 */
@Composable
fun PrivateLeagueGateScreen(
    currentUser: FirebaseUser?,
    onUserSignedIn: (FirebaseUser) -> Unit,
    onUnlockWithPasscode: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var passcodeInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPasscodeEntry by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .testTag("private_league_gate")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lock / Shield Icon
            Surface(
                shape = CircleShape,
                color = Color(0xFF131D31),
                border = androidx.compose.foundation.BorderStroke(2.dp, StadiumGold),
                modifier = Modifier.size(76.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Private League",
                        tint = StadiumGold,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumGold.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumGold)
            ) {
                Text(
                    text = "🔒 PRIVATE LEAGUE • INVITE ONLY",
                    color = StadiumGoldLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Spread Rivals Pick'em",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Restricted strictly to Clive, Mark, and Conor.\nUnauthorized access is blocked.",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Google Sign-In Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2B45))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TurfGreen.copy(alpha = 0.2f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = TurfGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sign in with Google",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Clive (clivecleary@gmail.com), Mark, or Conor",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    val context = androidx.compose.ui.platform.LocalContext.current
                    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
                    val credentialManager = remember { androidx.credentials.CredentialManager.create(context) }
                    var isSigningIn by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            isSigningIn = true
                            signInWithGoogle(
                                context = context,
                                credentialManager = credentialManager,
                                scope = coroutineScope,
                                onSuccess = { user ->
                                    isSigningIn = false
                                    onUserSignedIn(user)
                                },
                                onError = { err ->
                                    isSigningIn = false
                                    errorMessage = err
                                },
                                onCancelled = {
                                    isSigningIn = false
                                }
                            )
                        },
                        enabled = !isSigningIn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TurfGreen,
                            contentColor = StadiumNavy900
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gate_google_sign_in_btn")
                    ) {
                        if (isSigningIn) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = StadiumNavy900,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connecting to Google...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        } else {
                            Text("Sign in with Google", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Or enter passcode
            if (!showPasscodeEntry) {
                Button(
                    onClick = { showPasscodeEntry = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B2844),
                        contentColor = Color(0xFFE2E8F0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter League Access Passcode",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263350))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "LEAGUE SECRET PASSCODE",
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = passcodeInput,
                            onValueChange = {
                                passcodeInput = it
                                errorMessage = null
                            },
                            placeholder = { Text("e.g. RIVALS-CMC", color = Color(0xFF64748B), fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("league_passcode_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TurfGreen,
                                unfocusedBorderColor = Color(0xFF2B3C63),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = TurfGreen
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = errorMessage!!,
                                color = Color(0xFFF87171),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val success = onUnlockWithPasscode(passcodeInput)
                                if (!success) {
                                    errorMessage = "Invalid passcode. Check with Clive for the invite code."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TurfGreen,
                                contentColor = StadiumNavy900
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("unlock_league_button")
                        ) {
                            Text(
                                text = "Unlock Private League",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Commissioner footnote
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "League Commissioner: Clive (clivecleary@gmail.com)",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }
        }
    }
}
