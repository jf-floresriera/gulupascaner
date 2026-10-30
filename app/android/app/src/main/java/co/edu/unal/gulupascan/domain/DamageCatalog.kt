package co.edu.unal.gulupascan.domain

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import co.edu.unal.gulupascan.R
import co.edu.unal.gulupascan.ui.theme.DamageMechanical
import co.edu.unal.gulupascan.ui.theme.DamageMildew
import co.edu.unal.gulupascan.ui.theme.DamageScab
import co.edu.unal.gulupascan.ui.theme.DamageThrips
import co.edu.unal.gulupascan.ui.theme.Healthy
import co.edu.unal.gulupascan.ui.theme.Neutral

/**
 * Catálogo de tipos de daño. La API devuelve una "key"; aquí se traduce a nombre,
 * agente causal, descripción y color. Para agregar un daño nuevo del modelo de gulupa
 * basta con añadir una entrada aquí y sus textos en strings.xml.
 */
data class DamageInfo(
    val key: String,
    @StringRes val name: Int,
    val scientific: String?,
    @StringRes val description: Int,
    val color: Color,
)

object DamageCatalog {
    const val HEALTHY = "healthy"

    private val items = listOf(
        DamageInfo("scab", R.string.dmg_scab, "Cladosporium spp.", R.string.dmg_scab_desc, DamageScab),
        DamageInfo("mechanical", R.string.dmg_mechanical, null, R.string.dmg_mechanical_desc, DamageMechanical),
        DamageInfo("thrips", R.string.dmg_thrips, "Thysanoptera", R.string.dmg_thrips_desc, DamageThrips),
        DamageInfo("powdery_mildew", R.string.dmg_mildew, "Erysiphales", R.string.dmg_mildew_desc, DamageMildew),
        DamageInfo(HEALTHY, R.string.dmg_healthy, null, R.string.dmg_healthy_desc, Healthy),
    ).associateBy { it.key }

    fun get(key: String): DamageInfo =
        items[key] ?: DamageInfo(key, R.string.dmg_unknown, null, R.string.dmg_unknown_desc, Neutral)
}
