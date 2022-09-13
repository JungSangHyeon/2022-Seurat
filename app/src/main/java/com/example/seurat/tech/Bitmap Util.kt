package com.example.seurat.tech

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
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