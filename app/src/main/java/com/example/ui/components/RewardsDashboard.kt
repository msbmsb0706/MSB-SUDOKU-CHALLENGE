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
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var showCopiedToast by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // 0. Google Play Games Rewards & Hub Status Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F5132).copy(alpha = 0.15f) // Deep Google Play Green hue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .border(
                        1.dp,
                        Color(0xFF198754).copy(alpha = 0.5f),
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
                            .background(Color(0xFF198754)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Play Games Controller visual accent
                        Text("🎮", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GOOGLE PLAY GAMES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF198754),
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
                        // Progress bar for Level XP
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

            // 1. Point Balance Card (High-fidelity glass surface)
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
                                    color = Color(0xFFFFC107) // Gold coin look
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
                                    color = Color(0xFF00BCD4) // Cyan gem look
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

                    // Google Play Progress gauge to first reward ($5 Credit = 10,000 points)
                    val targetPoints = 10000
                    val percentProgress = (playGoldPoints.toFloat() / targetPoints).coerceAtMost(1f)
                    Text(
                        text = "Progress toward the next Google Play Gift ($5 Credit)",
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
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
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

            // 2. Google Play Store Gift Lists
            Text(
                text = "AVAILABLE GOOGLE PLAY GIFTS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            val gifts = listOf(
                GiftItemData("$5 Google Play Voucher", 10000, "$5.00 Value Code"),
                GiftItemData("$10 Google Play Voucher", 18000, "$10.00 Value Code"),
                GiftItemData("$25 Google Play Voucher", 40000, "$25.00 Value Code"),
                GiftItemData("$50 Google Play Voucher", 75000, "$50.00 Value Code")
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                gifts.take(2).forEach { gift ->
                    Box(modifier = Modifier.weight(1f)) {
                        GiftCard(gift, playGoldPoints, onClaimSelected)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                gifts.drop(2).forEach { gift ->
                    Box(modifier = Modifier.weight(1f)) {
                        GiftCard(gift, playGoldPoints, onClaimSelected)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Promo Code Card
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
                        modifier = Modifier.fillMaxWidth().height(48.dp),
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

            // 3. Achievements Dashboard Header
            Text(
                text = "ACHIEVEMENT MILESTONES & BADGES",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Calculate state metrics
            val consecutiveWins = remember(gameHistory) {
                var maxStreak = 0
                var currentStreak = 0
                // Sort history by timestamp ascending to calculate streak over time
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
                // Milestone 1: 10-Game Win Streak
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

                // Milestone 2: Speed Demon (Under 5 mins)
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

                // Milestone 3: Sudoku Master
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

        // 4. Secure Verification Transaction Overlay Portal
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
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
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
                                    text = "Syncing local player data state with Google Play OAuth nodes securely...",
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
                                    text = "Your Google Play Gift Card code has been synthesized securely. A confirmation message has been dispatched to your login identifier:\n$confirmedLogin",
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

        // 5. Native custom Copy Confirmation Overlay Toast
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

data class GiftItemData(
    val title: String,
    val pointsCost: Int,
    val badgeCode: String
)
