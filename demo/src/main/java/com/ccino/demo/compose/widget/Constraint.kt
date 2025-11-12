package com.ccino.demo.compose.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ccino.demo.R

@Composable
fun ConstraintCase(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        MemberCoinInviteStep()
        MsgConstraint()
    }
}

@Composable
fun MemberCoinInviteStep(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xffabe0df))
            .wrapContentHeight()
    ) {
        // Create references for each composable, similar to IDs in XML
        val (shareIcon, dash, doneIcon,
            shareText, doneText) = createRefs()

        // Create a horizontal chain：参数顺序决定了链的方向，默认彼此之间已经加了依赖
        // ChainStyle为Packed时，彼此间的 margin 需要通过 Modifier 来设置, 通过 constrainBlock ❌ 不生效（被 chain 接管）
        createHorizontalChain(shareIcon, dash, doneIcon, chainStyle = ChainStyle.Packed)

        // Share Icon
        Image(
            painter = painterResource(id = R.drawable.member_invite_share),
            contentDescription = null,
            modifier = Modifier.constrainAs(shareIcon) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(dash.start)
            }
        )

        // Dash
        DashedLine(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .width(28.dp)
                .height(1.dp)
                .constrainAs(dash) {
                    top.linkTo(shareIcon.top)
                    bottom.linkTo(shareIcon.bottom)
                }
        )


        // Done Icon
        Image(
            painter = painterResource(id = R.drawable.member_invite_price),
            contentDescription = null,
            modifier = Modifier.constrainAs(doneIcon) {
                top.linkTo(shareIcon.top)
                bottom.linkTo(shareIcon.bottom)
                start.linkTo(dash.end)
                end.linkTo(parent.end)
            }
        )


        // Share Text
        Text(
            text = "invite_share", // stringResource(R.string.member_invite_share),
            fontSize = 12.sp, // Approximating the style
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = 50.dp) // 限制最大宽度
                .constrainAs(shareText) {
                    top.linkTo(shareIcon.bottom, margin = 4.dp)
                    // Center horizontally to the icon
                    centerHorizontallyTo(shareIcon)
                }
        )

        // Done Text
        Text(
            text = "member_invite_done", // stringResource(R.string.member_invite_done),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(doneText) {
                top.linkTo(doneIcon.bottom, margin = 4.dp)
                centerHorizontallyTo(doneIcon)
            }
        )
    }
}

/**
 * A composable that draws a dashed horizontal line.
 */
@Composable
fun DashedLine(modifier: Modifier = Modifier, color: Color = Color.LightGray) {
    Canvas(modifier) {
        /**
         *  floatArrayOf(10f, 5f)•数组必须包含偶数个元素。：“画10像素，空5像素”，如此循环，
         *  第二个参数: 0f决定了虚线模式从哪里开始。•0f: 表示从路径的起点开始时，不做任何偏移，直接开始应用“画10空5”的模式。•如果是 3f，第一段实线会比正常的10像素短3像素，只有7像素长。
         */
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
        drawLine(
            color = color,
            start = Offset(0f, center.y),
            end = Offset(size.width, center.y),
            strokeWidth = size.height,
            pathEffect = pathEffect
        )
    }
}


/**
 * ImageView scaleType 在 Compose 中的对应关系：
 * - android:scaleType="centerCrop" -> contentScale = ContentScale.Crop + alignment = Alignment.Center
 * - android:scaleType="fitCenter" -> contentScale = ContentScale.Fit + alignment = Alignment.Center
 * - android:scaleType="fitXY" -> contentScale = ContentScale.FillBounds
 * - android:scaleType="centerInside" -> contentScale = ContentScale.Inside + alignment = Alignment.Center
 * - android:scaleType="center" -> contentScale = ContentScale.None + alignment = Alignment.Center
 *
 * layout_constrainedWidth="true" 对应 width = Dimension.fillToConstraints
 */
@Composable
fun MsgConstraint(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Create references for each composable
        val (avatar, nameView, levelView, actionView) = createRefs()

        // Avatar - ImageView
        // android:scaleType="centerCrop" 对应 contentScale = ContentScale.Crop 和 alignment = Alignment.Center
        Image(
            painter = painterResource(id = R.drawable.member_invite_price),
            contentDescription = null,
            contentScale = ContentScale.Crop, // 对应 android:scaleType="centerCrop"
            alignment = Alignment.Center, // 居中裁剪
            modifier = Modifier.constrainAs(avatar) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = 14.dp)
            }
        )

        // Name View - TextView with constrained width
        Text(
            text = "mack zhangmack zhangmack zhangmack zhang",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(nameView) {
                top.linkTo(avatar.top)
                bottom.linkTo(avatar.bottom)
                start.linkTo(avatar.end)
                end.linkTo(levelView.start)
                // layout_constrainedWidth="true" 对应 width = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }
        )

        // Level View - TextView
        Text(
            text = "13",
            color = Color(0xFF03DAC5), // teal_200
            modifier = Modifier.constrainAs(levelView) {
                top.linkTo(nameView.top)
                bottom.linkTo(nameView.bottom)
                start.linkTo(nameView.end, margin = 5.dp)
                end.linkTo(actionView.start, margin = 20.dp)
            }
        )

        // Action View - Button
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.constrainAs(actionView) {
                top.linkTo(avatar.top)
                bottom.linkTo(avatar.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Text("go")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MemberCoinInviteStepPreview() {
    MemberCoinInviteStep()
}

@Preview(showBackground = true)
@Composable
fun MsgPreview() {
    MsgConstraint()
}
