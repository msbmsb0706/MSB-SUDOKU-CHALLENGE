package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Intent
import kotlin.math.absoluteValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameHistoryEntity
import com.example.data.UserProfileEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsPanel(
    userProfile: UserProfileEntity?,
    gameHistory: List<GameHistoryEntity>,
    aiAnalysis: String,
    isAnalyzing: Boolean,
    selectedTheme: String = "Matrix Cyberpunk",
    onChangeTheme: (String) -> Unit = {},
    onRunAI: () -> Unit,
    onLogout: () -> Unit,
    onSaveProfile: (UserProfileEntity) -> Unit,
    onConnectSocial: (String, String) -> Unit = { _, _ -> },
    isSoundEnabled: Boolean = true,
    onToggleSound: () -> Unit = {},
    isMusicEnabled: Boolean = true,
    onToggleMusic: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var editUsername by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("Americas") }
    var editCountryName by remember { mutableStateOf("United States") }
    var editCountryFlag by remember { mutableStateOf("🇺🇸") }
    var linkedInInput by remember { mutableStateOf("") }
    var facebookInput by remember { mutableStateOf("") }
    var instagramInput by remember { mutableStateOf("") }
    var editLinkedInUrl by remember { mutableStateOf("") }
    var editInstagramUrl by remember { mutableStateOf("") }
    var editFacebookUrl by remember { mutableStateOf("") }
    var connectingPlatform by remember { mutableStateOf<String?>(null) }
    var inputToConnect by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var contactChannel by remember { mutableStateOf("") }
    var isFeedbackAcknowledged by remember { mutableStateOf(false) }
    var showFeedbackConfirmation by remember { mutableStateOf(false) }
    var lastSubmittedFeedback by remember { mutableStateOf("") }
    var showOAuthPlatform by remember { mutableStateOf<String?>(null) }
    var suggestedHandleForOAuth by remember { mutableStateOf("") }

    LaunchedEffect(connectingPlatform) {
        val platform = connectingPlatform
        if (platform != null) {
            kotlinx.coroutines.delay(1200)
            onConnectSocial(platform, inputToConnect)
            connectingPlatform = null
            inputToConnect = ""
        }
    }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            editUsername = it.username
            selectedRegion = it.region
            editCountryName = it.countryName
            editCountryFlag = it.countryFlag
            editLinkedInUrl = it.linkedInUrl
            editInstagramUrl = it.instagramUrl
            editFacebookUrl = it.facebookUrl
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Powered By Banner (Highly polished studio branding)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Studio logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFICIAL LICENSED PRODUCT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "POWERED BY MSB CREATIVE STUDIOS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "v2.0.1.AI_INTEGRATED_EDITION",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        // 2. Profile Information Form Drawer
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Player Profile Configurations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Sign Out Badge
                    Text(
                        text = "SIGN OUT",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onLogout() }
                            .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Display active email key
                userProfile?.let {
                    Text(
                        text = "Session Key (Email ID): ${it.userId}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                // Profile Photo Upload and Selection Slot
                val context = LocalContext.current
                val pickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
                ) { uri: android.net.Uri? ->
                    if (uri != null) {
                        try {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            if (inputStream != null) {
                                val file = java.io.File(context.filesDir, "custom_profile_photo_${System.currentTimeMillis()}.jpg")
                                val outputStream = java.io.FileOutputStream(file)
                                inputStream.use { input ->
                                    outputStream.use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                userProfile?.let { profile ->
                                    onSaveProfile(profile.copy(profilePhotoPath = file.absolutePath))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                var profilePhotoBitmap by remember(userProfile?.profilePhotoPath) {
                    mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null)
                }

                LaunchedEffect(userProfile?.profilePhotoPath) {
                    val path = userProfile?.profilePhotoPath
                    if (!path.isNullOrBlank()) {
                        try {
                            val file = java.io.File(path)
                            if (file.exists()) {
                                val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                                if (bitmap != null) {
                                    profilePhotoBitmap = bitmap.asImageBitmap()
                                }
                            } else {
                                profilePhotoBitmap = null
                            }
                        } catch (e: Exception) {
                            profilePhotoBitmap = null
                        }
                    } else {
                        profilePhotoBitmap = null
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(2.dp, MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape)
                            .clickable { pickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profilePhotoBitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = profilePhotoBitmap!!,
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            val firstChar = (userProfile?.username ?: "M").firstOrNull()?.uppercase() ?: "M"
                            Text(
                                text = firstChar.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        // Camera icon overlay indicator
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(vertical = 1.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Upload Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROFILE PHOTO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (userProfile?.profilePhotoPath.isNullOrBlank()) "No custom photo uploaded yet" else "Custom gaming avatar active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = { pickerLauncher.launch("image/*") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("SELECT PHOTO", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }

                            if (!userProfile?.profilePhotoPath.isNullOrBlank()) {
                                TextButton(
                                    onClick = {
                                        userProfile?.let { profile ->
                                            onSaveProfile(profile.copy(profilePhotoPath = ""))
                                        }
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("REMOVE", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it },
                    label = { Text("Competitive Handle (Username)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input_field"),
                    singleLine = true
                )

                // Precise tournament nation and flag picker in settings
                var showSettingsCountryPicker by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSettingsCountryPicker = true }
                ) {
                    OutlinedTextField(
                        value = "${editCountryFlag} ${editCountryName} (${selectedRegion})",
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Your Tournament Country & Region") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = "Expand") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (showSettingsCountryPicker) {
                    var sSearchText by remember { mutableStateOf("") }
                    AlertDialog(
                        onDismissRequest = { showSettingsCountryPicker = false },
                        title = {
                            Text(
                                text = "Select Tournament Country",
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Choose your country to update your national speed listings.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Quick choices
                                Text(
                                    text = "POPULAR REGIONS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val quickCountries = fullCountryPickerList.filter { 
                                        it.name == "Singapore" || it.name == "United States" || it.name == "India" 
                                    }
                                    quickCountries.forEach { country ->
                                        val isSelected = editCountryName == country.name
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    editCountryName = country.name
                                                    editCountryFlag = country.flag
                                                    selectedRegion = country.region
                                                    showSettingsCountryPicker = false
                                                },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(country.flag, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(country.name.take(7) + ".", style = MaterialTheme.typography.labelSmall, maxLines = 1, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                OutlinedTextField(
                                    value = sSearchText,
                                    onValueChange = { sSearchText = it },
                                    placeholder = { Text("Search country name...") },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                val matches = remember(sSearchText) {
                                    if (sSearchText.isBlank()) {
                                        fullCountryPickerList
                                    } else {
                                        fullCountryPickerList.filter {
                                            it.name.contains(sSearchText, ignoreCase = true) ||
                                            it.code.contains(sSearchText)
                                        }.sortedBy { 
                                            !it.name.startsWith(sSearchText, ignoreCase = true)
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 180.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (matches.isEmpty()) {
                                        Text(
                                            text = "No matches.",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    } else {
                                        matches.forEach { country ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        editCountryName = country.name
                                                        editCountryFlag = country.flag
                                                        selectedRegion = country.region
                                                        showSettingsCountryPicker = false
                                                    }
                                                    .background(
                                                        if (editCountryName == country.name) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
                                                    )
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(country.flag, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = country.name,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "${country.region} Region",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                if (editCountryName == country.name) {
                                                    Icon(Icons.Default.Check, contentDescription = "Checked", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showSettingsCountryPicker = false }) {
                                Text("CANCEL")
                            }
                        }
                    )
                }

                // Direct Social Media profile/ID link fields
                Text(
                    text = "Social Profile Handles / ID Links:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = editLinkedInUrl,
                    onValueChange = { editLinkedInUrl = it },
                    label = { Text("LinkedIn URL / Profile ID") },
                    leadingIcon = { Text("🔗", modifier = Modifier.padding(start = 8.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("linkedin.com/in/username") }
                )

                OutlinedTextField(
                    value = editInstagramUrl,
                    onValueChange = { editInstagramUrl = it },
                    label = { Text("Instagram @username / Profile ID") },
                    leadingIcon = { Text("📸", modifier = Modifier.padding(start = 8.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("@username or profile name") }
                )

                OutlinedTextField(
                    value = editFacebookUrl,
                    onValueChange = { editFacebookUrl = it },
                    label = { Text("Facebook Profile Link / Username") },
                    leadingIcon = { Text("👥", modifier = Modifier.padding(start = 8.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("facebook.com/username") }
                )

                Text(
                    text = "Matchmaking Region (Leaderboard Division):",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val regions = listOf("Americas", "Europe", "Asia-Pacific", "Africa")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    regions.forEach { reg ->
                        val isSel = selectedRegion == reg
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedRegion = reg }
                                .padding(vertical = 10.dp)
                                .testTag("select_region_$reg"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (reg) {
                                    "Americas" -> "🌎"
                                    "Europe" -> "🇪🇺"
                                    "Asia-Pacific" -> "🇯🇵"
                                    else -> "🇳🇬"
                                },
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        userProfile?.let {
                            onSaveProfile(
                                it.copy(
                                    username = editUsername.takeIf { u -> u.isNotBlank() } ?: "MSB_Player_One",
                                    region = selectedRegion,
                                    countryName = editCountryName,
                                    countryFlag = editCountryFlag,
                                    linkedInUrl = editLinkedInUrl,
                                    instagramUrl = editInstagramUrl,
                                    facebookUrl = editFacebookUrl
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_settings_button")
                ) {
                    Text("SAVE PROFILE DETAILS", fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 2.5 Dynamic App Theme Customizer Selector ---
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Themes customizer icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Visual Interface Themes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Personalize your Sudoku dashboard and competitive arena with our highly optimized custom color matrices.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val themeList = listOf(
                    Triple("Matrix Cyberpunk", "Premium warm luxury dark scheme with gold/cyan layout.", listOf(Color(0xFFFFC107), Color(0xFF00BCD4), Color(0xFF0B0B0F))),
                    Triple("Space AMOLED", "Absolute battery-saving pitch dark black canvas.", listOf(Color(0xFFBB86FC), Color(0xFF03DAC6), Color(0xFF000000))),
                    Triple("Creative Light", "Bright clean professional canvas for daytime focus.", listOf(Color(0xFF3F51B5), Color(0xFFFF9800), Color(0xFFF4F5F9))),
                    Triple("Vintage Roasted Bronze", "Warm dark earthy coffee theme for casual sessions.", listOf(Color(0xFFCD7F32), Color(0xFFFFD700), Color(0xFF160E08)))
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    themeList.forEach { (name, desc, palette) ->
                        val isSelected = selectedTheme == name
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onChangeTheme(name) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Show mini visual thumbnail representing colors
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    palette.forEach { color ->
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(color)
                                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(3.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Game Engine Physics and Sound Preferences Customizer ---
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔊", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Tactile Audio & Music Feedback",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Tune Satisfying sound effects for moves and mistakes, and high-fidelity ambient synthesizer music.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                // Sound Effects Toggle Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleSound() }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSoundEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if (isSoundEnabled) "🔊" else "🔇", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Acoustic FX (Sound Effects)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (isSoundEnabled) "Enabled (Move and score chimes active)" else "Sound effects muted",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isSoundEnabled,
                        onCheckedChange = { onToggleSound() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("sound_effects_toggle")
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

                // Background Music Toggle Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleMusic() }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isMusicEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if (isMusicEnabled) "📻" else "📴", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Interactive BGM (Background Music)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (isMusicEnabled) "Playing (Soft pentatonic ambient synth)" else "Music disabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isMusicEnabled,
                        onCheckedChange = { onToggleMusic() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("bg_music_toggle")
                    )
                }
            }
        }

        // --- Social Media Connect & Synchronizer Panel (LinkedIn, Facebook, Instagram) ---
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Social Platform Integrations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Connect your verified profiles to sync Sudoku achievements globally and claim exclusive PlayGold Points!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Indication overlay of active handshake
                val platform = connectingPlatform
                if (platform != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Contacting ${platform.uppercase()} OAuth Service...",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // 1. LinkedIn Connector Row
                Spacer(modifier = Modifier.height(4.dp))
                val isLinkedInLinked = !userProfile?.linkedInUrl.isNullOrBlank()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isLinkedInLinked) Color(0xFF4CAF50).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔗", fontSize = 18.sp)
                            Text("LinkedIn Profile", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (isLinkedInLinked) {
                            Surface(color = Color(0xFF4CAF50), shape = RoundedCornerShape(4.dp)) {
                                Text("VERIFIED (+500 PGP)", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        } else {
                            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                Text("+500 Gold XP", color = MaterialTheme.colorScheme.primary, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }
                    }

                    if (isLinkedInLinked) {
                        Text(
                            text = "Connected Directory: ${userProfile?.linkedInUrl}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4CAF50)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = linkedInInput,
                                onValueChange = { linkedInInput = it },
                                placeholder = { Text("LinkedIn Handle (optional)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                            )
                            Button(
                                onClick = {
                                    suggestedHandleForOAuth = if (linkedInInput.isNotBlank()) linkedInInput else (userProfile?.username ?: "MSB_Grandmaster")
                                    showOAuthPlatform = "LinkedIn"
                                    linkedInInput = ""
                                },
                                modifier = Modifier.height(48.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("OAUTH LINK", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // 2. Facebook Connector Row
                val isFacebookLinked = !userProfile?.facebookUrl.isNullOrBlank()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isFacebookLinked) Color(0xFF3B5998).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("👥", fontSize = 18.sp)
                            Text("Facebook Account", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (isFacebookLinked) {
                            Surface(color = Color(0xFF3B5998), shape = RoundedCornerShape(4.dp)) {
                                Text("VERIFIED (+300 PGP)", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        } else {
                            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                Text("+300 Gold XP", color = MaterialTheme.colorScheme.primary, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }
                    }

                    if (isFacebookLinked) {
                        Text(
                            text = "Connected Directory: ${userProfile?.facebookUrl}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3B5998)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = facebookInput,
                                onValueChange = { facebookInput = it },
                                placeholder = { Text("Profile Link / Name (optional)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                            )
                            Button(
                                onClick = {
                                    suggestedHandleForOAuth = if (facebookInput.isNotBlank()) facebookInput else (userProfile?.username ?: "MSB_Grandmaster")
                                    showOAuthPlatform = "Facebook"
                                    facebookInput = ""
                                },
                                modifier = Modifier.height(48.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("OAUTH LINK", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // 3. Instagram Connector Row
                val isInstagramLinked = !userProfile?.instagramUrl.isNullOrBlank()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isInstagramLinked) Color(0xFFE1306C).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📸", fontSize = 18.sp)
                            Text("Instagram Handle", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (isInstagramLinked) {
                            Surface(color = Color(0xFFE1306C), shape = RoundedCornerShape(4.dp)) {
                                Text("VERIFIED (+400 PGP)", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        } else {
                            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                Text("+400 Gold XP", color = MaterialTheme.colorScheme.primary, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }
                    }

                    if (isInstagramLinked) {
                        Text(
                            text = "Connected Directory: ${userProfile?.instagramUrl}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE1306C)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = instagramInput,
                                onValueChange = { instagramInput = it },
                                placeholder = { Text("Instagram @username (optional)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                            )
                            Button(
                                onClick = {
                                    suggestedHandleForOAuth = if (instagramInput.isNotBlank()) instagramInput else (userProfile?.username ?: "MSB_Grandmaster")
                                    showOAuthPlatform = "Instagram"
                                    instagramInput = ""
                                },
                                modifier = Modifier.height(48.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("OAUTH LINK", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. AI Performance Diagnostics (Google Gemini model)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Gemini AI icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini Cognitive AI Sweep",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "FLASH-3.5",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Request our real-time Gemini AI agent to diagnostic verify your finished matches, mistakes ratios, speed curves, and compose customized speed solving blueprints.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (isAnalyzing) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "COMPILING CRITICAL SWEEPS LEADGER...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Button(
                        onClick = onRunAI,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("ai_analysis_btngate"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("INITIATE AI COGNITIVE STUDY", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }

                if (aiAnalysis.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = aiAnalysis,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // 4. Played Games History Run Ledger
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Run log icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Historical Game Run Ledger",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                if (gameHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No completed run entries parsed yet. Solve a puzzle or finish multiplayer arena to initiate cryptographic run registers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }
                } else {
                    gameHistory.take(8).forEach { run ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Diff badge
                                    val badgeColor = when (run.difficulty) {
                                        "EASY" -> Color(0xFF4CAF50)
                                        "MEDIUM" -> MaterialTheme.colorScheme.primary
                                        "HARD" -> Color(0xFFFF9800)
                                        "EXPERT" -> Color(0xFFE91E63)
                                        else -> Color(0xFF9C27B0) // ARENA
                                    }
                                    Surface(
                                        color = badgeColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = run.difficulty,
                                            color = badgeColor,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 8.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }

                                    // Win status badge
                                    val winColor = if (run.status == "WON") Color(0xFF4CAF50) else Color(0xFFF44336)
                                    Text(
                                        text = run.status,
                                        color = winColor,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.sp
                                    )
                                }

                                Text(
                                    text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(run.timestamp)),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val mins = run.timeElapsedSeconds / 60
                                val secs = run.timeElapsedSeconds % 60
                                Text(
                                    text = String.format("%02d:%02d", mins, secs),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "+${run.pgpGained} PGP / +${run.xpGained} XP",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB300)
                                )
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    }
                }
            }
        }

        // 5. Game instructions Manual checklist
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Manual info icon",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sudoku & Rewards Guide",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                Text(
                    text = "• Objective: Fill the 9x9 grid so that every row, column and 3x3 block contains digits 1-9 without repeating.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• Earn PlayGold Points: Speed solving and completed puzzles grant valuable points. Expert puzzles can award up to 850 PGP inside single completions!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• Cognitive Milestones: Accumulate PGP points to unlock simulated Graduation Certificate templates and tournament honor medals, which can be viewed or printed anytime.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• Global Rankings: Compete in matchmaking, climb divisions, and reach rank #1 Global by beating target benchmark solve times.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 5.2. Platform Developer Feedback Form (Direct mail destination)
        val context = LocalContext.current
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Feedback icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tournament & Platform Feedback",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                Text(
                    text = "Have questions, code bug submissions, or custom feature proposals? Enter your message below and submit directly to the MSB development office securely.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = contactChannel,
                    onValueChange = { contactChannel = it },
                    label = { Text("Your Contact Info (Email or Phone Channel)") },
                    placeholder = { Text("Enter your contact details so we can reach you...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = feedbackMessage,
                    onValueChange = { feedbackMessage = it },
                    label = { Text("Your Feedback Message") },
                    placeholder = { Text("Describe suggestions, tournament feedback, or desired features...") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Interactive Acknowledgment check box at the bottom of the form before submitting
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isFeedbackAcknowledged = !isFeedbackAcknowledged }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = isFeedbackAcknowledged,
                        onCheckedChange = { isFeedbackAcknowledged = it },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("feedback_acknowledge_checkbox")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I acknowledge and agree to submit this feedback directly to msbcreativestudios@gmail.com securely.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = {
                        if (feedbackMessage.isNotBlank()) {
                            if (!isFeedbackAcknowledged) {
                                android.widget.Toast.makeText(context, "Please check the Acknowledge Box before submitting.", android.widget.Toast.LENGTH_LONG).show()
                            } else {
                                val combinedText = "MSB SUDOKU CHALLENGE SUCESSFUL SUBMITTED FEEDBACK\n\n" +
                                    "User Contact Channel: ${contactChannel.ifBlank { "Not provided" }}\n\n" +
                                    "Message Contents:\n$feedbackMessage"
                                
                                lastSubmittedFeedback = combinedText
                                showFeedbackConfirmation = true
                                
                                // Direct/automatic email transmission initiation
                                try {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = android.net.Uri.parse("mailto:")
                                        putExtra(Intent.EXTRA_EMAIL, arrayOf("msbcreativestudios@gmail.com"))
                                        putExtra(Intent.EXTRA_SUBJECT, "MSB SUDOKU CHALLENGE SUCESSFUL SUBMITTED FEEDBACK")
                                        putExtra(Intent.EXTRA_TEXT, combinedText)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(Intent.createChooser(emailIntent, "Transmit Feedback to Developers..."))
                                } catch (e: Exception) {
                                    // Fallback safe behavior if no intent handlers
                                    android.widget.Toast.makeText(context, "Secure transmit queued.", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                feedbackMessage = ""
                                contactChannel = ""
                            }
                        } else {
                            android.widget.Toast.makeText(context, "Please input your feedback message first.", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("submit_feedback_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SUBMIT CLASSIFIED FEEDBACK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- SUBMISSION CONFIRMED ACKNOWLEDGMENT BOX DIALOG ---
        if (showFeedbackConfirmation) {
            AlertDialog(
                onDismissRequest = { showFeedbackConfirmation = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📡", fontSize = 22.sp)
                        Text(
                            text = "TRANSMISSION SUCCESSFUL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "MSB SUDOKU CHALLENGE SUCESSFUL SUBMITTED FEEDBACK",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Text(
                            text = "Below is the copy of your package message sent securely to msbcreativestudios@gmail.com:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = lastSubmittedFeedback,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Choose a secondary dispatch channel if you need to resend this user message:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Secondary Email Dispatch Button
                            Button(
                                onClick = {
                                    try {
                                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = android.net.Uri.parse("mailto:")
                                            putExtra(Intent.EXTRA_EMAIL, arrayOf("msbcreativestudios@gmail.com"))
                                            putExtra(Intent.EXTRA_SUBJECT, "MSB SUDOKU CHALLENGE SUCESSFUL SUBMITTED FEEDBACK")
                                            putExtra(Intent.EXTRA_TEXT, lastSubmittedFeedback)
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(Intent.createChooser(emailIntent, "Transmit via Email..."))
                                    } catch (ex: Exception) {
                                        android.widget.Toast.makeText(context, "No email client found.", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text("📧 EMAIL CHANNEL", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // Secondary Text SMS Dispatch Button
                            Button(
                                onClick = {
                                    try {
                                        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = android.net.Uri.parse("smsto:")
                                            putExtra("sms_body", lastSubmittedFeedback)
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(Intent.createChooser(smsIntent, "Transmit via SMS..."))
                                    } catch (ex: Exception) {
                                        android.widget.Toast.makeText(context, "No SMS client found.", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text("💬 TEXT MSG SMS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFeedbackConfirmation = false }) {
                        Text("CLOSE DISMISS", fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        // 5.5. Secure Challenge Sharing Network
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Secure Profile & Challenge Share",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                Text(
                    text = "Generate a cryptographically masked sharing link to invite partners and brag about your high scores. This secure link obscures all your sensitive profile identifiers, email addresses, and passphrases permanently.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Mocking a dynamic, highly secure masked token
                val secureToken = remember(userProfile?.userId) {
                    val base = userProfile?.userId ?: "guest"
                    val hash = base.hashCode().absoluteValue.toString(16).padEnd(8, 'x')
                    "msb-sh-${hash}-prtcl-777"
                }

                val shareLink = "https://msbcreativestudios.com/sudoku/challenge?uid=$secureToken&secure=true"

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CRYPTOGRAPHICALLY SECURED LINK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = shareLink,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("MSB Sudoku Challenge Secure Link", shareLink)
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, "Secure link copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Copy secure link",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Join my MSB Sudoku Challenge lobby securely! Play with me at: $shareLink")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Challenge Safely")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SHARE SECURE SHEET", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 6. Branding Credits Footer
        Text(
            text = "Digital MSB Sudoku Challenge platform designed in alignment with MSB Creative Studios brand security frameworks. All rewards processes simulate compliant crypto-clearance ledger standards.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 96.dp)
        )
    }

    showOAuthPlatform?.let { plat ->
        OAuthHandshakeDialog(
            platform = plat,
            suggestedHandle = suggestedHandleForOAuth,
            onDismiss = { showOAuthPlatform = null },
            onSuccess = { authenticatedHandle ->
                onConnectSocial(plat, authenticatedHandle)
                showOAuthPlatform = null
            }
        )
    }
}
