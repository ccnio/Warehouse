package com.ccino.demo.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class LoadState {
    Loading,
    Empty,
    Error,
    Success
}

@Composable
fun StateLayout(
    state: LoadState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    /** Vertical offset from the center, in Dp. */
    verticalOffset: Dp = 0.dp,
    loadingText: String = "加载中",
    emptyText: String = "暂无内容",
    errorText: String = "加载失败",
    retryText: String = "重试",
    content: @Composable () -> Unit
) {
    if (state == LoadState.Success) {
        Box(modifier) { content() }
        return
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.offset(y = verticalOffset)) {
            when (state) {
                LoadState.Loading -> LoadingContent(loadingText)
                LoadState.Empty -> EmptyContent(emptyText)
                LoadState.Error -> ErrorContent(onRetry, errorText, retryText)
                LoadState.Success -> { /* Unreachable */ }
            }
        }
    }
}

@Composable
fun LoadingContent(text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator()
        Text(text = text, fontSize = 16.sp, color = Color.Gray)
    }
}

@Composable
fun EmptyContent(text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
//        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.Gray)
        Text(text = text, fontSize = 16.sp, color = Color.Gray)
    }
}

@Composable
fun ErrorContent(onRetry: () -> Unit, text: String, retryText: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
//        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.Red)
        Text(text = text, fontSize = 16.sp, color = Color.Red)
        Button(onClick = onRetry) {
            Text(text = retryText)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StateLayoutPreview() {
    Column {
        StateLayout(
            state = LoadState.Loading,
            onRetry = {},
            modifier = Modifier.weight(1f).background(Color(0xFF6B92D1))
        ) {}
        StateLayout(
            state = LoadState.Empty,
            onRetry = {},
            modifier = Modifier.weight(1f).background(Color(0xFF7cb18d))
        ) {}
        StateLayout(
            state = LoadState.Error,
            onRetry = {},
            modifier = Modifier.weight(1f)
        ) {}
    }
}