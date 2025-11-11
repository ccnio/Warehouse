package com.ccino.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ccino.demo.compose.ApiCaseActivity
import com.ccino.demo.ui.theme.CaseTheme

private const val TAG = "MainActivity"

/**
 * MainActivity
 * 从 Android 12 开始，对于一个任务（Task）的根 Activity（通常是你从桌面启动的第一个 Activity），
 * 点击返回键的默认行为不再是销毁（finish）该 Activity，而是将其移动到后台。 这个行为和按 Home 键非常相似。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Row(modifier = modifier) {
        Text(
            text = "ComposeApi",
            modifier = modifier.clickable { context.startActivity(Intent(context, ApiCaseActivity::class.java)) }
        )
    }

}