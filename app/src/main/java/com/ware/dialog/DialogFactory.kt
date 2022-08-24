package com.ware.dialog

/**
 * Created by jianfeng.li on 2022/8/24.
 */


//@SuppressWarnings(": LongParameterList")
//fun genCommonDialog(
//    title: String,
//    desc: String,
//    positiveDesc: String,
//    negativeDesc: String,
//    onNegativeClick: ((View, ZDialog) -> Unit)? = null,
//    onPositiveClick: ((View, ZDialog) -> Unit)? = null,
//    onDismiss: (() -> Unit)? = null,
//    canceledOnTouchOutside: Boolean = false,
//    width: Int = DIALOG_WIDTH_SMALL,
//    gravity: Int = Gravity.CENTER, offsetX: Int = 0, offsetY: Int = 0,
//): ZDialog {
//    val dialog = ZDialog.Builder().setGravity(gravity, offsetX, offsetY)
//        .setWidth(width)
//        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
//        .setLayoutRes(R.layout.dialog_layout_common)
//        .setOnDismissListener { onDismiss?.invoke() }
//        .isCanceledOnTouchOutside(canceledOnTouchOutside)
//        .addClickIds(R.id.negative_view, R.id.positive_view)
//        .setOnViewClick { view, dialog ->
//            when (view.id) {
//                R.id.negative_view -> onNegativeClick?.invoke(view, dialog)
//                R.id.positive_view -> onPositiveClick?.invoke(view, dialog)
//            }
//        }
//        .setOnViewBind { holder ->
//            val padding =
//                if (width == DIALOG_WIDTH_MEDIUM) DIALOG_PADDING_HOR_MEDIUM else DIALOG_PADDING_HOR_SMALL
//            (holder.view as ViewGroup).updatePadding(left = padding, right = padding)
//
//            holder.getView<TextView>(R.id.title_view).text = title
//            holder.getView<TextView>(R.id.desc_view).text = desc
//            holder.getView<TextView>(R.id.negative_view).text = negativeDesc
//            holder.getView<TextView>(R.id.positive_view).text = positiveDesc
//        }
//        .create()
//    return dialog
//}