package com.example.seurat.domain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.lifecycleScope
import com.example.seurat.tech.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    companion object{
        private const val canvasFillRatio = 0.8f
    }

    private val launcher = ContentPick.createLauncher(this){ imageUri ->
        lifecycleScope.launch {
            pickImageBitmap.value = withContext(Dispatchers.IO) {
                val rawBitmap = imageUri.getBitmap(this@MainActivity)
                val adjustedBitmap = canvasSize?.let { size ->
                    rawBitmap.getAdjustSizeBitmap(size, canvasFillRatio)
                } ?: throw Exception("Canvas Size Not Initialized")
                adjustedBitmap.asImageBitmap()
            }
        }
    }

    private var pickImageBitmap = mutableStateOf<ImageBitmap?>(null)
    private var canvasSize: IntSize? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcher.launch(ContentPickType.Image)

        setContent {

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        canvasSize = it.size
                    }
            ){
                pickImageBitmap.value?.let {
                    drawImage(
                        image = it,
                        topLeft = Offset(
                            center.x - it.width/2,
                            center.y - it.height/2
                        )
                    )
                }
            }
        }
    }
}


