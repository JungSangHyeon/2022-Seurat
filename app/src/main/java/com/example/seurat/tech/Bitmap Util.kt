package com.example.seurat.tech

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import android.net.Uri as AndroidURI

/**
 * This util need "Glide"
 * https://github.com/bumptech/glide
 */

/**
 * Use Glide to handle image rotate err
 * Other solution : https://stackoverflow.com/questions/14066038
 */
fun AndroidURI.getBitmap(
    activity: ComponentActivity
): Bitmap = Glide.with(activity).asBitmap().load(this@getBitmap).submit().get()

fun Bitmap.getAdjustSizeBitmap(
    canvasSize: IntSize,
    ratio: Float
): Bitmap {
    val widthFactor = canvasSize.width*ratio/this.width
    val heightFactor = canvasSize.height*ratio/this.height
    val factor = widthFactor.coerceAtMost(heightFactor)
    return this.scale(
        (this.width * factor).toInt(),
        (this.height * factor).toInt()
    )
}