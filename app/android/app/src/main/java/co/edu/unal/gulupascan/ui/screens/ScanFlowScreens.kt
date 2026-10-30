package co.edu.unal.gulupascan.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.edu.unal.gulupascan.R
import co.edu.unal.gulupascan.ScanState
import co.edu.unal.gulupascan.domain.Analysis
import co.edu.unal.gulupascan.domain.Classification
import co.edu.unal.gulupascan.domain.Damage
import co.edu.unal.gulupascan.domain.DamageCatalog
import co.edu.unal.gulupascan.domain.Severity
import co.edu.unal.gulupascan.ui.components.ColorDot
import co.edu.unal.gulupascan.ui.components.GsCard
import co.edu.unal.gulupascan.ui.components.GsTopBar
import co.edu.unal.gulupascan.ui.components.InfoNote
import co.edu.unal.gulupascan.ui.components.PathImage
import co.edu.unal.gulupascan.ui.components.PrimaryButton
import co.edu.unal.gulupascan.ui.components.SecondaryButton
import co.edu.unal.gulupascan.ui.components.SeverityScale
import co.edu.unal.gulupascan.ui.components.pct
import co.edu.unal.gulupascan.ui.components.shareContent
import co.edu.unal.gulupascan.ui.theme.DamageScab
import co.edu.unal.gulupascan.ui.theme.BrandDark
import co.edu.unal.gulupascan.ui.theme.BrandSoft
import co.edu.unal.gulupascan.ui.theme.GulupaPurple
import co.edu.unal.gulupascan.ui.theme.Healthy
import co.edu.unal.gulupascan.ui.theme.TextSoft
import co.edu.unal.gulupascan.ui.theme.WarnBg
import java.io.File
import java.util.Locale
import kotlinx.coroutines.delay

// ---------------------------------------------------------------------------
// Paso 2: Procesando imagen
// ---------------------------------------------------------------------------
@Composable
fun ProcessingScreen(
    state: ScanState,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onTryDemo: () -> Unit,
) {
    val preview = when (state) {
        is ScanState.Processing -> state.previewPath
        is ScanState.Failed -> state.previewPath
        else -> null
    }
    Scaffold(topBar = { GsTopBar(stringResource(R.string.proc_title), onBack = onCancel) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(16.dp))) {
                PathImage(preview, Modifier.fillMaxSize())
                if (state is ScanState.Processing) ScanLine(Modifier.fillMaxSize())
            }
            Spacer(Modifier.height(16.dp))
            when (state) {
                is ScanState.Failed -> FailedBox(state, onRetry, onTryDemo)
                else -> ProgressBox(onCancel)
            }
        }
    }
}

@Composable
private fun ProgressBox(onCancel: () -> Unit) {
    // Progreso simulado (el servidor no informa avance): se acerca a 95 % mientras espera.
    var progress by remember { mutableFloatStateOf(0.05f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(250)
            progress += (0.95f - progress) * 0.04f
        }
    }
    Column {
        Text(stringResource(R.string.proc_segmenting), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = Healthy,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(Modifier.width(10.dp))
            Text("${(progress * 100).toInt()} %", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(6.dp))
        Text(stringResource(R.string.proc_wait), style = MaterialTheme.typography.bodySmall, color = TextSoft)
        Spacer(Modifier.height(14.dp))
        SecondaryButton(stringResource(R.string.cancel), onCancel)
    }
}

@Composable
private fun FailedBox(state: ScanState.Failed, onRetry: () -> Unit, onTryDemo: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.ErrorOutline, null, tint = DamageScab)
            Spacer(Modifier.width(8.dp))
            Text(state.message, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(14.dp))
        PrimaryButton(stringResource(R.string.proc_retake), onRetry)
        if (state.canTryDemo) {
            Spacer(Modifier.height(8.dp))
            SecondaryButton(stringResource(R.string.proc_try_demo), onTryDemo, icon = Icons.Outlined.Science)
        }
    }
}

@Composable
private fun ScanLine(modifier: Modifier) {
    val t = rememberInfiniteTransition(label = "scan")
    val y by t.animateFloat(0f, 1f, infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse), label = "y")
    Canvas(modifier) {
        val yy = size.height * y
        drawRect(
            Brush.verticalGradient(listOf(Color.Transparent, Healthy.copy(alpha = 0.35f)), startY = yy - 120f, endY = yy),
            topLeft = Offset(0f, (yy - 120f).coerceAtLeast(0f)),
            size = androidx.compose.ui.geometry.Size(size.width, minOf(120f, yy)),
        )
        drawLine(Healthy, Offset(0f, yy), Offset(size.width, yy), strokeWidth = 4f)
    }
}

// ---------------------------------------------------------------------------
// Paso 3: Tipos de daño detectados
// ---------------------------------------------------------------------------
@Composable
fun DamageListScreen(a: Analysis, onBack: () -> Unit, onDamage: (String) -> Unit, onResult: () -> Unit) {
    Scaffold(topBar = { GsTopBar(stringResource(R.string.dl_title), onBack) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ModelChip(a)
                val list = a.harmfulDamages + a.damages.filter { it.key == DamageCatalog.HEALTHY }
                if (list.isEmpty()) InfoNote(stringResource(R.string.dl_none))
                list.forEach { d -> DamageRow(a, d) { onDamage(d.key) } }
            }
            PrimaryButton(stringResource(R.string.dl_full_result), onResult, Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun DamageRow(a: Analysis, d: Damage, onClick: () -> Unit) {
    val info = DamageCatalog.get(d.key)
    val healthy = d.key == DamageCatalog.HEALTHY
    GsCard(Modifier.clickable(onClick = onClick), color = if (healthy) BrandSoft else MaterialTheme.colorScheme.surface) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(72.dp).clip(RoundedCornerShape(10.dp)).border(2.dp, info.color, RoundedCornerShape(10.dp))) {
                PathImage(a.images.leaf ?: a.thumbnail, Modifier.fillMaxSize())
                if (!healthy) a.images.overlayFor(d.key)?.let { PathImage(it, Modifier.fillMaxSize().alpha(0.7f)) }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ColorDot(info.color)
                    Spacer(Modifier.width(6.dp))
                    Text(damageName(d.key), style = MaterialTheme.typography.titleSmall)
                }
                if (info.scientific != null) Text(info.scientific, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = TextSoft)
                if (d.areaCm2 != null) Text(stringResource(R.string.area_cm2, fmt(d.areaCm2)), style = MaterialTheme.typography.bodySmall)
                Text(stringResource(R.string.pct_of_area, pct(d.pct)), style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextSoft)
        }
    }
}

@Composable
private fun ModelChip(a: Analysis) {
    val text = if (a.isDemo) stringResource(R.string.chip_demo)
    else stringResource(
        R.string.chip_model,
        listOfNotNull(a.cropName, a.diseaseName).joinToString(" · ").ifBlank { a.modelClass.orEmpty() },
        (a.modelConfidence * 100).toInt(),
    )
    Surface(shape = RoundedCornerShape(50), color = if (a.isDemo) GulupaPurple.copy(alpha = 0.12f) else BrandSoft) {
        Text(text, Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.bodySmall,
            color = if (a.isDemo) GulupaPurple else BrandDark)
    }
}

// ---------------------------------------------------------------------------
// Paso 4: Detalle / severidad por daño
// ---------------------------------------------------------------------------
@Composable
fun DamageDetailScreen(a: Analysis, key: String, onBack: () -> Unit) {
    val d = a.damages.firstOrNull { it.key == key } ?: Damage(key, 0.0)
    val info = DamageCatalog.get(key)
    val healthy = key == DamageCatalog.HEALTHY
    var opacity by remember { mutableFloatStateOf(0.7f) }
    val level = if (healthy) 0 else Severity.level(d.pct)
    val overlay = if (healthy) null else a.images.overlayFor(key)

    Scaffold(topBar = { GsTopBar(stringResource(R.string.dd_title), onBack) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth().aspectRatio(1.3f).background(Color.Black)) {
                PathImage(a.images.leaf ?: a.images.crop ?: a.images.original, Modifier.fillMaxSize(), ContentScale.Fit)
                if (overlay != null) {
                    PathImage(overlay, Modifier.fillMaxSize().alpha(opacity), ContentScale.Fit)
                }
            }
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (overlay != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.dd_opacity), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.width(12.dp))
                        Slider(opacity, { opacity = it }, Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = BrandDark, activeTrackColor = Healthy))
                    }
                } else if (!healthy) {
                    InfoNote(stringResource(R.string.dd_no_mask))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ColorDot(info.color, 18.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(damageName(key), style = MaterialTheme.typography.titleLarge)
                    if (info.scientific != null) {
                        Spacer(Modifier.width(6.dp))
                        Text("(${info.scientific})", style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, color = TextSoft)
                    }
                }
                Text(stringResource(info.description), style = MaterialTheme.typography.bodyMedium, color = TextSoft)

                if (!healthy) {
                    GsCard {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(stringResource(R.string.dd_severity), style = MaterialTheme.typography.titleSmall)
                            SeverityScale(level)
                            Row {
                                Text(stringResource(R.string.dd_severity_label) + " ", style = MaterialTheme.typography.bodyMedium)
                                Text(stringResource(Severity.levelLabel(level)), style = MaterialTheme.typography.bodyMedium,
                                    color = Severity.levelColor(level), fontWeight = FontWeight.SemiBold)
                            }
                            val area = stringResource(R.string.pct_of_area, pct(d.pct)) +
                                (d.areaCm2?.let { " (${fmt(it)} cm²)" } ?: "")
                            Text(stringResource(R.string.dd_affected) + " " + area, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    Text(stringResource(R.string.pct_of_area, pct(d.pct)), style = MaterialTheme.typography.titleMedium, color = Healthy)
                }
                InfoNote(stringResource(R.string.dd_note))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Paso 5: Resultado final y recomendaciones
// ---------------------------------------------------------------------------
@Composable
fun ResultScreen(
    a: Analysis,
    onBack: () -> Unit,
    onDamage: (String) -> Unit,
    onSave: () -> Unit,
    onTech: () -> Unit,
    onNewScan: () -> Unit,
) {
    val context = LocalContext.current
    val cls = a.classification
    val shareText = buildShareText(a)

    Scaffold(topBar = { GsTopBar(stringResource(R.string.res_title), onBack) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.fillMaxWidth().aspectRatio(1.6f).clip(RoundedCornerShape(16.dp))) {
                PathImage(a.images.result ?: a.images.crop ?: a.images.original, Modifier.fillMaxSize())
            }
            ModelChip(a)

            // Clasificación general
            Surface(shape = RoundedCornerShape(14.dp), color = if (cls == Classification.HEALTHY) BrandSoft else WarnBg) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.Top) {
                    Box(Modifier.size(36.dp).clip(CircleShape).background(cls.color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.WarningAmber, null, tint = cls.color)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(stringResource(R.string.res_general), style = MaterialTheme.typography.bodySmall)
                        Text(stringResource(cls.title), style = MaterialTheme.typography.titleMedium, color = cls.color)
                        Spacer(Modifier.height(4.dp))
                        Text(stringResource(cls.advice), style = MaterialTheme.typography.bodySmall, color = TextSoft)
                    }
                }
            }

            Text(stringResource(R.string.res_summary), style = MaterialTheme.typography.titleSmall)
            GsCard {
                Column {
                    val rows = a.harmfulDamages + a.damages.filter { it.key == DamageCatalog.HEALTHY }
                    rows.forEachIndexed { i, d ->
                        Row(
                            Modifier.fillMaxWidth().clickable { onDamage(d.key) }.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ColorDot(DamageCatalog.get(d.key).color)
                            Spacer(Modifier.width(10.dp))
                            Text(damageName(d.key), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            Text(pct(d.pct), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextSoft)
                        }
                        if (i < rows.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            PrimaryButton(
                stringResource(if (a.saved) R.string.res_saved else R.string.res_save),
                onSave, enabled = !a.saved,
                icon = if (a.saved) Icons.Outlined.BookmarkAdded else Icons.Outlined.BookmarkBorder,
            )
            SecondaryButton(stringResource(R.string.res_share), {
                val img = (a.images.result ?: a.images.crop ?: a.images.original)?.let { File(it) }
                shareContent(context, shareText, img)
            }, icon = Icons.Outlined.Share)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onTech) { Text(stringResource(R.string.res_tech), color = BrandDark) }
                TextButton(onClick = onNewScan) { Text(stringResource(R.string.res_new), color = BrandDark) }
            }
        }
    }
}

@Composable
private fun buildShareText(a: Analysis): String {
    val sb = StringBuilder()
    sb.append(stringResource(R.string.share_header)).append('\n')
    sb.append(stringResource(R.string.res_general)).append(": ").append(stringResource(a.classification.title)).append('\n')
    (a.harmfulDamages + a.damages.filter { it.key == DamageCatalog.HEALTHY }).forEach {
        sb.append("• ").append(damageName(it.key)).append(": ").append(pct(it.pct)).append('\n')
    }
    sb.append(stringResource(a.classification.advice))
    if (a.isDemo) sb.append("\n(").append(stringResource(R.string.chip_demo)).append(')')
    return sb.toString()
}

// ---------------------------------------------------------------------------
// Detalles técnicos (lámina 1, paso 5 "Detalles")
// ---------------------------------------------------------------------------
@Composable
fun TechDetailsScreen(a: Analysis, onBack: () -> Unit) {
    val imgs = a.images
    val tiles = listOf(
        R.string.tech_original to imgs.original,
        R.string.tech_leaf to imgs.leaf,
        R.string.tech_mask to imgs.mask,
        R.string.tech_result to imgs.result,
        R.string.tech_hue to imgs.hue,
        R.string.tech_crop to imgs.crop,
    ).filter { it.second != null }

    Scaffold(topBar = { GsTopBar(stringResource(R.string.tech_title), onBack) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tiles.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    pair.forEach { (label, path) ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(label), style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(4.dp))
                            PathImage(path, Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(8.dp)).background(Color.Black), ContentScale.Fit)
                        }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            if (tiles.size <= 1) InfoNote(stringResource(R.string.tech_no_layers))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Legend(DamageScab, stringResource(R.string.tech_legend_damage))
                Legend(Healthy, stringResource(R.string.tech_legend_healthy))
            }

            if (imgs.panel != null) {
                Text(stringResource(R.string.tech_panel), style = MaterialTheme.typography.titleSmall)
                PathImage(imgs.panel, Modifier.fillMaxWidth().aspectRatio(10f / 6f).clip(RoundedCornerShape(8.dp)), ContentScale.Fit)
            }

            GsCard {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.tech_metrics), style = MaterialTheme.typography.titleSmall)
                    Metric(stringResource(R.string.tech_source), when {
                        a.isDemo -> stringResource(R.string.chip_demo)
                        a.modelClass?.startsWith("gulupa") == true -> "YOLO11-seg (gulupa)"
                        else -> "YOLO11 + Chan-Vese (PowderyVision7)"
                    })
                    a.modelClass?.let { Metric(stringResource(R.string.tech_class), it) }
                    if (!a.isDemo) {
                        Metric(stringResource(R.string.tech_conf), "${(a.modelConfidence * 100).toInt()} %")
                        if (a.leafConfidence > 0) Metric(stringResource(R.string.tech_leaf_conf), "${(a.leafConfidence * 100).toInt()} %")
                        Metric(stringResource(R.string.tech_time_cls), "${a.clsTimeMs.toInt()} ms")
                        Metric(stringResource(R.string.tech_time_seg), "${a.segTimeMs.toInt()} ms")
                    }
                    Metric(stringResource(R.string.tech_total), pct(a.totalDamagePct))
                }
            }
        }
    }
}

@Composable
private fun Legend(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(14.dp).border(2.dp, color, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = TextSoft)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End)
    }
}

// ---------------------------------------------------------------------------
@Composable
fun damageName(key: String): String {
    val info = DamageCatalog.get(key)
    return if (info.name == R.string.dmg_unknown) key.replace('_', ' ') else stringResource(info.name)
}

private fun fmt(v: Double) = String.format(Locale.getDefault(), "%.1f", v)

