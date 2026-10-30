package co.edu.unal.gulupascan.domain

import kotlinx.serialization.Serializable

/** Resultado de un análisis, tal como se guarda en el historial (analysis.json). */
@Serializable
data class Analysis(
    val id: String,
    val createdAt: Long,
    /** "server" (pipeline YOLO11 + Chan-Vese) o "demo" (datos de ejemplo del mockup). */
    val source: String,
    val modelClass: String? = null,
    val modelConfidence: Double = 0.0,
    val leafConfidence: Double = 0.0,
    val cropName: String? = null,
    val diseaseName: String? = null,
    val isHealthy: Boolean = false,
    val damages: List<Damage> = emptyList(),
    val images: AnalysisImages = AnalysisImages(),
    val clsTimeMs: Double = 0.0,
    val segTimeMs: Double = 0.0,
    val saved: Boolean = false,
) {
    val isDemo: Boolean get() = source == SOURCE_DEMO

    /** Daños reales (sin el tejido sano), ordenados de mayor a menor. */
    val harmfulDamages: List<Damage>
        get() = damages.filter { it.key != DamageCatalog.HEALTHY }.sortedByDescending { it.pct }

    val totalDamagePct: Double get() = harmfulDamages.sumOf { it.pct }.coerceIn(0.0, 100.0)

    val healthyPct: Double
        get() = damages.firstOrNull { it.key == DamageCatalog.HEALTHY }?.pct ?: (100.0 - totalDamagePct)

    val classification: Classification get() = Severity.classify(this)

    /** Imagen principal para miniaturas. */
    val thumbnail: String? get() = images.result ?: images.crop ?: images.original

    companion object {
        const val SOURCE_SERVER = "server"
        const val SOURCE_DEMO = "demo"
    }
}

@Serializable
data class Damage(
    val key: String,
    /** Porcentaje del área del fruto/hoja (0-100). */
    val pct: Double,
    /** Área estimada en cm² (solo si se conoce la escala). */
    val areaCm2: Double? = null,
)

/** Rutas absolutas de las imágenes guardadas en el almacenamiento interno. */
@Serializable
data class AnalysisImages(
    val original: String? = null,
    val crop: String? = null,
    val leaf: String? = null,      // foto sin fondo
    val mask: String? = null,      // máscara binaria de lesión
    val overlay: String? = null,   // máscara coloreada transparente
    val result: String? = null,    // resultado final con contornos
    val hue: String? = null,       // canal H (HSV)
    val panel: String? = null,     // panel 2x3 del pipeline original
)
