package com.ccino.demo.compose

//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CheckCircle
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ccino.demo.compose.ui.theme.WarehouseTheme

private const val TAG = "SelectorActivity"

class SelectorActivity : ComponentActivity() {
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
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column {
                            SelectorEx()
                            TextSpanExample()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TextSpanExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 基本的文本样式混合
        Text(
            text = buildAnnotatedString {
                append("普通文本")
                withStyle(
                    style = SpanStyle(
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("红色粗体")
                }
                append("普通文本")
            }
        )

        // 2. 可点击的文本部分
        val annotatedText = buildAnnotatedString {
            append("我同意")
            pushStringAnnotation(tag = "URL", annotation = "terms_url")
            withStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("《用户协议》")
            }
            pop()
            append("和")
            pushStringAnnotation(tag = "URL", annotation = "privacy_url")
            withStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("《隐私政策》")
            }
            pop()
        }

        val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }


        Text(
            text = annotatedText,
            onTextLayout = { layoutResult.value = it },
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures { offset ->
                    val position = layoutResult.value?.getOffsetForPosition(offset) ?: return@detectTapGestures

                    annotatedText.getStringAnnotations(
                        tag = "URL",
                        start = position,
                        end = position
                    ).firstOrNull()?.let { annotation ->
                        when (annotation.item) {
                            "terms_url" -> println("打开用户协议")
                            "privacy_url" -> println("打开隐私政策")
                        }
                    }
                }
            }
        )

        // 3. 行内图标与文本混合
        Text(
            text = buildAnnotatedString {
                append("状态：")
                withStyle(SpanStyle(color = Color.Green)) {
                    append("成功")
                }
                appendInlineContent(id = "success")
            },
            inlineContent = mapOf(
                "success" to InlineTextContent(
                    Placeholder(
                        width = 16.sp,
                        height = 16.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                    )
                ) {
//                    Icon(
////                        imageVector = Icons.Default.CheckCircle,
//                        contentDescription = "成功",
//                        tint = Color.Green,
//                        modifier = Modifier.size(16.dp)
//                    )
                }
            )
        )

        // 4. 字体样式混合
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontSize = with(LocalDensity.current) { 16.dp.toSp() }
                )) {
                    append("大字体")
                }
                append(" 普通字体 ")
                withStyle(
                    SpanStyle(
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Cursive
                    )
                ) {
                    append("斜体特殊字体")
                }
            }
        )
    }
}

@Composable
fun SelectorEx() {
    var enabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatefulText(
            text = "点击我试试",
            enabled = enabled,
            onClick = { println("文本被点击") },
            modifier = Modifier.fillMaxWidth() // 可以添加其他修饰符
        )

        Button(onClick = { enabled = !enabled }) {
            Text(if (enabled) "禁用文本" else "启用文本")
        }
    }
}

@Composable
fun StatefulText(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        when {
            !enabled -> Color.LightGray
            isPressed -> Color.Gray
            else -> Color.White
        }
    )

    val textColor by animateColorAsState(
        if (!enabled) Color.DarkGray else Color.Black
    )

    Text(
        text = text,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null, //ripple(bounded = true), 去除波纹
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}