package com.example.seurat.tech

import android.net.Uri as AndroidURI
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

object ContentPick {

    fun createLauncher(
        activity: ComponentActivity,
        callback: (AndroidURI)->Unit
    ) = activity.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){
        callback(it)
    }
}

enum class ContentPickType(
    val intentInput: String
) {
    Image("image/*"),
    Audio("audio/*")
}

fun ActivityResultLauncher<String>.launch(
    contentPickType: ContentPickType
) = this.launch(
    contentPickType.intentInput
)