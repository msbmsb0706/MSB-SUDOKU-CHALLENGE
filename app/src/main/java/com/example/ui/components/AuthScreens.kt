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
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AuthState
import com.example.ui.SudokuViewModel

data class CountryItem(val name: String, val code: String, val flag: String, val region: String)

val fullCountryPickerList = listOf(
    // Americas
    CountryItem("United States", "+1", "🇺🇸", "Americas"),
    CountryItem("Canada", "+1", "🇨🇦", "Americas"),
    CountryItem("Brazil", "+55", "🇧🇷", "Americas"),
    CountryItem("Mexico", "+52", "🇲🇽", "Americas"),
    CountryItem("Argentina", "+54", "🇦🇷", "Americas"),
    CountryItem("Colombia", "+57", "🇨🇴", "Americas"),
    CountryItem("Chile", "+56", "🇨🇱", "Americas"),
    CountryItem("Peru", "+51", "🇵🇪", "Americas"),
    CountryItem("Ecuador", "+593", "🇪🇨", "Americas"),
    CountryItem("Venezuela", "+58", "🇻🇪", "Americas"),
    
    // Europe
    CountryItem("United Kingdom", "+44", "🇬🇧", "Europe"),
    CountryItem("Germany", "+49", "🇩🇪", "Europe"),
    CountryItem("France", "+33", "🇫🇷", "Europe"),
    CountryItem("Italy", "+39", "🇮🇹", "Europe"),
    CountryItem("Spain", "+34", "🇪🇸", "Europe"),
    CountryItem("Netherlands", "+31", "🇳🇱", "Europe"),
    CountryItem("Switzerland", "+41", "🇨🇭", "Europe"),
    CountryItem("Sweden", "+46", "🇸🇪", "Europe"),
    CountryItem("Norway", "+47", "🇳🇴", "Europe"),
    CountryItem("Austria", "+43", "🇦🇹", "Europe"),
    CountryItem("Belgium", "+32", "🇧🇪", "Europe"),
    CountryItem("Denmark", "+45", "🇩🇰", "Europe"),
    CountryItem("Finland", "+358", "🇫🇮", "Europe"),
    CountryItem("Poland", "+48", "🇵🇱", "Europe"),
    CountryItem("Portugal", "+351", "🇵🇹", "Europe"),
    CountryItem("Greece", "+30", "🇬🇷", "Europe"),
    CountryItem("Turkey", "+90", "🇹🇷", "Europe"),

    // Asia-Pacific
    CountryItem("Singapore", "+65", "🇸🇬", "Asia-Pacific"),
    CountryItem("India", "+91", "🇮🇳", "Asia-Pacific"),
    CountryItem("Japan", "+81", "🇯🇵", "Asia-Pacific"),
    CountryItem("South Korea", "+82", "🇰🇷", "Asia-Pacific"),
    CountryItem("China", "+86", "🇨🇳", "Asia-Pacific"),
    CountryItem("Australia", "+61", "🇦🇺", "Asia-Pacific"),
    CountryItem("New Zealand", "+64", "🇳🇿", "Asia-Pacific"),
    CountryItem("Indonesia", "+62", "🇮🇩", "Asia-Pacific"),
    CountryItem("Malaysia", "+60", "🇲🇾", "Asia-Pacific"),
    CountryItem("Pakistan", "+92", "🇵🇰", "Asia-Pacific"),
    CountryItem("Bangladesh", "+880", "🇧🇩", "Asia-Pacific"),
    CountryItem("Vietnam", "+84", "🇻🇳", "Asia-Pacific"),
    CountryItem("Thailand", "+66", "🇹🇭", "Asia-Pacific"),
    CountryItem("Philippines", "+63", "🇵🇭", "Asia-Pacific"),

    // Africa
    CountryItem("Nigeria", "+234", "🇳🇬", "Africa"),
    CountryItem("Egypt", "+20", "🇪🇬", "Africa"),
    CountryItem("South Africa", "+27", "🇿🇦", "Africa"),
    CountryItem("Kenya", "+254", "🇰🇪", "Africa"),
    CountryItem("Ghana", "+233", "🇬🇭", "Africa"),
    CountryItem("Morocco", "+212", "🇲🇦", "Africa"),
    CountryItem("Algeria", "+213", "🇩🇿", "Africa"),
    CountryItem("Ethiopia", "+251", "🇪🇹", "Africa")
)

@Composable
fun AuthStateContainer(
    viewModel: SudokuViewModel,
    authenticatedContent: @Composable () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = authState,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "auth_anim"
    ) { state ->
        when (state) {
            is AuthState.Authenticated -> {
                authenticatedContent()
            }
            is AuthState.Welcome -> {
                AuthWelcomeScreen(
                    viewModel = viewModel,
                    onLoginClick = { viewModel.authState.value = AuthState.LoggingIn },
                    onRegisterClick = { viewModel.authState.value = AuthState.Registering }
                )
            }
            is AuthState.LoggingIn -> {
                AuthLoginScreen(
                    viewModel = viewModel,
                    onBackClicked = { viewModel.authState.value = AuthState.Welcome },
                    onResetPassClicked = { viewModel.authState.value = AuthState.ForgetPasswordStep1("") }
                )
            }
            is AuthState.OtpVerification -> {
                AdminOtpScreen(
                    viewModel = viewModel,
                    email = state.email,
                    generatedOtp = state.generatedOtp,
                    onBackClicked = { viewModel.authState.value = AuthState.LoggingIn }
                )
            }
            is AuthState.Registering -> {
                AuthRegisterScreen(
                    viewModel = viewModel,
                    onBackClicked = { viewModel.authState.value = AuthState.Welcome }
                )
            }
            is AuthState.ForgetPasswordStep1 -> {
                ForgetPasswordStep1Screen(
                    viewModel = viewModel,
                    initialEmail = state.email,
                    onBackClicked = { viewModel.authState.value = AuthState.LoggingIn }
                )
            }
            is AuthState.ForgetPasswordStep2 -> {
                ForgetPasswordStep2Screen(
                    viewModel = viewModel,
                    email = state.email,
                    question = state.question,
                    onBackClicked = { viewModel.authState.value = AuthState.ForgetPasswordStep1(state.email) }
                )
            }
            is AuthState.ForgetPasswordSuccess -> {
                ForgetPasswordSuccessScreen(email = state.email)
            }
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEEEEEE),
            contentColor = Color(0xFF1F1F1F)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.testTag("google_login_btn")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Google Logo",
                tint = Color(0xFFEA4335),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "CONTINUE WITH GOOGLE",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun AuthWelcomeScreen(
    viewModel: SudokuViewModel,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            AppBrandingLogo()

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Register a secure global player profile to earn real-time PlayGold Points (PGP), unlock AI strategy reports, climb region leaderboards and claim Google Play reward codes seamlessly.",
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            GoogleSignInButton(
                onClick = { viewModel.signInWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("welcome_login_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SIGN IN TO PROFILE", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("welcome_register_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CREATE NEW ACCOUNT", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Guest Auto-bypass
            Text(
                text = "Continue on Guest Session instead",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                modifier = Modifier
                    .clickable { onLoginClick() } // Standard redirect triggers guest values too
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun AuthLoginScreen(
    viewModel: SudokuViewModel,
    onBackClicked: () -> Unit,
    onResetPassClicked: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.Start)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
            }

            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                AppBrandingLogo()
            }

            Text(
                text = "SIGN IN PROFILE",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Unlock your registered play bank balance.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            val loginIcon = if (email.contains("@")) Icons.Default.Email else if (email.startsWith("+") || email.any { it.isDigit() }) Icons.Default.Call else Icons.Default.AccountBox
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Registered Email or Phone with Country Code") },
                placeholder = { Text("e.g. user@domain.com or +2348031234567") },
                leadingIcon = { Icon(loginIcon, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Secret Password") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.Done else Icons.Default.PlayArrow
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                shape = RoundedCornerShape(12.dp)
            )

            loginError?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = err, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                    }
                }
            }

            val secureOtp by viewModel.secureOtpEnabled.collectAsStateWithLifecycle()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .clickable { viewModel.secureOtpEnabled.value = !secureOtp }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "OTP",
                        tint = if (secureOtp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Enable Secure OTP Verification",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Require 6-digit Code for safety",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = secureOtp,
                    onCheckedChange = { viewModel.secureOtpEnabled.value = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.loginUser(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("VERIFY & SIGN IN", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            GoogleSignInButton(
                onClick = { viewModel.signInWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Forgot password? Reset via security challenge",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onResetPassClicked() }
                    .padding(8.dp)
                    .testTag("forgot_password_link")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Authorized Admin Credentials:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• Email: msbcreativestudios@gmail.com\nAny password triggers secure SMS/Email OTP flow.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AuthRegisterScreen(
    viewModel: SudokuViewModel,
    onBackClicked: () -> Unit
) {
    var isEmailMode by remember { mutableStateOf(true) }
    var emailInput by remember { mutableStateOf("") }
    var phoneCountryCode by remember { mutableStateOf("+1") }
    var phoneDigits by remember { mutableStateOf("") }
    var countryCodeExpanded by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("Europe") }
    var securityQ by remember { mutableStateOf("What is your first pet's name?") }
    var securityA by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var termsAgreed by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    
    val regions = listOf("Americas", "Europe", "Asia-Pacific", "Africa")
    val recoveryQuestions = listOf(
        "What is your first pet's name?",
        "What was the name of your first school?",
        "What is your favorite mobile game studio?",
        "What city were you born in?"
    )

    var countrySearchQuery by remember { mutableStateOf("") }

    var regionExpanded by remember { mutableStateOf(false) }
    var questionExpanded by remember { mutableStateOf(false) }

    val registerError by viewModel.registerError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
            }

            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                AppBrandingLogo()
            }

            Text(
                text = "CREATE ACCOUNT",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Join global competitive lobbies now.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Choose registration method toggle
            TabRow(
                selectedTabIndex = if (isEmailMode) 0 else 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(8.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Tab(
                    selected = isEmailMode,
                    onClick = { isEmailMode = true },
                    text = { Text("Email Address", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = !isEmailMode,
                    onClick = { isEmailMode = false },
                    text = { Text("Phone Number", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            if (isEmailMode) {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email Address ID") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_email_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.42f)
                            .clickable { countryCodeExpanded = true }
                    ) {
                        OutlinedTextField(
                            value = phoneCountryCode,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Code") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Select country code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = phoneDigits,
                        onValueChange = { phoneDigits = it.filter { char -> char.isDigit() } },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                        singleLine = true,
                        placeholder = { Text("e.g. 8031234567") },
                        modifier = Modifier
                            .weight(0.58f)
                            .testTag("reg_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Display Username") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_username_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Precise tournament nation and flag picker
            var showCountryPickerDialog by remember { mutableStateOf(false) }
            val defaultCountry = remember { fullCountryPickerList.first { it.name == "United States" } }
            var selectedCountry by remember { mutableStateOf(defaultCountry) }
            
            // Auto bind the region of the chosen country
            region = selectedCountry.region

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCountryPickerDialog = true }
            ) {
                OutlinedTextField(
                    value = "${selectedCountry.flag} ${selectedCountry.name} (${selectedCountry.region})",
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

            if (showCountryPickerDialog) {
                var pickerSearchText by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showCountryPickerDialog = false },
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
                                text = "Choose your home nation to compete in regional leaderboards and secure worldwide speed rankings.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Quick choices
                            Text(
                                text = "MOST POPULAR REGIONS",
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
                                    val isSelected = selectedCountry == country
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedCountry = country
                                                region = country.region
                                                showCountryPickerDialog = false
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
                                value = pickerSearchText,
                                onValueChange = { pickerSearchText = it },
                                placeholder = { Text("Search country name, code, or prefix...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            val matches = remember(pickerSearchText) {
                                if (pickerSearchText.isBlank()) {
                                    fullCountryPickerList
                                } else {
                                    fullCountryPickerList.filter {
                                        it.name.contains(pickerSearchText, ignoreCase = true) ||
                                        it.code.contains(pickerSearchText)
                                    }.sortedBy { 
                                        !it.name.startsWith(pickerSearchText, ignoreCase = true)
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (matches.isEmpty()) {
                                    Text(
                                        text = "No countries matched search query.",
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
                                                    selectedCountry = country
                                                    region = country.region
                                                    showCountryPickerDialog = false
                                                }
                                                .background(
                                                    if (selectedCountry == country) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
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
                                                    text = "${country.region} Region (${country.code})",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            if (selectedCountry == country) {
                                                Icon(Icons.Default.Check, contentDescription = "Checked", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showCountryPickerDialog = false }) {
                            Text("CANCEL")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Secure Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val icon = if (passVisible) Icons.Default.Done else Icons.Default.PlayArrow
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle password")
                    }
                },
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_password_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "PASSWORD RECOVERY QUIZ CONFIGURATION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Security Question Dropdown
            Box {
                OutlinedTextField(
                    value = securityQ,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Recovery Security Challenge Question") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = "Expand") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { questionExpanded = true }
                        .testTag("reg_question_dropdown"),
                    shape = RoundedCornerShape(12.dp)
                )
                DropdownMenu(
                    expanded = questionExpanded,
                    onDismissRequest = { questionExpanded = false }
                ) {
                    recoveryQuestions.forEach { q ->
                        DropdownMenuItem(
                            text = { Text(q) },
                            onClick = {
                                securityQ = q
                                questionExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = securityA,
                onValueChange = { securityA = it },
                label = { Text("Your Secret Security Answer") },
                leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_answer_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Terms and User Agreement Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .clickable { termsAgreed = !termsAgreed }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Checkbox(
                    checked = termsAgreed,
                    onCheckedChange = { termsAgreed = it },
                    modifier = Modifier.testTag("reg_terms_checkbox")
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Agree to ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Terms & User Agreement",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { showTermsDialog = true }
                        )
                    }
                    Text(
                        text = "I confirm all information resides strictly under absolute user control (MSB SUDOKU CHALLENGE POWERED BY MSB CREATIVE STUDIOS).",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 13.sp
                    )
                }
            }

            if (showTermsDialog) {
                AlertDialog(
                    onDismissRequest = { showTermsDialog = false },
                    title = {
                        Text(
                            text = "Terms & User Agreement",
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            var activeDocTab by remember { mutableStateOf(0) }
                            
                            TabRow(
                                selectedTabIndex = activeDocTab,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(bottom = 12.dp)
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
                                    text = { Text("Agreement", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                                )
                            }
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                when (activeDocTab) {
                                    0 -> {
                                        Text(
                                            text = "TERMS & CONDITIONS",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "1. Acceptance of Terms\n" +
                                                   "By registering or logging in via Direct Google SSO, you agree to the regulatory policies of MSB SUDOKU CHALLENGE POWERED BY MSB CREATIVE STUDIOS.\n\n" +
                                                   "2. Authenticity & Fair Play\n" +
                                                   "Speed solving, tournament logs, offline highscores, and PlayGold Points (PGP) accumulated must be obtained without visual cheats or external automation engines to protect fair play.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    1 -> {
                                        Text(
                                            text = "PRIVACY POLICY",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "1. Data Isolation & Security\n" +
                                                   "All user details, password hashes, security challenge responses, and regional highscores reside inside localized SQLite registries. No remote user profiling is initiated.\n\n" +
                                                   "2. Direct Google SSO privacy\n" +
                                                   "One-tap Google SSO bypasses password entry securely. If Two-Factor secure OTP is selected, a administrative simulated SMS flow is processed to protect registrations from breach.\n\n" +
                                                   "3. Share Anchors\n" +
                                                   "Custom URLs for LinkedIn, Facebook, and Instagram are securely maintained to render achievement certificate overlays and share cards.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    2 -> {
                                        Text(
                                            text = "USER AGREEMENT",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "1. Reward Ownership\n" +
                                                   "Accumulated PlayGold Points (PGP) are redeemable for Google Play Gift Cards under absolute user ownership and control.\n\n" +
                                                   "2. Competitive Latency Agreement\n" +
                                                   "Multiplayer lobbies utilize fair ping-matching metrics. Competitive interactions (sabotage, swaps, time penalties) are rate-limited to maintain espost sportsmanship.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                
                                Text(
                                    text = "By checking the agreement box on the registration screen, you consent to these policies as backed by MSB Creative Studios.",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                termsAgreed = true
                                showTermsDialog = false
                            }
                        ) {
                            Text("AGREE & ACCEPT", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTermsDialog = false }) {
                            Text("DISMISS")
                        }
                    }
                )
            }

            if (countryCodeExpanded) {
                AlertDialog(
                    onDismissRequest = { countryCodeExpanded = false },
                    title = {
                        Text(
                            text = "Select Country Dial Code",
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = countrySearchQuery,
                                onValueChange = { countrySearchQuery = it },
                                placeholder = { Text("Search country name or code...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            
                            val filteredCountries = remember(countrySearchQuery) {
                                fullCountryPickerList.filter {
                                    it.name.contains(countrySearchQuery, ignoreCase = true) ||
                                    it.code.contains(countrySearchQuery)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (filteredCountries.isEmpty()) {
                                    Text(
                                        text = "No countries matched search.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                } else {
                                    filteredCountries.forEach { country ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    phoneCountryCode = country.code
                                                    countryCodeExpanded = false
                                                    countrySearchQuery = ""
                                                }
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = country.flag,
                                                fontSize = 18.sp,
                                                modifier = Modifier.padding(end = 12.dp)
                                            )
                                            Text(
                                                text = country.name,
                                                modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = country.code,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { countryCodeExpanded = false }) {
                            Text("CLOSE")
                        }
                    }
                )
            }

            registerError?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = err, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!termsAgreed) {
                        viewModel.registerError.value = "You must agree to the Terms & User Agreement under your absolute control."
                        return@Button
                    }
                    val finalId = if (isEmailMode) {
                        emailInput.trim().lowercase()
                    } else {
                        if (phoneDigits.isNotBlank()) "${phoneCountryCode}${phoneDigits.trim()}" else ""
                    }
                    viewModel.registerUser(
                        email = finalId,
                        username = username,
                        region = region,
                        securityQ = securityQ,
                        securityA = securityA,
                        passwordRaw = password,
                        countryName = selectedCountry.name,
                        countryFlag = selectedCountry.flag
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("register_submit_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SECURE & REGISTER PROFILE", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            GoogleSignInButton(
                onClick = { viewModel.signInWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ForgetPasswordStep1Screen(
    viewModel: SudokuViewModel,
    initialEmail: String,
    onBackClicked: () -> Unit
) {
    var email by remember { mutableStateOf(initialEmail) }
    val forgetPasswordError by viewModel.forgetPasswordError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
            }

            Text(
                text = "RECOVER ACCOUNT",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Step 1 of 2: Look up security questions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Registered Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recovery_email_input"),
                shape = RoundedCornerShape(12.dp)
            )

            forgetPasswordError?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = err, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.requestRecoveryQuestion(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("recovery_lookup_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("LOOK UP SECURITY QUESTION", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ForgetPasswordStep2Screen(
    viewModel: SudokuViewModel,
    email: String,
    question: String,
    onBackClicked: () -> Unit
) {
    var answer by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }

    val forgetPasswordError by viewModel.forgetPasswordError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
            }

            Text(
                text = "ANSWER CHALLENGE",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Step 2 of 2: Verify question and set new code",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your Recovery Security Question:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it },
                label = { Text("Your Secret Security Answer") },
                leadingIcon = { Icon(Icons.Default.Done, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recovery_answer_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Secure Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val icon = if (passVisible) Icons.Default.Done else Icons.Default.PlayArrow
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle password")
                    }
                },
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recovery_newpass_input"),
                shape = RoundedCornerShape(12.dp)
            )

            forgetPasswordError?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = err, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.verifyRecoveryAnswerAndReset(email, answer, newPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("recovery_submit_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("RESET PASSWORD & SIGN IN", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun ForgetPasswordSuccessScreen(email: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success tick icon",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PASSWORD LOG SECURED",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Security challenge solved successfully! Your profile credentials for $email have been reset. Directing you to global hub...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
        }
    }
}

@Composable
fun AdminOtpScreen(
    viewModel: SudokuViewModel,
    email: String,
    generatedOtp: String,
    onBackClicked: () -> Unit
) {
    var enteredOtp by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                modifier = Modifier
                    .size(80.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Shield Security",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ADMIN SECURE AUTH",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "OTP VERIFICATION PORTAL",
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "We have transmitted a highly confidential 6-digit administrative one-time passcode to $email. Enter it below to unlock the secure Play Store dashboard.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = enteredOtp,
                onValueChange = { if (it.length <= 6) enteredOtp = it },
                label = { Text("6-Digit Secret OTP") },
                placeholder = { Text("e.g. 123456") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_otp_input"),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Professional, highly visible sandbox proxy notice (meeting offline mock instruction)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🔒 SECURE SMS/EMAIL SIMULATION PROXY",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "In obedience with safe local verification guidelines, the simulated admin access code currently allocated to you is:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                    Text(
                        text = "ADMIN OTP CODE: $generatedOtp",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }

            loginError?.let { err ->
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = err, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.verifyAdminOtp(email, enteredOtp, generatedOtp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("admin_otp_submit_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("VERIFY & LOAD SESSION", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
