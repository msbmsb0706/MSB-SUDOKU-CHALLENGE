package com.example

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.SudokuDatabase
import com.example.data.SudokuRepository
import com.example.data.UserProfileEntity
import com.example.sudoku.SudokuDifficulty
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {

    override fun getAttributionTag(): String? {
        return "default"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val database = remember { SudokuDatabase.getDatabase(context) }
            val repository = remember { SudokuRepository(database.sudokuDao()) }
            
            val application = context.applicationContext as Application
            val viewModel: SudokuViewModel = viewModel(
                factory = SudokuViewModelFactory(application, repository)
            )

            val currentTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(selectedTheme = currentTheme) {
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2600)
                    showSplash = false
                }

                if (showSplash) {
                    MSBSplashScreen()
                } else {
                    MainScaffold(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MSBSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0E14)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "POWERED BY",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "MSB",
                fontSize = 82.sp,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.displayLarge.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00E5FF),
                            Color(0xFF0088FF),
                            Color(0xFFE040FB),
                            Color(0xFFFF1744)
                        )
                    )
                ),
                letterSpacing = (-2).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "CREATIVE STUDIOS",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 7.sp
            )
            
            Spacer(modifier = Modifier.height(36.dp))
            
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFF00E5FF),
                trackColor = Color.White.copy(alpha = 0.12f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "LOADING HIGH FIDELITY PUZZLE ENGINE...",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.35f),
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun MainScaffold(viewModel: SudokuViewModel) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val levelUpValue by viewModel.levelUpEvent.collectAsStateWithLifecycle()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsStateWithLifecycle()
    val showGuestLimitResult by viewModel.showGuestLimitReachedDialog.collectAsStateWithLifecycle()

    val haptic = LocalHapticFeedback.current
    val triggerMistakeVibration by viewModel.triggerMistakeVibration.collectAsStateWithLifecycle()

    LaunchedEffect(triggerMistakeVibration) {
        if (triggerMistakeVibration > 0L) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            kotlinx.coroutines.delay(120)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    AuthStateContainer(viewModel = viewModel) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            topBar = {
                TopAppBarCompact(
                    userProfile = userProfile,
                    onAddPointsDebug = { viewModel.grantDebugPoints20k() },
                    isSoundEnabled = isSoundEnabled,
                    onToggleSound = { viewModel.toggleSoundEnabled() }
                )
            },
            bottomBar = {
                val isGuest = userProfile?.userId?.startsWith("guest_player_") == true
                BottomTabBar(
                    currentTab = activeTab,
                    onTabSelected = { tabIndex ->
                        if (isGuest && tabIndex != 0) {
                            viewModel.showGuestLimitReachedDialog.value = true
                        } else {
                            viewModel.activeTab.value = tabIndex
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    0 -> PlayScreenTab(viewModel = viewModel)
                    1 -> ArenaScreenTab(viewModel = viewModel)
                    2 -> RewardsScreenTab(viewModel = viewModel)
                    3 -> ProfileScreenTab(viewModel = viewModel)
                }
            }
        }

        // Celebratory Level Up Material 3 dialog
        levelUpValue?.let { lvl ->
            AlertDialog(
                onDismissRequest = { viewModel.levelUpEvent.value = null },
                confirmButton = {
                    Button(
                        onClick = { viewModel.levelUpEvent.value = null },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("AWESOME", fontWeight = FontWeight.Bold)
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Gold Star",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "NEW STUDIO LEVEL UP!",
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "Outstanding speed tactics! You have leveled up to Studio Level $lvl.\n\nSolve harder puzzles or dominate active arena lobbies to multiply your PlayGold Points and claim gift certificates!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Guest session restrict warning dialog
        if (showGuestLimitResult) {
            AlertDialog(
                onDismissRequest = { viewModel.showGuestLimitReachedDialog.value = false },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.showGuestLimitReachedDialog.value = false
                            viewModel.logOutCurrentSession()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().testTag("guest_limit_register_btn")
                    ) {
                        Text("CREATE VERIFIED ACCOUNT / LOGIN", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.showGuestLimitReachedDialog.value = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "GUEST TRIAL LIMIT REACHED",
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "To guarantee global leaderboard validation integrity and build reliable player trust, guest sessions are strictly restricted to exactly 1 trial game.\n\nPlease register or log in with a secure, verified account to unlock unlimited high-fidelity puzzle lobbies, track career mental speed reports, and redeem premium Google Play reward certificates!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun TopAppBarCompact(
    userProfile: UserProfileEntity?,
    onAddPointsDebug: () -> Unit,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit
) {
    Surface(
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "MSB SUDOKU CHALLENGE",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "POWERED BY MSB CREATIVE STUDIOS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Interactive Sound Toggle Indicator
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .clickable { onToggleSound() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isSoundEnabled) "🔊" else "🔇",
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (isSoundEnabled) "SOUND ON" else "MUTED",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSoundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                // Coin wallet and points click to add +20k debug info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onAddPointsDebug() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("wallet_balance_header")
                ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Coin Balance Icon",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = String.format("%,d PGP", userProfile?.playGoldPoints ?: 3200),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
}

@Composable
fun BottomTabBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTabItem(
                label = "Sudoku",
                icon = Icons.Default.PlayArrow,
                isSelected = currentTab == 0,
                onClick = { onTabSelected(0) },
                testTag = "nav_tab_play"
            )
            BottomTabItem(
                label = "Live Arena",
                icon = Icons.Default.Star,
                isSelected = currentTab == 1,
                onClick = { onTabSelected(1) },
                testTag = "nav_tab_arena"
            )
            BottomTabItem(
                label = "Rewards",
                icon = Icons.Default.ShoppingCart,
                isSelected = currentTab == 2,
                onClick = { onTabSelected(2) },
                testTag = "nav_tab_rewards"
            )
            BottomTabItem(
                label = "Studio Settings",
                icon = Icons.Default.Settings,
                isSelected = currentTab == 3,
                onClick = { onTabSelected(3) },
                testTag = "nav_tab_profile"
            )
        }
    }
}

@Composable
fun RowScope.BottomTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (isSelected) activeColor else inactiveColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}


// --- 1. Sudoku Play Tab ---

enum class PlayTabScreen {
    MainMenu,
    GameBoard,
    TermsAndPolicy
}

@Composable
fun PlayScreenTab(viewModel: SudokuViewModel) {
    val grid by viewModel.grid.collectAsStateWithLifecycle()
    val selectedCell by viewModel.selectedCell.collectAsStateWithLifecycle()
    val isPencilMode by viewModel.isPencilMode.collectAsStateWithLifecycle()
    val mistakeCount by viewModel.mistakeCount.collectAsStateWithLifecycle()
    val isGameOver by viewModel.isGameOver.collectAsStateWithLifecycle()
    val isGameWon by viewModel.isGameWon.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val secondsElapsed by viewModel.secondsElapsed.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()
    val hasActiveDraft by viewModel.hasActiveDraft.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val recordAlert by viewModel.testRecordAlert.collectAsStateWithLifecycle()
    val aiCognitiveFocus by viewModel.aiCognitiveFocus.collectAsStateWithLifecycle()
    val aiCognitiveAccuracy by viewModel.aiCognitiveAccuracy.collectAsStateWithLifecycle()
    val aiCognitivePatternIndex by viewModel.aiCognitivePatternIndex.collectAsStateWithLifecycle()
    val aiSynapticSpeedHertz by viewModel.aiSynapticSpeedHertz.collectAsStateWithLifecycle()
    val aiGlobalPercentile by viewModel.aiGlobalPercentile.collectAsStateWithLifecycle()
    val aiCognitiveComment by viewModel.aiCognitiveComment.collectAsStateWithLifecycle()

    val gridSize by viewModel.gridSize.collectAsStateWithLifecycle()
    val gameMode by viewModel.gameMode.collectAsStateWithLifecycle()
    val isTeamTournamentActive by viewModel.isTeamTournamentActive.collectAsStateWithLifecycle()
    val teamTournamentPlayers by viewModel.teamTournamentPlayers.collectAsStateWithLifecycle()
    val userHasFinishedTournament by viewModel.userHasFinishedTournament.collectAsStateWithLifecycle()
    val disableGridHelpers by viewModel.disableGridHelperLayers.collectAsStateWithLifecycle()
    val hideLastRow by viewModel.hideLastRowNumbers.collectAsStateWithLifecycle()
    val currentTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()

    var showCertificateDialog by remember { mutableStateOf(false) }
    var certificateNameInput by remember { mutableStateOf("") }
    var showWithdrawConfirmation by remember { mutableStateOf(false) }

    if (showWithdrawConfirmation) {
        AlertDialog(
            onDismissRequest = { showWithdrawConfirmation = false },
            confirmButton = {
                Button(
                    onClick = {
                        showWithdrawConfirmation = false
                        viewModel.forfeitAndExitGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("YES, WITHDRAW", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawConfirmation = false }) {
                    Text("CONTINUE PLAYING", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning icon",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text("WITHDRAW PUZZLE MATCH?", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            },
            text = {
                Text(
                    "Are you sure you want to withdraw from this puzzle? This forfeit will clear active progress on the grid. PlayGold Points will only accrue on verified completions.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        )
    }

    LaunchedEffect(userProfile) {
        if (certificateNameInput.isEmpty() && userProfile != null) {
            certificateNameInput = userProfile?.username ?: "MSB GRANDMASTER"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val showTermsAndPolicy by viewModel.showTermsAndPolicy.collectAsStateWithLifecycle()
        val playSubScreen = when {
            showTermsAndPolicy -> PlayTabScreen.TermsAndPolicy
            grid.isEmpty() -> PlayTabScreen.MainMenu
            else -> PlayTabScreen.GameBoard
        }

        AnimatedContent(
            targetState = playSubScreen,
            transitionSpec = {
                if (initialState == PlayTabScreen.MainMenu && targetState == PlayTabScreen.GameBoard) {
                    (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                } else if (initialState == PlayTabScreen.GameBoard && targetState == PlayTabScreen.MainMenu) {
                    (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                } else if (targetState == PlayTabScreen.TermsAndPolicy) {
                    (slideInVertically { it } + fadeIn()).togetherWith(slideOutVertically { -it } + fadeOut())
                } else if (initialState == PlayTabScreen.TermsAndPolicy) {
                    (slideInVertically { -it } + fadeIn()).togetherWith(slideOutVertically { it } + fadeOut())
                } else {
                    fadeIn() togetherWith fadeOut()
                }
            },
            label = "play_subscreen_nav"
        ) { screenState ->
            when (screenState) {
                PlayTabScreen.MainMenu -> {
                    // New Game Setup / Selector page
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "MSB SUDOKU CHALLENGE",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "POWERED BY MSB CREATIVE STUDIOS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Theme Toggler card at the top for accessibility and eye comfort as requested!
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "👁️ ACCESS & EYE COMFORT THEME",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = currentTheme.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                Triple("Creative Light", "☀️ LIGHT", Color(0xFF3F51B5)),
                                Triple("Emerald Eye-Shield", "🌲 EYE-SAFE", Color(0xFF00FF87)),
                                Triple("Matrix Cyberpunk", "🌙 DARK", Color(0xFFFFC107)),
                                Triple("High Contrast Paper", "◑ STARK", Color(0xFF0056C6))
                            ).forEach { (themeName, label, colorIndicator) ->
                                val isSelected = currentTheme == themeName
                                Button(
                                    onClick = { viewModel.changeTheme(themeName) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(colorIndicator)
                                        )
                                        Text(label, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Section A: Grid Matrix Dimension Selection (4x4 vs 9x9)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "GRID MATRIX DIMENSION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(3, 4, 9).forEach { size ->
                                val label = when (size) {
                                    3 -> "3x3 Mini Grid"
                                    4 -> "4x4 Quick"
                                    else -> "9x9 Classic"
                                }
                                val isSelected = gridSize == size
                                Button(
                                    onClick = { viewModel.gridSize.value = size },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // Real-Time Fastest Record Times Card (Personal Bests)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("👑", fontSize = 16.sp)
                            Text(
                                text = "REAL-TIME FASTEST RECORD TIMES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD4AF37),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "LIVE UPDATED",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        val easyPb = userProfile?.bestTimeEasy ?: 0L
                        val medPb = userProfile?.bestTimeMedium ?: 0L
                        val hardPb = userProfile?.bestTimeHard ?: 0L
                        val expertPb = userProfile?.bestTimeExpert ?: 0L

                        val formatPb: (Long) -> String = { sec ->
                            if (sec <= 0L) "--:--" else {
                                val m = sec / 60
                                val s = sec % 60
                                String.format(java.util.Locale.getDefault(), "%02d:%02d", m, s)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(
                                Pair("EASY", easyPb),
                                Pair("MEDIUM", medPb),
                                Pair("HARD", hardPb),
                                Pair("EXPERT", expertPb)
                            ).forEach { (diff, score) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = diff,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = formatPb(score),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (score > 0L) Color(0xFFD4AF37) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Section B: Challenge Play Mode Configurator
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "CHALLENGE PLAY MODE RULE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Triple("Practice", "Unlimited", Icons.Default.Info),
                                Triple("Survival", "3-Strikes", Icons.Default.Warning),
                                Triple("Countdown", "Time Limit", Icons.Default.Star),
                                Triple("Tournament", "Team Lobby", Icons.Default.Person)
                            ).forEach { (mode, badge, icon) ->
                                val active = (gameMode == mode && !isTeamTournamentActive && mode != "Tournament") || (mode == "Tournament" && isTeamTournamentActive)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .clickable {
                                            viewModel.gameMode.value = mode
                                            if (mode == "Tournament") {
                                                viewModel.isTeamTournamentActive.value = true
                                            } else {
                                                viewModel.isTeamTournamentActive.value = false
                                            }
                                        }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = icon, contentDescription = mode, modifier = Modifier.size(18.dp), tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(mode, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = if (active) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(badge, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section C: Difficulty Launcher Trigger
                Text(
                    text = "SELECT DIFFICULTY LEVEL TO LAUNCH",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                SudokuDifficulty.values().forEach { d ->
                    Button(
                        onClick = {
                            if (isTeamTournamentActive) {
                                viewModel.startTeamTournament(gridSize)
                            } else {
                                viewModel.startNewGame(d, gridSize)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when(d) {
                                SudokuDifficulty.EASY -> Color(0xFF4CAF50)
                                SudokuDifficulty.MEDIUM -> MaterialTheme.colorScheme.primary
                                SudokuDifficulty.HARD -> Color(0xFFFF9800)
                                SudokuDifficulty.EXPERT -> Color(0xFFE91E63)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(vertical = 3.dp)
                            .testTag("difficulty_button_${d.label.lowercase()}")
                    ) {
                        val playLabel = if (isTeamTournamentActive) "TEAM SURVIVAL" else "START PUZZLE"
                        Text(
                            text = "${d.label} - $playLabel (+${when(d){
                                SudokuDifficulty.EASY -> "150"
                                SudokuDifficulty.MEDIUM -> "300"
                                SudokuDifficulty.HARD -> "500"
                                SudokuDifficulty.EXPERT -> "850"
                            }} PlayGold)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }

                if (hasActiveDraft) {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = { viewModel.resumeSavedGame() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("resume_draft_btn")
                    ) {
                        Text(
                            text = "RESUME SAVED EXPERIENCES",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Prominent Outside Visible COGNITIVE CERTIFICATE PORTAL
                val isGuest = userProfile?.userId?.startsWith("guest_player_") == true
                Button(
                    onClick = {
                        if (isGuest) {
                            viewModel.showGuestLimitReachedDialog.value = true
                        } else {
                            showCertificateDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(25.dp)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Certificate Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🏆 COGNITIVE CERTIFICATE PORTAL",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App function & gesture guide card for new users as requested!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎓", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "APP FUNCTIONS & GESTURE GUIDE",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(
                            text = "Welcome! This app has been optimized for effortless play across all device sizes. Below is a quick guide on gestures & functions:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Point 1: Swiping number bar
                        Row(verticalAlignment = Alignment.Top) {
                            Text("👉", fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Swipe/Drag the Number Row",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "During classic 9x9 matches, if the number 9 (or others) is cut off on your screen, simply drag/slide the numbers bar horizontally. You can also tap the ◀ and ▶ buttons on the sides to scroll instantly!",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Point 2: Custom grids and locks
                        Row(verticalAlignment = Alignment.Top) {
                            Text("🔒", fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Optimized Boards & Locks",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Play 3x3, 4x4, or 9x9 games! Grid structures are locked firmly in place with edge-to-edge padding so they remain stable, comfortable to tap, and never move or cover your screen layout.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Point 3: Tool functions
                        Row(verticalAlignment = Alignment.Top) {
                            Text("💡", fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Notes, Erase, and Hint Actions",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Tap a cell first, then choose an action. Toggle PENCIL mode to write small drafts inside cells, ERASE to clear errors, or use HINT (spending earned gems) to reveal a cell's correct number!",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { viewModel.showTermsAndPolicy.value = true },
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("menu_terms_policy_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "info icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VIEW TERMS & PRIVACY POLICY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
        PlayTabScreen.GameBoard -> {
            // Live Puzzle Active Canvas
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isCompactHeight = maxHeight < 640.dp
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = if (isCompactHeight) 4.dp else 12.dp)
                        .then(
                            if (isCompactHeight) Modifier.verticalScroll(rememberScrollState()) else Modifier
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = if (isCompactHeight) Arrangement.spacedBy(8.dp) else Arrangement.SpaceBetween
                ) {
                // Sleek Two-Tier Info Header - compact, preventing any layout wrapping/squeezing!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Row 1: Difficulty & Actions (Withdraw / Pause / Terms)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Difficulty Level Capsule
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isTeamTournamentActive) "TEAM TOURNAMENT" else selectedDifficulty.label.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Right: Controls Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // WITHDRAW / LEAVE button
                            Button(
                                onClick = {
                                    if (isTeamTournamentActive) {
                                        viewModel.leaveAndShowTournamentLeaderboard()
                                    } else {
                                        showWithdrawConfirmation = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.85f),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("forfeit_exit_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Exit to Main Screen",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isTeamTournamentActive) "LEAVE" else "WITHDRAW",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            // Policy Info Button
                            IconButton(
                                onClick = { viewModel.showTermsAndPolicy.value = true },
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("board_terms_policy_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Terms",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Pause Button
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { viewModel.togglePause() }
                                    .testTag("toggle_pause_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Menu,
                                    contentDescription = "Play/Pause Icon",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Row 2: Mode, Timer, Mistakes Summary Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Active Mode indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val modeIcon = when (gameMode) {
                                "Practice" -> "🎮"
                                "Survival" -> "⚠️"
                                "Countdown" -> "⏱️"
                                "Tournament" -> "🏆"
                                else -> "🎲"
                            }
                            Text(
                                text = "$modeIcon ${gameMode.uppercase()}",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Center: Chrono Timer
                        val mins = secondsElapsed / 60
                        val secs = secondsElapsed % 60
                        val timerText = if (gameMode == "Countdown") {
                            String.format("⏳ %02d:%02d", mins, secs)
                        } else {
                            String.format("⏱️ %02d:%02d", mins, secs)
                        }
                        Text(
                            text = timerText,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (gameMode == "Countdown" && secondsElapsed < 30) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )

                        // Right: Mistakes non-wrapping badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "MISTAKES: ",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "$mistakeCount/3",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (mistakeCount >= 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // 1.5. Inline Eye Comfort Theme Toggler during active game play as requested!
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👁️ THEME: ",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            Triple("Creative Light", "☀️ LIGHT", Color(0xFF3F51B5)),
                            Triple("Emerald Eye-Shield", "🌲 EYE-SAFE", Color(0xFF00FF87)),
                            Triple("Matrix Cyberpunk", "🌙 DARK", Color(0xFFFFC107)),
                            Triple("High Contrast Paper", "◑ STARK", Color(0xFF0056C6))
                        ).forEach { (themeName, label, colorIndicator) ->
                            val isSelected = currentTheme == themeName
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                    .clickable { viewModel.changeTheme(themeName) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(colorIndicator)
                                    )
                                    Text(
                                        text = label,
                                        fontSize = 8.8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // If tournament active, show horizontal live teammate progress bars
                if (isTeamTournamentActive) {
                    TeammatesMiniProgressGrid(players = teamTournamentPlayers)
                }

                // 2. The Custom dynamic grid (4x4 or 9x9!)
                Box(
                    modifier = if (isCompactHeight) {
                        Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1f)
                    } else {
                        Modifier
                            .weight(1.0f)
                            .fillMaxWidth()
                    },
                    contentAlignment = Alignment.Center
                ) {
                    SudokuGrid(
                        grid = grid,
                        selectedCell = selectedCell,
                        onCellSelected = { r, c -> viewModel.selectCell(r, c) },
                        isPaused = isPaused,
                        disableGridHelpers = disableGridHelpers,
                        hideLastRow = hideLastRow,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                // 3. User actions and pad keys
                ControlPanel(
                    pencilMode = isPencilMode,
                    onPencilToggle = { viewModel.isPencilMode.value = !isPencilMode },
                    onClear = { viewModel.clearCell() },
                    onHint = { viewModel.getHint() },
                    onNumberEntered = { viewModel.enterNumber(it) },
                    gemsRemaining = userProfile?.gems ?: 50,
                    gridSize = gridSize,
                    disableGridHelpers = disableGridHelpers,
                    onToggleGridHelpers = { viewModel.toggleDisableGridHelperLayers() },
                    hideLastRow = hideLastRow,
                    onToggleHideLastRow = { viewModel.toggleHideLastRowNumbers() }
                )
            }
            }
        }
        PlayTabScreen.TermsAndPolicy -> {
            TermsAndPolicyDocumentScreen(
                viewModel = viewModel,
                onBack = { viewModel.showTermsAndPolicy.value = false }
            )
        }
    }
}

        // Overlays: Victory / GameOver / Tournament Leaderboard Screens
        if (isTeamTournamentActive && userHasFinishedTournament) {
            TournamentLeaderboardOverlay(
                players = teamTournamentPlayers,
                gridSize = gridSize,
                gameMode = gameMode,
                onRestart = {
                    viewModel.startTeamTournament(gridSize)
                },
                onClose = {
                    viewModel.isTeamTournamentActive.value = false
                    viewModel.clearSavedGame()
                    viewModel.gridSize.value = 9
                    viewModel.startNewGame(selectedDifficulty, 9)
                    viewModel.clearSavedGame()
                }
            )
        } else {
            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.82f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Game over icon",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = "PUZZLE TERMINATED",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "You triggered mistakes inside the game mode constraints. Keep sharpening your cognitive speed!",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.startNewGame(selectedDifficulty, gridSize) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.fillMaxWidth().testTag("restart_after_over_btn")
                            ) {
                                Text("TRY ONCE MORE", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.clearSavedGame(); viewModel.startNewGame(selectedDifficulty, gridSize) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("CHOOSE OTHER DIFFICULTY")
                            }
                        }
                    }
                }
            }

            if (isGameWon) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.88f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .verticalScroll(rememberScrollState()),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // High energy visual crown
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD700).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Victory Medal icon",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            Text(
                                text = "PUZZLE TERMINATED - SOLVED!",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4CAF50),
                                letterSpacing = 1.sp
                            )

                            // RECORD BREAKING ACHIEVEMENT BANNER (International / National / Global alert trigger)
                            recordAlert?.let { alertText ->
                                Surface(
                                    color = Color(0xFFD32F2F), // Brilliant scarlet banner
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = alertText,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Verified on official global cognitive servers in real-time!",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.9f),
                                            textAlign = TextAlign.Center,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            // AI Cognitive Diagnostics Scanning Evaluation
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🧠 AI COGNITIVE PERFORMANCE CORE",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "99.8% RAW SYNAPSE",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Metrics Grid
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Speed
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                                .padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Synapse Spd", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${String.format("%.2f", aiSynapticSpeedHertz)} Hz", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }

                                        // Focus Accuracy
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                                .padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Attention Co", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${String.format("%.1f", aiCognitiveFocus)}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                        }

                                        // Global Percent
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                                .padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Global Rank", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("Top ${String.format("%.3f", aiGlobalPercentile)}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Detailed Instant analysis summary text
                                    Text(
                                        text = aiCognitiveComment.ifEmpty { "Evaluating cognitive scans and performance metrics..." },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            // Standard Rewards Checklist
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "PlayGold Granted", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = "+${when(selectedDifficulty){
                                                SudokuDifficulty.EASY -> "150"
                                                SudokuDifficulty.MEDIUM -> "300"
                                                SudokuDifficulty.HARD -> "500"
                                                SudokuDifficulty.EXPERT -> "850"
                                            }} PGP",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = Color(0xFFFFC107)
                                        )
                                    }
                                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "XP Points Awarded", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = "+${when(selectedDifficulty){
                                                SudokuDifficulty.EASY -> "50"
                                                SudokuDifficulty.MEDIUM -> "100"
                                                SudokuDifficulty.HARD -> "180"
                                                SudokuDifficulty.EXPERT -> "300"
                                            }} XP",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            // Buttons
                            Button(
                                onClick = { viewModel.startNewGame(selectedDifficulty, gridSize) },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("victory_restart_btn")
                            ) {
                                Text("SOLVE ANOTHER MATRIX", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.forfeitAndExitGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("victory_main_menu_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Main Menu Icon",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("CHANGE GAME GRID / MAIN MENU", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.activeTab.value = 2 },
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("VIEW REWARDS REDEEM TAB")
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val isGuest = userProfile?.userId?.startsWith("guest_player_") == true
                            Button(
                                onClick = {
                                    if (isGuest) {
                                        viewModel.showGuestLimitReachedDialog.value = true
                                    } else {
                                        showCertificateDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Certificate Icon",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🏆 GENERATE & SHARE CERTIFICATE", color = Color.Black, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            if (showCertificateDialog) {
                WinningCertificateOverlay(
                    userName = certificateNameInput,
                    onNameChange = { certificateNameInput = it },
                    gridSize = gridSize,
                    difficultyLabel = selectedDifficulty.label,
                    durationSeconds = secondsElapsed,
                    recordText = recordAlert ?: "OFFICIAL COGNITIVE SPEED RECORD MASTER",
                    synapticSpeed = aiSynapticSpeedHertz,
                    focusRating = aiCognitiveFocus,
                    globalPercentile = aiGlobalPercentile,
                    countryCode = when(userProfile?.countryName) {
                        "United States" -> "US"
                        "Canada" -> "CA"
                        "Brazil" -> "BR"
                        "Mexico" -> "MX"
                        "Argentina" -> "AR"
                        "Colombia" -> "CO"
                        "Chile" -> "CL"
                        "Peru" -> "PE"
                        "Ecuador" -> "EC"
                        "Venezuela" -> "VE"
                        "United Kingdom" -> "GB"
                        "Germany" -> "DE"
                        "France" -> "FR"
                        "Italy" -> "IT"
                        "Spain" -> "ES"
                        "Netherlands" -> "NL"
                        "Switzerland" -> "CH"
                        "Sweden" -> "SE"
                        "Norway" -> "NO"
                        "Austria" -> "AT"
                        "Belgium" -> "BE"
                        "Denmark" -> "DK"
                        "Finland" -> "FI"
                        "Poland" -> "PL"
                        "Portugal" -> "PT"
                        "Greece" -> "GR"
                        "Turkey" -> "TR"
                        "Singapore" -> "SG"
                        "India" -> "IN"
                        "Japan" -> "JP"
                        "South Korea" -> "KR"
                        "China" -> "CN"
                        "Australia" -> "AU"
                        "New Zealand" -> "NZ"
                        "Indonesia" -> "ID"
                        "Malaysia" -> "MY"
                        "Pakistan" -> "PK"
                        "Bangladesh" -> "BD"
                        "Vietnam" -> "VN"
                        "Thailand" -> "TH"
                        "Philippines" -> "PH"
                        "Nigeria" -> "NG"
                        "Egypt" -> "EG"
                        "South Africa" -> "ZA"
                        "Kenya" -> "KE"
                        "Ghana" -> "GH"
                        "Morocco" -> "MA"
                        "Algeria" -> "DZ"
                        "Ethiopia" -> "ET"
                        else -> when(userProfile?.region?.lowercase()) {
                            "americas" -> "US"
                            "europe" -> "DE"
                            "asia-pacific" -> "JP"
                            "india" -> "IN"
                            "vietnam" -> "VN"
                            "africa" -> "NG"
                            else -> "US"
                        }
                    },
                    expectedCertificatePassword = userProfile?.certificatePassword ?: "",
                    linkedInUrl = userProfile?.linkedInUrl ?: "",
                    instagramUrl = userProfile?.instagramUrl ?: "",
                    onClose = { showCertificateDialog = false }
                )
            }
    }
    }
}


// --- 2. Live Arena competitive multiplayer Tab ---

@Composable
fun ArenaScreenTab(viewModel: SudokuViewModel) {
    val selectedRegion by viewModel.regionFilter.collectAsStateWithLifecycle()
    val players by viewModel.selectedRegionLeaderboard.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val recentMatchResult by viewModel.recentMatchResult.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val fastestTimes by viewModel.fastestCompletionTimes.collectAsStateWithLifecycle()
    val isRefreshingFastest by viewModel.isFetchingFastestTimes.collectAsStateWithLifecycle()
    val disableGridHelpers by viewModel.disableGridHelperLayers.collectAsStateWithLifecycle()
    val hideLastRow by viewModel.hideLastRowNumbers.collectAsStateWithLifecycle()

    var showCertificateDialog by remember { mutableStateOf(false) }
    var certificateNameInput by remember { mutableStateOf("") }
    LaunchedEffect(userProfile) {
        if (certificateNameInput.isEmpty() && userProfile != null) {
            certificateNameInput = userProfile?.username ?: "MSB GRANDMASTER"
        }
    }

    var pvpGridSize by remember { mutableStateOf(9) }
    var pvpDifficultyLabel by remember { mutableStateOf("PVP Duel Deciphers") }
    var pvpDurationSeconds by remember { mutableStateOf(120L) }
    var pvpSynapticSpeed by remember { mutableStateOf(3.1) }
    var pvpFocusRating by remember { mutableStateOf(98.0) }
    var pvpGlobalPercentile by remember { mutableStateOf(0.12) }

    Box(modifier = Modifier.fillMaxSize()) {
        LeaderboardScreen(
            players = players,
            selectedRegion = selectedRegion,
            onRegionSelected = { viewModel.regionFilter.value = it },
            searchState = searchState,
            recentMatchResult = recentMatchResult,
            onEnterArena = { mode, size -> viewModel.enterCompetitiveArena(mode, size) },
            onDismissMatch = { viewModel.dismissMatchScreen() },
            userProfile = userProfile,
            fastestTimes = fastestTimes,
            isRefreshingFastest = isRefreshingFastest,
            onRefreshFastest = { viewModel.refreshGlobalFastestTimes() },
            onSendNudge = { viewModel.sendNudge() },
            onSolveBoost = { amount -> viewModel.boostPvpProgress(amount) },
            onPvpCellSelected = { row, col -> viewModel.selectPvpCell(row, col) },
            onPvpNumberEntered = { number -> viewModel.enterPvpNumber(number) },
            onPvpClearCell = { viewModel.clearPvpCell() },
            onSendSocialNudge = { platform -> viewModel.sendSocialNudge(platform) },
            onPvpWithdraw = { viewModel.withdrawPvpMatch() },
            onPvpToggleEraseMode = { viewModel.togglePvpEraseMode() },
            onSendPvpChatMessage = { message -> viewModel.sendPvpChatMessage(message) },
            disableGridHelpers = disableGridHelpers,
            hideLastRow = hideLastRow,
            onToggleDisableGridHelpers = { viewModel.toggleDisableGridHelperLayers() },
            onToggleHideLastRow = { viewModel.toggleHideLastRowNumbers() },
            onClaimPvpCertificate = { size, diff, duration, speed, focus, percentile ->
                val isGuest = userProfile?.userId?.startsWith("guest_player_") == true
                if (isGuest) {
                    viewModel.showGuestLimitReachedDialog.value = true
                } else {
                    pvpGridSize = size
                    pvpDifficultyLabel = diff
                    pvpDurationSeconds = duration
                    pvpSynapticSpeed = speed
                    pvpFocusRating = focus
                    pvpGlobalPercentile = percentile
                    showCertificateDialog = true
                }
            }
        )

        if (showCertificateDialog) {
            WinningCertificateOverlay(
                userName = certificateNameInput,
                onNameChange = { certificateNameInput = it },
                gridSize = pvpGridSize,
                difficultyLabel = pvpDifficultyLabel,
                durationSeconds = pvpDurationSeconds,
                recordText = "OFFICIAL PVP ONLINE MULTIPLAYER VICTOR",
                synapticSpeed = pvpSynapticSpeed,
                focusRating = pvpFocusRating,
                globalPercentile = pvpGlobalPercentile,
                countryCode = when(userProfile?.countryName) {
                    "United States" -> "US"
                    "United Kingdom" -> "GB"
                    "Japan" -> "JP"
                    "Czech Republic" -> "CZ"
                    "Greece" -> "GR"
                    "Spain" -> "ES"
                    "India" -> "IN"
                    "Ghana" -> "GH"
                    "Germany" -> "DE"
                    "France" -> "FR"
                    "Canada" -> "CA"
                    "Australia" -> "AU"
                    else -> "UN"
                },
                expectedCertificatePassword = userProfile?.certificatePassword ?: "",
                linkedInUrl = userProfile?.linkedInUrl ?: "",
                instagramUrl = userProfile?.instagramUrl ?: "",
                onClose = { showCertificateDialog = false }
            )
        }
    }
}


// --- 3. Google Play Rewards Tab ---

@Composable
fun RewardsScreenTab(viewModel: SudokuViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val transactions by viewModel.rewardTransactions.collectAsStateWithLifecycle()
    val claimingState by viewModel.claimingState.collectAsStateWithLifecycle()
    val gameHistory by viewModel.gameHistory.collectAsStateWithLifecycle()

    val pgp = userProfile?.playGoldPoints ?: 3200
    val gems = userProfile?.gems ?: 50
    val isGuest = userProfile?.userId?.startsWith("guest_player_") == true

    RewardsDashboard(
        playGoldPoints = pgp,
        gemsCount = gems,
        userProfile = userProfile,
        gameHistory = gameHistory,
        transactions = transactions,
        claimingState = claimingState,
        onClaimSelected = { title, cost ->
            if (isGuest) {
                viewModel.showGuestLimitReachedDialog.value = true
            } else {
                viewModel.claimGooglePlayGift(title, cost)
            }
        },
        onConfirmReceipt = { tx -> viewModel.confirmReceiptClaimedTransaction(tx) },
        onCancelClaim = { viewModel.cancelClaimMode() },
        onRedeemPromoCode = { code, callback ->
            if (isGuest) {
                viewModel.showGuestLimitReachedDialog.value = true
            } else {
                viewModel.redeemPromoCode(code, callback)
            }
        },
        onNavigateToLeaderboard = { viewModel.activeTab.value = 1 }
    )
}


// --- 4. Profile / Studio credits Tab ---

@Composable
fun ProfileScreenTab(viewModel: SudokuViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val gameHistory by viewModel.gameHistory.collectAsStateWithLifecycle()
    val aiAnalysis by viewModel.aiAnalysis.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsStateWithLifecycle()
    val isMusicEnabled by viewModel.isMusicEnabled.collectAsStateWithLifecycle()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()

    SettingsPanel(
        userProfile = userProfile,
        gameHistory = gameHistory,
        aiAnalysis = aiAnalysis,
        isAnalyzing = isAnalyzing,
        selectedTheme = selectedTheme,
        onChangeTheme = { theme -> viewModel.changeTheme(theme) },
        onRunAI = { viewModel.runPlayerStatsAIAnalysis() },
        onLogout = { viewModel.logOutCurrentSession() },
        onSaveProfile = { profile -> viewModel.saveProfile(profile) },
        onConnectSocial = { platform, handle -> viewModel.connectSocialMedia(platform, handle) },
        isSoundEnabled = isSoundEnabled,
        onToggleSound = { viewModel.toggleSoundEnabled() },
        isMusicEnabled = isMusicEnabled,
        onToggleMusic = { viewModel.toggleMusicEnabled() },
        isBiometricEnabled = isBiometricEnabled,
        onToggleBiometric = { viewModel.toggleBiometricEnabled() }
    )
}

@Composable
fun TeammatesMiniProgressGrid(players: List<TournamentPlayer>) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "TEAM CHALLENGERS SOLVING LIVE PROGRESS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                players.forEach { player ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (player.isUser) "YOU" else player.name.split("_").first(),
                            fontSize = 9.sp,
                            fontWeight = if (player.isUser) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (player.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(player.progress / 100f)
                                    .background(
                                        if (player.progress == 100) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (player.finishTimeSeconds != null) {
                                val m = player.finishTimeSeconds / 60
                                val s = player.finishTimeSeconds % 60
                                String.format("%02d:%02d", m, s)
                            } else {
                                "${player.progress}%"
                            },
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Light,
                            color = if (player.finishTimeSeconds != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentLeaderboardOverlay(
    players: List<TournamentPlayer>,
    gridSize: Int,
    gameMode: String,
    onRestart: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "MSB TEAM ARENA TOURNAMENT RESULTS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Text(
                    text = "Matrix Size: ${gridSize}x${gridSize}  |  Constraint Mode: ${gameMode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))

                // Display all competitors in sorted ranking order
                val sortedPlayers = players.sortedWith(compareBy({ it.status == "Left" }, { it.finishTimeSeconds ?: Int.MAX_VALUE }, { -it.progress }))
                sortedPlayers.forEachIndexed { idx, player ->
                    val rank = idx + 1
                    val isSvenUser = player.isUser
                    val rowBg = if (isSvenUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else Color.Transparent

                    Surface(
                        color = rowBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = when (rank) {
                                        1 -> Color(0xFFFFD700)
                                        2 -> Color(0xFFC0C0C0)
                                        3 -> Color(0xFFCD7F32)
                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    },
                                    shape = CircleShape,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = rank.toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (rank in 1..3) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = if (isSvenUser) "${player.name} (YOU)" else player.name,
                                    fontWeight = if (isSvenUser) FontWeight.ExtraBold else FontWeight.Medium,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            val timeText = if (player.finishTimeSeconds != null) {
                                val m = player.finishTimeSeconds / 60
                                val s = player.finishTimeSeconds % 60
                                String.format("%02d:%02d", m, s)
                            } else {
                                player.status
                            }

                            Text(
                                text = timeText,
                                fontWeight = FontWeight.Bold,
                                color = if (player.status == "Left") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("PLAY NEW TEAM CHALLENGE", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RETURN TO HOME ACADEMY")
                }
            }
        }
    }
}

data class CertificateTemplate(
    val id: String,
    val name: String,
    val matchTitle: String,
    val gridSize: Int,
    val difficulty: String,
    val durationSeconds: Long,
    val synapticSpeed: Double,
    val focusRating: Double,
    val globalPercentile: Double,
    val score: Int
)

@Composable
fun WinningCertificateOverlay(
    userName: String,
    onNameChange: (String) -> Unit,
    gridSize: Int,
    difficultyLabel: String,
    durationSeconds: Long,
    recordText: String,
    synapticSpeed: Double,
    focusRating: Double,
    globalPercentile: Double,
    countryCode: String,
    expectedCertificatePassword: String = "",
    linkedInUrl: String = "",
    instagramUrl: String = "",
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var isSavingPdf by remember { mutableStateOf(false) }
    var saveProgress by remember { mutableStateOf(0f) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    // local name state to allow buttery input while typing
    var localNameInput by remember { mutableStateOf(userName.ifBlank { "MSB COGNITIVE SOLVER".uppercase() }) }

    // 5 custom preset achievements, plus the dynamic latest solve
    val currentSolveTemplate = remember(gridSize, difficultyLabel, durationSeconds, synapticSpeed, focusRating, globalPercentile) {
        CertificateTemplate(
            id = "latest_solve",
            name = "🏆 Live Solve (${gridSize}x${gridSize})",
            matchTitle = if (gridSize == 3) "3x3 Mini Grid Resolve" else if (gridSize == 4) "4x4 Children Grid Resolve" else "9x9 Standard Matrix Resolution",
            gridSize = gridSize,
            difficulty = difficultyLabel,
            durationSeconds = durationSeconds,
            synapticSpeed = synapticSpeed,
            focusRating = focusRating,
            globalPercentile = globalPercentile,
            score = maxOf(350, (synapticSpeed * 45 + (100 - globalPercentile) * 30 + (gridSize * 150) - (durationSeconds * 0.1)).toInt())
        )
    }

    val templates = remember(currentSolveTemplate) {
        listOf(
            currentSolveTemplate,
            CertificateTemplate(
                id = "one_to_one",
                name = "⚔️ 1v1 Arena Duel",
                matchTitle = "Competitive Arena 1v1 Duel",
                gridSize = 9,
                difficulty = "Arena High Speed",
                durationSeconds = 150, // 2m 30s
                synapticSpeed = 16.5,
                focusRating = 95.8,
                globalPercentile = 0.380,
                score = 880
            ),
            CertificateTemplate(
                id = "group_challenge",
                name = "👥 Group Speed Arena",
                matchTitle = "Group Speed Challenge",
                gridSize = 9,
                difficulty = "Hyper Drive Arena",
                durationSeconds = 125, // 2m 05s
                synapticSpeed = 19.4,
                focusRating = 97.4,
                globalPercentile = 0.180,
                score = 975
            ),
            CertificateTemplate(
                id = "tournament_cup",
                name = "👑 Tournament Cup",
                matchTitle = "Tournament Cup Championship",
                gridSize = 9,
                difficulty = "Expert Grandmaster",
                durationSeconds = 112, // 1m 52s
                synapticSpeed = 22.8,
                focusRating = 99.2,
                globalPercentile = 0.045,
                score = 1150
            ),
            CertificateTemplate(
                id = "standard_9x9",
                name = "🧩 Standard 9x9 Classic",
                matchTitle = "9x9 Standard Matrix Challenge",
                gridSize = 9,
                difficulty = "Hard",
                durationSeconds = 245, // 4m 05s
                synapticSpeed = 10.8,
                focusRating = 93.5,
                globalPercentile = 1.650,
                score = 690
            ),
            CertificateTemplate(
                id = "children_4x4",
                name = "👶 Children 4x4 Quick",
                matchTitle = "4x4 Children Matrix Challenge",
                gridSize = 4,
                difficulty = "Easy",
                durationSeconds = 68, // 1m 08s
                synapticSpeed = 15.2,
                focusRating = 98.7,
                globalPercentile = 0.720,
                score = 425
            )
        )
    }

    var selectedTemplateIndex by remember { mutableStateOf(0) }
    val currentTemplate = templates[selectedTemplateIndex]

    val certificateStyles = listOf("COSMIC_LUXURY", "CLASSIC_IVORY", "NEON_CYBER")
    var selectedStyleIndex by remember { mutableStateOf(0) }
    val currentStyle = certificateStyles[selectedStyleIndex]

    // Cognitive AI audit state parameters
    var aiEndorsementText by remember { mutableStateOf<String?>(null) }
    var isGeneratingEndorsement by remember { mutableStateOf(false) }
    var endorsementStatus by remember { mutableStateOf("Pending AI audit seal verification...") }
    var lastLoadedHtml by remember { mutableStateOf("") }
    var isWebViewLoaded by remember { mutableStateOf(false) }

    val mMin = currentTemplate.durationSeconds / 60
    val mSec = currentTemplate.durationSeconds % 60
    val mTimeStr = String.format(Locale.getDefault(), "%02d:%02d", mMin, mSec)
    val categoryLabel = when (currentTemplate.gridSize) {
        4 -> "4x4 (Children Category)"
        9 -> "9x9 (Standard Area Category)"
        else -> "${currentTemplate.gridSize}x${currentTemplate.gridSize} Grid"
    }
    val localFallbackText = "GOOGLE AI COGNITIVE SWEEP DATA: Active matrix scan on $categoryLabel completed with 100% precision. Synaptic speed is ${String.format(Locale.getDefault(), "%.2f", currentTemplate.synapticSpeed)}Hz, Focus Rating: ${String.format(Locale.getDefault(), "%.1f", currentTemplate.focusRating)}%, Global Percentile: Top ${String.format(Locale.getDefault(), "%.3f", currentTemplate.globalPercentile)}%."

    LaunchedEffect(localNameInput, selectedTemplateIndex) {
        isGeneratingEndorsement = true
        endorsementStatus = "Querying live Cognitive AI audit..."
        try {
            val response = com.example.data.GeminiClient.getCertificateEndorsement(
                apiKey = BuildConfig.GEMINI_API_KEY,
                userName = localNameInput,
                gridSize = currentTemplate.gridSize,
                difficulty = currentTemplate.difficulty,
                durationSeconds = currentTemplate.durationSeconds,
                synapticSpeed = currentTemplate.synapticSpeed,
                focusRating = currentTemplate.focusRating,
                globalPercentile = currentTemplate.globalPercentile,
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

    LaunchedEffect(isSaving) {
        if (isSaving) {
            saveProgress = 0f
            while (saveProgress < 0.85f) {
                kotlinx.coroutines.delay(80)
                saveProgress += 0.15f
            }
            saveProgress = 0.9f
            val savedPath = com.example.utils.CertificateDownloader.generateAndSaveCertificate(
                context = context,
                userName = localNameInput,
                gridSize = currentTemplate.gridSize,
                difficultyLabel = currentTemplate.difficulty,
                durationSeconds = currentTemplate.durationSeconds,
                synapticSpeed = currentTemplate.synapticSpeed,
                focusRating = currentTemplate.focusRating,
                globalPercentile = currentTemplate.globalPercentile,
                aiEndorsement = aiEndorsementText,
                countryCode = countryCode,
                matchTitle = currentTemplate.matchTitle,
                designStyle = currentStyle
            )
            saveProgress = 1.0f
            kotlinx.coroutines.delay(150)
            isSaving = false
            if (savedPath != null) {
                actionMessage = "💾 SUCCESS! Saved to your device's Downloads directory as:\n$savedPath\n\nYou can easily find and view your certificate inside the Files app or Google Photos gallery! Verified metadata signature complete."
            } else {
                actionMessage = "❌ FAILED: Unable to write file. Please verify storage permissions are enabled for the application."
            }
        }
    }

    LaunchedEffect(isSavingPdf) {
        if (isSavingPdf) {
            saveProgress = 0f
            while (saveProgress < 0.85f) {
                kotlinx.coroutines.delay(80)
                saveProgress += 0.15f
            }
            saveProgress = 0.9f
            val savedPath = com.example.utils.CertificateDownloader.generateAndSavePdfCertificate(
                context = context,
                userName = localNameInput,
                gridSize = currentTemplate.gridSize,
                difficultyLabel = currentTemplate.difficulty,
                durationSeconds = currentTemplate.durationSeconds,
                synapticSpeed = currentTemplate.synapticSpeed,
                focusRating = currentTemplate.focusRating,
                globalPercentile = currentTemplate.globalPercentile,
                aiEndorsement = aiEndorsementText,
                countryCode = countryCode,
                matchTitle = currentTemplate.matchTitle,
                designStyle = currentStyle
            )
            saveProgress = 1.0f
            kotlinx.coroutines.delay(150)
            isSavingPdf = false
            if (savedPath != null) {
                actionMessage = "💾 SUCCESS! Saved to your device's Downloads directory as PDF document:\n$savedPath\n\nYou can easily find, print, or share your verified PDF certificate direct to your professional career portfolios!"
            } else {
                actionMessage = "❌ FAILED: Unable to write PDF document. Please verify storage permissions are enabled for the application."
            }
        }
    }

    var enteredCertPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isCertUnlocked by remember { mutableStateOf(expectedCertificatePassword.isBlank()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.93f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isCertUnlocked) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD4AF37))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD4AF37).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = Color(0xFFD4AF37),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "VERIFY CERTIFICATE KEY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD4AF37)
                    )

                    Text(
                        text = "This graduation document is linked under your secured profile. Enter your Certificate Password to unlock PDF/PNG exports:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    var certVisible by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = enteredCertPassword,
                        onValueChange = {
                            enteredCertPassword = it
                            passwordError = null
                        },
                        label = { Text("Certificate Password") },
                        visualTransformation = if (certVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { certVisible = !certVisible }) {
                                Icon(
                                    imageVector = if (certVisible) Icons.Default.Done else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    passwordError?.let { err ->
                        Text(text = err, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onClose,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("CANCEL", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (enteredCertPassword == expectedCertificatePassword) {
                                    isCertUnlocked = true
                                } else {
                                    passwordError = "Incorrect password! Handshake failed."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("UNLOCK", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        } else {
            Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD4AF37).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆 COGNITIVE CERTIFICATE HUB",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFD4AF37)
                    )
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Certificate button",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Achievement Selection presetter tabs selector
                Text(
                    text = "SELECT ACHIEVEMENT TO PREVIEW & CONFER:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    templates.forEachIndexed { idx, temp ->
                        val isSelected = selectedTemplateIndex == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) Color(0xFFD4AF37) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFFD4AF37) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedTemplateIndex = idx }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = temp.name,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "CUSTOMIZE GRADUATE NAME FOR CREDENTIAL:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = localNameInput,
                        onValueChange = {
                            localNameInput = it
                            onNameChange(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter holder's name...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "DESIGN STYLE & CHASSIS CUSTOMIZATION:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        certificateStyles.forEachIndexed { sIdx, styleName ->
                            val isStyleSelected = selectedStyleIndex == sIdx
                            val prettyStyleName = when(styleName) {
                                "COSMIC_LUXURY" -> "⭐ COSMIC LUXURY"
                                "CLASSIC_IVORY" -> "✒️ CLASSIC IVORY"
                                "NEON_CYBER" -> "🌌 NEON CYBER"
                                else -> styleName
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isStyleSelected) Color(0xFFD4AF37) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isStyleSelected) Color(0xFFD4AF37) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedStyleIndex = sIdx }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = prettyStyleName,
                                    color = if (isStyleSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 10.sp,
                                    fontWeight = if (isStyleSelected) FontWeight.Black else FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Shaded Cognitive AI Status Alert Segment
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1E25)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E88E5).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E88E5).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isGeneratingEndorsement) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFF4DE8F4),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("💡", fontSize = 14.sp)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "COGNITIVE AI CERTIFICATE AUDITOR",
                                fontSize = 9.sp,
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
                        if (!isGeneratingEndorsement) {
                            TextButton(
                                onClick = {
                                    isGeneratingEndorsement = true
                                    endorsementStatus = "Re-analyzing via Cognitive AI..."
                                    coroutineScope.launch {
                                        try {
                                            val response = com.example.data.GeminiClient.getCertificateEndorsement(
                                                apiKey = BuildConfig.GEMINI_API_KEY,
                                                userName = localNameInput,
                                                gridSize = currentTemplate.gridSize,
                                                difficulty = currentTemplate.difficulty,
                                                durationSeconds = currentTemplate.durationSeconds,
                                                synapticSpeed = currentTemplate.synapticSpeed,
                                                focusRating = currentTemplate.focusRating,
                                                globalPercentile = currentTemplate.globalPercentile,
                                                localFallbackReport = localFallbackText
                                            )
                                            aiEndorsementText = response
                                            endorsementStatus = "Audited successfully with custom remarks!"
                                        } catch(e: Exception) {
                                            aiEndorsementText = localFallbackText
                                            endorsementStatus = "Offline calibration verified."
                                        } finally {
                                            isGeneratingEndorsement = false
                                        }
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("RE-AUDIT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E88E5))
                            }
                        }
                    }
                }

                val m = currentTemplate.durationSeconds / 60
                val s = currentTemplate.durationSeconds % 60
                val timeStr = String.format(Locale.getDefault(), "%02d:%02d", m, s)
                val computedScore = currentTemplate.score

                val calendar = java.util.Calendar.getInstance()
                val dayStr = String.format(Locale.getDefault(), "%02d", calendar.get(java.util.Calendar.DAY_OF_MONTH))
                val monthStr = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.getDefault())?.uppercase() ?: "JUNE"
                val yearStr = calendar.get(java.util.Calendar.YEAR).toString()

                val userNameStr = localNameInput.ifBlank { "MSB COGNITIVE SOLVER" }.uppercase()
                    .replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\"", "\\\"")
                val synapticSpeedStr = String.format(Locale.getDefault(), "%.2f", currentTemplate.synapticSpeed)
                val computedScoreStr = "$computedScore PGP"
                val globalPercentileStr = "TOP ${String.format(Locale.getDefault(), "%.3f", currentTemplate.globalPercentile)}%"
                val finalEndorsementStr = (aiEndorsementText ?: localFallbackText)
                    .replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\"", "\\\"")
                    .replace("\n", " ")
                    .replace("\r", " ")

                val jsBgColorCenter = when (currentStyle) {
                    "NEON_CYBER" -> "#0d0f1a"
                    "CLASSIC_IVORY" -> "#FAF6EE"
                    else -> "#1c180d"
                }
                val jsBgColorEdge = when (currentStyle) {
                    "NEON_CYBER" -> "#040409"
                    "CLASSIC_IVORY" -> "#EEDBBA"
                    else -> "#020202"
                }
                val jsOuterBorderColor = when (currentStyle) {
                    "NEON_CYBER" -> "#FF007F"
                    "CLASSIC_IVORY" -> "#800020"
                    else -> "#D4AF37"
                }
                val jsInnerBorderColor = when (currentStyle) {
                    "NEON_CYBER" -> "#00F0FF"
                    "CLASSIC_IVORY" -> "#0D233A"
                    else -> "#D4AF37"
                }
                val jsHeaderColor = when (currentStyle) {
                    "NEON_CYBER" -> "#39FF14"
                    "CLASSIC_IVORY" -> "#0D233A"
                    else -> "#D4AF37"
                }
                val jsHeaderFont = when (currentStyle) {
                    "NEON_CYBER" -> "bold 13px monospace"
                    "CLASSIC_IVORY" -> "bold 14px Georgia, serif"
                    else -> "bold 15px Georgia, serif"
                }
                val jsHeaderText = when (currentStyle) {
                    "NEON_CYBER" -> "[ COGNITIVE SOLVER MATRIX SYSTEM OVERRIDE ]"
                    "CLASSIC_IVORY" -> "★  COGNITIO ET RESOLUTIO LUX VESTRA  ★"
                    else -> "★ ★ ★  COGNITIVE GRADUATED SOLVER  ★ ★ ★"
                }
                val jsTitleColor = when (currentStyle) {
                    "NEON_CYBER" -> "#00F0FF"
                    "CLASSIC_IVORY" -> "#800020"
                    else -> "#FFFFFF"
                }
                val jsTitleFont = when (currentStyle) {
                    "NEON_CYBER" -> "bold 22px monospace"
                    "CLASSIC_IVORY" -> "bold 26px Georgia, serif"
                    else -> "bold 24px sans-serif"
                }
                val jsTitleText = when (currentStyle) {
                    "NEON_CYBER" -> "// CONG. ${currentTemplate.matchTitle.uppercase()}"
                    else -> currentTemplate.matchTitle.uppercase()
                }
                val jsSubtitleColor = when (currentStyle) {
                    "NEON_CYBER" -> "#FF007F"
                    "CLASSIC_IVORY" -> "#0D233A"
                    else -> "#D4AF37"
                }
                val jsSubtitleFont = when (currentStyle) {
                    "NEON_CYBER" -> "bold 10px monospace"
                    "CLASSIC_IVORY" -> "bold 10px Georgia, serif"
                    else -> "bold 10px monospace"
                }
                val jsSubtitleText = when (currentStyle) {
                    "NEON_CYBER" -> "NODE_RECORD // LEVEL COGNITIVE CONFR_DEGREE_SECURE"
                    "CLASSIC_IVORY" -> "ACADEMIC TESTIMONIAL OF COGNITIVE LAUREATE"
                    else -> "OFFICIAL CERTIFICATE OF COGNITIVE GRADUATION"
                }
                val jsIntroText = when (currentStyle) {
                    "NEON_CYBER" -> ">> This terminal hereby registers logical confirmation for:"
                    "CLASSIC_IVORY" -> "This solemn academic credential is formally awarded and recognized to"
                    else -> "This prestigious cognitive credential is formally awarded to"
                }
                val jsIntroColor = when (currentStyle) {
                    "NEON_CYBER" -> "#bebebe"
                    "CLASSIC_IVORY" -> "#222222"
                    else -> "#bebebe"
                }
                val jsIntroFont = when (currentStyle) {
                    "NEON_CYBER" -> "11px monospace"
                    "CLASSIC_IVORY" -> "italic 11px Georgia, serif"
                    else -> "italic 12px Georgia, serif"
                }
                val jsUserNameColor = when (currentStyle) {
                    "NEON_CYBER" -> "#FF007F"
                    "CLASSIC_IVORY" -> "#0D233A"
                    else -> "#FFD700"
                }
                val jsUserNameFont = when (currentStyle) {
                    "NEON_CYBER" -> "bold 23px monospace"
                    "CLASSIC_IVORY" -> "bold 24px Georgia, serif"
                    else -> "bold 24px Georgia, serif"
                }
                val jsDescText1 = when (currentStyle) {
                    "NEON_CYBER" -> "// CONTEXT: demonstrating flawless execution profiles, parsing complex multi-quadrant"
                    "CLASSIC_IVORY" -> "for demonstrating exquisite logical precision, rapid matrix solving capacity, and elite performance"
                    else -> "for exceptional logical precision, matrix resolution speed, and cognitive excellence"
                }
                val jsDescText2 = when (currentStyle) {
                    "NEON_CYBER" -> "// matrices and achieving extreme synaptic computation metrics in real-time execution tests."
                    "CLASSIC_IVORY" -> "within the rigorous intellectual specifications established by MSB Academy."
                    else -> "within the boundaries of MSB Creative Studios challenge specifications."
                }
                val jsDescColor = when (currentStyle) {
                    "NEON_CYBER" -> "#00F0FF"
                    "CLASSIC_IVORY" -> "#222222"
                    else -> "#9e9e9e"
                }
                val jsDescFont = when (currentStyle) {
                    "NEON_CYBER" -> "9px monospace"
                    "CLASSIC_IVORY" -> "10px Georgia, serif"
                    else -> "9px sans-serif"
                }
                val jsAiHeaderColor = when (currentStyle) {
                    "NEON_CYBER" -> "#39FF14"
                    "CLASSIC_IVORY" -> "#800020"
                    else -> "#4DE8F4"
                }
                val jsAiHeaderFont = when (currentStyle) {
                    "NEON_CYBER" -> "bold 9px monospace"
                    "CLASSIC_IVORY" -> "bold 9px Georgia, serif"
                    else -> "bold 9px monospace"
                }
                val jsAiHeaderText = when (currentStyle) {
                    "NEON_CYBER" -> "<< COGNITIVE AI LOGICAL CRITIQUE VERDICT_SECURE >>"
                    "CLASSIC_IVORY" -> "✒️ ACADEMIC COGNITIVE AI ENDORSEMENT VERIFY:"
                    else -> "⚡ INTEGRATED COGNITIVE AI CRITIQUE (HIGH PRECISION):"
                }
                val jsAiBodyColor = when (currentStyle) {
                    "NEON_CYBER" -> "#39FF14"
                    "CLASSIC_IVORY" -> "#222222"
                    else -> "#FFFFFF"
                }
                val jsAiBodyFont = when (currentStyle) {
                    "NEON_CYBER" -> "9px monospace"
                    "CLASSIC_IVORY" -> "italic 9px Georgia, serif"
                    else -> "italic 10px Georgia, serif"
                }
                val jsPanelBg = when (currentStyle) {
                    "NEON_CYBER" -> "rgba(0, 240, 255, 0.05)"
                    "CLASSIC_IVORY" -> "rgba(128, 0, 32, 0.04)"
                    else -> "rgba(255, 255, 255, 0.02)"
                }
                val jsPanelStroke = when (currentStyle) {
                    "NEON_CYBER" -> "#FF007F"
                    "CLASSIC_IVORY" -> "#0D233A"
                    else -> "rgba(212, 175, 55, 0.25)"
                }
                val jsMetricsLabelColor = when (currentStyle) {
                    "NEON_CYBER" -> "#00F0FF"
                    "CLASSIC_IVORY" -> "#800020"
                    else -> "#D4AF37"
                }
                val jsMetricsLabelFont = when (currentStyle) {
                    "NEON_CYBER" -> "bold 9px monospace"
                    "CLASSIC_IVORY" -> "bold 9px Georgia, serif"
                    else -> "bold 9px monospace"
                }
                val jsMetricsValColor = when (currentStyle) {
                    "NEON_CYBER" -> "#FFFFFF"
                    "CLASSIC_IVORY" -> "#222222"
                    else -> "#FFFFFF"
                }
                val jsMetricsValFont = when (currentStyle) {
                    "NEON_CYBER" -> "9px monospace"
                    "CLASSIC_IVORY" -> "9px Georgia, serif"
                    else -> "9px monospace"
                }
                val jsScoreColor = when (currentStyle) {
                    "NEON_CYBER" -> "#39FF14"
                    "CLASSIC_IVORY" -> "#800020"
                    else -> "#FFD700"
                }
                val jsRankColor = when (currentStyle) {
                    "NEON_CYBER" -> "#FF007F"
                    "CLASSIC_IVORY" -> "#0D233A"
                    else -> "#FF9800"
                }
                val jsSepColor = when (currentStyle) {
                    "NEON_CYBER" -> "rgba(0, 240, 255, 0.2)"
                    "CLASSIC_IVORY" -> "rgba(128, 0, 32, 0.2)"
                    else -> "rgba(212, 175, 55, 0.15)"
                }
                val jsBrandColor = when (currentStyle) {
                    "NEON_CYBER" -> "#FF007F"
                    "CLASSIC_IVORY" -> "#800020"
                    else -> "#FFD700"
                }
                val jsBrandText = when (currentStyle) {
                    "NEON_CYBER" -> "[ CYBERNETIC COGNITIVE COMPETENCY NODE • MSB EXPERIMENTAL ]"
                    "CLASSIC_IVORY" -> "EX COGNITIONE TRIUMPHUS • POWERED BY MSB CREATIVE STUDIOS"
                    else -> "HIGHLIGHT MSB SUDOKU CHALLENGE • POWERED BY MSB CREATIVE STUDIOS"
                }
                val jsBrandFont = when (currentStyle) {
                    "NEON_CYBER" -> "bold 11px monospace"
                    "CLASSIC_IVORY" -> "bold 11px Georgia, serif"
                    else -> "bold 12px sans-serif"
                }
                val jsSubtitleFooterText = when (currentStyle) {
                    "NEON_CYBER" -> "Digital Certificate Generated via Client-Side HTML5 Canvas Vector Pipeline"
                    "CLASSIC_IVORY" -> "Digital Certificate Generated via Client-Side Core Serif Vector Pipeline"
                    else -> "Digital Certificate Generated via Client-Side HTML5 Canvas Vector Pipeline"
                }

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 2.dp,
                            color = when (currentStyle) {
                                "NEON_CYBER" -> Color(0xFFFF007F)
                                "CLASSIC_IVORY" -> Color(0xFF800020)
                                else -> Color(0xFFD4AF37)
                            },
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    val htmlContent = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                        <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />
                        <style>
                          html, body {
                            margin: 0;
                            padding: 0;
                            background: #000000;
                            width: 100%;
                            height: 100%;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            overflow: hidden;
                          }
                          #canvasContainer {
                            width: 100%;
                            height: 100%;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                          }
                          canvas {
                            width: 100%;
                            height: 100%;
                            object-fit: contain;
                            background-color: #050505;
                          }
                        </style>
                        </head>
                        <body>
                        <div id="canvasContainer">
                          <canvas id="certCanvas" width="800" height="600"></canvas>
                        </div>
                        <script>
                          function drawCert() {
                            const canvas = document.getElementById('certCanvas');
                            if (!canvas) return;
                            const ctx = canvas.getContext('2d');
                            
                            // Background
                            ctx.fillStyle = '#050505';
                            ctx.fillRect(0, 0, 800, 600);
                            
                            // Radial background glow
                            const radial = ctx.createRadialGradient(400, 300, 50, 400, 300, 450);
                            radial.addColorStop(0, '${jsBgColorCenter}');
                            radial.addColorStop(1, '${jsBgColorEdge}');
                            ctx.fillStyle = radial;
                            ctx.fillRect(0, 0, 800, 600);

                            // Cyber Grid line drawing inside preview
                            if ('${currentStyle}' === 'NEON_CYBER') {
                              ctx.strokeStyle = 'rgba(0, 240, 255, 0.05)';
                              ctx.lineWidth = 1;
                              for (let gY = 0; gY < 600; gY += 30) {
                                ctx.beginPath(); ctx.moveTo(0, gY); ctx.lineTo(800, gY); ctx.stroke();
                              }
                              for (let gX = 0; gX < 800; gX += 30) {
                                ctx.beginPath(); ctx.moveTo(gX, 0); ctx.lineTo(gX, 600); ctx.stroke();
                              }
                            }

                            // Elegant Outer Border
                            ctx.strokeStyle = '${jsOuterBorderColor}';
                            ctx.lineWidth = 10;
                            ctx.strokeRect(15, 15, 770, 570);
                            
                            // Inner Border
                            ctx.strokeStyle = '${jsInnerBorderColor}';
                            ctx.lineWidth = 2;
                            ctx.strokeRect(30, 30, 740, 540);
                            
                            // Corner accents
                            if ('${currentStyle}' === 'COSMIC_LUXURY') {
                              ctx.fillStyle = '#D4AF37';
                              // Top-Left
                              ctx.beginPath(); ctx.moveTo(30, 30); ctx.lineTo(60, 30); ctx.lineTo(30, 60); ctx.fill();
                              // Top-Right
                              ctx.beginPath(); ctx.moveTo(770, 30); ctx.lineTo(740, 30); ctx.lineTo(770, 60); ctx.fill();
                              // Bottom-Left
                              ctx.beginPath(); ctx.moveTo(30, 570); ctx.lineTo(60, 570); ctx.lineTo(30, 540); ctx.fill();
                              // Bottom-Right
                              ctx.beginPath(); ctx.moveTo(770, 570); ctx.lineTo(740, 570); ctx.lineTo(770, 540); ctx.fill();
                            } else if ('${currentStyle}' === 'CLASSIC_IVORY') {
                              ctx.fillStyle = '#0D233A';
                              const cornerRad = 10;
                              ctx.beginPath(); ctx.arc(44, 44, cornerRad, 0, 2*Math.PI); ctx.fill();
                              ctx.beginPath(); ctx.arc(756, 44, cornerRad, 0, 2*Math.PI); ctx.fill();
                              ctx.beginPath(); ctx.arc(44, 556, cornerRad, 0, 2*Math.PI); ctx.fill();
                              ctx.beginPath(); ctx.arc(756, 556, cornerRad, 0, 2*Math.PI); ctx.fill();
                              
                              ctx.fillStyle = '#D4AF37';
                              ctx.font = 'bold 12px Georgia, serif';
                              ctx.textAlign = 'center';
                              ctx.fillText('★', 44, 48);
                              ctx.fillText('★', 756, 48);
                              ctx.fillText('★', 44, 560);
                              ctx.fillText('★', 756, 560);
                            } else if ('${currentStyle}' === 'NEON_CYBER') {
                              ctx.strokeStyle = '#00F0FF';
                              ctx.lineWidth = 4;
                              // TL
                              ctx.beginPath(); ctx.moveTo(30,30); ctx.lineTo(80,30); ctx.moveTo(30,30); ctx.lineTo(30,80); ctx.stroke();
                              // TR
                              ctx.beginPath(); ctx.moveTo(770,30); ctx.lineTo(720,30); ctx.moveTo(770,30); ctx.lineTo(770,80); ctx.stroke();
                              // BL
                              ctx.beginPath(); ctx.moveTo(30,570); ctx.lineTo(80,570); ctx.moveTo(30,570); ctx.lineTo(30,520); ctx.stroke();
                              // BR
                              ctx.beginPath(); ctx.moveTo(770,570); ctx.lineTo(720,570); ctx.moveTo(770,570); ctx.lineTo(770,520); ctx.stroke();
                            }

                            ctx.textAlign = 'center';
                            
                            // Header Star Label
                            ctx.fillStyle = '${jsHeaderColor}';
                            ctx.font = '${jsHeaderFont}';
                            ctx.fillText('${jsHeaderText}', 400, 65);

                            // Title: MSB SUDOKU CHALLENGE Categories
                            ctx.fillStyle = '${jsTitleColor}';
                            ctx.font = '${jsTitleFont}';
                            ctx.fillText('${jsTitleText}', 400, 105);

                            ctx.fillStyle = '${jsSubtitleColor}';
                            ctx.font = '${jsSubtitleFont}';
                            ctx.fillText('${jsSubtitleText}', 400, 132);
                            
                            // Divider
                            ctx.strokeStyle = '${jsSepColor}';
                            ctx.lineWidth = 1.5;
                            ctx.beginPath();
                            ctx.moveTo(200, 145);
                            ctx.lineTo(600, 145);
                            ctx.stroke();

                            // Certified text
                            ctx.fillStyle = '${jsIntroColor}';
                            ctx.font = '${jsIntroFont}';
                            ctx.fillText('${jsIntroText}', 400, 172);

                            // User Name
                            ctx.fillStyle = '${jsUserNameColor}';
                            ctx.font = '${jsUserNameFont}';
                            if ('${currentStyle}' === 'COSMIC_LUXURY') {
                              ctx.shadowColor = 'rgba(255, 215, 0, 0.3)';
                              ctx.shadowBlur = 10;
                            }
                            ctx.fillText('${userNameStr}', 400, 215);
                            ctx.shadowBlur = 0; // reset

                            // Subscript
                            ctx.fillStyle = '${jsDescColor}';
                            ctx.font = '${jsDescFont}';
                            ctx.fillText('${jsDescText1}', 400, 245);
                            ctx.fillText('${jsDescText2}', 400, 260);

                            // Draw Cognitive AI Endorsement Seal inside WebView Canvas (No Google)
                            const endorsementStr = '${finalEndorsementStr}';
                            ctx.fillStyle = '${jsAiHeaderColor}';
                            ctx.font = '${jsAiHeaderFont}';
                            ctx.fillText('${jsAiHeaderText}', 400, 285);
                            
                            ctx.fillStyle = '${jsAiBodyColor}';
                            ctx.font = '${jsAiBodyFont}';
                            ctx.fillText(endorsementStr, 400, 302);

                            // Panel for specifications (height adjusted to 140)
                            ctx.fillStyle = '${jsPanelBg}';
                            ctx.fillRect(80, 325, 640, 140);
                            ctx.strokeStyle = '${jsPanelStroke}';
                            ctx.lineWidth = 1;
                            ctx.strokeRect(80, 325, 640, 140);

                            // Left details
                            ctx.textAlign = 'left';
                            ctx.fillStyle = '${jsMetricsLabelColor}';
                            ctx.font = '${jsMetricsLabelFont}';
                            ctx.fillText('MATRIX SIZE:', 110, 350);
                            ctx.fillText('DIFFICULTY:', 110, 375);
                            ctx.fillText('SYNAPTIC SPEED:', 110, 400);
                            ctx.fillText('ACHIEVED DATE:', 110, 425);

                            ctx.fillStyle = '${jsMetricsValColor}';
                            ctx.font = '${jsMetricsValFont}';
                            let matrixLabel = '${currentTemplate.gridSize}x${currentTemplate.gridSize} Grid';
                            if (${currentTemplate.gridSize} === 4) {
                                matrixLabel = '4x4 Grid (Children Category)';
                            } else if (${currentTemplate.gridSize} === 9) {
                                matrixLabel = '9x9 Grid (Standard Area Category)';
                            }
                            ctx.fillText(matrixLabel, 230, 350);
                            ctx.fillText('${currentTemplate.difficulty.uppercase()}', 230, 375);
                            ctx.fillText('${synapticSpeedStr} Hz', 230, 400);

                            // Right details
                            ctx.fillStyle = '${jsMetricsLabelColor}';
                            ctx.font = '${jsMetricsLabelFont}';
                            ctx.fillText('RECORD SOLVE TIME:', 400, 350);
                            ctx.fillText('FINAL GAME SCORE:', 400, 375);
                            ctx.fillText('GLOBAL PERCENTILE:', 400, 400);
                            ctx.fillText('COUNTRY CODE:', 400, 425);

                            ctx.fillStyle = '${jsMetricsValColor}';
                            ctx.font = '${jsMetricsValFont}';
                            ctx.fillText('${timeStr} Duration', 545, 350);
                            
                            ctx.fillStyle = '${jsScoreColor}';
                            ctx.font = 'bold 9px monospace';
                            ctx.fillText('${computedScoreStr}', 545, 375);
                            
                            ctx.fillStyle = '${jsRankColor}';
                            ctx.font = 'bold 9px monospace';
                            ctx.fillText('${globalPercentileStr}', 545, 400);
                            
                            ctx.fillStyle = '${jsMetricsValColor}';
                            ctx.font = '${jsMetricsValFont}';
                            ctx.fillText('${countryCode}', 545, 425);

                            // Footer separator
                            ctx.strokeStyle = '${jsSepColor}';
                            ctx.beginPath();
                            ctx.moveTo(80, 480);
                            ctx.lineTo(720, 480);
                            ctx.stroke();

                            // Powered by footer & verify hashes
                            ctx.textAlign = 'center';
                            ctx.fillStyle = '#555555';
                            ctx.font = '8px monospace';
                            ctx.fillText('VERIFIED LEDGER CREDENTIAL HASH ID: MSB-' + Math.floor(Math.random() * 899999 + 100000), 400, 498);

                            ctx.fillStyle = '${jsBrandColor}';
                            ctx.font = '${jsBrandFont}';
                            ctx.fillText('${jsBrandText}', 400, 524);

                            ctx.fillStyle = '#666666';
                            ctx.font = '8px monospace';
                            ctx.fillText('${jsSubtitleFooterText}', 400, 538);
                          }
                          if (document.readyState === 'complete' || document.readyState === 'interactive') {
                            drawCert();
                          } else {
                            window.onload = drawCert;
                          }
                        </script>
                        </body>
                        </html>
                    """.trimIndent()

                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.useWideViewPort = true
                                settings.loadWithOverviewMode = true
                                settings.domStorageEnabled = true
                                webViewClient = WebViewClient()
                            }
                        },
                        update = { webView ->
                            if (lastLoadedHtml != htmlContent) {
                                webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                                lastLoadedHtml = htmlContent
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (isSaving) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "CRAFTING HIGH-RES PNG CERTIFICATE FILE...",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4AF37)
                        )
                        LinearProgressIndicator(
                            progress = { saveProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFD4AF37),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                actionMessage?.let { msg ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isSaving = true },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                        enabled = !isSaving && !isSavingPdf
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Download custom image",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("💾 PNG IMAGE", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { isSavingPdf = true },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Red for PDF
                        enabled = !isSaving && !isSavingPdf
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Download PDF",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("📄 PDF DOCUMENT", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("BACK TO GAME", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "SHARE CREDENTIAL TO CAREER & SOCIAL NETWORKS:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val m = currentTemplate.durationSeconds / 60
                    val s = currentTemplate.durationSeconds % 60
                    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", m, s)
                    val speedStr = String.format(Locale.getDefault(), "%.2f", currentTemplate.synapticSpeed)
                    val focusStr = String.format(Locale.getDefault(), "%.1f", currentTemplate.focusRating)
                    val rankStr = String.format(Locale.getDefault(), "%.3f", currentTemplate.globalPercentile)

                    val makeSharePost: (String) -> String = { platform ->
                        val verificationTag = when (platform) {
                            "LinkedIn" -> if (linkedInUrl.isNotBlank()) " [Verified Professional Solver: $linkedInUrl]" else ""
                            "Instagram" -> if (instagramUrl.isNotBlank()) " [Verified Athlete Profile: @$instagramUrl]" else ""
                            else -> ""
                        }
                        when (platform) {
                            "LinkedIn" -> "I am proud to share my official Graduation Certificate for the " + currentTemplate.matchTitle.uppercase() + "! I completed the " + currentTemplate.gridSize + "x" + currentTemplate.gridSize + " matrix on " + currentTemplate.difficulty.uppercase() + " level in " + timeStr + " with an AI-certified synaptic speed of " + speedStr + "Hz (Top " + rankStr + "% globally).$verificationTag Powered by MSB Creative Studios! [Verification ID: MSB-" + (System.currentTimeMillis() % 100000) + "]"
                            "Resume" -> "MSB Advanced Cognitive Sudoku Graduate - " + currentTemplate.matchTitle + " (Top " + rankStr + "% Global Rank, Synaptic Speed: " + speedStr + "Hz, Focus Rating: " + focusStr + "%, Difficulty: " + currentTemplate.difficulty.uppercase() + "). Awarded by MSB Creative Studios."
                            "Twitter" -> "Shattered the cognitive record on " + currentTemplate.matchTitle.uppercase() + "! solved " + currentTemplate.gridSize + "x" + currentTemplate.gridSize + " (" + currentTemplate.difficulty.uppercase() + ") in " + timeStr + ". Synaptic speed: " + speedStr + "Hz! 🧠$verificationTag Powered by @MSBCreative #Sudoku #CognitiveElite"
                            "Facebook" -> "Cerebral graduation unlocked! Just earned my certified Cognitive Sudoku Master credential for the " + currentTemplate.matchTitle + " from MSB Creative Studios. Solved in " + timeStr + ", rank: TOP " + rankStr + "%! 👑$verificationTag #MSBSudoku #CognitiveChallenge"
                            else -> "Graduated from " + currentTemplate.matchTitle + "! Time: " + timeStr + ", Speed: " + speedStr + "Hz.$verificationTag Powered by MSB Creative Studios."
                        }
                    }

                    val applyShareAction: (String) -> Unit = { platform ->
                        val postContent = makeSharePost(platform)
                        try {
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("MSBSudokuCert", postContent)
                            clipboard.setPrimaryClip(clip)
                        } catch (e: Exception) {}

                        try {
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "MSB Sudoku Challenge Achievement Credential")
                                putExtra(android.content.Intent.EXTRA_TEXT, postContent)
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Achievement via"))
                        } catch (e: Exception) {}

                        actionMessage = "🔗 COPIED FOR " + platform + "! Pre-formatted professional post and verification link copied to clipboard. Share dialogue triggered!"
                    }

                    IconButton(
                        onClick = { applyShareAction("LinkedIn") },
                        modifier = Modifier
                            .background(Color(0xFF0077B5), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Share to LinkedIn",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { applyShareAction("Twitter") },
                        modifier = Modifier
                            .background(Color(0xFF1DA1F2), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share to Twitter",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { applyShareAction("Resume") },
                        modifier = Modifier
                            .background(Color(0xFF009688), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Format for CV / Resume",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { applyShareAction("Facebook") },
                        modifier = Modifier
                            .background(Color(0xFF1877F2), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Share to Facebook",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val postContent = makeSharePost("Instagram")
                            try {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("MSBSudokuCert", postContent)
                                clipboard.setPrimaryClip(clip)
                            } catch (e: Exception) {}
                            actionMessage = "📸 INSTAGRAM READY: Caption formatted and copied to clipboard! Ready to paste under your story/feed."
                        },
                        modifier = Modifier
                            .background(Color(0xFFE1306C), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Instagram copy verification",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
fun TermsAndPolicyDocumentScreen(
    viewModel: SudokuViewModel,
    onBack: () -> Unit
) {
    var activeDocTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Safe spacing
        Spacer(modifier = Modifier.height(12.dp))

        // Core header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("terms_back_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "TERMS & SECURITY LEDGER",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "SECURED BY MSB CREATIVE STUDIOS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp
                )
            }
        }
        
        // Document Tab Selector
        TabRow(
            selectedTabIndex = activeDocTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .padding(bottom = 16.dp)
        ) {
            Tab(
                selected = activeDocTab == 0,
                onClick = { activeDocTab = 0 },
                text = { Text("Terms", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = activeDocTab == 1,
                onClick = { activeDocTab = 1 },
                text = { Text("Privacy", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = activeDocTab == 2,
                onClick = { activeDocTab = 2 },
                text = { Text("Anti-Cheat", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
        }
        
        // Content Panel with rounded outline, styled card body
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (activeDocTab) {
                    0 -> {
                        Text(
                            text = "TERMS & CONDITIONS OF DISCOVERY",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "1. Acceptance of Analytical Terms\n" +
                                   "By utilizing Direct OTP verification shortcut credentials or local database directories under your control, you explicitly agree to these regulatory terms backing MSB SUDOKU CHALLENGE.\n\n" +
                                   "2. Personal Account Integrity\n" +
                                   "All puzzle results, certificates, offline highscores, and PlayGold Points (PGP) accumulated must be obtained via active manual play. Visual cheats or speed automation engines are prohibited.\n\n" +
                                   "3. Secure Sandbox Sandbox Links\n" +
                                   "Linked account protocols are protected by local encryption. Revocations via custom setting panel options execute instantly and discard stored tokens from offline directories.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                    1 -> {
                        Text(
                            text = "PRIVACY STATEMENT SUMMARY",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Your privacy is paramount. MSB Creative Studios implements safe offline-first architectural storage metrics:\n\n" +
                                   "• Derivative Account Signatures: Stored locally inside database caches as secure hashes. We do not transmit clear-text emails or security answers.\n" +
                                   "• Local Credentials Preservation: Phone dial codes, biometric shortcuts, and email keys reside entirely in secure local SharedPreferences.\n" +
                                   "• Third-Party Tracking Disclaimers: Analytics, dynamic themes, and leaderboard records are sanitized before display to maintain absolute user control.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                    2 -> {
                        Text(
                            text = "ANTI-CHEAT & FAIR PLAY MANIFEST",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "1. Cryptographic PGP Clearance\n" +
                                   "PGPs represents genuine mathematical solve latency stats. If speed spikes or coordinate jumps violate physical tap latency limits, system reserves rights to clear PGP logs.\n\n" +
                                   "2. Multiplayer Swap Balance\n" +
                                   "Competitive Arena lobbies restrict swap-cooldowns and score sabotage to maintain sportsmanship.\n\n" +
                                   "3. Local Data Security\n" +
                                   "Any tamper attempts of local files are auto-resolved by database resets. Always secure your local device using biometric face locks.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
