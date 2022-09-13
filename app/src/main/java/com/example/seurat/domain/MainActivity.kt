package com.example.seurat.domain

import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
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

        private const val ballSizeDiff = 10
        private const val ballCountPerFrameDiff = 100
    }

    private var canvasSize: IntSize? = null
    private var pickImageBitmap: Bitmap? = null
    private var pointageBitmap = mutableStateOf<Bitmap?>(null)

    private var ballSizeSlideValue = mutableStateOf(0.5f)
    private var ballCountPerFrameSlideValue = mutableStateOf(0.5f)

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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_brightness_1_24),
                        contentDescription = null,
                        colorFilter = tint(Color.Gray),
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20))
                            .clickable { pickImage() }
                            .border(1.dp, Color.LightGray, RoundedCornerShape(20))
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Slider(ballSizeSlideValue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(ballCountPerFrameSlideValue)
                    }
                }
            }
        }
    }

    @Composable
    private fun Slider(
        value: MutableState<Float>
    ){
        Slider(
            value = value.value,
            onValueChange = { value.value = it },
            colors = SliderDefaults.colors(
                thumbColor = Color.Gray,
                activeTrackColor = Color.Gray,
                inactiveTrackColor = Color.LightGray,
            )
        )
    }

    private fun pickImage() {
        launcher.launch(ContentPickType.Image)
        pickImageBitmap = null
        pointageBitmap.value = null
    }

    private val launcher = ContentPick.createLauncher(this){
        it?.let { imageUri ->
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
    }

    private fun startDrawPointage() = lifecycleScope.launch{
        while (true){
            pickImageBitmap?.let { imageBitmap ->
                val temp = Bitmap.createBitmap(imageBitmap.width, imageBitmap.height, Bitmap.Config.ARGB_8888)

                android.graphics.Canvas(temp).run {
                    pointageBitmap.value?.let { bitmap ->
                        drawBitmap(bitmap, 0f, 0f, null)
                    }

                    repeat(
                        (ballCountPerFrame + ballCountPerFrameDiff*(ballCountPerFrameSlideValue.value-1)).toInt()
                    ){
                        val biggestBallSize = ballSize + ballSizeDiff
                        val randomX = (biggestBallSize until imageBitmap.width - biggestBallSize).random()
                        val randomY = (biggestBallSize until imageBitmap.height - biggestBallSize).random()
                        val randomSize = ballSize + ballSizeDiff * ballSizeSlideValue.value
                        val randomPointColor = imageBitmap[randomX, randomY]

                        drawCircle(
                            randomX.toFloat(),
                            randomY.toFloat(),
                            randomSize,
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


