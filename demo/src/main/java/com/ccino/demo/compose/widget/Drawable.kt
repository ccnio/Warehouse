package com.ccino.demo.compose.widget

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ccino.demo.R

@Composable
fun DrawableCase(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.background(Color.White)
    ) {
        Shape()
        GradientCase()
    }
}

@Composable
fun GradientCase(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(50.dp)
            .height(30.dp)
            .background(
                brush = Brush.horizontalGradient(listOf(Color.Blue, Color.White)),
                shape = RoundedCornerShape(20.dp)
            )
    )
}

@Composable
fun Shape(modifier: Modifier = Modifier) {
    var isEnabled by remember { mutableStateOf(true) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val (backgroundColor, textColor) = when {
        !isEnabled -> Color.LightGray to Color.Gray // 禁用状态
        isPressed -> Color(0xFF00A9A0) to Color.LightGray   // 点击状态
        else -> Color(0xff00d8d0) to Color.Black      // 可用状态
    }

    Text(
        text = stringResource(if (isEnabled) R.string.action_get else R.string.action_getted),
        color = textColor,
        fontSize = 16.sp,
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(20.dp))
            .border(1.dp, Color.Red, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                // 2. 将可用状态传给 clickable
                enabled = isEnabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                isEnabled = !isEnabled
                Log.d("DrawableCase", "Shape: click")
            }
    )
}


@Preview
@Composable
private fun BodyPreview() {
    DrawableCase(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    )
}
