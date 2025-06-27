package com.ccino.demo.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ccino.demo.compose.ui.theme.WarehouseTheme

private const val TAG = "StateVmActivity"

class StateVmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarehouseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Enter(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Enter(modifier: Modifier = Modifier, vm: DataVM = viewModel()) {
    val topics by vm.topics.collectAsStateWithLifecycle()
    Log.d(TAG, "Enter:")
    Column(modifier = modifier.fillMaxWidth()) {
        Log.d(TAG, "Enter: $this")
        Button(onClick = { vm.changeData() }) { Text(text = "click") }
        HorGrid(list = topics)
        Text("third")
    }
}

@Composable
fun Test(modifier: Modifier = Modifier) {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Column {  // 这里的 column 是 Column 组件
            Text("Count: $count")
            Text("Static text")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WarehouseTheme {
        Enter()
    }
}


@Composable
fun HorGrid(modifier: Modifier = Modifier, list: List<String>) {
    val state = rememberLazyGridState()
    Box {
        LazyHorizontalGrid(
// 横向网格，不能实现页面滑动效果
            state = state,
            rows = GridCells.Fixed(3),//显示几行
            modifier = modifier
                .fillMaxWidth()
                .height(150.dp) // 必须设置高度，否则撑满父布局，即使item设置高度也不起作用
                .background(Color.DarkGray),
            // 设置内容内边距, 边距里能显示内容，如果不想让边距里显示内容，可以在modifier里设置padding
            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp), // 水平间距
            verticalArrangement = Arrangement.spacedBy(8.dp), // 垂直间距
            overscrollEffect = null,
        ) {
            items(
                items = list,
                key = { it },
            ) {
                Text(
                    it,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.LightGray)
                        .width(120.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun HorGridPreview(modifier: Modifier = Modifier) {
    val list = List(20) { "Item $it" }
    HorGrid(modifier = modifier, list = list)
}