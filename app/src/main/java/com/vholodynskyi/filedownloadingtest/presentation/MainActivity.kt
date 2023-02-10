package com.vholodynskyi.filedownloadingtest.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.vholodynskyi.filedownloadingtest.presentation.downloading.DownloadingScreen
import com.vholodynskyi.filedownloadingtest.presentation.ui.theme.FileDownloadingTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FileDownloadingTestTheme {
                Surface(color = MaterialTheme.colors.background) {
                    DownloadingScreen()
                }
            }
        }
    }
}


