package com.example.seurat.domain

import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.get
import androidx.lifecycle.lifecycleScope
import com.example.seurat.R
import com.example.seurat.tech.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    companion object{
        private const val canvasFillRatio = 0.8f

        private const val frameDelay = 16L // 16ms. for 60 FPS
        private const val ballCountPerFrame = 100
        private const val ballSize = 10
    }

    private var canvasSize: IntSize? = null
    private var pickImageBitmap: Bitmap? = null
    private var pointageBitmap = mutableStateOf<Bitmap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startDrawPointage()

        setContent {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .onGloballyPositioned {
                            canvasSize = it.size
                        }
                ){
                    pointageBitmap.value?.let {
                        drawImage(
                            image = it.asImageBitmap(),
                            topLeft = Offset(
                                center.x - it.width/2,
                                center.y - it.height/2
                            )
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_brightness_1_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { pickImage() }
                )
            }
        }
    }

    private fun pickImage() {
        launcher.launch(ContentPickType.Image)
        pickImageBitmap = null
        pointageBitmap.value = null
    }

    private val launcher = ContentPick.createLauncher(this){ imageUri ->
        lifecycleScope.launch {
            pickImageBitmap = withContext(Dispatchers.IO) {
                val rawBitmap = imageUri.getBitmap(this@MainActivity)
                val adjustedBitmap = canvasSize?.let { size ->
                    rawBitmap.getAdjustSizeBitmap(size, canvasFillRatio)
                } ?: throw Exception("Canvas Size Not Initialized") // never happen

                adjustedBitmap
            }
        }
    }

    private fun startDrawPointage() = lifecycleScope.launch{
        while (true){
            pickImageBitmap?.let { imageBitmap ->
                val temp = Bitmap.createBitmap(imageBitmap.width, imageBitmap.height, Bitmap.Config.ARGB_8888)

                android.graphics.Canvas(temp).run {
                    pointageBitmap.value?.let { bitmap ->
                        drawBitmap(bitmap, 0f, 0f, null)
                    }

                    repeat(ballCountPerFrame){
                        val randomX = (ballSize until imageBitmap.width-ballSize).random()
                        val randomY = (ballSize until imageBitmap.height-ballSize).random()
                        val randomPointColor = imageBitmap[randomX, randomY]

                        drawCircle(
                            randomX.toFloat(),
                            randomY.toFloat(),
                            ballSize.toFloat(),
                            Paint().apply {
                                color = randomPointColor
                            }
                        )
                    }
                }
                pointageBitmap.value = temp
            }

            delay(frameDelay)
        }
    }
}


