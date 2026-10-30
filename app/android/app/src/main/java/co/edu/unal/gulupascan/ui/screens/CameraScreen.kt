package co.edu.unal.gulupascan.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import co.edu.unal.gulupascan.R
import co.edu.unal.gulupascan.ui.components.GsTopBar
import co.edu.unal.gulupascan.ui.components.PrimaryButton
import co.edu.unal.gulupascan.ui.components.StepBadge
import co.edu.unal.gulupascan.ui.theme.Pulp
import java.io.File

@Composable
fun CameraScreen(onImage: (Uri) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission = it }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onImage(uri)
    }
    fun openGallery() = galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    var flashOn by remember { mutableStateOf(false) }
    var backCamera by remember { mutableStateOf(true) }
    var showTips by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }

    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }
    val imageCapture = remember { ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build() }

    LaunchedEffect(Unit) { if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA) }

    // (Re)vincula la cámara cuando cambia el lente o el permiso.
    DisposableEffect(hasPermission, backCamera) {
        var provider: ProcessCameraProvider? = null
        if (hasPermission) {
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener({
                provider = future.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                val selector = if (backCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
                runCatching {
                    provider?.unbindAll()
                    provider?.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
                }
            }, ContextCompat.getMainExecutor(context))
        }
        onDispose { runCatching { provider?.unbindAll() } }
    }
    LaunchedEffect(flashOn) {
        imageCapture.flashMode = if (flashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }

    fun takePhoto() {
        if (busy) return
        busy = true
        val file = File(context.cacheDir, "capture_${System.currentTimeMillis()}.jpg")
        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    busy = false
                    onImage(Uri.fromFile(file))
                }

                override fun onError(exception: ImageCaptureException) {
                    busy = false
                }
            },
        )
    }

    Column(Modifier.fillMaxSize().background(Color.Black)) {
        GsTopBar(stringResource(R.string.cam_title), onBack = null, actions = {
            IconButton(onClick = { showTips = true }) {
                Icon(Icons.AutoMirrored.Outlined.HelpOutline, stringResource(R.string.cam_tips_title))
            }
        })

        Box(Modifier.weight(1f).fillMaxWidth().background(Color(0xFFDADFE3)), contentAlignment = Alignment.Center) {
            if (hasPermission) {
                AndroidView({ previewView }, Modifier.fillMaxSize())
                FrameCorners(Modifier.fillMaxSize().padding(28.dp))
            } else {
                Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.PhotoCamera, null, Modifier.size(56.dp), tint = Color.DarkGray)
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.cam_permission), textAlign = TextAlign.Center, color = Color.DarkGray)
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(stringResource(R.string.cam_grant), { permissionLauncher.launch(Manifest.permission.CAMERA) })
                }
            }
        }

        // Barra inferior oscura del mockup
        Column(Modifier.fillMaxWidth().background(Color(0xFF111111)).padding(vertical = 14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.cam_photo), color = Pulp, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.size(40.dp, 1.dp))
                Text(
                    stringResource(R.string.cam_gallery), color = Color.White, fontSize = 14.sp,
                    modifier = Modifier.clickable { openGallery() },
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { flashOn = !flashOn }) {
                    Icon(if (flashOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff, stringResource(R.string.cam_flash), tint = Color.White)
                }
                Box(
                    Modifier.size(72.dp).border(4.dp, Color.White, CircleShape).padding(6.dp)
                        .background(if (busy) Color.Gray else Color.White, CircleShape)
                        .clickable(enabled = hasPermission && !busy) { takePhoto() },
                )
                IconButton(onClick = { backCamera = !backCamera }) {
                    Icon(Icons.Outlined.Cameraswitch, stringResource(R.string.cam_switch), tint = Color.White)
                }
            }
        }
    }

    if (showTips) {
        AlertDialog(
            onDismissRequest = { showTips = false },
            confirmButton = { TextButton(onClick = { showTips = false }) { Text(stringResource(R.string.ok)) } },
            title = { Text(stringResource(R.string.cam_tips_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(R.string.cam_tip1, R.string.cam_tip2, R.string.cam_tip3, R.string.cam_tip4).forEachIndexed { i, res ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StepBadge(i + 1, 24.dp)
                            Spacer(Modifier.size(10.dp))
                            Text(stringResource(res), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
        )
    }
}

/** Esquinas guía para centrar el fruto, como en el mockup. */
@Composable
private fun FrameCorners(modifier: Modifier) {
    Canvas(modifier) {
        val len = size.minDimension * 0.12f
        val sw = 3.dp.toPx()
        val c = Color.White
        val w = size.width
        val h = size.height
        fun l(a: Offset, b: Offset) = drawLine(c, a, b, sw, StrokeCap.Round)
        l(Offset(0f, 0f), Offset(len, 0f)); l(Offset(0f, 0f), Offset(0f, len))
        l(Offset(w, 0f), Offset(w - len, 0f)); l(Offset(w, 0f), Offset(w, len))
        l(Offset(0f, h), Offset(len, h)); l(Offset(0f, h), Offset(0f, h - len))
        l(Offset(w, h), Offset(w - len, h)); l(Offset(w, h), Offset(w, h - len))
    }
}
