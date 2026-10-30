package co.edu.unal.gulupascan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Temas disponibles. El id se guarda en las preferencias. */
enum class AppTheme(val id: String) {
    GREEN("green"),     // mockup del Reto 2 (verde + crema)
    PURPLE("purple");   // presentación del Reto 2 (morado gulupa + lavanda + rosa)

    companion object {
        fun from(id: String?) = entries.firstOrNull { it.id == id } ?: GREEN
    }
}

/** Colores de marca que cambian con el tema. */
@Immutable
data class BrandColors(
    val dark: Color,        // splash, botones principales
    val main: Color,        // títulos, iconos
    val mid: Color,         // cabecera del perfil
    val soft: Color,        // chips, fondos suaves, indicador de pestaña
    val background: Color,  // fondo general
    val card: Color,        // tarjetas
    val border: Color,
    val surfaceVariant: Color,
    val text: Color,
    val textSoft: Color,
    val accent: Color,      // detalles decorativos (manchas del splash en tema morado)
    val decorated: Boolean, // dibuja las formas orgánicas de la presentación
)

// Paleta de los logos: ciruela #5D2741, verde #005828, amarillo #FEDC7A,
// morado #9034BD, hoja #00A85A, fondo claro #F7F7F7.
val GreenBrand = BrandColors(
    dark = Color(0xFF005828),
    main = Color(0xFF005828),
    mid = Color(0xFF0B7A3E),
    soft = Color(0xFFE1F2E7),
    background = Color(0xFFF7F7F7),
    card = Color(0xFFFFFFFF),
    border = Color(0xFFE4E2E3),
    surfaceVariant = Color(0xFFEFEDEE),
    text = Color(0xFF2A1A22),
    textSoft = Color(0xFF6A5E64),
    accent = Color(0xFF00A85A),
    decorated = false,
)

val PurpleBrand = BrandColors(
    dark = Color(0xFF9034BD),
    main = Color(0xFF5D2741),
    mid = Color(0xFF9034BD),
    soft = Color(0xFFF3E6F9),
    background = Color(0xFFF7F7F7),
    card = Color(0xFFFFFFFF),
    border = Color(0xFFEADBF1),
    surfaceVariant = Color(0xFFF2EAF5),
    text = Color(0xFF2A1A22),
    textSoft = Color(0xFF6E6178),
    accent = Color(0xFFD9B8EC),
    decorated = true,
)

val LocalBrand = staticCompositionLocalOf { GreenBrand }

// ---- Accesos rápidos usados por las pantallas (dependen del tema activo) ----
val BrandDark: Color @Composable @ReadOnlyComposable get() = LocalBrand.current.dark
val Brand: Color @Composable @ReadOnlyComposable get() = LocalBrand.current.main
val BrandMid: Color @Composable @ReadOnlyComposable get() = LocalBrand.current.mid
val BrandSoft: Color @Composable @ReadOnlyComposable get() = LocalBrand.current.soft
val AppBg: Color @Composable @ReadOnlyComposable get() = LocalBrand.current.background
val CardBg: Color @Composable @ReadOnlyComposable get() = LocalBrand.current.card
val BorderColor: Color @Composable @ReadOnlyComposable get() = LocalBrand.current.border
val TextMain: Color @Composable @ReadOnlyComposable get() = LocalBrand.current.text
val TextSoft: Color @Composable @ReadOnlyComposable get() = LocalBrand.current.textSoft

// ---- Colores fijos (iguales en ambos temas) ----
val Pulp = Color(0xFFFEDC7A)           // pulpa de la gulupa
val Seed = Color(0xFF1A1A1A)
val LeafGreen = Color(0xFF00A85A)
val WarnBg = Color(0xFFFFF4D6)
val WarnText = Color(0xFFD35400)
val GulupaPurple = Color(0xFF9034BD)
val Neutral = Color(0xFF7A7A7A)

// Colores por tipo de daño (leyendas del mockup): no cambian con el tema.
val DamageScab = Color(0xFFE53935)       // Roña
val DamageMechanical = Color(0xFF2F80ED) // Daño mecánico
val DamageThrips = Color(0xFFF2994A)     // Trips
val DamageMildew = Color(0xFF8E44AD)     // Mildiu polvoso (modelo actual)
val Healthy = Color(0xFF2E9E44)          // Tejido sano

private fun scheme(b: BrandColors) = lightColorScheme(
    primary = b.dark,
    onPrimary = Color.White,
    primaryContainer = b.soft,
    onPrimaryContainer = b.dark,
    secondary = b.main,
    onSecondary = Color.White,
    tertiary = b.accent,
    onTertiary = Color.White,
    background = b.background,
    onBackground = b.text,
    surface = b.card,
    onSurface = b.text,
    surfaceVariant = b.surfaceVariant,
    onSurfaceVariant = b.textSoft,
    outline = b.border,
    error = DamageScab,
)

private fun typography(b: BrandColors) = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, color = b.main),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, color = b.main),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
)

fun brandOf(theme: AppTheme) = if (theme == AppTheme.PURPLE) PurpleBrand else GreenBrand

@Composable
fun GulupaScanTheme(theme: AppTheme = AppTheme.GREEN, content: @Composable () -> Unit) {
    val brand = brandOf(theme)
    CompositionLocalProvider(LocalBrand provides brand) {
        MaterialTheme(colorScheme = scheme(brand), typography = typography(brand), content = content)
    }
}
