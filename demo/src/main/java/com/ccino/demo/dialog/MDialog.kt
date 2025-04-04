package com.ccino.demo.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager

/**
 * Created by jianfeng.li on 2021/6/13.
 */

open class MDialog : BaseDialogFragment() {
    private val controller: DialogController = DialogController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
           dismiss()
            return
        }
    }

    override fun setUpWindow(window: Window) {
        super.setUpWindow(window)
        if(controller.dimBehind){
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    override fun bindView(view: View) {
        val holder = DialogHolder(view, this)
        getClickIds()?.let { holder.addClickIds(it) }
        holder.setOnViewClick(getOnViewClick())
        getOnViewBind()?.invoke(holder)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        getOnDismissListener()?.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        getOnCancelListener()?.onCancel(dialog)
    }

    fun show(manager: FragmentManager) {
        show(manager, getFragmentTag())
    }

    override fun getWidth() = controller.width
    override fun getHeight() = controller.height
    override fun getGravity() = controller.gravity
    override fun getOffsetX() = controller.offsetX
    override fun getOffsetY() = controller.offsetY
    override fun getAnimation() = controller.animationStyle
    override fun getDialogView() = controller.view

    @LayoutRes
    override fun getLayoutRes() = controller.layoutRes
    override fun isCanceledOnTouchOutside() = controller.isCanceledOnTouchOutside

    protected open fun getOnDismissListener() = controller.onDismissListener
    protected open fun getOnCancelListener() = controller.onCancelListener
    protected open fun getClickIds() = controller.clickIds
    protected open fun getOnViewClick() = controller.onViewClick
    protected open fun getOnViewBind() = controller.onViewBind
    protected open fun getFragmentTag() = controller.tag

    open class Builder {
        private val param = DialogController.Params()

        fun setLayoutRes(@LayoutRes layoutRes: Int) = apply { param.layoutRes = layoutRes }
        fun setWidth(width: Int) = apply { param.width = width }
        fun setHeight(height: Int) = apply { param.height = height }
        fun setAnimationRes(@StyleRes res: Int) = apply { param.animationStyle = res }
        fun isCanceledOnTouchOutside(cancel: Boolean) =
            apply { param.isCanceledOnTouchOutside = cancel }

        fun setTag(tag: String) = apply { param.tag = tag }

        fun setDimbehind(dimBehind: Boolean) = apply { param.dimBehind = dimBehind }

        @JvmOverloads
        fun setGravity(gravity: Int, offsetX: Int = 0, offsetY: Int = 0) = apply {
            param.gravity = gravity
            param.offsetX = offsetX
            param.offsetY = offsetY
        }

        fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) =
            apply { param.onDismissListener = onDismissListener }

        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener) =
            apply { param.onCancelListener = onCancelListener }

        fun setView(view: View) = apply { param.view = view }
        fun setOnViewClick(onViewClick: (View, MDialog) -> Unit) =
            apply { param.onViewClick = onViewClick }

        fun addClickIds(vararg clickIds: Int) = apply { param.clickIds = clickIds }
        fun setOnViewBind(onViewBind: (DialogHolder) -> Unit) =
            apply { param.onViewBind = onViewBind }

        fun create() = MDialog().apply { param.apply(controller) }
    }
}