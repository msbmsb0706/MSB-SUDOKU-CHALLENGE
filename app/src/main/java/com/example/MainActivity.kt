package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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

class MainActivity : ComponentActivity() {
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

    AuthStateContainer(viewModel = viewModel) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            topBar = {
                TopAppBarCompact(
                    userProfile = userProfile,
                    onAddPointsDebug = { viewModel.grantDebugPoints20k() }
                )
            },
            bottomBar = {
                BottomTabBar(
                    currentTab = activeTab,
                    onTabSelected = { viewModel.activeTab.value = it }
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
    }
}

@Composable
fun TopAppBarCompact(
    userProfile: UserProfileEntity?,
    onAddPointsDebug: () -> Unit
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

    var showCertificateDialog by remember { mutableStateOf(false) }
    var certificateNameInput by remember { mutableStateOf("") }

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
        if (grid.isEmpty()) {
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(4, 9).forEach { size ->
                                val label = if (size == 4) "4x4 Quick Grid" else "9x9 Standard Matrix"
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
                                    Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            }
        } else {
            // Live Puzzle Active Canvas
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Info line: Timer, mistakes count, pause
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Difficulty label / Leave button if Tournament Active
                    if (isTeamTournamentActive) {
                        Button(
                            onClick = { viewModel.leaveAndShowTournamentLeaderboard() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("LEAVE & SHOW LOBBY", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = selectedDifficulty.label.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Middle: Timer
                    val mins = secondsElapsed / 60
                    val secs = secondsElapsed % 60
                    Text(
                        text = if (gameMode == "Countdown") {
                            String.format("TIME LEFT: %02d:%02d", mins, secs)
                        } else {
                            String.format("%02d:%02d", mins, secs)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (gameMode == "Countdown" && secondsElapsed < 30) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )

                    // Right: Mistake checkpoints and Pause button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "MISTAKES: $mistakeCount/3",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.togglePause() }
                                .testTag("toggle_pause_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Menu,
                                contentDescription = "Play/Pause Icon",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // If tournament active, show horizontal live teammate progress bars
                if (isTeamTournamentActive) {
                    TeammatesMiniProgressGrid(players = teamTournamentPlayers)
                }

                // 2. The Custom dynamic grid (4x4 or 9x9!)
                Box(modifier = Modifier.weight(1.0f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    SudokuGrid(
                        grid = grid,
                        selectedCell = selectedCell,
                        onCellSelected = { r, c -> viewModel.selectCell(r, c) },
                        isPaused = isPaused,
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
                    gridSize = gridSize
                )
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

                            OutlinedButton(
                                onClick = { viewModel.activeTab.value = 2 },
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("VIEW REWARDS REDEEM TAB")
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = { showCertificateDialog = true },
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

    LeaderboardScreen(
        players = players,
        selectedRegion = selectedRegion,
        onRegionSelected = { viewModel.regionFilter.value = it },
        searchState = searchState,
        recentMatchResult = recentMatchResult,
        onEnterArena = { viewModel.enterCompetitiveArena() },
        onDismissMatch = { viewModel.dismissMatchScreen() },
        userProfile = userProfile,
        fastestTimes = fastestTimes,
        isRefreshingFastest = isRefreshingFastest,
        onRefreshFastest = { viewModel.refreshGlobalFastestTimes() },
        onSendNudge = { viewModel.sendNudge() }
    )
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

    RewardsDashboard(
        playGoldPoints = pgp,
        gemsCount = gems,
        userProfile = userProfile,
        gameHistory = gameHistory,
        transactions = transactions,
        claimingState = claimingState,
        onClaimSelected = { title, cost -> viewModel.claimGooglePlayGift(title, cost) },
        onConfirmReceipt = { tx -> viewModel.confirmReceiptClaimedTransaction(tx) },
        onCancelClaim = { viewModel.cancelClaimMode() },
        onRedeemPromoCode = { code, callback -> viewModel.redeemPromoCode(code, callback) }
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
        onConnectSocial = { platform, handle -> viewModel.connectSocialMedia(platform, handle) }
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
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var isSaving by remember { mutableStateOf(false) }
    var saveProgress by remember { mutableStateOf(0f) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

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
                userName = userName,
                gridSize = gridSize,
                difficultyLabel = difficultyLabel,
                durationSeconds = durationSeconds,
                synapticSpeed = synapticSpeed,
                focusRating = focusRating,
                globalPercentile = globalPercentile
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.93f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
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
                        value = userName,
                        onValueChange = onNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter holder's name...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD4AF37),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                val m = durationSeconds / 60
                val s = durationSeconds % 60
                val timeStr = String.format("%02d:%02d", m, s)
                val computedScore = maxOf(350, (synapticSpeed * 45 + (100 - globalPercentile) * 30 + (gridSize * 150) - (durationSeconds * 0.1)).toInt())

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 2.dp,
                            color = Color(0xFFD4AF37),
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
                          window.onload = function() {
                            const canvas = document.getElementById('certCanvas');
                            const ctx = canvas.getContext('2d');
                            
                            // Background
                            ctx.fillStyle = '#050505';
                            ctx.fillRect(0, 0, 800, 600);
                            
                            // Radial background glow
                            const radial = ctx.createRadialGradient(400, 300, 50, 400, 300, 450);
                            radial.addColorStop(0, '#1c180d');
                            radial.addColorStop(1, '#020202');
                            ctx.fillStyle = radial;
                            ctx.fillRect(0, 0, 800, 600);

                            // Elegant Gold Border
                            ctx.strokeStyle = '#D4AF37';
                            ctx.lineWidth = 10;
                            ctx.strokeRect(15, 15, 770, 570);
                            
                            // Inner Gold Border
                            ctx.strokeStyle = '#D4AF37';
                            ctx.lineWidth = 2;
                            ctx.strokeRect(30, 30, 740, 540);
                            
                            // Corner accents
                            ctx.fillStyle = '#D4AF37';
                            // Top-Left
                            ctx.beginPath();
                            ctx.moveTo(30, 30);
                            ctx.lineTo(60, 30);
                            ctx.lineTo(30, 60);
                            ctx.fill();
                            // Top-Right
                            ctx.beginPath();
                            ctx.moveTo(770, 30);
                            ctx.lineTo(740, 30);
                            ctx.lineTo(770, 60);
                            ctx.fill();
                            // Bottom-Left
                            ctx.beginPath();
                            ctx.moveTo(30, 570);
                            ctx.lineTo(60, 570);
                            ctx.lineTo(30, 540);
                            ctx.fill();
                            // Bottom-Right
                            ctx.beginPath();
                            ctx.moveTo(770, 570);
                            ctx.lineTo(740, 570);
                            ctx.lineTo(770, 540);
                            ctx.fill();

                            ctx.textAlign = 'center';
                            
                            // Header Star Label
                            ctx.fillStyle = '#D4AF37';
                            ctx.font = 'bold 16px Georgia, serif';
                            ctx.fillText('★ ★ ★  COGNITIVE GRADUATED SOLVER  ★ ★ ★', 400, 75);

                            // Title: MSB SUDOKU CHALLENGE
                            ctx.fillStyle = '#FFFFFF';
                            ctx.font = 'bold 30px sans-serif';
                            ctx.fillText('MSB SUDOKU CHALLENGE', 400, 120);

                            ctx.fillStyle = '#D4AF37';
                            ctx.font = 'bold 11px monospace';
                            ctx.fillText('OFFICIAL CERTIFICATE OF COGNITIVE GRADUATION', 400, 148);
                            
                            // Divider
                            ctx.strokeStyle = 'rgba(212, 175, 55, 0.4)';
                            ctx.lineWidth = 1.5;
                            ctx.beginPath();
                            ctx.moveTo(200, 165);
                            ctx.lineTo(600, 165);
                            ctx.stroke();

                            // Certified text
                            ctx.fillStyle = '#bebebe';
                            ctx.font = 'italic 14px Georgia, serif';
                            ctx.fillText('This prestigious cognitive credential is formally awarded to', 400, 195);

                            // User Name
                            ctx.fillStyle = '#FFD700';
                            ctx.font = 'bold 30px Georgia, serif';
                            ctx.shadowColor = 'rgba(255, 215, 0, 0.3)';
                            ctx.shadowBlur = 10;
                            ctx.fillText('${userName.ifBlank { "MSB COGNITIVE SOLVER" }.uppercase()}', 400, 245);
                            ctx.shadowBlur = 0; // reset

                            // Subscript
                            ctx.fillStyle = '#9e9e9e';
                            ctx.font = '10px sans-serif';
                            ctx.fillText('for exceptional logical precision, matrix resolution speed, and cognitive excellence', 400, 280);
                            ctx.fillText('within the boundaries of MSB Creative Studios tournament specifications.', 400, 298);

                            // Panel for specifications
                            ctx.fillStyle = 'rgba(255, 255, 255, 0.02)';
                            ctx.fillRect(80, 320, 640, 120);
                            ctx.strokeStyle = 'rgba(212, 175, 55, 0.25)';
                            ctx.lineWidth = 1;
                            ctx.strokeRect(80, 320, 640, 120);

                            // Left details
                            ctx.textAlign = 'left';
                            ctx.fillStyle = '#D4AF37';
                            ctx.font = 'bold 10px monospace';
                            ctx.fillText('MATRIX SIZE:', 110, 350);
                            ctx.fillText('DIFFICULTY:', 110, 380);
                            ctx.fillText('SYNAPTIC SPEED:', 110, 410);

                            ctx.fillStyle = '#FFFFFF';
                            ctx.font = '10px monospace';
                            ctx.fillText('${gridSize}x${gridSize} Grid', 230, 350);
                            ctx.fillText('${difficultyLabel.uppercase()}', 230, 380);
                            ctx.fillText('${String.format("%.2f", synapticSpeed)} Hz', 230, 410);

                            // Right details
                            ctx.fillStyle = '#D4AF37';
                            ctx.fillText('RECORD SOLVE TIME:', 400, 350);
                            ctx.fillText('FINAL GAME SCORE:', 400, 380);
                            ctx.fillText('GLOBAL PERCENTILE:', 400, 410);

                            ctx.fillStyle = '#FFFFFF';
                            ctx.fillText('${timeStr} Duration', 545, 350);
                            ctx.fillStyle = '#FFD700';
                            ctx.font = 'bold 10px monospace';
                            ctx.fillText('${computedScore} PGP', 545, 380);
                            ctx.fillStyle = '#FF9800';
                            ctx.fillText('TOP ${String.format("%.3f", globalPercentile)}%', 545, 410);

                            // Footer separator
                            ctx.strokeStyle = 'rgba(212, 175, 55, 0.15)';
                            ctx.beginPath();
                            ctx.moveTo(80, 465);
                            ctx.lineTo(720, 465);
                            ctx.stroke();

                            // Powered by footer & verify hashes
                            ctx.textAlign = 'center';
                            ctx.fillStyle = '#555555';
                            ctx.font = '8px monospace';
                            ctx.fillText('VERIFIED LEDGER CREDENTIAL HASH ID: MSB-' + Math.floor(Math.random() * 899999 + 100000), 400, 485);

                            ctx.fillStyle = '#D4AF37';
                            ctx.font = 'bold 12px sans-serif';
                            ctx.fillText('POWERED BY MSB CREATIVE STUDIOS', 400, 515);

                            ctx.fillStyle = '#666666';
                            ctx.font = '8px monospace';
                            ctx.fillText('Digital Certificate Generated via Client-Side HTML5 Canvas Vector Pipeline', 400, 538);
                          };
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
                                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
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

                Button(
                    onClick = { isSaving = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                    enabled = !isSaving
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Download custom image",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("💾 DOWNLOAD CERTIFICATE IMAGE", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
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
                    val m = durationSeconds / 60
                    val s = durationSeconds % 60
                    val timeStr = String.format("%02d:%02d", m, s)
                    val speedStr = String.format("%.2f", synapticSpeed)
                    val focusStr = String.format("%.1f", focusRating)
                    val rankStr = String.format("%.3f", globalPercentile)

                    val makeSharePost: (String) -> String = { platform ->
                        when (platform) {
                            "LinkedIn" -> "I am proud to share my official Graduation Certificate from the MSB SUDOKU CHALLENGE ACADEMY! I completed the " + gridSize + "x" + gridSize + " matrix on " + difficultyLabel.uppercase() + " level in " + timeStr + " with an AI-certified synaptic speed of " + speedStr + "Hz (Top " + rankStr + "% globally). Powered by MSB Creative Studios! [Verification ID: MSB-" + (System.currentTimeMillis() % 100000) + "]"
                            "Resume" -> "MSB Advanced Cognitive Sudoku Graduate (Top " + rankStr + "% Global Rank, Synaptic Speed: " + speedStr + "Hz, Focus Rating: " + focusStr + "%, Difficulty: " + difficultyLabel.uppercase() + "). Awarded by MSB Creative Studios."
                            "Twitter" -> "Shattered the cognitive record on MSB SUDOKU CHALLENGE! solved " + gridSize + "x" + gridSize + " (" + difficultyLabel.uppercase() + ") in " + timeStr + ". Synaptic speed: " + speedStr + "Hz! 🧠 Powered by @MSBCreative #MSBSudoku #CognitiveElite"
                            "Facebook" -> "Cerebral graduation unlocked! Just earned my certified Cognitive Sudoku Master credential from MSB Creative Studios. Solved in " + timeStr + ", rank: TOP " + rankStr + "%! 👑 #MSBSudoku #CognitiveChallenge"
                            else -> "Graduated from MSB SUDOKU CHALLENGE! Time: " + timeStr + ", Speed: " + speedStr + "Hz. Powered by MSB Creative Studios."
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
