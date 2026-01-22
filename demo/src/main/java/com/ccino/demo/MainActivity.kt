package com.ccino.demo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ccino.demo.compose.ApiCaseActivity
import com.ccino.demo.compose.layout.TouchActivity
import com.ccino.demo.dialog.chain.AdTask
import com.ccino.demo.dialog.chain.DialogChainController
import com.ccino.demo.dialog.chain.NoticeTask
import com.ccino.demo.http.HttpActivity
import com.ccino.demo.jetpack.Store
import com.ccino.demo.kt.KotlinActivity
import com.ccino.demo.media.PlayerListActivity
import com.ccino.demo.media.PlayerPageActivity
import com.ccino.demo.media.PlayerSwitchActivity
import com.ccino.demo.ui.theme.CaseTheme
import com.ccino.timing.annotation.Timing
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

/**
 * MainActivity
 * 从 Android 12 开始，对于一个任务（Task）的根 Activity（通常是你从桌面启动的第一个 Activity），
 * 点击返回键的默认行为不再是销毁（finish）该 Activity，而是将其移动到后台。 这个行为和按 Home 键非常相似。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val dialogChainController = DialogChainController()

    @Inject
    lateinit var store: Store
    
    @Timing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogChainController.attach(this)
        Handler().postDelayed({ /*store.printCategory(ID_CATEGORY_FOOD)*/ }, 3000)
        setContent {
            CaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top).asPaddingValues())
                        .fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Column {
            Row(modifier = modifier) {
                Text(
                    text = "ComposeApi",
                    modifier = modifier.clickable { context.startActivity(Intent(context, ApiCaseActivity::class.java)) }
                )
                Button(onClick = { context.startActivity(Intent(context, KotlinActivity::class.java)) }) {
                    Text("kotlin")
                }
                Button(onClick = { context.startActivity(Intent(context, TouchActivity::class.java)) }) {
                    Text("touch")
                }
                Button(onClick = { context.startActivity(Intent(context, HttpActivity::class.java)) }) {
                    Text("http")
                }
            }
            Row(modifier = modifier) {
                Button(onClick = {
                    dialogChainController.addTask(NoticeTask())
                    dialogChainController.addTask(AdTask())
                    dialogChainController.start()
                }) {
                    Text("dialogTask")
                }
                Button(onClick = { context.startActivity(Intent(context, PlayerSwitchActivity::class.java)) }) {
                    Text("PlayerSwitch")
                }
                Button(onClick = { context.startActivity(Intent(context, PlayerListActivity::class.java)) }) {
                    Text("PlayerList")
                }
            }
            Row(modifier = modifier) {
                Button(onClick = { context.startActivity(Intent(context, PlayerPageActivity::class.java)) }) {
                    Text("PlayerPage")
                }
            }
        }
    }
}
