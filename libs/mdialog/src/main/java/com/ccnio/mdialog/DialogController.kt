package com.ccnio.mdialog

import android.content.DialogInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentManager
import java.io.Serializable

/**
 * Created by jianfeng.li on 2021/6/13.
 */
private const val DEFAULT_WIDTH = ViewGroup.LayoutParams.WRAP_CONTENT
private const val DEFAULT_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT
private const val DEFAULT_GRAVITY = Gravity.CENTER

internal class DialogController : Serializable {
    var onViewClick: ((View, MDialog) -> Unit)? = null
    var onViewBind: ((View) -> Unit)? = null

    var clickIds: IntArray? = null

    @StyleRes
    var animationStyle = 0
    val tag: String? = null

    lateinit var fragmentManager: FragmentManager
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

    fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        view?.parent?.let { (it as ViewGroup).removeView(view) }
        return view ?: inflater.inflate(layoutRes, container)
    }

    class Params(val fragmentManager: FragmentManager) {
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
            controller.fragmentManager = fragmentManager
            controller.layoutRes = layoutRes
            controller.animationStyle = animationStyle
            controller.view = view
            controller.clickIds = clickIds
            controller.onViewClick = onViewClick
            controller.onViewBind = onViewBind
        }

        var onViewBind: ((View) -> Unit)? = null
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