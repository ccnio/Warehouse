package com.ccino.demo.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ccino.demo.compose.sdk.DialogCase
import com.ccino.demo.compose.sdk.EffectCase
import com.ccino.demo.compose.sdk.GithubUserScreen
import com.ccino.demo.compose.sdk.StateCase
import com.ccino.demo.compose.ui.theme.WarehouseTheme
import com.ccino.demo.compose.widget.ConstraintCase
import com.ccino.demo.compose.widget.DrawableCase
import com.ccino.demo.compose.widget.StateLayoutCase

class ApiCaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarehouseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ApiCaseScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun ApiCaseScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        EffectCase()
        Spacer(modifier = Modifier.height(16.dp))
        GithubUserScreen()
        ConstraintCase()
        DrawableCase()
        StateLayoutCase()
        DialogCase()
        StateCase()
    }
}


@Preview
@Composable
private fun CaseScreenPreview() {
    ApiCaseScreen()
}