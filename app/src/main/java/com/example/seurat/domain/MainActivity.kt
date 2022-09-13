package com.example.seurat.domain

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.lifecycleScope
import com.example.seurat.tech.ContentPick
import com.example.seurat.tech.ContentPickType
import com.example.seurat.tech.getBitmap
import com.example.seurat.tech.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val launcher = ContentPick.createLauncher(this){
        lifecycleScope.launch {
            pickImageBitmap.value = withContext(Dispatchers.IO) {
                it.getBitmap(this@MainActivity).asImageBitmap()
            }
        }
    }

    private var pickImageBitmap = mutableStateOf<ImageBitmap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcher.launch(ContentPickType.Image)

        setContent {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ){
                pickImageBitmap.value?.let {
                    drawImage(it)
                }
            }
        }
    }
}