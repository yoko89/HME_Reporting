package com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case

import android.app.Application
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class SaveBitmapUseCase @Inject constructor(
    private val app: Application
) {

    operator fun invoke(bitmap: Bitmap, folderName: String?, fileName: String):Boolean {

        val directory = File(
            app.applicationContext.filesDir.absolutePath + "/" + (folderName ?: "")
        )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "$fileName.png")

        val fileOutputStream = FileOutputStream(file)

        val result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        return result
    }
}