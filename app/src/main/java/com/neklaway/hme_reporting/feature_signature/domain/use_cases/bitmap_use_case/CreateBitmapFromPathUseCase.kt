package com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import javax.inject.Inject

class CreateBitmapFromPathUseCase @Inject constructor()  {

    operator fun invoke(path: Path , size:Size):Bitmap{
        val bitmap = createBitmap(size.width.toInt(), size.height.toInt())
        bitmap.applyCanvas {
            val paint = Paint()
            paint.color = Color.Blue.toArgb()
            paint.style = Paint.Style.STROKE
            drawPath(path.asAndroidPath(), paint)
        }
        return bitmap
    }

}