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

val GreenBrand = BrandColors(
    dark = Color(0xFF0E4421),
    main = Color(0xFF1B5E32),
    mid = Color(0xFF3E8E4A),
    soft = Color(0xFFE3F0E3),
    background = Color(0xFFF8F5EA),
    card = Color(0xFFFFFDF6),
    border = Color(0xFFE7E1CF),
    surfaceVariant = Color(0xFFF1EDE0),
    text = Color(0xFF1F2A22),
    textSoft = Color(0xFF5C665E),
    accent = Color(0xFF4CAF50),
    decorated = false,
)

val PurpleBrand = BrandColors(
    dark = Color(0xFF5B3A7A),       // fondo de la lámina "¡Gracias!"
    main = Color(0xFF6A4391),
    mid = Color(0xFF8C6BC4),
    soft = Color(0xFFEDE3F8),
    background = Color(0xFFFDF1FB), // rosado claro de las formas orgánicas
    card = Color(0xFFFFFBFE),
    border = Color(0xFFEBD9F0),
    surfaceVariant = Color(0xFFF4E9F6),
    text = Color(0xFF2B1F36),
    textSoft = Color(0xFF6E6178),
    accent = Color(0xFFA78BDA),     // lavanda del título "¡GRACIAS!"
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
val Pulp = Color(0xFFF6D365)           // pulpa de la gulupa
val Seed = Color(0xFF1A1A1A)
val LeafGreen = Color(0xFF4CAF50)
val WarnBg = Color(0xFFFFF4D6)
val WarnText = Color(0xFFD35400)
val GulupaPurple = Color(0xFF5B3A7A)
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
