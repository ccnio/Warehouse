package com.ccino.demo.compose.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun GestureCase(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        DraggableBallExample()
    }
}

@Preview
@Composable
fun DraggableBallExample() {
    val coroutineScope = rememberCoroutineScope()

    // 1. 创建一个 Animatable 来驱动位置。
    // 使用 Offset.VectorConverter 来让 Animatable 处理二维坐标 (x, y)。
    // 初始位置在 (0f, 0f)，代表相对原始位置的偏移量。
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // 将小球的初始位置放在屏幕中央
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                // 2. 使用 graphicsLayer 来应用 Animatable 的值，实现高性能位移。
                // 这确保了在拖动时不会触发重组或重新布局。
                .graphicsLayer {
                    translationX = offset.value.x
                    translationY = offset.value.y
                }
                .pointerInput(Unit) {
                    // 3. 使用 detectDragGestures 来监听手势。
                    detectDragGestures(
                        onDragStart = {
                            // 可选：拖动开始时，可以停止任何正在进行的动画。
                            coroutineScope.launch {
                                offset.stop()
                            }
                        },
                        onDrag = { change, dragAmount ->
                            // 手势“跟手”阶段
                            change.consume() // 消费掉手势事件
                            coroutineScope.launch {
                                // 4. 使用 snapTo() 立即更新位置，实现“跟手”效果。
                                // 新位置 = 当前偏移量 + 本次手势的拖动量
                                val newOffset = offset.value + dragAmount
                                offset.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            // 手势“松手”阶段
                            coroutineScope.launch {
                                // 5. 使用 animateTo() 播放弹性动画，回到原点 (Offset.Zero)。
                                // 我们使用 spring() 动画规格来创建弹性效果。
                                offset.animateTo(
                                    targetValue = Offset.Zero,
                                    animationSpec = spring(
                                        dampingRatio = 0.4f, // 阻尼比，越小越弹
                                        stiffness = 200f     // 刚度，越大动画越快
                                    )
                                )
                            }
                        }
                    )
                }
                .background(Color.Blue, CircleShape) // 绘制蓝色小球
        )
    }
}


@Preview
@Composable
private fun DraggableBallExamplePreview() {
    DraggableBallExample()
}