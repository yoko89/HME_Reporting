package com.neklaway.hme_reporting.utils

import android.util.Log
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
