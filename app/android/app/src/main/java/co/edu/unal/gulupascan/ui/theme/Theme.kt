package co.edu.unal.gulupascan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ---- Paleta tomada del mockup (Reto 2) ----
val GreenDark = Color(0xFF0E4421)      // splash, botones principales
val Green = Color(0xFF1B5E32)          // títulos, iconos
val GreenMid = Color(0xFF3E8E4A)       // cabecera de perfil
val GreenLight = Color(0xFFE3F0E3)     // fondos suaves
val Cream = Color(0xFFF8F5EA)          // fondo general
val CreamCard = Color(0xFFFFFDF6)      // tarjetas
val CreamBorder = Color(0xFFE7E1CF)
val Pulp = Color(0xFFF6D365)           // pulpa de la gulupa
val Seed = Color(0xFF1A1A1A)
val TextMain = Color(0xFF1F2A22)
val TextSoft = Color(0xFF5C665E)
val WarnBg = Color(0xFFFFF4D6)
val WarnText = Color(0xFFD35400)
val GulupaPurple = Color(0xFF5B3A7A)   // morado de la presentación

// Colores por tipo de daño (leyendas del mockup)
val DamageScab = Color(0xFFE53935)       // Roña
val DamageMechanical = Color(0xFF2F80ED) // Daño mecánico
val DamageThrips = Color(0xFFF2994A)     // Trips
val DamageMildew = Color(0xFF8E44AD)     // Mildiu polvoso (modelo actual)
val Healthy = Color(0xFF2E9E44)          // Tejido sano

private val colors = lightColorScheme(
    primary = GreenDark,
    onPrimary = Color.White,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
    secondary = Green,
    onSecondary = Color.White,
    tertiary = GulupaPurple,
    onTertiary = Color.White,
    background = Cream,
    onBackground = TextMain,
    surface = CreamCard,
    onSurface = TextMain,
    surfaceVariant = Color(0xFFF1EDE0),
    onSurfaceVariant = TextSoft,
    outline = CreamBorder,
    error = DamageScab,
)

private val typography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, color = Green),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, color = Green),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
)

@Composable
fun GulupaScanTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, typography = typography, content = content)
}
