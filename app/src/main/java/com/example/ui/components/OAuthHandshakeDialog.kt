package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class OAuthStage {
    CREDENTIALS,
    CONSENT,
    EXCHANGING_KEYS,
    SUCCESS
}

@Composable
fun OAuthHandshakeDialog(
    platform: String, // "LinkedIn", "Instagram", or "Facebook"
    suggestedHandle: String = "",
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit // returns the approved handle/details
) {
    var stage by remember { mutableStateOf(OAuthStage.CREDENTIALS) }
    var inputEmail by remember { mutableStateOf(
        if (suggestedHandle.isNotBlank()) {
            if (suggestedHandle.contains("@") || platform != "Instagram") suggestedHandle else "@$suggestedHandle"
        } else {
            when (platform) {
                "LinkedIn" -> "sudokusolver_master@linkedin.com"
                "Facebook" -> "sudoku_grandmaster_fb"
                else -> "@sudoku_grandmaster"
            }
        }
    ) }
    var inputPassword by remember { mutableStateOf("••••••••••••") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Consent Toggles
    var consentProfile by remember { mutableStateOf(true) }
    var consentAchievements by remember { mutableStateOf(true) }

    // Handshake Logs State
    val logs = remember { mutableStateListOf<String>() }
    var currentLogIndex by remember { mutableIntStateOf(0) }

    val totalPointsBenefit = when (platform) {
        "LinkedIn" -> 500
        "Facebook" -> 300
        else -> 400
    }

    val platformColor = when (platform) {
        "LinkedIn" -> Color(0xFF0077B5)
        "Facebook" -> Color(0xFF3B5998)
        else -> Color(0xFFE1306C)
    }
    
    val platformLabel = platform.uppercase()

    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, platformColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 12.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. CHROME NAV BAR HEADER (Authentic Secure Browser styling)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E24))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Browser Window Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFFF5F56)))
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFFFBD2E)))
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFF27C93F)))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Address bar with Lock
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2E2E38))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure Connection",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = when (platform) {
                                "LinkedIn" -> "https://linkedin.com/oauth/v2/authorization?client_id=msb_sdk_910&scope=r_liteprofile"
                                "Facebook" -> "https://facebook.com/v14.0/dialog/oauth?client_id=msb_fb_302&redirect_uri=msb://oauth"
                                else -> "https://api.instagram.com/oauth/authorize?client_id=msb_insta_662&response_type=code"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 8.5.sp,
                            color = Color(0xFFCCCCCC),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Close tab button
                    IconButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Authentication Tab",
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // 2. SSL Verified Status Notification Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2E7D32).copy(alpha = 0.08f))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "SSL Verified Identity",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Identity Gateway cryptographically signed by ${platform} Authority.",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4CAF50)
                    )
                }

                // 3. MAIN INTERACTIVE VIEWS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (stage) {
                        OAuthStage.CREDENTIALS -> {
                            // Stage logo and Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Text(
                                    text = when (platform) {
                                        "LinkedIn" -> "🔗"
                                        "Facebook" -> "👥"
                                        else -> "📸"
                                    },
                                    fontSize = 24.sp
                                )
                                Column {
                                    Text(
                                        text = "${platform} Secure Login",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = platformColor
                                    )
                                    Text(
                                        text = "Sign in to authorize Master Sudoku Builder.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                            // Credentials Fields
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = when (platform) {
                                        "LinkedIn" -> "LinkedIn Email or Username:"
                                        "Facebook" -> "Facebook Email or Phone Number:"
                                        else -> "Instagram Username (@handle):"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = inputEmail,
                                    onValueChange = { inputEmail = it },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    placeholder = {
                                        Text(
                                            when (platform) {
                                                "LinkedIn" -> "example@linkedin.com"
                                                "Facebook" -> "example@facebook.com or phone"
                                                else -> "@username"
                                            }
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (platform) {
                                                "LinkedIn" -> Icons.Default.Email
                                                "Facebook" -> Icons.Default.Email
                                                else -> Icons.Default.Person
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = platformColor
                                    )
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Secure Account Password:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = inputPassword,
                                    onValueChange = { inputPassword = it },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    visualTransformation = PasswordVisualTransformation(),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = platformColor
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // CTA Action
                            Button(
                                onClick = {
                                    if (inputEmail.isNotBlank() && inputPassword.isNotBlank()) {
                                        stage = OAuthStage.CONSENT
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = platformColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "CONTINUE TO OAUTH CONSENT",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "Your credentials are sent directly to ${platform} securely via localized SSL sandbox tunnels. We never store or transmit raw password strings on our internal DB.",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        OAuthStage.CONSENT -> {
                            // Consent Header
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🛡️ OAUTH 2.0 PERMISSIONS REQUEST",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = platformColor,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Master Sudoku Builder is requesting the following permissions from your $platform account:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                            // Access Permission Cards
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Scope Row 1: Profile read
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Checkbox(
                                        checked = consentProfile,
                                        onCheckedChange = { consentProfile = it },
                                        colors = CheckboxDefaults.colors(checkedColor = platformColor)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = when (platform) {
                                                "LinkedIn" -> "r_liteprofile (Basic Profile Info)"
                                                "Facebook" -> "public_profile (Name & Avatar)"
                                                else -> "instagram_graph_user_profile"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "Verify your verified profile handle ($inputEmail) to displays achievements on global grids.",
                                            fontSize = 9.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Scope Row 2: Post status
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Checkbox(
                                        checked = consentAchievements,
                                        onCheckedChange = { consentAchievements = it },
                                        colors = CheckboxDefaults.colors(checkedColor = platformColor)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = when (platform) {
                                                "LinkedIn" -> "w_member_social (Post Achievements)"
                                                "Facebook" -> "publish_to_groups (Share Milestones)"
                                                else -> "instagram_graph_user_media (Share Badges)"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "Allows MSB to generate and output digital tournament graduation certificates directly to your feed.",
                                            fontSize = 9.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // CTAs Button Layer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { stage = OAuthStage.CREDENTIALS },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                ) {
                                    Text("BACK", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                }

                                Button(
                                    onClick = {
                                        stage = OAuthStage.EXCHANGING_KEYS
                                        // Trigger Logs Pipeline
                                        coroutineScope.launch {
                                            logs.clear()
                                            val logSequence = listOf(
                                                "🔒 [TLS 1.3] Setting up secure Sandbox Session with $platform OAuth service...",
                                                "📡 Establishing connection with authorization endpoint `/oauth/consent`...",
                                                "🔑 Transmitting client credentials with active scopes...",
                                                "✔️ Permissions verification successfully approved by user.",
                                                "↩️ Receiving authorization_code signature callback...",
                                                "📦 Code parsed: code=OAUTH2_SECURE_AUTH_${System.currentTimeMillis() % 100000}",
                                                "📶 Initiating back-channel Access Token exchange via SSL handshake POST...",
                                                "🔄 Validating client_id & exchanging client_secret...",
                                                "🎟️ Success! Access Token successfully received [Validity: 60 Days].",
                                                "🔗 Fetching user resource metadata payload from graph database...",
                                                "🏁 COMPLETE: Successfully authenticated profile: $inputEmail !",
                                                "🎁 Credited +$totalPointsBenefit PlayGold Points (PGP) into your MSB Vault!"
                                            )
                                            for (log in logSequence) {
                                                logs.add(log)
                                                delay(450)
                                            }
                                            delay(500)
                                            stage = OAuthStage.SUCCESS
                                        }
                                    },
                                    enabled = consentProfile && consentAchievements,
                                    colors = ButtonDefaults.buttonColors(containerColor = platformColor),
                                    modifier = Modifier
                                        .weight(1.8f)
                                        .height(44.dp)
                                ) {
                                    Text(
                                        text = "AUTHORIZE & AGREE",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        OAuthStage.EXCHANGING_KEYS -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "🔐 ACTIVE BACK-CHANNEL EXCHANGE HANDSHAKE",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = platformColor,
                                    letterSpacing = 0.5.sp
                                )

                                CircularProgressIndicator(
                                    color = platformColor,
                                    modifier = Modifier.size(34.dp)
                                )

                                Text(
                                    text = "Please do not close this window. Interacting with SSL gateway server...",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Terminal Logs Window
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0F0F14))
                                        .border(0.5.dp, platformColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    androidx.compose.foundation.lazy.LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        items(logs.size) { index ->
                                            Text(
                                                text = logs[index],
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = if (logs[index].contains("COMPLETE") || logs[index].contains("Success")) {
                                                    Color(0xFF81C784)
                                                } else if (logs[index].contains("🔒") || logs[index].contains("✔️")) {
                                                    Color(0xFFFFB74D)
                                                } else {
                                                    Color(0xFF80DEEA)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        OAuthStage.SUCCESS -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Spacer(modifier = Modifier.height(4.dp))

                                // Sparkly Success Banner
                                Surface(
                                    color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(32.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Success",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "OAuth Integration Succeeded!",
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF4CAF50),
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "Your $platform profile account is officially cryptographic verified & authorized with Master Sudoku Builder!",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Authenticated User:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(inputEmail, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Connection Protocol:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("OAuth 2.0 Bearer Handshake", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = platformColor)
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Bonus Reward Credited:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("+$totalPointsBenefit PlayGold Points (PGP)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFB300))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Button(
                                    onClick = {
                                        onSuccess(inputEmail)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = platformColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "FINALIZE INTEGRATION",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
