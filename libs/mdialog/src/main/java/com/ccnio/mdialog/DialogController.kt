package com.ccnio.mdialog

import android.content.DialogInterface
import android.os.Parcel
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes

/**
 * Created by jianfeng.li on 2021/6/13.
 */
private const val DEFAULT_WIDTH = ViewGroup.LayoutParams.MATCH_PARENT
private const val DEFAULT_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT
private const val DEFAULT_GRAVITY = Gravity.CENTER

internal class DialogController() : Parcelable {
    var isCanceledOnTouchOutside: Boolean = false
    var onViewClick: ((View, MDialog) -> Unit)? = null
    var onViewBind: ((DialogHolder) -> Unit)? = null
    var clickIds: IntArray? = null

    @StyleRes
    var animationStyle = 0
    var tag: String? = null
    var offsetY = 0
    var offsetX = 0
    var gravity = DEFAULT_GRAVITY
    var height = DEFAULT_HEIGHT
    var width = DEFAULT_WIDTH
    var onKeyListener: DialogInterface.OnKeyListener? = null
    var onDismissListener: DialogInterface.OnDismissListener? = null
    var onCancelListener: DialogInterface.OnCancelListener? = null
    var cancelable = false

    @LayoutRes
    var layoutRes: Int = 0
    var view: View? = null

    constructor(parcel: Parcel) : this()

    override fun writeToParcel(parcel: Parcel, flags: Int) = Unit

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DialogController> {
        override fun createFromParcel(parcel: Parcel): DialogController {
            return DialogController(parcel)
        }

        override fun newArray(size: Int): Array<DialogController?> {
            return arrayOfNulls(size)
        }
    }

    internal class Params {
        fun apply(controller: DialogController) {
            controller.offsetX = offsetX
            controller.offsetY = offsetY
            controller.gravity = gravity
            controller.width = width
            controller.height = height
            controller.cancelable = cancelable
            controller.onKeyListener = onKeyListener
            controller.onDismissListener = onDismissListener
            controller.onCancelListener = onCancelListener
            controller.layoutRes = layoutRes
            controller.animationStyle = animationStyle
            controller.view = view
            controller.clickIds = clickIds
            controller.onViewClick = onViewClick
            controller.onViewBind = onViewBind
            controller.isCanceledOnTouchOutside = isCanceledOnTouchOutside
            controller.tag = tag
        }

        var tag: String? = null
        var isCanceledOnTouchOutside: Boolean = false
        var onViewBind: ((DialogHolder) -> Unit)? = null
        var onViewClick: ((View, MDialog) -> Unit)? = null
        var clickIds: IntArray? = null

        @StyleRes
        var animationStyle = 0
        var view: View? = null
        var offsetX = 0
        var offsetY = 0
        var gravity = DEFAULT_GRAVITY
        var height = DEFAULT_HEIGHT
        var width = DEFAULT_WIDTH
        var onKeyListener: DialogInterface.OnKeyListener? = null
        var onDismissListener: DialogInterface.OnDismissListener? = null
        var onCancelListener: DialogInterface.OnCancelListener? = null
        var cancelable = true

        @LayoutRes
        var layoutRes: Int = 0
    }
}