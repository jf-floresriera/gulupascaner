package co.edu.unal.gulupascan.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max

object ImageUtils {

    /**
     * Lee una imagen (cámara o galería), corrige la rotación EXIF, la reduce a [maxSide]
     * y la devuelve como JPEG. Así se envía menos información por la red.
     */
    fun loadAsJpeg(context: Context, uri: Uri, maxSide: Int = 1600, quality: Int = 90): ByteArray {
        val resolver = context.contentResolver

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Imagen inválida" }

        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2

        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        var bmp = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            ?: error("No se pudo decodificar la imagen")

        val rotation = resolver.openInputStream(uri)?.use {
            when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f

        val scale = maxSide.toFloat() / max(bmp.width, bmp.height)
        if (rotation != 0f || scale < 1f) {
            val m = Matrix().apply {
                if (scale < 1f) postScale(scale, scale)
                if (rotation != 0f) postRotate(rotation)
            }
            val rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
            if (rotated != bmp) bmp.recycle()
            bmp = rotated
        }

        return ByteArrayOutputStream().use { out ->
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, out)
            bmp.recycle()
            out.toByteArray()
        }
    }

    /** Guarda una imagen base64 de la API en [dir]/[name]. Devuelve la ruta o null. */
    fun saveBase64(dir: File, name: String, b64: String?): String? {
        if (b64.isNullOrBlank()) return null
        return runCatching {
            val bytes = Base64.decode(b64, Base64.DEFAULT)
            File(dir, name).apply { writeBytes(bytes) }.absolutePath
        }.getOrNull()
    }
}
