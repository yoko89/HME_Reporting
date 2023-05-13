package com.neklaway.hme_reporting.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import java.io.*

private const val TAG = "Copy File"

fun copyFiles(source: InputStream, dest: File) {
    try {
        val out: OutputStream
        if (dest.exists()) {
            dest.delete()
        }

        dest.createNewFile()
        out = FileOutputStream(dest)
        val buf = ByteArray(1024)
        var len: Int
        while (source.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        source.close()
        out.close()

    } catch (e: IOException) {
        e.printStackTrace()
        Log.d(TAG, "copyFiles: " + e.message)
    }
}

fun copyFiles(context: Context, sourceUri: Uri, destUri:Uri){
    try {
        val out: OutputStream
        val dest = destUri.toFile()
        if (dest.exists()) {
            dest.delete()
        }

        val source = context.contentResolver.openInputStream(sourceUri) ?: return

        dest.createNewFile()
        out = FileOutputStream(dest)
        val buf = ByteArray(1024)
        var len: Int
        while (source.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        source.close()
        out.close()

    } catch (e: IOException) {
        e.printStackTrace()
        Log.d(TAG, "copyFiles: " + e.message)
    }
}
