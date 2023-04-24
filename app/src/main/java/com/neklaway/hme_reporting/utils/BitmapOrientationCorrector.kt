package com.neklaway.hme_reporting.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import javax.inject.Inject

class BitmapOrientationCorrector @Inject constructor(){

    operator fun invoke(path:String,image:Bitmap):Bitmap{
        val matrix = Matrix()
        when (ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED)){
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
        }
        return Bitmap.createBitmap(image,0,0,image.width,image.height,matrix,true)
    }
}