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
import androidx.compose.ui.platform.LocalContext
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
    val showGoogleOAuthDialog by viewModel.showGoogleOAuthDialog.collectAsStateWithLifecycle()
    val activeNotification by viewModel.activeNotification.collectAsStateWithLifecycle()

    if (showGoogleOAuthDialog) {
        OAuthHandshakeDialog(
            platform = "Google",
            suggestedHandle = "msbmsb0706@gmail.com",
            onDismiss = { viewModel.showGoogleOAuthDialog.value = false },
            onSuccess = { email ->
                viewModel.showGoogleOAuthDialog.value = false
                viewModel.finalizeGoogleLogin(email)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = authState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "auth_anim",
            modifier = Modifier.fillMaxSize()
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
                        onBackClicked = { viewModel.authState.value = AuthState.Welcome }
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
                is AuthState.ForgetPasswordOtpVerification -> {
                    ForgetPasswordOtpScreen(
                        viewModel = viewModel,
                        email = state.email,
                        generatedOtp = state.generatedOtp,
                        onBackClicked = { viewModel.authState.value = AuthState.ForgetPasswordStep1(state.email) }
                    )
                }
                is AuthState.ForgetPasswordReset -> {
                    ForgetPasswordResetScreen(
                        viewModel = viewModel,
                        email = state.email,
                        onBackClicked = { viewModel.authState.value = AuthState.ForgetPasswordStep1(state.email) }
                    )
                }
                is AuthState.ForgetPasswordSuccess -> {
                    ForgetPasswordSuccessScreen(email = state.email)
                }
            }
        }

        // Beautiful floating native-style simulated notification alert banner at the top
        activeNotification?.let { notif ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Automatically bypass security and head directly to password reset destination!
                            viewModel.authState.value = AuthState.ForgetPasswordReset(notif.account)
                            viewModel.activeNotification.value = null
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = notif.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = notif.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "👉 TAP HERE TO INSTANTLY VERIFY & RESET PASSWORD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.activeNotification.value = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss Notification",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
                text = "Register a secure global player profile to earn real-time PlayGold Points (PGP), unlock AI strategy reports, climb region leaderboards and claim certified cognitive achievements seamlessly.",
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
                text = "Continue on Guest Session instead (Strict 1-Game Trial, Highly Restricted)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                modifier = Modifier
                    .clickable { viewModel.signInAsGuest() }
                    .padding(8.dp)
                    .testTag("guest_bypass_btn")
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

    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()
    var showBiometricAuthDialog by remember { mutableStateOf(false) }
    var biometricValidatedEmail by remember { mutableStateOf("") }

    if (showBiometricAuthDialog && biometricValidatedEmail.isNotEmpty()) {
        BiometricSimulatedAuthDialog(
            email = biometricValidatedEmail,
            onDismiss = { showBiometricAuthDialog = false },
            onSuccess = { emailStr ->
                showBiometricAuthDialog = false
                viewModel.onBiometricSuccess(emailStr)
            }
        )
    }

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

            if (isBiometricEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.loginWithBiometrics(
                            onBiometricsValidated = { validatedEmail ->
                                biometricValidatedEmail = validatedEmail
                                showBiometricAuthDialog = true
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("login_biometric_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Face, contentDescription = "Biometric Lock")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIGN IN WITH BIOMETRICS", fontWeight = FontWeight.Bold)
                }
            }

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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Continue on Guest Session instead (Strict 1-Game Trial, Highly Restricted)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.signInAsGuest() }
                    .padding(8.dp)
                    .testTag("login_guest_bypass_btn")
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AuthRegisterScreen(
    viewModel: SudokuViewModel,
    onBackClicked: () -> Unit
) {
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
    var certPassword by remember { mutableStateOf("") }
    var certPassVisible by remember { mutableStateOf(false) }
    var termsAgreed by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var registerWithBiometric by remember { mutableStateOf(true) }
    
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

            // Email Field
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

            Spacer(modifier = Modifier.height(10.dp))

            // Secured Phone Section with Country Code dropdown selector
            Text(
                text = "PHONE SECTION WITH COUNTRY DIAL CODE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            // Choose Secure Certificate Password field
            OutlinedTextField(
                value = certPassword,
                onValueChange = { certPassword = it },
                label = { Text("Choose Secure Certificate Password") },
                placeholder = { Text("Required to unlock/generate certificates") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD4AF37)) },
                trailingIcon = {
                    val icon = if (certPassVisible) Icons.Default.Done else Icons.Default.PlayArrow
                    IconButton(onClick = { certPassVisible = !certPassVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle certificate password")
                    }
                },
                singleLine = true,
                visualTransformation = if (certPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_cert_password_input"),
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
                                                   "Speed solving, tournament logs, offline highscores, and PlayGold Points (PGP) accumulated must be obtained without visual cheats or external automation engines to protect fair play.\n\n" +
                                                   "3. Sandbox Social Integrations\n" +
                                                   "Integrations with third-party social networks (LinkedIn, Facebook, Instagram) use official sandboxed OAuth 2.0 pathways. Users agree to execute authorizations inside secure sandboxed frames.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    1 -> {
                                        Text(
                                            text = "PRIVACY POLICY Statement",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Privacy Policy for MSB CREATIVE STUDIOS\n" +
                                                   "Last Updated: 2026 onwards\n\n" +
                                                   "Welcome to MSB CREATIVE STUDIOS (\"we,\" \"our,\" or \"us\"). We are committed to protecting your personal information and your right to privacy. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you visit or use our mobile applications and services.\n\n" +
                                                   "Please read this privacy policy carefully. If you do not agree with the terms of this privacy policy, please do not access our applications.\n\n" +
                                                   "1. Information We Collect\n" +
                                                   "We may collect information about you in a variety of ways depending on how you interact with our applications:\n" +
                                                   "• Personal Data: We do not automatically collect personally identifiable information (such as your name or phone number) unless you voluntarily provide it to us (e.g., when contacting customer support).\n" +
                                                   "• Derivative Data & Device Information: Our servers or third-party tools may automatically collect information when you access our apps, such as your mobile device ID, model, manufacturer, operating system version, and basic usage statistics.\n" +
                                                   "• Financial Data: For any in-app purchases or app sales, all transactions are processed securely by the Google Play Store payment systems. We do not store or collect your credit card or bank account details.\n\n" +
                                                   "2. How We Use Your Information\n" +
                                                   "We use the information collected to:\n" +
                                                   "• Operate, maintain, and improve our mobile applications.\n" +
                                                   "• Understand user trends to design better app features.\n" +
                                                   "• Respond to customer support requests or inquiries.\n" +
                                                   "• Deliver relevant updates, notifications, or in-app announcements.\n\n" +
                                                   "3. Disclosure of Your Information\n" +
                                                   "We do not sell, trade, or rent your personal information to third parties. We may share information with third-party service providers (like analytics or advertising networks) that perform services for us, provided they adhere to strict confidentiality agreements. We may also disclose information if required to do so by law or to protect our legal rights.\n\n" +
                                                   "4. Third-Party Services & Analytics\n" +
                                                   "Our apps may utilize third-party SDKs or libraries (such as Google Analytics for Firebase or Google AdMob) to optimize app performance and display advertisements. These third-party services have their own independent privacy policies governing data tracking.\n\n" +
                                                   "5. Data Security\n" +
                                                   "We implement reasonable administrative, technical, and physical security measures to protect your personal information. However, please be aware that no electronic transmission over the internet or data storage technology can be guaranteed 100% secure.\n\n" +
                                                   "6. Children's Privacy\n" +
                                                   "Our applications are designed to comply with global privacy standards, including the Children's Online Privacy Protection Act (COPPA). If our app collects any data from children under the age of 13, it is done solely for internal operations (such as game saves or basic analytics) and is never shared with third parties for profiling.\n\n" +
                                                   "7. Changes to This Privacy Policy\n" +
                                                   "We reserve the right to make changes to this Privacy Policy at any time. We will notify you of any changes by updating the \"Last Updated\" date at the top of this policy.\n\n" +
                                                   "8. Contact Us\n" +
                                                   "If you have questions or comments about this Privacy Policy, please contact us at:\n" +
                                                   "Business Entity: MSB CREATIVE STUDIOS\n" +
                                                   "Email: msbcreativestudios@gmail.com",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    2 -> {
                                        Text(
                                            text = "USER AGREEMENT & DATA POLICY",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "1. Skill Standing & Honors\n" +
                                                   "Accumulated PlayGold Points (PGP) represent mathematical resolution stats, verified solving accuracy and highscores under absolute user ownership and control as elite cognitive markers.\n\n" +
                                                   "2. Competitive Latency Agreement\n" +
                                                   "Multiplayer lobbies utilize fair ping-matching metrics. Competitive interactions (sabotage, swaps, time penalties) are rate-limited to maintain espost sportsmanship.\n\n" +
                                                   "3. Social Credentials Revocation\n" +
                                                   "In accordance with updated user policy directives of LinkedIn, Facebook, and Instagram, users have the absolute right to unlink, clear, or revoke consent for any linked social profile handle instantly through the system settings panel.",
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
                                viewModel.signInAsGuest()
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                    .clickable { registerWithBiometric = !registerWithBiometric }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Biometric Lock",
                        tint = if (registerWithBiometric) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Enable Biometric / Face Lock",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Authenticate securely with device credentials upon setup",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = registerWithBiometric,
                    onCheckedChange = { registerWithBiometric = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                    if (emailInput.isBlank()) {
                        viewModel.registerError.value = "Email address is required for registration ID."
                        return@Button
                    }
                    if (phoneDigits.isBlank()) {
                        viewModel.registerError.value = "Phone number digits are required to complete phone section."
                        return@Button
                    }
                    if (certPassword.isBlank()) {
                        viewModel.registerError.value = "Please choose a secure Certificate Password."
                        return@Button
                    }
                    val finalId = emailInput.trim().lowercase()
                    val fullPhoneNo = "${phoneCountryCode} ${phoneDigits.trim()}"
                    viewModel.setBiometricEnabled(registerWithBiometric)
                    viewModel.registerUser(
                        email = finalId,
                        username = username,
                        region = region,
                        securityQ = securityQ,
                        securityA = securityA,
                        passwordRaw = password,
                        countryName = selectedCountry.name,
                        countryFlag = selectedCountry.flag,
                        phoneNumber = fullPhoneNo,
                        certificatePassword = certPassword
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Continue on Guest Session instead (Strict 1-Game Trial, Highly Restricted)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.signInAsGuest() }
                    .padding(8.dp)
                    .testTag("register_guest_bypass_btn")
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

    // Dynamically show the correct icon based on user input
    val inputIcon = remember(email) {
        val trimmed = email.trim()
        when {
            trimmed.isEmpty() -> Icons.Default.Email
            trimmed.all { it.isDigit() || it == '+' || it == '-' || it == ' ' } -> Icons.Default.Phone
            trimmed.contains("gmail", ignoreCase = true) || trimmed.contains("google", ignoreCase = true) -> Icons.Default.Email
            else -> Icons.Default.Email
        }
    }

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
                text = "Reset password via Email, Google Account, or Mobile",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address or Mobile Number") },
                placeholder = { Text("example@gmail.com or +123456789") },
                leadingIcon = { Icon(inputIcon, contentDescription = null) },
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

            // Method 1: Look up security question
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

            Spacer(modifier = Modifier.height(12.dp))

            // Method 2: Send Recovery OTP
            Button(
                onClick = { viewModel.requestRecoveryOtp(email) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("recovery_otp_request_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SEND RECOVERY OTP CODE", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Method 3: Send Change Password Link
            Button(
                onClick = { viewModel.requestRecoveryLink(email) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("recovery_link_request_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SEND PASSWORD RESET LINK", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("recovery_back_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("BACK TO SIGN IN", fontWeight = FontWeight.Bold)
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
                    if (question == "What is your favorite game?") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "(Tip: The default answer is 'Sudoku')",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("recovery_step2_back_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("BACK", fontWeight = FontWeight.Bold)
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
                text = "We have transmitted a highly secure 6-digit verification code to $email. Enter it below to unlock the MSB Sudoku Arena Hub.",
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
                        text = "🔒 SECURE VERIFICATION PROXY",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "In obedience with safe local verification guidelines, the simulated routing access code currently allocated to you is:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                    Text(
                        text = "SUDOKU ACCESS CODE: $generatedOtp",
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

@Composable
fun ForgetPasswordOtpScreen(
    viewModel: SudokuViewModel,
    email: String,
    generatedOtp: String,
    onBackClicked: () -> Unit
) {
    var enteredOtp by remember { mutableStateOf("") }
    val forgetPasswordError by viewModel.forgetPasswordError.collectAsStateWithLifecycle()

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
                        imageVector = Icons.Default.Email,
                        contentDescription = "Mail OTP",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "RECOVERY OTP AUTH",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "PASSWORD RESET CODE",
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "We have simulated transmitting a safe recovery OTP to $email. Use it below to perform a password reset.",
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
                label = { Text("6-Digit Recovery OTP") },
                placeholder = { Text("e.g. 123456") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recovery_otp_input"),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🔒 SIMULATED SECURE EMAIL DEVIATION",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "To allow testing in offline evaluation setups, the generated authorization code for this profile is:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                    Text(
                        text = "PASSWORD RESET CODE: $generatedOtp",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }

            forgetPasswordError?.let { err ->
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
                onClick = { viewModel.verifyRecoveryOtp(email, enteredOtp, generatedOtp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("recovery_otp_submit_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("VERIFY & RESET PASSWORD", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun ForgetPasswordResetScreen(
    viewModel: SudokuViewModel,
    email: String,
    onBackClicked: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
            }

            Text(
                text = "SET NEW PASSWORD",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Authentication via OTP passed. Secure your login with a new access key.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
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
                    .testTag("recovery_direct_newpass_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recovery_direct_confirm_input"),
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
                onClick = {
                    if (newPassword.isBlank()) {
                        viewModel.forgetPasswordError.value = "Please enter a valid non-empty password."
                        return@Button
                    }
                    if (newPassword != confirmPassword) {
                        viewModel.forgetPasswordError.value = "Passwords do not match."
                        return@Button
                    }
                    viewModel.resetPasswordDirectly(email, newPassword)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("recovery_direct_reset_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("RESET PASSWORD & LOG IN", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BiometricSimulatedAuthDialog(
    email: String,
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) {
        var currentContext = context
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is androidx.fragment.app.FragmentActivity) {
                break
            }
            currentContext = currentContext.baseContext
        }
        currentContext as? androidx.fragment.app.FragmentActivity
    }

    val biometricManager = remember(context) { androidx.biometric.BiometricManager.from(context) }
    val canAuthenticate = remember(biometricManager) {
        biometricManager.canAuthenticate(
            androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or 
            androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
    }

    LaunchedEffect(activity) {
        if (activity != null && canAuthenticate) {
            try {
                val executor = androidx.core.content.ContextCompat.getMainExecutor(activity)
                val biometricPrompt = androidx.biometric.BiometricPrompt(
                    activity,
                    executor,
                    object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            // User cancelled or error, fall back to allow using the visual touch scanner
                        }

                        override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            onSuccess(email)
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                        }
                    }
                )

                val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                    .setTitle("MSB Sudoku Verification")
                    .setSubtitle("Confirm biological credentials to authenticate as $email")
                    .setAllowedAuthenticators(
                        androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                        androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    .build()

                biometricPrompt.authenticate(promptInfo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var isScanning by remember { mutableStateOf(false) }
    var scanComplete by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            progress = 0f
            while (progress < 1f) {
                kotlinx.coroutines.delay(60)
                progress += 0.05f
            }
            scanComplete = true
            isScanning = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "BIOMETRIC SYSTEM LOG",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Requesting device-level face unlock or fingerprint sensor clearance for account authorization ($email).",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
                        .border(
                            2.dp,
                            if (scanComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            RoundedCornerShape(24.dp)
                        )
                        .clickable(enabled = !isScanning && !scanComplete) {
                            isScanning = true
                        }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (scanComplete) Icons.Default.Done else Icons.Default.Face,
                            contentDescription = "Sensor Touch",
                            tint = if (scanComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (scanComplete) "CLEARED" else if (isScanning) "SCANNING..." else "START SCAN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (scanComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isScanning) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "COMPLIANCE & CAPABILITY DETAILS:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "• Hardware target status: EMULATION CONTAINER\n" +
                                   "• Device keys backup format: SHARED SIGNATURE KEY\n" +
                                   "• Sandbox capability level: COMPLIANT ACTIVE",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (scanComplete) {
                Button(
                    onClick = { onSuccess(email) }
                ) {
                    Text("PROCEED LOG IN", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        isScanning = false
                        scanComplete = true
                    }
                ) {
                    Text("SIMULATE SCAN", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
