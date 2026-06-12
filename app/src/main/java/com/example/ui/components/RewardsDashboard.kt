package com.example.ui.components

import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RewardTransactionEntity
import com.example.data.UserProfileEntity
import com.example.data.GameHistoryEntity
import com.example.ui.ClaimingProgress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ==========================================================================================
 * MSB SUDOKU CHALLENGE ACADEMY - REWARDS & COGNITIVE DASHBOARD
 * ==========================================================================================
 *
 * This component acts as the central hub for gamified intellectual accomplishments, progress tracking,
 * and value redemptions. Developed under modern Material Design 3 guidelines, it offers a visually
 * rich UI that communicates technical milestones through gorgeous grids, micro-interactions, and 
 * high-contrast color pairings.
 *
 * KEY ARCHITECTURAL RESPONSIBILITIES:
 * ----------------------------------
 * 1. STATUS HEADER:
 *    Displays active profile parameters, level progression, and gamified experience (XP) levels.
 *
 * 2. SECURE WALLET:
 *    Highlights PlayGold Points (PGP) and premium Gems balances. Shows progression towards milestones.
 *
 * 3. COGNITIVE SHOP & REDEMPTION STORE:
 *    Allows solvers to translate their logical efforts (PGP) into premium in-app resources or simulated
 *    digital gift certificates, processed securely via a compliance transceiver overlay.
 *
 * 4. CAREER & CV CERTIFICATION HUB:
 *    Integrates the Google Gemini API to analyze personal puzzle speeds and draft customized cognitive 
 *    endorsements. Supports exporting high-fidelity credentials as PNG/PDF certificates, and share intents.
 *
 * 5. COMPLIANCE TRANSCEIVER:
 *    An animated multi-phase transaction validator simulation confirming secure SSL handshakes, anti-cheat,
 *    and cryptographically synthesized voucher receipts.
 * ==========================================================================================
 */

@Composable
fun RewardsDashboard(
    playGoldPoints: Int,
    gemsCount: Int,
    userProfile: UserProfileEntity?,
    gameHistory: List<GameHistoryEntity>,
    transactions: List<RewardTransactionEntity>,
    claimingState: ClaimingProgress,
    onClaimSelected: (String, Int) -> Unit,
    onConfirmReceipt: (RewardTransactionEntity) -> Unit,
    onCancelClaim: () -> Unit,
    onRedeemPromoCode: ((String, (String) -> Unit) -> Unit)? = null,
    onNavigateToLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Utility to copy verification codes, certificate links, and CV captions to the device clipboard
    val clipboardManager = LocalClipboardManager.current
    var showCopiedToast by remember { mutableStateOf(false) }
    
    // Resolve ISO country codes dynamically based on selected user profile region settings
    val countryCode = when(userProfile?.region?.lowercase()) {
        "americas" -> "US"
        "europe" -> "DE"
        "asia-pacific" -> "JP"
        "india" -> "IN"
        "vietnam" -> "VN"
        "africa" -> "NG"
        else -> "US"
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            /**
             * ------------------------------------------------------------------------------
             * 0. MSB SUDOKU HUB & STATUS HEADER
             * ------------------------------------------------------------------------------
             * Renders current user identities, level indicators, and an interactive XP bar.
             * Safe level progression is estimated symmetrically based on wins and saved PlayGold Points.
             */
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎮", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                    text = "MSB COGNITIVE ARENA HUB",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFF198754),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "CONNECTED",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (userProfile != null) userProfile.username else "CerebralSolver_XP",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        // Calculated XP limits and percentages to trigger leveling transitions gracefully
                        val currentLevel = userProfile?.level ?: 3
                        val wins = gameHistory.count { it.status == "WON" }
                        val currentXp = (wins * 350 + (playGoldPoints) / 4) % 1000
                        val targetXp = 1000
                        val xpProgress = currentXp.toFloat() / targetXp

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Lvl $currentLevel",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF198754)
                            )
                            // Linear indicator mirroring verified progression mathematically
                            LinearProgressIndicator(
                                progress = { xpProgress },
                                color = Color(0xFF198754),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(CircleShape)
                             )
                            Text(
                                text = "$currentXp / $targetXp XP",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            /**
             * ------------------------------------------------------------------------------
             * 1. POINT BALANCE CARD (High-Fidelity Glass Surface)
             * ------------------------------------------------------------------------------
             * Standardized display for redeemable assets. It manages the user's primary points currency 
             * (PlayGold Points) and Gem counts side-by-side inside custom rounded layouts.
             */
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "SECURE WALLET STATUS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "PlayGold Balance",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = String.format("%,d", playGoldPoints),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFC107)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PGP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFC107)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Gems Bank",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = gemsCount.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00BCD4)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "GEMS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00BCD4)
                                )
                            }
                        }
                    }

                    // Progress bar mapping current point metrics against next $5 threshold (10,000 points)
                    val targetPoints = 10000
                    val percentProgress = (playGoldPoints.toFloat() / targetPoints).coerceAtMost(1f)
                    Text(
                        text = "Progress toward the next MSB Cognitive Reward Milestone ($5 value credit)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { percentProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFFFFC107),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(percentProgress * 100).toInt()}% achieved",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${playGoldPoints}/$targetPoints PGP",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * ------------------------------------------------------------------------------
             * SAFE, GENUINE EDUCATIONAL INFORMATION CARD
             * ------------------------------------------------------------------------------
             * Explicit educational guide teaching players about how point assets, ratings,
             * and leaderboards verify authentic intellectual achievement while remaining fully compliant
             * with competitive standards. Contains route navigation triggers directly to the main Arena.
             */
            val context = androidx.compose.ui.platform.LocalContext.current
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🏆", fontSize = 16.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "MSB ELITE LEADERBOARD HONORS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Compete globally through Cognitive AI score points",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "To guarantee fair tournament standards, this network is 100% free and client-driven. PlayGold Points (PGP) indicate your verified cognitive resolution capacities, tracked securely via MSB Creative Studios. Continue solving matrices to rise through the global ranks and establish mathematical supremacy with our custom metrics!",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onNavigateToLeaderboard,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("⚡ VIEW LEADERBOARD HIGHLIGHTS & RANKINGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * ------------------------------------------------------------------------------
             * 2. COGNITIVE LEADERBOARD MINI-PREVIEW
             * ------------------------------------------------------------------------------
             * Evaluates and sorts performance indices dynamically. Compares local profile score records 
             * directly against a set on-device global solver roster in real-time.
             */
            Text(
                text = "🏆 COGNITIVE LEADERBOARD & PLAYER POINTS SYNC",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Rewards now focus entirely on intellectual achievement. Rank up, build streaks, and compare your cognitive power (PGP) against global solvers in real-time.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToLeaderboard() }
                    .testTag("leaderboard_rewards_preview_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("RANK", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(44.dp))
                        Text("PLAYER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        Text("REGION", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(90.dp), textAlign = TextAlign.Center)
                        Text("SCORE (PGP)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(80.dp), textAlign = TextAlign.End)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    val currentUsername = userProfile?.username ?: "MSB GRANDMASTER"
                    val currentRegion = userProfile?.region ?: "Americas"
                    val mockTopList = listOf(
                        Triple("🥇 1", "Yuki_Tokyo", 3120 to "Asia-Pacific"),
                        Triple("🥈 2", "Sven_Berlin", 2980 to "Europe"),
                        Triple("🥉 3", "Alex_NYC", 2750 to "Americas"),
                        Triple("🎖️ 4", "Amara_Lagos", 2580 to "Africa"),
                        Triple("⚡ YOU", currentUsername, playGoldPoints to currentRegion)
                    ).sortedByDescending { it.third.first }

                    mockTopList.forEachIndexed { index, entry ->
                        val isUser = entry.second == currentUsername
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent)
                                .padding(vertical = 6.dp, horizontal = if (isUser) 6.dp else 0.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isUser) "⭐ YOU" else entry.first,
                                style = if (isUser) MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold) else MaterialTheme.typography.bodySmall,
                                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(44.dp)
                            )
                            Text(
                                text = entry.second,
                                style = if (isUser) MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold) else MaterialTheme.typography.bodySmall,
                                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Text(
                                    text = entry.third.second,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(90.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${String.format("%,d", entry.third.first)} PGP",
                                style = if (isUser) MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold) else MaterialTheme.typography.bodySmall,
                                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(80.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            /**
             * ------------------------------------------------------------------------------
             * DEVELOPER PROMO CODE VAULT
             * ------------------------------------------------------------------------------
             * Allows players to inject preset debug coupons (e.g. WELCOME_BONUS, MSB_CHALLENGE) 
             * to immediately update points and gems counts without puzzle completions.
             */
            var promoCodeInput by remember { mutableStateOf("") }
            var promoResultMsg by remember { mutableStateOf<String?>(null) }
            var isPromoSuccess by remember { mutableStateOf(false) }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🎟️ COUPON REDEMPTION VAULT",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Redeem exclusive developer promo codes (WELCOME_BONUS, MSB_CHALLENGE, SUDOKU_FREE_GP) to secure free points, gems, and instant redeemed vouchers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        lineHeight = 13.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promoCodeInput,
                            onValueChange = { promoCodeInput = it },
                            placeholder = { Text("Code: e.g. WELCOME_BONUS", fontSize = 12.sp) },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (onRedeemPromoCode != null) {
                                    onRedeemPromoCode(promoCodeInput) { result ->
                                        promoResultMsg = result
                                        isPromoSuccess = result.contains("Applied successfully")
                                        if (isPromoSuccess) {
                                            promoCodeInput = ""
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Text("REDEEM", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    promoResultMsg?.let { msg ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isPromoSuccess) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFF44336).copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg,
                                    color = if (isPromoSuccess) Color(0xFF388E3C) else Color(0xFFD32F2F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { promoResultMsg = null },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Close promo alert",
                                        tint = if (isPromoSuccess) Color(0xFF388E3C) else Color(0xFFD32F2F),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /**
             * ------------------------------------------------------------------------------
             * 3. MILESTONES & ACTIVE METRICS HEADER
             * ------------------------------------------------------------------------------
             * Initializes calculation algorithms for consecutive wins, best times, speed badges, 
             * and master designations based on user game histories.
             */
            Text(
                text = "ACHIEVEMENT MILESTONES & BADGES",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val consecutiveWins = remember(gameHistory) {
                var maxStreak = 0
                var currentStreak = 0
                val sortedHistory = gameHistory.sortedBy { it.timestamp }
                for (game in sortedHistory) {
                    if (game.status == "WON") {
                        currentStreak++
                        if (currentStreak > maxStreak) {
                            maxStreak = currentStreak
                        }
                    } else if (game.status == "LOST") {
                        currentStreak = 0
                    }
                }
                maxStreak
            }

            val bestTimeSeconds = remember(gameHistory) {
                val wonGames = gameHistory.filter { it.status == "WON" && it.timeElapsedSeconds > 0 }
                if (wonGames.isEmpty()) Long.MAX_VALUE else wonGames.minOf { it.timeElapsedSeconds }
            }
            val isSpeedDemonEarned = bestTimeSeconds in 1..299

            val totalWinsCount = remember(gameHistory) {
                gameHistory.count { it.status == "WON" }
            }
            val hasCompletedExpertOrHardOrArena = remember(gameHistory) {
                gameHistory.any { it.status == "WON" && (it.difficulty.uppercase() == "HARD" || it.difficulty.uppercase() == "EXPERT" || it.difficulty.uppercase() == "ARENA") }
            }
            val isSudokuMasterEarned = totalWinsCount >= 10 || (userProfile != null && userProfile.level >= 5) || hasCompletedExpertOrHardOrArena

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                /**
                 * ------------------------------------------------------------------------------
                 * DYNAMIC BRAIN METRICS AND STATS CARD
                 * ------------------------------------------------------------------------------
                 * Reads user history models to output:
                 * - Wins vs total played matches.
                 * - Exact win percentages.
                 * - Active puzzle-streak ratings.
                 * - Estimated Synaptic Speed (measured in Hertz throughput).
                 * - Personal Best time metrics for Easy, Medium, Hard, and Expert categories.
                 */
                item {
                    val winsCount = remember(gameHistory) { gameHistory.count { it.status == "WON" } }
                    val totalPlayed = gameHistory.size
                    val winPercentage = if (totalPlayed > 0) (winsCount.toFloat() / totalPlayed * 100).toInt() else 0
                    
                    val bestEasy = userProfile?.bestTimeEasy ?: 0L
                    val bestMedium = userProfile?.bestTimeMedium ?: 0L
                    val bestHard = userProfile?.bestTimeHard ?: 0L
                    val bestExpert = userProfile?.bestTimeExpert ?: 0L

                    val level = userProfile?.level ?: 1

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Text("📊", fontSize = 20.sp)
                                Column {
                                    Text(
                                        text = "MY COGNITIVE BRAIN METRICS",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.secondary,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Real-time verification of your puzzle metrics",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text("Puzzles Solved", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$winsCount / $totalPlayed", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Win Ratio: $winPercentage%", fontSize = 9.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
                                }
                                
                                Box(modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))

                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text("Max Win Streak", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$consecutiveWins Wins", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                                    Text("Tactical Streak", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Box(modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))

                                val calculatedHz = remember(level, winsCount) {
                                    8.5 + (level * 1.5) + (winsCount * 0.15).coerceAtMost(5.0)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text("Synaptic Speed", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(String.format(Locale.getDefault(), "%.2f Hz", calculatedHz), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                                    Text("Mental Throughput", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "⏱️ PERSONAL BEST SOLVE DURATION RECORDS:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            val formatTime: (Long) -> String = { sec ->
                                if (sec <= 0) "No record" else "${sec / 60}m ${sec % 60}s"
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val difficulties = listOf("Easy", "Medium", "Hard", "Expert")
                                val times = listOf(bestEasy, bestMedium, bestHard, bestExpert)
                                val colors = listOf(Color(0xFF81C784), Color(0xFFFFD54F), Color(0xFFFF8A65), Color(0xFFE57373))

                                difficulties.forEachIndexed { idx, diff ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f)),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(diff.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = colors[idx])
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(formatTime(times[idx]), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                /**
                 * ------------------------------------------------------------------------------
                 * 🎁 COGNITIVE PRODUCTS & REDEMPTION SHOP GRID
                 * ------------------------------------------------------------------------------
                 * Promotes dynamic exchanges for in-game enhancers like Gems boosters, active
                 * puzzle-mistake shields, mindfulness background tracks, or $5/$10 Play Gift Vouchers.
                 */
                item {
                    Text(
                        text = "🎁 COGNITIVE SHOP & REDEMPTION STORE",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
                    )
                    Text(
                        text = "Exchange your earned PlayGold Points (PGP) to gain premium Gems or redeem simulated Google Play Gift Cards instantly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    val storeGifts = listOf(
                        GiftItemData("🎁 $5 Google Play Card", 5000, "GIFT-CODE-$5"),
                        GiftItemData("🎁 $10 Google Play Card", 10000, "GIFT-CODE-$10"),
                        GiftItemData("💎 25 Gems Booster Pack", 1500, "GEMS-BOOST-25"),
                        GiftItemData("💎 100 Gems Vault Combo", 5000, "GEMS-VAULT-100"),
                        GiftItemData("🛡️ Mistakes Shield Pack", 1200, "SHIELD-SAVER-M"),
                        GiftItemData("🎵 Mindfulness Ambiance Pass", 2000, "AMBIENT-MUSIC-BG")
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in storeGifts.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    GiftCard(
                                        gift = storeGifts[i],
                                        userPoints = playGoldPoints,
                                        onClaimSelected = onClaimSelected
                                    )
                                }
                                if (i + 1 < storeGifts.size) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        GiftCard(
                                            gift = storeGifts[i + 1],
                                            userPoints = playGoldPoints,
                                            onClaimSelected = onClaimSelected
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                /**
                 * ------------------------------------------------------------------------------
                 * CUSTOM STANDALONE CAREER CERTIFICATE AND RESUME / CV HUB
                 * ------------------------------------------------------------------------------
                 * A centerpiece feature. Generates academic certifications reflecting the solver's best
                 * accomplishments on the Sudoku matrix.
                 *
                 * LOGICAL WORKFLOW:
                 * 1. Dynamic Metric Assembly: Gathers best difficulty solved, shortest time duration,
                 *    global percentile, estimated focus rating, and synaptic speed.
                 * 2. Gemini Endorsement Integration: Triggers an asynchronous network query to Gemini AI via
                 *    `GeminiClient.getCertificateEndorsement`, parsing stats to draft a customized academic
                 *    endorsement code text block. Falls back to pre-formulated templates if offline.
                 * 3. Export Operations: Downloader triggers render certificates instantly as on-device PNG or PDF,
                 *    relying on `CertificateDownloader` utility methods inside the device's main Downloads folder.
                 * 4. Social Integration: Provides structured clipboard caption templates for LinkedIn, Twitter,
                 *    Facebook, and Instagram, auto-launching standard chooser Intents.
                 */
                item {
                    var hubStatusText by remember { mutableStateOf<String?>(null) }
                    var certNameInput by remember { mutableStateOf(userProfile?.username ?: "MSB GRANDMASTER") }
                    val context = androidx.compose.ui.platform.LocalContext.current

                    val wonGames = remember(gameHistory) { gameHistory.filter { it.status == "WON" } }
                    val bestGame = remember(wonGames) { wonGames.minByOrNull { it.timeElapsedSeconds } }

                    val hubGridSize = 9
                    val hubDifficulty = bestGame?.difficulty ?: "Medium"
                    val hubDuration = if (bestGame != null && bestGame.timeElapsedSeconds > 0) bestGame.timeElapsedSeconds else 315L

                    val hubFocusRating = remember(wonGames) {
                        if (wonGames.isEmpty()) 92.5 else (92.5 + (wonGames.count() * 0.5)).coerceAtMost(99.9)
                    }

                    val hubSynapticSpeed = remember(userProfile, wonGames) {
                        val lvl = userProfile?.level ?: 3
                        val base = 8.5 + (lvl * 1.5)
                        val variance = if (wonGames.isNotEmpty()) (wonGames.size * 0.15).coerceAtMost(5.0) else 1.2
                        base + variance
                    }

                    val hubGlobalPercentile = remember(userProfile, wonGames) {
                        val lvl = userProfile?.level ?: 3
                        val wins = wonGames.size
                        val calc = 8.5 / (lvl * 1.8 + wins * 0.6 + 1.2)
                        maxOf(0.005, minOf(45.0, calc))
                    }

                    val m = hubDuration / 60
                    val s = hubDuration % 60
                    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", m, s)
                    val speedStr = String.format(Locale.getDefault(), "%.2f", hubSynapticSpeed)
                    val focusStr = String.format(Locale.getDefault(), "%.1f", hubFocusRating)
                    val rankStr = String.format(Locale.getDefault(), "%.3f", hubGlobalPercentile)

                    var aiEndorsementText by remember { mutableStateOf<String?>(null) }
                    var isGeneratingEndorsement by remember { mutableStateOf(false) }
                    var endorsementStatus by remember { mutableStateOf("Pending AI audit seal verification...") }

                    val localFallbackText = "COMMENDATION: Demonstrated supreme algorithmic pattern recognition. Solved a ${hubGridSize}x${hubGridSize} (${hubDifficulty.uppercase()}) matrix in ${timeStr} with ${String.format("%.2f", hubSynapticSpeed)}Hz average throughput."

                    // Launches an audit event whenever name strings change
                    LaunchedEffect(certNameInput) {
                        isGeneratingEndorsement = true
                        endorsementStatus = "Querying live Cognitive AI audit..."
                        try {
                            val response = com.example.data.GeminiClient.getCertificateEndorsement(
                                apiKey = com.example.BuildConfig.GEMINI_API_KEY,
                                userName = certNameInput,
                                gridSize = hubGridSize,
                                difficulty = hubDifficulty,
                                durationSeconds = hubDuration,
                                synapticSpeed = hubSynapticSpeed,
                                focusRating = hubFocusRating,
                                globalPercentile = hubGlobalPercentile,
                                localFallbackReport = localFallbackText
                            )
                            aiEndorsementText = response
                            endorsementStatus = "Endorsement verified by Cognitive AI."
                        } catch (e: Exception) {
                            aiEndorsementText = localFallbackText
                            endorsementStatus = "Endorsement offline fallback loaded."
                        } finally {
                            isGeneratingEndorsement = false
                        }
                    }

                    val cvQuote = "ADVANCED COGNITIVE SUDOKU GRADUATE (Awarded by MSB Sudoku Challenge Academy). Global Rank: TOP $rankStr%, Synaptic Speed: ${speedStr}Hz, Cognitive Focus: $focusStr%, Grid Mastered: ${hubGridSize}x${hubGridSize} (${hubDifficulty.uppercase()}). Verification ID: MSB-${(System.currentTimeMillis() % 100000)}."

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFD4AF37),
                                        Color(0xFFE0C068),
                                        Color(0xFFFFD54F)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD4AF37).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🏆", fontSize = 18.sp)
                                }
                                Column {
                                    Text(
                                        text = "COGNITIVE CREDENTIAL & CAREER HUB",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFD4AF37),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Download verified certificates & copy resume/CV credentials",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)))

                            Text(
                                text = "CUSTOMIZE GRADUATE NAME:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            OutlinedTextField(
                                value = certNameInput,
                                onValueChange = { certNameInput = it },
                                placeholder = { Text("e.g. MSB Solver", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD4AF37),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )

                            // Auditor Status Alert Subpanel
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1E25)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E88E5).copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E88E5).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isGeneratingEndorsement) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                color = Color(0xFF4DE8F4),
                                                strokeWidth = 1.5.dp
                                            )
                                        } else {
                                            Text("💡", fontSize = 12.sp)
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "COGNITIVE AI CERTIFICATE AUDITOR",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE0F7FA),
                                            letterSpacing = 0.8.sp
                                        )
                                        Text(
                                            text = endorsementStatus,
                                            fontSize = 11.sp,
                                            color = if (isGeneratingEndorsement) Color(0xFFB0BEC5) else Color(0xFF4DE8F4),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            // PNG Image Export Command Action Block
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val path = com.example.utils.CertificateDownloader.generateAndSaveCertificate(
                                            context = context,
                                            userName = certNameInput,
                                            gridSize = hubGridSize,
                                            difficultyLabel = hubDifficulty,
                                            durationSeconds = hubDuration,
                                            synapticSpeed = hubSynapticSpeed,
                                            focusRating = hubFocusRating,
                                            globalPercentile = hubGlobalPercentile,
                                            aiEndorsement = aiEndorsementText,
                                            countryCode = countryCode
                                        )
                                        if (path != null) {
                                            hubStatusText = "💾 Saved Image (PNG) to Downloads folder:\n$path"
                                        } else {
                                            hubStatusText = "❌ FAILED: Unable to save image. Verify storage permissions."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Text("💾 IMAGE (PNG)", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                                }

                                // PDF Document Compile Command Action Block
                                Button(
                                    onClick = {
                                        val path = com.example.utils.CertificateDownloader.generateAndSavePdfCertificate(
                                            context = context,
                                            userName = certNameInput,
                                            gridSize = hubGridSize,
                                            difficultyLabel = hubDifficulty,
                                            durationSeconds = hubDuration,
                                            synapticSpeed = hubSynapticSpeed,
                                            focusRating = hubFocusRating,
                                            globalPercentile = hubGlobalPercentile,
                                            aiEndorsement = aiEndorsementText,
                                            countryCode = countryCode
                                        )
                                        if (path != null) {
                                            hubStatusText = "📄 Saved PDF Document to Downloads folder:\n$path"
                                        } else {
                                            hubStatusText = "❌ FAILED: Unable to compile PDF. Verify storage permissions."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Text("📄 PDF DIRECT", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                                }
                            }

                            hubStatusText?.let { info ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = info,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { hubStatusText = null },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Done,
                                                contentDescription = "Clear msg",
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💼 PROFESSIONAL RESUME / CV CITATION:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "LIVE RATING",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier
                                        .background(Color(0xFF4CAF50).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = cvQuote,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    try {
                                        clipboardManager.setText(AnnotatedString(cvQuote))
                                        hubStatusText = "📋 COPIED CV CITATION! Copied verified academic Sudoku credentials code seamlessly to your clipboard."
                                    } catch (e: Exception) {}
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("📋 COPY PROFESSIONAL CV CITATION", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }

                            // Social Media post caption generator formatting and picker trigger launcher logic
                            Text(
                                text = "QUICK POST CONVERTER (COPIES & CHOOSE APP):",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val triggerShare: (String) -> Unit = { p ->
                                    val postContent = when (p) {
                                        "LinkedIn" -> "I am proud to share my verified graduation credentials from the MSB SUDOKU CHALLENGE ACADEMY! I completed the ${hubGridSize}x${hubGridSize} Sudoku matrix on ${hubDifficulty.uppercase()} difficulty inside ${timeStr}. Calculated Synaptic Speed: ${speedStr}Hz (Top $rankStr% global performers). Awarded by MSB Creative Studios! [ID: MSB-${(System.currentTimeMillis() % 100000)}]"
                                        "Twitter" -> "Shattered my cognitive records on the MSB SUDOKU CHALLENGE grid! Just graduated with an AI-verified synaptic speed of ${speedStr}Hz! 🧠 Top $rankStr% globally. @MSBCreative #MSBSudoku #CognitiveCert"
                                        "Facebook" -> "Cognitive Graduation fully unlocked! 👑 Solved the ${hubGridSize}x${hubGridSize} Sudoku matrix in ${timeStr} with an AI focus rating of $focusStr%. Honored by MSB Creative Studios! #MSBSudoku"
                                        "Instagram" -> "Cerebral excellence certified! 🧠 Just earned my official cognitive credential from MSB Creative Studios. Calculated Synaptic Speed: ${speedStr}Hz. Top $rankStr% globally! #MSBSudoku #AIEvaluated"
                                        else -> cvQuote
                                    }
                                    try {
                                        clipboardManager.setText(AnnotatedString(postContent))
                                    } catch (e: Exception) {}

                                    try {
                                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_SUBJECT, "MSB Sudoku Challenge Certified Graduation")
                                            putExtra(android.content.Intent.EXTRA_TEXT, postContent)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Achievement via"))
                                    } catch (e: Exception) {}

                                    hubStatusText = "🔗 Caption copied for $p & share dialogue triggered!"
                                }

                                val platforms = listOf("LinkedIn", "Twitter", "Facebook", "Instagram")
                                val platformColors = listOf(Color(0xFF0077B5), Color(0xFF1DA1F2), Color(0xFF1877F2), Color(0xFFE1306C))
                                
                                platforms.forEachIndexed { idx, plat ->
                                    Button(
                                        onClick = { triggerShare(plat) },
                                        colors = ButtonDefaults.buttonColors(containerColor = platformColors[idx]),
                                        contentPadding = PaddingValues(horizontal = 4.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 1.dp)
                                            .height(32.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(plat, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Milestone Badge 1: 10-Game Win Streak Tracker Card Layout
                item {
                    val progress = (consecutiveWins / 10f).coerceIn(0f, 1f)
                    val isEarned = consecutiveWins >= 10
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.5.dp,
                                if (isEarned) Color(0xFFFF9800) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                RoundedCornerShape(16.dp)
                            )
                            .testTag("win_streak_badge"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEarned) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (isEarned) Color(0xFFFF9800).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🔥",
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "10-Game Win Streak",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEarned) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Conquer consecutive puzzles without registering any matrix mistakes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        color = Color(0xFFFF9800),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "$consecutiveWins / 10 W",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isEarned) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Milestone Badge 2: Under 5 mins Speed Demon Tracker Card Layout
                item {
                    val isEarned = isSpeedDemonEarned
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.5.dp,
                                if (isEarned) Color(0xFF00E5FF) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                RoundedCornerShape(16.dp)
                            )
                            .testTag("speed_demon_badge"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEarned) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (isEarned) Color(0xFF00E5FF).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⚡",
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Speed Demon (Under 5 mins)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEarned) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Dissect and solve any standard Sudoku challenge in less than 300 seconds.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (bestTimeSeconds == Long.MAX_VALUE) {
                                        "STATUS: LOCKED | Best: N/A"
                                    } else {
                                        val m = bestTimeSeconds / 60
                                        val s = bestTimeSeconds % 60
                                        if (isEarned) "UNLOCKED! Best time: ${m}m ${s}s (Earned ⚡)" else "STATUS: LOCKED | Best time: ${m}m ${s}s"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEarned) Color(0xFF00B8D4) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Milestone Badge 3: Sudoku Master Badge Card Layout
                item {
                    val isEarned = isSudokuMasterEarned
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.5.dp,
                                if (isEarned) Color(0xFFE91E63) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                RoundedCornerShape(16.dp)
                            )
                            .testTag("sudoku_master_badge"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEarned) Color(0xFFE91E63).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (isEarned) Color(0xFFE91E63).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "👑",
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sudoku Master",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEarned) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Complete an Expert matrix, win 10 matches, or achieve global Level 5.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("Wins: $totalWinsCount/10") },
                                        enabled = false
                                    )
                                    val currentLvl = userProfile?.level ?: 1
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("Level: $currentLvl/5") },
                                        enabled = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * ------------------------------------------------------------------------------
         * 4. SECURE VERIFICATION TRANSACTION OVERLAY PORTAL
         * ------------------------------------------------------------------------------
         * Animated modal layout triggered during processing/claiming transactions.
         * Communicates strict transaction integrity transparently through five consecutive automated phases:
         *
         * PHASES:
         * 1. SECURING CHANNEL (SecuringChannel): Init SSL proxy handshakes.
         * 2. CERTIFICATE HASHING (HashingCertificates): Runs on-device hashing on parameters & order numbers.
         * 3. ANTI-CHEAT INQUEST (VerifyingAntiCheat): Visual audits parsing validation logs.
         * 4. DECRYPTION (GeneratingGiftCode): Displays voucher code creation phases.
         * 5. COMPLETED (ClaimCompleted): Presents final synthetic gift vouchers ready for clipboard copy.
         * 6. SERVER ERROR (Error): Gracefully captures and notifies users about invalid balances or anomalies.
         */
        AnimatedVisibility(
            visible = claimingState != ClaimingProgress.Idle,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SECURE COMPLIANCE TRANSCEIVER",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        when (claimingState) {
                            ClaimingProgress.Idle -> {}
                            ClaimingProgress.SecuringChannel -> {
                                Text(
                                    text = "Initializing SSL Handshake...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Setting up secure TLS tunnel proxy to redeem cluster systems...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            is ClaimingProgress.HashingCertificates -> {
                                Text(
                                    text = "Hashing Certificates...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Order assigned: ${claimingState.orderNo}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Syncing local player data state with MSB secure OAuth nodes securely...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            is ClaimingProgress.VerifyingAntiCheat -> {
                                Text(
                                    text = "Certifying Anti-Cheat Policy...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = claimingState.log,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(12.dp),
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }

                            is ClaimingProgress.GeneratingGiftCode -> {
                                Text(
                                    text = "Decrypting Gift Voucher...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "CODE: * * * * - * * * * - " + claimingState.code.takeLast(4),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                CircularProgressIndicator()
                            }

                            is ClaimingProgress.ClaimCompleted -> {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Success",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "CLAIM COMPLETED!",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val confirmedLogin = userProfile?.userId ?: "the registered email or phone"
                                Text(
                                    text = "Your simulated Cognitive Achievement Voucher code has been synthesized securely. A confirmation message has been dispatched to your login identifier:\n$confirmedLogin",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = claimingState.finalTransaction.giftCardTitle.uppercase(),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = claimingState.finalTransaction.secureCode,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Order: ${claimingState.finalTransaction.orderNumber}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(claimingState.finalTransaction.secureCode))
                                            showCopiedToast = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.weight(1.0f)
                                    ) {
                                        Text("COPY CODE", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { onConfirmReceipt(claimingState.finalTransaction) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50),
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1.0f)
                                    ) {
                                        Text("REDEEM STORE", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            is ClaimingProgress.Error -> {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock Secure Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "VERIFICATION DISRUPTED",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = claimingState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = onCancelClaim,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("DISMISS")
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * ------------------------------------------------------------------------------
         * 5. NATIVE CUSTOM COPY CONFIRMATION COGNITIVE TOAST
         * ------------------------------------------------------------------------------
         * Subtle dynamic transient confirmation pill. Clears itself cleanly using standard delays.
         */
        AnimatedVisibility(
            visible = showCopiedToast,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(50.dp),
                tonalElevation = 8.dp
            ) {
                Text(
                    text = "Voucher code copied to clipboard successfully!",
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }

            LaunchedEffect(showCopiedToast) {
                if (showCopiedToast) {
                    delay(1800)
                    showCopiedToast = false
                }
            }
        }
    }
}

/**
 * ------------------------------------------------------------------------------
 * PREMIUM SHOP ITEM INFRASTRUCTURE CARD
 * ------------------------------------------------------------------------------
 * Individual product visual representation card template designed to handle:
 * - Dynamic color highlight based on points affordability.
 * - Point validation parameters preventing unearned redemption clicks.
 * - TestTag assignment ensuring structured end-to-end user flows.
 */
@Composable
fun GiftCard(
    gift: GiftItemData,
    userPoints: Int,
    onClaimSelected: (String, Int) -> Unit
) {
    val canRedeem = userPoints >= gift.pointsCost

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (canRedeem) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canRedeem) MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (canRedeem) Color(0xFFFFC107).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Value Card Icon",
                    tint = if (canRedeem) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = gift.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = gift.badgeCode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onClaimSelected(gift.title, gift.pointsCost) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canRedeem) Color(0xFFFFB300) else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (canRedeem) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("claim_btn_${gift.pointsCost}")
            ) {
                Text(
                    text = "${String.format("%,d", gift.pointsCost)} PGP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

/**
 * Data Schema representing individual redemption products in the cognitive store.
 */
data class GiftItemData(
    val title: String,
    val pointsCost: Int,
    val badgeCode: String
)
