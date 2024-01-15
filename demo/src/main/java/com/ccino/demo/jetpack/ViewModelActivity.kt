package com.ccino.demo.jetpack


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ccino.demo.kt.mutexDeadLock
import com.ccino.demo.ui.theme.CaseTheme
import com.ccino.demo.ui.widget.Label

private const val TAG = "ViewModelActivity"

// 顶层函数版本
inline fun <reified T : ViewModel> getViewModel(owner: ViewModelStoreOwner, configLiveData: T.() -> Unit = {}): T =
    ViewModelProvider(owner)[T::class.java].apply { configLiveData() }

// 扩展函数版本
inline fun <reified T : ViewModel> ViewModelStoreOwner.getSelfViewModel(configLiveData: T.() -> Unit = {}): T =
    getViewModel(this, configLiveData)

private const val TAG_L = "ViewModelActivityL"

class ViewModelActivity : ComponentActivity() {

    @Composable
    fun Case() {
        Column {
            Row {
                Label("mutex") { mutexDeadLock() }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CaseTheme { Case() } }
    }
}
