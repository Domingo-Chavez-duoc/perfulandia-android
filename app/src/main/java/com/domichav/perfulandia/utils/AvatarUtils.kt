package com.domichav.perfulandia.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun copyUriToInternalStorage(context: Context, uri: Uri, targetFileName: String): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        if (inputStream == null) return null

        val avatarsDir = File(context.filesDir, "avatars")
        if (!avatarsDir.exists()) avatarsDir.mkdirs()

        val targetFile = File(avatarsDir, targetFileName)
        // Overwrite if exists
        val outputStream: OutputStream = targetFile.outputStream()

        inputStream.use { input ->
            outputStream.use { out ->
                input.copyTo(out)
            }
        }

        targetFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
