package com.ccino.demo.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ccino.demo.compose.sdk.DisposeEffect
import com.ccino.demo.compose.sdk.LaunchEffect
import com.ccino.demo.compose.sdk.RememberScope
import com.ccino.demo.compose.sdk.SideCase
import com.ccino.demo.compose.ui.theme.WarehouseTheme

private const val TAG = "ApiCaseActivity"

class ApiCaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarehouseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ApiCase(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ApiCase(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        DisposeEffect()
        LaunchEffect(Modifier.padding(start = 8.dp))
        RememberScope(Modifier.padding(start = 8.dp))
        SideCase(Modifier.padding(start = 8.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun DisposeEffectPreview2() {
    WarehouseTheme {
        ApiCase(modifier = Modifier.padding(16.dp))
    }
}