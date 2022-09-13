package com.example.seurat.domain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.seurat.tech.ContentPick
import com.example.seurat.tech.ContentPickType
import com.example.seurat.tech.launch

class MainActivity : ComponentActivity() {

    private val launcher = ContentPick.createLauncher(this){ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcher.launch(ContentPickType.Image)

        setContent {
        }
    }
}