package com.ccino.demo.compose.layout

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ccino.demo.compose.ApiCaseScreen
import com.ccino.demo.compose.ui.theme.WarehouseTheme

class TouchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarehouseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GestureCase(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Preview
@Composable
private fun CaseScreenPreview() {
    ApiCaseScreen()
}