package com.ccino.demo.jetpack


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.ccino.demo.ui.theme.CaseTheme
import com.ccino.demo.ui.widget.Label

private const val TAG = "ViewModelActivity"

class ViewModelActivity : ComponentActivity() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            SavedStateViewModelFactory(application, this)
        )[MyViewModel::class.java]
    }

    @Composable
    fun Case() {
        Column {
            Row {
                Label("SaveStateHandle") { Log.d(TAG, "Case: ${viewModel.info}") }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CaseTheme { Case() } }
    }
}
