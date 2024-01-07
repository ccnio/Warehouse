package com.ccino.demo.inject

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.ccino.demo.ui.theme.CaseTheme
import com.ccino.demo.ui.widget.Label

private const val TAG = "KspActivity"
class KspActivity : ComponentActivity() {
    @Composable
    fun Case() {
        Column {
            Row {
                Label("接收者函数类型") {
                    Log.d(TAG, "Case: ")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CaseTheme { Case() } }
    }
}