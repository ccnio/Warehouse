package com.ccnio.ware.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ccnio.ware.compose.ui.theme.WarehouseTheme

private const val TAG = "ComposeActivity"

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WarehouseTheme {
                // A surface container using the 'background' color from the theme
                Surface(//https://juejin.cn/post/6861464723699957768
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val list = mutableListOf<String>()
                    for (i in 0..50) list.add("Item $i")
                    MyList(items = list)
                }
            }
        }
    }
}

@Composable
fun MyList(items: List<String>) {
    val context = LocalContext.current
    LazyColumn {
        items(items) { item ->
            ClickableText(
                text = AnnotatedString(item),
                onClick = { Toast.makeText(context, "$item  $context", Toast.LENGTH_SHORT).show() },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )

            Divider(
                color = Color.Blue,
                thickness = 1.dp,
                startIndent = 16.dp,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WarehouseTheme {
        MyList(items = listOf("a", "b", "c"))
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}