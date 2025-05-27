package com.ccino.demo.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ccino.demo.compose.ui.theme.WarehouseTheme
import com.ccino.demo.util.DisplayUtil
import kotlin.math.roundToInt

private const val TAG = "PopActivity"
class PopActivity : ComponentActivity() {
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
                        // 示例使用
                        val options = listOf("选项1", "选项2")
                        ButtonWithDropdown(options = options)
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonWithDropdown(
    options: List<String>,
    initialLabel: String = "请选择",
    modifier: Modifier = Modifier
) {
    var selectedText by remember { mutableStateOf(initialLabel) }

    Row(
        modifier = modifier.background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DropdownButton(
            options = options,
            onOptionSelected = { selectedText = it }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = selectedText)
    }
}

@Composable
fun DropdownButton(
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPopup by remember { mutableStateOf(false) }
    var buttonPosition by remember { mutableStateOf(Offset.Zero) }
    var buttonSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    Button(
        onClick = { showPopup = !showPopup },
        modifier = modifier.padding(start = 20.dp, top = 10.dp).onGloballyPositioned { coordinates ->
            buttonPosition = coordinates.positionInRoot()
            buttonSize = coordinates.size
            Log.d(TAG, "DropdownButton: buttonPos=$buttonPosition, size=$buttonSize, sw=${DisplayUtil.screenWidth},sh=${DisplayUtil.screenHeight}" )
        }
    ) {
        Text(text = "选择")
    }

    if (showPopup) {
        PopupMenu(
            options = options,
            position = buttonPosition,
            buttonHeightPx = buttonSize.height,  // 直接传递像素值
            onOptionSelected = { option ->
                onOptionSelected(option)
                showPopup = false
            },
            onDismissRequest = { showPopup = false }
        )
    }
}

/**
 * 1. 参照基准：父布局的边界
 * Popup 的定位默认基于其父布局的坐标系（通常是调用 Popup 的 Composable 的直接父容器）。如果未指定 alignment 或 offset，Popup 默认显示在父布局的左上角（0, 0）。
 */
@Composable
fun PopupMenu(
    options: List<String>,
    position: Offset,
    buttonHeightPx: Int,  // 接收像素值
    onOptionSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {

    Log.d(TAG, "PopupMenu: pos=$position, size=$buttonHeightPx" )
    Popup(
        alignment = Alignment.TopStart,

        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .width(IntrinsicSize.Max)
        ) {
            Column {
                options.forEach { option ->
                    Text(
                        text = option,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}