package com.vholodynskyi.filedownloadingtest.presentation.downloading

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DownloadingScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val downloadPercentage by viewModel.percentage.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = state.file,
            color = MaterialTheme.colors.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .align(Alignment.Center)
        )

        if (state.error.isNotBlank()) {
            Text(
                text = state.error,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
        if (state.isLoading) {

            CircularProgressBar(Modifier.align(Alignment.Center),downloadPercentage.toFloat(), 100 )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = { viewModel.cancelDownloading() }) {
                Text(text = "Cancel downloading")
            }
        }
    }
}


@Composable
fun CircularProgressBar(
    modifier:Modifier,
    currentPercentage: Float,
    number: Int,
    fontSize: TextUnit = 28.sp,
    radius: Dp = 50.dp,
    strokeWidth: Dp = 8.dp
) {

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val color: Color by animateColorAsState(Color(currentPercentage, 0.2f, 0.5f))

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(radius * 2f)
    ) {
        Canvas(modifier = Modifier.size(radius * 2f)) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * currentPercentage,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = (currentPercentage * number).toInt().toString(),
            color = Color.Black,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}