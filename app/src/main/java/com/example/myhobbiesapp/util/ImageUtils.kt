    package com.example.myhobbiesapp.util

    import android.content.Context
    import android.graphics.Bitmap
    import android.graphics.ImageDecoder
    import android.net.Uri
    import android.os.Build
    import android.provider.MediaStore
    import android.util.Base64
    import java.io.ByteArrayOutputStream

    object ImageUtils {
        fun makeBase64Thumbnail(context: Context, uri: Uri, maxSize: Int = 200): String? {
            return try {
                val src = if (Build.VERSION.SDK_INT >= 28) {
                    val s = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(s)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                val ratio = (maxOf(src.width, src.height).toFloat() / maxSize.toFloat()).coerceAtLeast(1f)
                val w = (src.width / ratio).toInt().coerceAtLeast(1)
                val h = (src.height / ratio).toInt().coerceAtLeast(1)
                val bmp = Bitmap.createScaledBitmap(src, w, h, true)
                val baos = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos)
                Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
            } catch (_: Exception) { null }
        }
    }
