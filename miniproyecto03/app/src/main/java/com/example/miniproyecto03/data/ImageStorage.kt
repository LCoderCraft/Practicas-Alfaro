package com.example.miniproyecto03.data

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object ImageStorage {

    fun guardarBitmap(context: Context, bitmap: Bitmap): String {
        val dir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
        val file = File(dir, "img_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file.absolutePath
    }

    fun guardarUri(context: Context, uri: android.net.Uri): String? {
        val dir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
        val file = File(dir, "img_${System.currentTimeMillis()}.jpg")
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { out -> input.copyTo(out) }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
