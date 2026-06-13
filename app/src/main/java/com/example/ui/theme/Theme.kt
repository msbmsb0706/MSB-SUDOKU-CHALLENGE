package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val MatrixCyberpunkScheme = darkColorScheme(
    primary = Color(0xFFFFC107),       // Glowing Gold
    secondary = Color(0xFF00BCD4),     // Cyan Telemetry
    tertiary = Color(0xFFE91E63),      // Pink Neon Accent
    background = Color(0xFF0B0B0F),    // Deep Space Black
    surface = Color(0xFF15161E),       // Grid slate card
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFA0A5C0)
)

private val SpaceAmoledScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),       // Soft Purple
    secondary = Color(0xFF03DAC6),     // Muted Electric Teal
    tertiary = Color(0xFFCF6679),      // Rose Gold
    background = Color(0xFF000000),    // Absolute Pure Black
    surface = Color(0xFF0C0C0C),       // Dark Charcoal Card
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val CreativeLightScheme = lightColorScheme(
    primary = Color(0xFF3F51B5),       // Royal Indigo
    secondary = Color(0xFFFF9800),     // Vivid Tangerine
    tertiary = Color(0xFF009688),      // Cool Sea foam
    background = Color(0xFFF4F5F9),    // Pearl White
    surface = Color(0xFFFFFFFF),       // Clear white card
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1E1E24),
    onSurface = Color(0xFF1E1E24),
    onSurfaceVariant = Color(0xFF6A6A75)
)

private val VintageBronzeScheme = darkColorScheme(
    primary = Color(0xFFCD7F32),       // Bronze Coin
    secondary = Color(0xFFFFD700),     // Bright Gold
    tertiary = Color(0xFF8B4513),      // Roasted wood
    background = Color(0xFF160E08),    // Dark wood background
    surface = Color(0xFF261A10),       // Wood panel card
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFFAEDE3),
    onSurface = Color(0xFFF5E4D7)
)

private val HighContrastPaperScheme = lightColorScheme(
    primary = Color(0xFF0056C6),       // Royal High-Visibility Blue
    secondary = Color(0xFF2E7D32),     // Crisp Green Focus
    tertiary = Color(0xFFC62828),      // Accent alert Red
    background = Color(0xFFFFFFFF),    // Ultra Pure White
    surface = Color(0xFFF2F4F7),       // Soft Light Grey card
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF000000),  // Deep crisp charcoal black
    onSurface = Color(0xFF000000),
    onSurfaceVariant = Color(0xFF2C3240)
)

private val EmeraldEyeShieldScheme = darkColorScheme(
    primary = Color(0xFF00FF87),       // Vibrant Mint/Emerald
    secondary = Color(0xFF64FFDA),     // Pale Neon Cyan
    tertiary = Color(0xFFFF5252),      // Bright Coral Red
    background = Color(0xFF0A120E),    // Deep Spruce Black - extremely eye-friendly
    surface = Color(0xFF13221C),       // Dark Spruce Green Card
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE8F5E9),  // Pale mint-white
    onSurface = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFFA5D6A7)
)

private val StandardDarkScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val StandardLightScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun MyApplicationTheme(
    selectedTheme: String = "Matrix Cyberpunk",
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (selectedTheme) {
        "Matrix Cyberpunk" -> MatrixCyberpunkScheme
        "Space AMOLED" -> SpaceAmoledScheme
        "Creative Light" -> CreativeLightScheme
        "Vintage Roasted Bronze" -> VintageBronzeScheme
        "High Contrast Paper" -> HighContrastPaperScheme
        "Emerald Eye-Shield" -> EmeraldEyeShieldScheme
        "System Theme" -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) StandardDarkScheme else StandardLightScheme
            }
        }
        else -> MatrixCyberpunkScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
