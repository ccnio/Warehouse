package com.ccino.demo.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ccino.demo.R

val DIALOG_WIDTH = 280.dp
val DIALOG_BOTTOM_OFFSET = 50.dp // 向上偏移30dp（通过底部padding实现）
val DIALOG_BG_RADIUS = 10.dp
val DIALOG_PADDING_TOP = 20.dp
val DIALOG_PADDING_HOR = 20.dp
val DIALOG_PADDING_BOTTOM = 16.dp
val DIALOG_CHECK_MARGIN_TOP = 8.dp
private val DIALOG_DIVIDER_THICKNESS = 1.dp


private val DESC_TO_TITLE = 6.dp
val ACTION_MARGIN_TOP = 16.dp
val BTN_GAP = 10.dp
val MINOR_MARGIN_TOP = 8.dp

val dialogModifierContainer = Modifier
    .width(DIALOG_WIDTH)
    .background(Color.White, shape = RoundedCornerShape(DIALOG_BG_RADIUS))
    .padding(top = DIALOG_PADDING_TOP)

private val textStyleTitle = TextStyle(
    color = Color(0xFF222222),
    fontSize = 17.sp,
    lineHeight = 22.sp,
    fontWeight = FontWeight.W500,
    textAlign = TextAlign.Center,
)

private val textStyleDesc = TextStyle(
    color = Color(0xFF222222),
    fontSize = 14.sp,
    lineHeight = 22.sp,
    fontWeight = FontWeight.W400,
    textAlign = TextAlign.Center,
)

private val txtStyleAction = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.W500,
    textAlign = TextAlign.Center
)

/**
 * title/desc
 */
@Composable
fun DialogTitleAndDesc(modifier: Modifier = Modifier, title: String? = null, desc: String? = null) {
    Column(modifier = modifier) {
        if (!title.isNullOrEmpty()) {
            Text(
                text = title, style = textStyleTitle,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (!desc.isNullOrEmpty()) {
            Text(
                text = desc, style = textStyleDesc, modifier = Modifier
                    .padding(top = DESC_TO_TITLE)
                    .fillMaxWidth()
            )
        }
    }
}


@Composable
fun DialogRemindCheck(modifier: Modifier = Modifier, checked: Boolean? = null, txt: String? = null, onCheckedChange: ((Boolean) -> Unit)? = null) {
    if (txt.isNullOrEmpty()) return
    val checkState = remember { mutableStateOf(checked ?: false) }
    Row(
        modifier
            .clickable(interactionSource = null, indication = null) {
                checkState.value = !checkState.value;
                onCheckedChange?.invoke(checkState.value)
            }
            .padding(horizontal = DIALOG_PADDING_HOR),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = if (!checkState.value) R.drawable.not_prompting else R.drawable.checkbox_checked_prompting),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.padding(end = 4.dp).width(15.dp).height(15.dp)
        )
        Text(
            text = txt,
            style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFF666666))
        )
    }
}

@Composable
fun DialogActionTxtHor(
    modifier: Modifier = Modifier,
    negativeTxt: String? = null, positiveTxt: String? = null,
    onNegClick: (() -> Unit)? = null, onPosClick: (() -> Unit)? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.dialog_txt_action_h))
    ) {
        DialogDividerHor()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val showNeg = !negativeTxt.isNullOrEmpty()
            val showPos = !positiveTxt.isNullOrEmpty()
            if (showNeg) DialogBtnTxt(modifier = Modifier.weight(1f), txt = negativeTxt, isPositive = false, onClick = onNegClick)
            if (showNeg && showPos) DialogDividerVer(modifier = Modifier.fillMaxHeight())
            if (showPos) DialogBtnTxt(modifier = Modifier.weight(1f), txt = positiveTxt, isPositive = true, onClick = onPosClick)
        }
    }
}

@Composable
fun DialogBtnTxt(modifier: Modifier = Modifier, txt: String? = null, isPositive: Boolean = true, onClick: (() -> Unit)? = null) {
    if (txt.isNullOrEmpty()) return
    Text(
        text = txt,
        modifier.clickable(interactionSource = null, indication = null) { onClick?.invoke() },
        style = txtStyleAction,
        color = if (isPositive) colorResource(R.color.main_theme_color) else Color(0xFF999999)
    )
}

@Composable
fun DialogBtnPositive(modifier: Modifier = Modifier, txt: String? = null, onClick: (() -> Unit)? = null) {
    if (txt.isNullOrEmpty()) return
    val bgColor = colorResource(id = R.color.main_theme_color)
    val txtColor = Color(0xff222222)
    Box(
        modifier = modifier
            .height(42.dp)
            .border(1.dp, bgColor, RoundedCornerShape(42.dp))
            .padding(2.dp)
            .background(color = bgColor, shape = RoundedCornerShape(42.dp))
            .clickable(indication = null, interactionSource = null) { onClick?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = txt, style = txtStyleAction, color = txtColor)
    }
}

@Composable
fun DialogBtnNegative(modifier: Modifier = Modifier, txt: String? = null, onClick: (() -> Unit)? = null) {
    if (txt.isNullOrEmpty()) return
    val bgColor = Color(0xfff3f3f5)
    val txtColor = Color(0xff666666)
    Box(
        modifier = modifier
            .height(42.dp)
            .background(color = bgColor, shape = RoundedCornerShape(42.dp))
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(42.dp))
            .clickable(indication = null, interactionSource = null) { onClick?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = txt, style = txtStyleAction, color = txtColor)
    }
}

@Composable
fun DialogMinorText(modifier: Modifier = Modifier, txt: String? = null, onClick: (() -> Unit)? = null) {
    if (txt.isNullOrEmpty()) return
    Text(
        text = txt, color = Color(0xff989898), style = TextStyle(
            fontSize = 12.sp,
            textDecoration = TextDecoration.Underline,
            color = Color(0xff989898), textAlign = TextAlign.Center
        ),
        modifier = modifier.clickable(indication = null, interactionSource = null) { onClick?.invoke() })
}

@Composable
fun DialogDividerHor(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        color = Color(0xFFEEEEEE),
        thickness = DIALOG_DIVIDER_THICKNESS
    )
}

@Composable
fun DialogDividerVer(modifier: Modifier = Modifier) {
    VerticalDivider(
        modifier = modifier,
        color = Color(0xFFEEEEEE),
        thickness = DIALOG_DIVIDER_THICKNESS
    )
}

@Composable
fun DialogFrame(onDismiss: (() -> Unit)? = null, content: @Composable () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss ?: {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = DIALOG_BOTTOM_OFFSET),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogHeaderPreview() {
    DialogTitleAndDesc(
        title = "生成回数チャージ切れ！",
        desc = "今日分の無料ストー\\nリー やりとり回数がなくなっちゃった！\\n 課金するとすぐ復活→ \\n【有料会員】or【エネルギーチャージ】",
    )
}

@Preview(showBackground = true)
@Composable
private fun DialogActionTxtHorPreview() {
    DialogActionTxtHor(
        modifier = Modifier.padding(top = ACTION_MARGIN_TOP),
        negativeTxt = "キャンセル",
        positiveTxt = "課金する",
        onNegClick = {},
        onPosClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xfff3f3f5)
@Composable
private fun DialogBtnPreview() {
    DialogBtnPositive(modifier = Modifier.fillMaxWidth(), txt = "按钮", onClick = {})
}

@Preview(showBackground = true)
@Composable
private fun DialogRemindPreview() {
    DialogRemindCheck(txt = "今日分の無料ストー今日分の無料ストー今日分の無料ストー今日分の無料ストー") {

    }
}