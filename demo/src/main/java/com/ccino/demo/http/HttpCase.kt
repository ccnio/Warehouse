package com.ccino.demo.http

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ccino.demo.http.adapter.createService
import kotlinx.coroutines.launch

private const val TAG = "HttpCase"

@Composable
fun HttpCase(modifier: Modifier = Modifier) {
    Column(modifier) {
        Row {
            HttpResult()
        }
    }
}

@Composable
fun HttpResult() {
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            val banner = createService(WanApiService::class.java).getBanner()
            if (banner is Resource.Success) {
                Log.d(TAG, "HttpResult: imgUrl=${banner.data?.data?.firstOrNull()?.imgUrl}")
            } else Log.d(TAG, "HttpResult: err=$banner")
        }
    }) {
        Text(text = "请求")
    }
}