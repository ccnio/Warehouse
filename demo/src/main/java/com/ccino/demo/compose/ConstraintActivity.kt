package com.ccino.demo.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ccino.demo.compose.ui.theme.WarehouseTheme

private const val TAG = "ConstraintEx"

class ConstraintActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarehouseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            ConstraintEx()
                            RowDivideExample()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConstraintEx() {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        // 创建三个引用，用于关联三个文本组件
        val (text1, text2, text3) = createRefs()

        // 第一个文本
        Text(
            text = "文本1",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(text1) {
                    start.linkTo(parent.start)
                    end.linkTo(text2.start)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                .background(Color(0xFFE1F5FE))
                .padding(vertical = 16.dp)
        )

        // 第二个文本
        Text(
            text = "文本2",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(text2) {
                    start.linkTo(text1.end)
                    end.linkTo(text3.start)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                .background(Color(0xFFB3E5FC))
                .padding(vertical = 16.dp)
        )

        // 第三个文本
        Text(
            text = "文本3",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(text3) {
                    start.linkTo(text2.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                .background(Color(0xFF81D4FA))
                .padding(vertical = 16.dp)
        )

        // 创建水平链约束，使三个文本平均分配空间
        createHorizontalChain(text1, text2, text3, chainStyle = ChainStyle.Spread)
    }
}

@Composable
fun RowDivideExample() {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // 第一个文本，权重为1
        Text(
            text = "文本1",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)  // 分配1/3空间
                .background(Color(0xFFE1F5FE))
                .padding(vertical = 16.dp)
        )

        // 第二个文本，权重为1
        Text(
            text = "文本2",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)  // 分配1/3空间
                .background(Color(0xFFB3E5FC))
                .padding(vertical = 16.dp)
        )

        // 第三个文本，权重为1
        Text(
            text = "文本3",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)  // 分配1/3空间
                .background(Color(0xFF81D4FA))
                .padding(vertical = 16.dp)
        )
    }
}