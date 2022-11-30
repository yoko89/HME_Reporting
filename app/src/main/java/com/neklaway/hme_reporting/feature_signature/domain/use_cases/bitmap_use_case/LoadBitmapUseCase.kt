package com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.neklaway.hme_reporting.utils.Resource
import java.io.File
import javax.inject.Inject

class LoadBitmapUseCase @Inject constructor(
    private val app: Application
) {

    operator fun invoke(folderName: String?, fileName: String,scaled:Boolean = true): Resource<Bitmap> {
        val options = BitmapFactory.Options()
        options.inScaled = scaled

        val directory = File(
            app.applicationContext.filesDir.absolutePath + "/" + (folderName ?: "")
        )

        if (!directory.exists()) {
            return Resource.Error(message = "Signature is not available")
        }

        val file = File(directory, "$fileName.png")
        return if (!file.exists()) {
            Resource.Error(message = "Signature is not available")
        } else {
            Resource.Success(BitmapFactory.decodeFile(file.path, options))
        }

    }
}