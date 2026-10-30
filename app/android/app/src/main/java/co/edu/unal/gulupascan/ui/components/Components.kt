package co.edu.unal.gulupascan.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import co.edu.unal.gulupascan.R
import co.edu.unal.gulupascan.domain.Severity
import co.edu.unal.gulupascan.ui.theme.CreamBorder
import co.edu.unal.gulupascan.ui.theme.Green
import co.edu.unal.gulupascan.ui.theme.GreenDark
import co.edu.unal.gulupascan.ui.theme.Pulp
import co.edu.unal.gulupascan.ui.theme.Seed
import co.edu.unal.gulupascan.ui.theme.TextSoft
import coil.compose.AsyncImage
import java.io.File
import kotlin.math.cos
import kotlin.math.sin

/** Logo del mockup: gulupa abierta con semillas, anillo verde, tallo y hoja. */
@Composable
fun GulupaLogo(modifier: Modifier = Modifier, size: Dp = 64.dp, ring: Color = Green, onDark: Boolean = false) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val c = Offset(w / 2f, w * 0.58f)
        val r = w * 0.38f
        // Tallo y hoja
        drawLine(ring, Offset(c.x, c.y - r), Offset(c.x + w * 0.04f, c.y - r - w * 0.12f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
        val leaf = Path().apply {
            val sx = c.x + w * 0.04f
            val sy = c.y - r - w * 0.12f
            moveTo(sx, sy)
            cubicTo(sx + w * 0.06f, sy - w * 0.12f, sx + w * 0.22f, sy - w * 0.12f, sx + w * 0.28f, sy - w * 0.08f)
            cubicTo(sx + w * 0.2f, sy + w * 0.02f, sx + w * 0.08f, sy + w * 0.04f, sx, sy)
            close()
        }
        drawPath(leaf, Color(0xFF4CAF50))
        // Fruto
        if (onDark) drawCircle(Color.White, r * 1.02f, c)
        drawCircle(Pulp, r * 0.8f, c)
        drawCircle(ring, r * 0.9f, c, style = Stroke(width = r * 0.2f))
        // Semillas en dos anillos
        val seedSize = Size(r * 0.13f, r * 0.2f)
        for (i in 0 until 8) {
            val ang = Math.toRadians(i * 45.0)
            val p = Offset(c.x + (r * 0.5f * cos(ang)).toFloat(), c.y + (r * 0.5f * sin(ang)).toFloat())
            rotate(i * 45f + 90f, p) {
                drawOval(Seed, Offset(p.x - seedSize.width / 2, p.y - seedSize.height / 2), seedSize)
            }
        }
        drawCircle(Seed, r * 0.08f, c)
    }
}

/** Encabezado de marca: logo + "GulupaScan" + subtítulo (como en todas las láminas del mockup). */
@Composable
fun BrandHeader(subtitle: String?, modifier: Modifier = Modifier, logoSize: Dp = 56.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        GulupaLogo(size = logoSize)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium, color = GreenDark)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSoft)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GsTopBar(title: String, onBack: (() -> Unit)?, actions: @Composable () -> Unit = {}) {
    CenterAlignedTopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            if (onBack != null) IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
    )
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, icon: ImageVector? = null) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
    ) {
        if (icon != null) {
            Icon(icon, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(26.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        if (icon != null) {
            Icon(icon, null, Modifier.size(20.dp), tint = GreenDark); Spacer(Modifier.width(8.dp))
        }
        Text(text, color = GreenDark, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun ColorDot(color: Color, size: Dp = 12.dp) {
    Box(Modifier.size(size).clip(CircleShape).background(color))
}

/** Número dentro de un círculo verde, como los pasos 1-5 del mockup. */
@Composable
fun StepBadge(number: Int, size: Dp = 28.dp) {
    Box(Modifier.size(size).clip(CircleShape).background(Green), contentAlignment = Alignment.Center) {
        Text("$number", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun InfoNote(text: String, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.Info, null, tint = GreenDark, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(text, style = MaterialTheme.typography.bodySmall, color = TextSoft)
        }
    }
}

@Composable
fun GsCard(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.surface, content: @Composable () -> Unit) {
    Surface(
        modifier.fillMaxWidth().border(1.dp, CreamBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = color,
    ) { content() }
}

/** Escala 1-10 de severidad con el nivel actual resaltado (pantalla "Detalle del daño"). */
@Composable
fun SeverityScale(level: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..10) {
            val active = i == level
            Box(
                Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(6.dp))
                    .background(if (active) Severity.levelColor(level) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "$i",
                    color = if (active) Color.White else TextSoft,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun PathImage(path: String?, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Crop, description: String? = null) {
    if (path == null) {
        Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant))
    } else {
        AsyncImage(model = File(path), contentDescription = description, modifier = modifier, contentScale = contentScale)
    }
}

/** Comparte texto y (opcionalmente) un archivo mediante el menú del sistema (WhatsApp, correo…). */
fun shareContent(context: Context, text: String, file: File?, mime: String = "image/*") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        if (file != null && file.exists()) {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
            putExtra(Intent.EXTRA_STREAM, uri)
            type = mime
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            type = "text/plain"
        }
    }
    context.startActivity(Intent.createChooser(intent, null))
}

fun pct(v: Double): String = String.format(java.util.Locale.getDefault(), "%.1f %%", v)
