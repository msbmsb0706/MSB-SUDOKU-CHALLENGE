package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LeaderboardPlayerEntity
import com.example.data.UserProfileEntity
import com.example.data.network.GlobalFastestPlayer
import com.example.ui.MatchResult
import com.example.ui.MatchmakingState

@Composable
fun LeaderboardScreen(
    players: List<LeaderboardPlayerEntity>,
    selectedRegion: String,
    onRegionSelected: (String) -> Unit,
    searchState: MatchmakingState,
    recentMatchResult: MatchResult?,
    onEnterArena: (String, Int) -> Unit,
    onDismissMatch: () -> Unit,
    userProfile: UserProfileEntity?,
    fastestTimes: List<GlobalFastestPlayer> = emptyList(),
    isRefreshingFastest: Boolean = false,
    onRefreshFastest: () -> Unit = {},
    onSendNudge: () -> Unit = {},
    onSolveBoost: (Int) -> Unit = {},
    onPvpCellSelected: (Int, Int) -> Unit = { _, _ -> },
    onPvpNumberEntered: (Int) -> Unit = { _ -> },
    onPvpClearCell: () -> Unit = {},
    onSendSocialNudge: (String) -> Unit = {},
    onPvpWithdraw: () -> Unit = {},
    onPvpToggleEraseMode: () -> Unit = {},
    onSendPvpChatMessage: (String) -> Unit = {},
    disableGridHelpers: Boolean = false,
    hideLastRow: Boolean = false,
    onToggleDisableGridHelpers: () -> Unit = {},
    onToggleHideLastRow: () -> Unit = {},
    onClaimPvpCertificate: (Int, String, Long, Double, Double, Double) -> Unit = { _, _, _, _, _, _ -> },
    lazyListState: LazyListState = androidx.compose.foundation.lazy.rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Points Ladder, 1: Fastest Times (Firestore)
    var activeArenaMode by remember { mutableStateOf("One-to-One") } // "One-to-One" vs "Group Challenge" vs "Tournament Cup"
    var pvpGridSizeOption by remember { mutableIntStateOf(9) } // Default 9x9, options 4x4 or 9x9
    var customChatMessage by remember { mutableStateOf("") }
    var pvpActiveTab by remember { mutableStateOf("board") } // "board" or "chat" or "progress"

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // 1. Competitive Arena Area with Mode Selector Tabs
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MULTIPLAYER CHALLENGE HUB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Enjoy realistic offline simulated tournament and opponent action!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Selection Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("One-to-One", "Group Challenge", "Tournament Cup").forEach { mode ->
                            val isSel = activeArenaMode == mode
                            val bgCol by animateColorAsState(
                                targetValue = if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            val textCol by animateColorAsState(
                                targetValue = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(bgCol)
                                    .clickable { activeArenaMode = mode }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when(mode) {
                                        "One-to-One" -> "⚔️ 1v1 Duel"
                                        "Group Challenge" -> "👥 Group"
                                        "Tournament Cup" -> "🏆 Tourney"
                                        else -> mode
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textCol,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Board Grid Size Option
                    Text(
                        text = "Match Board Grid Size Option:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(9, 4, 3).forEach { size ->
                            val isSel = pvpGridSizeOption == size
                            val borderCol = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            val bgCol = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgCol)
                                    .border(1.5.dp, borderCol, RoundedCornerShape(8.dp))
                                    .clickable { pvpGridSizeOption = size }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (size) {
                                        9 -> "🧩 Classic 9x9"
                                        4 -> "👶 Kids 4x4"
                                        else -> "⚡ Mini 3x3"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic details card based on selected mode
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            val description = when(activeArenaMode) {
                                "One-to-One" -> "Solve standard head-to-head Sudoku matrices against a simulated international rival. Stay sharp!"
                                "Group Challenge" -> "Engage 4 online bots simultaneously in a rapid 5-player speed solver race. Send speed nudges!"
                                "Tournament Cup" -> "Engage in the prestigious Grand Academy Cup Championship. 3 rounds of hard Sudoku elimination."
                                else -> ""
                            }
                            val rewards = when(activeArenaMode) {
                                "One-to-One" -> "Rewards: +300 PlayGold, +50 Rating Points (RP), +3 Gems\nBuy-in Cost: 5 Gems"
                                "Group Challenge" -> "Rewards: +500 PlayGold, +80 Rating Points (RP), +6 Gems\nBuy-in Cost: 8 Gems"
                                "Tournament Cup" -> "Rewards: +800 PlayGold, +120 Rating Points (RP), +12 Gems, Endorsement\nBuy-in Cost: 12 Gems"
                                else -> ""
                            }

                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = rewards,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val buyInLabel = when(activeArenaMode) {
                        "One-to-One" -> "5 GEMS BUY-IN: SOLVE LIVE"
                        "Group Challenge" -> "8 GEMS BUY-IN: LAUNCH RACE"
                        "Tournament Cup" -> "12 GEMS BUY-IN: PLAY CUP"
                        else -> "JOIN ARENA"
                    }

                    Button(
                        onClick = { onEnterArena(activeArenaMode, pvpGridSizeOption) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("enter_arena_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Arena Match Icon",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = buyInLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Dynamic Sub-Tab Selector Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("🏆 RATING POINTS", "⚡ FASTEST TIMES").forEachIndexed { index, title ->
                    val isSelected = selectedSubTab == index
                    val tabBgColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                    val tabTextColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tabBgColor)
                            .clickable { selectedSubTab = index }
                            .padding(vertical = 10.dp)
                            .testTag("leaderboard_sub_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = tabTextColor
                        )
                    }
                }
            }

            if (selectedSubTab == 0) {
                // 2. Regional Filters Row
                val regions = listOf("Global", "Americas", "Europe", "Asia-Pacific", "Africa")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                regions.forEach { region ->
                    val isSel = selectedRegion == region
                    val chipColor by animateColorAsState(
                        targetValue = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50.dp))
                            .background(chipColor)
                            .clickable { onRegionSelected(region) }
                            .padding(vertical = 8.dp)
                            .testTag("region_chip_$region"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when(region) {
                                "Americas" -> "🌎 Am"
                                "Europe" -> "🇪🇺 Eu"
                                "Asia-Pacific" -> "🇯🇵 Asia"
                                "Africa" -> "🇳🇬 Afr"
                                else -> "🌐 Global"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }

            // 3. User Current Standing Header
            if (userProfile != null) {
                val rating = 2000 + (userProfile.xp / 10)
                
                var myPhotoBitmap by remember(userProfile.profilePhotoPath) {
                    mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null)
                }

                LaunchedEffect(userProfile.profilePhotoPath) {
                    if (!userProfile.profilePhotoPath.isNullOrBlank()) {
                        try {
                            val file = java.io.File(userProfile.profilePhotoPath)
                            if (file.exists()) {
                                val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                                if (bitmap != null) {
                                    myPhotoBitmap = bitmap.asImageBitmap()
                                }
                            } else {
                                myPhotoBitmap = null
                            }
                        } catch (e: Exception) {
                            myPhotoBitmap = null
                        }
                    } else {
                        myPhotoBitmap = null
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (myPhotoBitmap != null) {
                                Image(
                                    bitmap = myPhotoBitmap!!,
                                    contentDescription = "User avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User avatar",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${userProfile.username} (YOU)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Country: ${userProfile.countryFlag} ${userProfile.countryName} (Region: ${userProfile.region})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$rating RP",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Level ${userProfile.level}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 4. Rankings List
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                itemsIndexed(players) { idx, player ->
                    val isUser = player.isCurrentUser
                    val playerBg = if (isUser) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = playerBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank number
                            Text(
                                text = player.rank.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = when (player.rank) {
                                    1 -> Color(0xFFFFD700) // Gold
                                    2 -> Color(0xFFC0C0C0) // Silver
                                    3 -> Color(0xFFCD7F32) // Bronze
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.width(28.dp),
                                textAlign = TextAlign.Center
                            )

                            // Avatar Circle
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(if (player.avatarColorSeed != 0) player.avatarColorSeed.toLong() else 0xFF607D8BL)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = player.username.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))

                            // Name & region
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = player.username,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.SemiBold
                                    )
                                    if (isUser) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.padding(2.dp)
                                        ) {
                                            Text(
                                                text = "YOU",
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (userProfile != null) {
                                            if (!userProfile.linkedInUrl.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Surface(
                                                    color = Color(0xFF0077B5),
                                                    shape = RoundedCornerShape(4.dp),
                                                    modifier = Modifier.padding(2.dp)
                                                ) {
                                                    Text(
                                                        text = "In",
                                                        color = Color.White,
                                                        fontSize = 7.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            if (!userProfile.facebookUrl.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Surface(
                                                    color = Color(0xFF3B5998),
                                                    shape = RoundedCornerShape(4.dp),
                                                    modifier = Modifier.padding(2.dp)
                                                ) {
                                                    Text(
                                                        text = "FB",
                                                        color = Color.White,
                                                        fontSize = 7.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            if (!userProfile.instagramUrl.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Surface(
                                                    color = Color(0xFFE1306C),
                                                    shape = RoundedCornerShape(4.dp),
                                                    modifier = Modifier.padding(2.dp)
                                                ) {
                                                    Text(
                                                        text = "IG",
                                                        color = Color.White,
                                                        fontSize = 7.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = if (isUser && userProfile != null) {
                                        "${userProfile.countryFlag} ${userProfile.countryName} (${userProfile.region})"
                                    } else {
                                        when (player.region) {
                                            "Americas" -> "🌎 Americas"
                                            "Europe" -> "🇪🇺 Europe"
                                            "Asia-Pacific" -> "🇯🇵 Asia-Pacific"
                                            "Africa" -> "🇳🇬 Africa"
                                            else -> "🌐 Global"
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Solve Speed Icon",
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    val (easyTime, medTime) = when (player.username) {
                                        "Yuki_Tokyo" -> "1m 58s" to "3m 04s"
                                        "Sven_Berlin" -> "2m 14s" to "3m 22s"
                                        "Alex_NYC" -> "2m 32s" to "3m 48s"
                                        "Amara_Lagos" -> "2m 45s" to "4m 10s"
                                        "Chloe_Paris" -> "2m 58s" to "4m 25s"
                                        "Mateo_Rio" -> "3m 10s" to "4m 50s"
                                        "Priya_Mumbai" -> "3m 22s" to "5m 05s"
                                        "Fatima_Cairo" -> "3m 40s" to "5m 28s"
                                        "Li_Shanghai" -> "3m 52s" to "5m 45s"
                                        "Hans_Vienna" -> "4m 05s" to "6m 12s"
                                        else -> {
                                            val minutesEasy = 2L + ((userProfile?.level ?: 1) % 3)
                                            val secondsEasy = (30 + ((userProfile?.xp ?: 0) % 30)) % 60
                                            val minutesMed = 4L + ((userProfile?.level ?: 1) % 4)
                                            val secondsMed = (15 + ((userProfile?.xp ?: 0) % 45)) % 60
                                            String.format("%01dm %02ds", minutesEasy, secondsEasy) to String.format("%01dm %02ds", minutesMed, secondsMed)
                                        }
                                    }
                                    Text(
                                        text = "Best: Easy $easyTime | Med $medTime",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Rating points
                            Text(
                                text = "${player.points} RP",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        } else {
                // --- Brand-new Global Real-time Firestore Fastest Solver Times Tab ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚡ REAL-TIME SPEED LEADERBOARD",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Global Top 10 fastest verified puzzle solves",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Refresh Button
                    IconButton(
                        onClick = onRefreshFastest,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                shape = CircleShape
                            )
                            .testTag("refresh_fastest_times_btn")
                    ) {
                        if (isRefreshingFastest) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "🔄",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (fastestTimes.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No records found on Firestore yet. Solve a puzzle to log the first record!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        itemsIndexed(fastestTimes) { idx, player ->
                            val isUser = userProfile != null && player.username == userProfile.username
                            val isTop3 = idx < 3
                            val cardBg = if (isUser) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }

                            val borderBrush = if (isUser) {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            } else if (isTop3) {
                                val goldColor = Color(0xFFFFD700)
                                Brush.horizontalGradient(
                                    colors = listOf(goldColor.copy(alpha = 0.7f), goldColor.copy(alpha = 0.15f))
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                        Color.Transparent
                                    )
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = if (isUser || isTop3) 2.dp else 1.dp,
                                        brush = borderBrush,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .testTag("fastest_rank_$idx"),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Rank Medal or Number
                                    Box(
                                        modifier = Modifier.width(36.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = when (idx + 1) {
                                                1 -> "🥇"
                                                2 -> "🥈"
                                                3 -> "🥉"
                                                else -> "  #${idx + 1}"
                                            },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Country flag emoji and User details
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = player.countryFlag,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = player.username,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )

                                            // "YOU" tag
                                            if (isUser) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "YOU",
                                                        color = MaterialTheme.colorScheme.onPrimary,
                                                        fontSize = 7.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Text(
                                                text = "${player.countryName} (${player.region})",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            // Solve timestamp date representation
                                            Text(
                                                text = "• ${formatRelativeDate(player.timestamp)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        }
                                    }

                                    // Time elapsed & difficulty tag column
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = formatTime(player.timeElapsedSeconds),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        // Difficulty Label colored chip
                                        val diffUpper = player.difficulty.uppercase()
                                        Surface(
                                            color = when {
                                                diffUpper.contains("EXPERT") -> Color(0xFFE91E63).copy(alpha = 0.15f)
                                                diffUpper.contains("ARENA") -> Color(0xFF00E676).copy(alpha = 0.15f)
                                                diffUpper.contains("HARD") -> Color(0xFF29B6F6).copy(alpha = 0.15f)
                                                diffUpper.contains("MEDIUM") -> Color(0xFFFFB74D).copy(alpha = 0.15f)
                                                else -> Color(0xFF66BB6A).copy(alpha = 0.15f)
                                            },
                                            contentColor = when {
                                                diffUpper.contains("EXPERT") -> Color(0xFFFF4081)
                                                diffUpper.contains("ARENA") -> Color(0xFF00C853)
                                                diffUpper.contains("HARD") -> Color(0xFF0288D1)
                                                diffUpper.contains("MEDIUM") -> Color(0xFFF57C00)
                                                else -> Color(0xFF388E3C)
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = player.difficulty,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

        // 5. Fullscreen Matchmaking Overlay
        AnimatedVisibility(
            visible = searchState != MatchmakingState.Idle,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val isConflict = searchState is MatchmakingState.SolvingConflict
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(if (isConflict) 0.dp else 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = if (isConflict) Modifier.fillMaxSize() else Modifier.fillMaxWidth().wrapContentHeight(),
                    shape = RoundedCornerShape(if (isConflict) 0.dp else 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isConflict) 12.dp else 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (searchState) {
                            MatchmakingState.Idle -> {}
                            MatchmakingState.Searching -> {
                                Text(
                                    text = "CONNECTING SIMULATOR",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Searching matchmaking lobbies details...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Contacting master nodes on MSB Creative Studios proxy hub...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            is MatchmakingState.FoundOpponent -> {
                                Text(
                                    text = "MATCH SECURED!",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF4CAF50),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Opponent: ${searchState.opponentFlag} ${searchState.opponentName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Country: ${searchState.opponentCountry} (Region: ${searchState.opponentRegion}) | Latency: ${searchState.latencyMs}ms",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Injecting unique Sudoku conflict puzzles...",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }

                            is MatchmakingState.SolvingConflict -> {
                                // Full screen Header with Withdraw button
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "SUDOKU MULTIPLAYER RIVALRY",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Speed Solving Board Active (${searchState.pvpGridSize}x${searchState.pvpGridSize})",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Button(
                                        onClick = onPvpWithdraw,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("WITHDRAW 🏳️", fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))

                                // Ticking clock showing seconds remaining under different speeds
                                Surface(
                                    color = if (searchState.secondsLeft < 30) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Active Clock",
                                            tint = if (searchState.secondsLeft < 30) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "TIME SECONDS LEFT: ${searchState.secondsLeft}s",
                                            fontWeight = FontWeight.Black,
                                            color = if (searchState.secondsLeft < 30) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Composed Tab Selector row so we have separated, full screen withdraw, erase, chat option without disruptive overlaps
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { pvpActiveTab = "board" },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (pvpActiveTab == "board") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (pvpActiveTab == "board") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.0f).height(34.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("🧩 Board", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { pvpActiveTab = "chat" },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (pvpActiveTab == "chat") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (pvpActiveTab == "chat") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.3f).height(34.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        val chatCount = searchState.liveChatLog.size
                                        Text("💬 Chat & Nudges (${chatCount})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { pvpActiveTab = "progress" },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (pvpActiveTab == "progress") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (pvpActiveTab == "progress") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.2f).height(34.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("🏆 Rivals (${searchState.progressSelf}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Tab Content
                                when (pvpActiveTab) {
                                    "board" -> {
                                        // Dynamic interactive Sudoku board
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            SudokuGrid(
                                                grid = searchState.pvpGrid,
                                                selectedCell = searchState.pvpSelectedCell,
                                                onCellSelected = { r, c -> onPvpCellSelected(r, c) },
                                                isPaused = false,
                                                disableGridHelpers = disableGridHelpers,
                                                hideLastRow = hideLastRow,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }

                                if (pvpActiveTab == "board") {
                                    // Eraser Tool Selection + Keyboard
                                    Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. Interactive Eraser mode Button
                                    Button(
                                        onClick = onPvpToggleEraseMode,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (searchState.eraseModeActive) Color(0xFFC2185B) else MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = if (searchState.eraseModeActive) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.3f).height(38.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eraser Toggle",
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (searchState.eraseModeActive) "ERASING" else "ERASE MODE",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // 2. Standard Digits 1 to size + CLEAR Button
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.weight(3f)
                                    ) {
                                        val size = searchState.pvpGridSize
                                        for (num in 1..size) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)

                                                    .aspectRatio(1f)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                                    .clickable { onPvpNumberEntered(num) },
                                                 contentAlignment = Alignment.Center
  
                                            ) {
                                                Text(
                                                    text = num.toString(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                        // Specific Clear Button
                                        Box(
                                            modifier = Modifier
                                                .weight(1.3f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(MaterialTheme.colorScheme.errorContainer)
                                                .clickable { onPvpClearCell() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "CLEAR",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 7.5.sp,
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                if (pvpActiveTab == "chat") {
                                // Real-time chat logs
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = "LIVE RIVALRY ACTIVITY LOG:",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(modifier = Modifier.weight(1f)) {
                                        androidx.compose.foundation.lazy.LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            itemsIndexed(searchState.liveChatLog) { index, chat ->
                                                Text(
                                                    text = chat,
                                                    fontSize = 8.5.sp,
                                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                    color = if (chat.contains("System:")) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurface,
                                                    lineHeight = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Custom text messages entry chat box
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = customChatMessage,
                                        onValueChange = { customChatMessage = it },
                                        placeholder = { Text("Send quick lobby rivalry chat text...", fontSize = 9.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).height(46.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                                        )
                                    )
                                    IconButton(
                                        onClick = {
                                            if (customChatMessage.isNotBlank()) {
                                                onSendPvpChatMessage(customChatMessage)
                                                customChatMessage = ""
                                            }
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = "Send Chat Message",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (pvpActiveTab == "progress") {
                                // Progress bars comparison
                                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("You (${userProfile?.countryFlag ?: "🇺🇸"} ${userProfile?.username ?: "MSB"})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            Text("${searchState.progressSelf}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                        LinearProgressIndicator(
                                            progress = { searchState.progressSelf / 100f },
                                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                            color = Color(0xFFD4AF37) // Premium gold for user
                                        )
                                    }

                                    if (searchState.opponentProgresses.isEmpty()) {
                                        Column {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Opponent", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                Text("${searchState.progressOpponent}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                            LinearProgressIndicator(
                                                progress = { searchState.progressOpponent / 100f },
                                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                color = Color(0xFFE91E63)
                                            )
                                        }
                                    } else {
                                        searchState.opponentProgresses.forEach { (name, progress) ->
                                            val flag = when {
                                                name.contains("Tokyo") || name.contains("Yuki") -> "🇯🇵"
                                                name.contains("Prague") || name.contains("Max") -> "🇨🇿"
                                                name.contains("Athens") || name.contains("Sophia") -> "🇬🇷"
                                                name.contains("Berlin") || name.contains("Sven") -> "🇩🇪"
                                                name.contains("Accra") || name.contains("Adebayo") -> "🇬🇭"
                                                name.contains("Paris") || name.contains("Chloe") -> "🇫🇷"
                                                name.contains("Sydney") || name.contains("Emma") -> "🇦🇺"
                                                else -> "🌐"
                                            }
                                            Column {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("$flag $name", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                                    Text("$progress%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                LinearProgressIndicator(
                                                    progress = { progress / 100f },
                                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                    color = when {
                                                        name.contains("Tokyo") -> Color(0xFF2196F3)
                                                        name.contains("Berlin") -> Color(0xFFFF9800)
                                                        name.contains("Athens") -> Color(0xFF9C27B0)
                                                        else -> Color(0xFFE91E63)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                if (pvpActiveTab == "chat") {
                                // Realtime Nudge activity feedback banner
                                if (searchState.lastNudgeMessage.isNotEmpty()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Nudge Feed",
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = searchState.lastNudgeMessage,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Interactive Nudges row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // 1. Standard PvP Nudge
                                    Button(
                                        onClick = onSendNudge,
                                        enabled = searchState.nudgeCountLeft > 0,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = MaterialTheme.colorScheme.onSecondary
                                        ),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .height(38.dp)
                                            .testTag("pvp_nudge_action_btn"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Nudge Standard",
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "NUDGE (${searchState.nudgeCountLeft}/3)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }

                                    // 2. LinkedIn Nudge
                                    Button(
                                        onClick = { onSendSocialNudge("LinkedIn") },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF0077B5),
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "LINKEDIN",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }

                                    // 3. Instagram Nudge
                                    Button(
                                        onClick = { onSendSocialNudge("Instagram") },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE1306C),
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "INSTAGRAM",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                }
                            }

                            is MatchmakingState.MatchFinished -> {
                                recentMatchResult?.let { result ->
                                    val titleText = if (result.isWon) "VICTORY!" else "DEFEAT"
                                    val titleColor = if (result.isWon) Color(0xFF4CAF50) else Color(0xFFE91E63)

                                    Text(
                                        text = titleText,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = titleColor,
                                        letterSpacing = 1.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Solve Time & Analysis Comparison:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Your Time: ${result.solveTimeSelf}s | Mistakes: ${result.mistakes}\nOpponent (${result.opponentName}): ${result.solveTimeOpponent}s",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "rewards claimed:",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "PlayGold awarded: +${result.playGoldAwarded} PGP",
                                                color = Color(0xFFFFC107),
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "Competitive Rating: ${if (result.pointsDelta >= 0) "+" else ""}${result.pointsDelta} RP",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Bonus gems: +${result.gemsAwarded}",
                                                color = Color(0xFF00BCD4),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    // Claim Certificate button
                                    if (result.isWon) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = {
                                                val size = result.size
                                                val diffLabel = "PVP " + when(result.size) {
                                                    3 -> "Mini 3x3 Speed"
                                                    4 -> "Children 4x4 Quick"
                                                    else -> "Standard 9x9 Classic"
                                                }
                                                val durationSecs = result.solveTimeSelf.toLong()
                                                val synapticSpeed = (size * size * 100f) / maxOf(5, result.solveTimeSelf)
                                                val focusRating = (100.0 - result.mistakes * 15.0).coerceIn(52.0, 100.0)
                                                val globalPercentile = (result.solveTimeSelf * 0.05).coerceAtLeast(0.005)
                                                onClaimPvpCertificate(
                                                    size,
                                                    diffLabel,
                                                    durationSecs,
                                                    synapticSpeed.toDouble(),
                                                    focusRating,
                                                    globalPercentile
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                                            modifier = Modifier.fillMaxWidth().height(48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Certificate Unlocked",
                                                tint = Color.Black,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("🏆 CLAIM PVP CERTIFICATE & SHARE", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = onDismissMatch,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("RETURN TO LOBBY", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            is MatchmakingState.Error -> {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Error Locking",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "CANNOT JOIN MATCHMAKING",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (searchState as MatchmakingState.Error).message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = onDismissMatch,
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
    }
}

private fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}

private fun formatRelativeDate(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60000L -> "just now"
        diff < 3600000L -> "${diff / 60000L}m ago"
        diff < 86400000L -> "${diff / 3600000L}h ago"
        else -> java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
    }
}

