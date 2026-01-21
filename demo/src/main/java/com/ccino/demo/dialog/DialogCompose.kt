package com.ccino.demo.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
internal fun BtnDialogContent(
    title: String? = null, desc: String? = null,
    checked: Boolean? = null, checkTxt: String? = null, onCheckClick: ((Boolean) -> Unit)? = null,
    posBtnTxt: String? = null, negBtnTxt: String? = null, minorTxt: String? = null,
    onPosClick: (() -> Unit)? = null, onNegClick: (() -> Unit)? = null, onMinorClick: (() -> Unit)? = null,
) {
    ConstraintLayout(modifier = dialogModifierContainer.then(Modifier.padding(bottom = DIALOG_PADDING_BOTTOM, start = DIALOG_PADDING_TOP, end = DIALOG_PADDING_TOP))) {
        val (headerRef, checkRef, posBtnRef, negBtnRef, minorBtnRef) = createRefs()
        DialogTitleAndDesc(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(headerRef) {
                }, title = title, desc = desc
        )

        DialogRemindCheck(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(checkRef) {
                    top.linkTo(headerRef.bottom, margin = DIALOG_CHECK_MARGIN_TOP)
                }, checked = checked, txt = checkTxt, onCheckedChange = onCheckClick
        )

        val btnTopAnchor = createBottomBarrier(checkRef, headerRef)
        DialogBtnNegative(txt = negBtnTxt, onClick = onNegClick, modifier = Modifier.constrainAs(negBtnRef) {
            top.linkTo(btnTopAnchor, margin = ACTION_MARGIN_TOP)
            end.linkTo(if (!posBtnTxt.isNullOrEmpty()) posBtnRef.start else parent.end, margin = BTN_GAP)
            width = Dimension.fillToConstraints
            start.linkTo(parent.start)
        })

        DialogBtnPositive(txt = posBtnTxt, onClick = onPosClick, modifier = Modifier.constrainAs(posBtnRef) {
            top.linkTo(btnTopAnchor, margin = ACTION_MARGIN_TOP)
            start.linkTo(if (negBtnTxt.isNullOrEmpty()) parent.start else negBtnRef.end)
            width = Dimension.fillToConstraints
            end.linkTo(parent.end)
        })

        DialogMinorText(txt = minorTxt, onClick = onMinorClick, modifier = Modifier.constrainAs(minorBtnRef) {
            top.linkTo(posBtnRef.bottom, margin = MINOR_MARGIN_TOP)
            centerHorizontallyTo(parent)
        })
    }
}


@Composable
fun BtnDialog(
    title: String? = null, desc: String? = null,
    checked: Boolean? = null, checkTxt: String? = null, onCheckClick: ((Boolean) -> Unit)? = null,
    posTxt: String? = null, negTxt: String? = null, minorTxt: String? = null,
    onPosClick: (() -> Unit)? = null, onNegClick: (() -> Unit)? = null, onMinorClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    DialogFrame(onDismiss = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = DIALOG_BOTTOM_OFFSET),
            contentAlignment = Alignment.Center
        ) {
            BtnDialogContent(
                title = title, desc = desc,
                checked = checked, checkTxt = checkTxt, onCheckClick = onCheckClick,
                posBtnTxt = posTxt, negBtnTxt = negTxt, minorTxt = minorTxt,
                onPosClick = onPosClick, onNegClick = onNegClick, onMinorClick = onMinorClick
            )
        }
    }
}

@Composable
internal fun TextDialogContent(
    title: String? = null, desc: String? = null,
    posTxt: String? = null, negTxt: String? = null, onPosClick: (() -> Unit)? = null, onNegClick: (() -> Unit)? = null,
    checked: Boolean? = null, checkTxt: String? = null, onCheckClick: ((Boolean) -> Unit)? = null
) {
    Column(modifier = dialogModifierContainer) {
        DialogTitleAndDesc(
            modifier = Modifier.padding(horizontal = DIALOG_PADDING_HOR),
            title = title,
            desc = desc
        )
        DialogRemindCheck(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    start = DIALOG_PADDING_HOR,
                    end = DIALOG_PADDING_HOR,
                    top = DIALOG_CHECK_MARGIN_TOP
                ), checked = checked, txt = checkTxt
        ) {
            onCheckClick?.invoke(it)
        }
        DialogActionTxtHor(
            modifier = Modifier.padding(top = ACTION_MARGIN_TOP),
            negativeTxt = negTxt,
            positiveTxt = posTxt,
            onNegClick = onNegClick,
            onPosClick = onPosClick
        )
    }
}


@Composable
fun TextDialog(
    title: String? = null, desc: String? = null,
    posTxt: String? = null, negTxt: String? = null, onPosClick: (() -> Unit)? = null, onNegClick: (() -> Unit)? = null,
    checked: Boolean? = null, checkTxt: String? = null, onCheckClick: ((Boolean) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    DialogFrame(onDismiss = onDismiss) {
        TextDialogContent(
            title = title, desc = desc,
            checked = checked, checkTxt = checkTxt, onCheckClick = onCheckClick,
            posTxt = posTxt, negTxt = negTxt, onPosClick = onPosClick, onNegClick = onNegClick
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun BtnDialogPreview() {
    val checkState = remember { mutableStateOf(true) }
    val showDialog = remember { mutableStateOf(true) }
    if (showDialog.value) {
        BtnDialog(
            title = "生成回数チャージ切れ！",
            desc = "今日分の無料ストー\\nリー やりとり回数がなくなっちゃった！\\n 課金するとすぐ復活→ \\n【有料会員】or【エネルギーチャージ】",
            posTxt = "确定",
//        negBtnTxt = "取消",
            minorTxt = "次要操作",
            checked = checkState.value,
            checkTxt = "勾选后，我同意相关条款",
            onCheckClick = { checkState.value = it },
            onDismiss = { showDialog.value = false },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xfff3f3f5)
@Composable
private fun TxtPreviewDialog() {
//    val checkState = remember { mutableStateOf(true) }
    val showDialog = remember { mutableStateOf(true) }
    if (showDialog.value) {
        TextDialog(
            title = "生成回数チャージ切れ！",
            desc = "今日分の無料ストー\\nリー やりとり回数がなくなっちゃった！\\n 課金するとすぐ復活→ \\n【有料会員】or【エネルギーチャージ】",
            checkTxt = "勾选后，我同",
            checked = true,
            posTxt = "确定",
            negTxt = "取消",
            onPosClick = {
                showDialog.value = false
            },
            onNegClick = {
                showDialog.value = false
            },
            onDismiss = {
                showDialog.value = false
            }
        )
    }
}