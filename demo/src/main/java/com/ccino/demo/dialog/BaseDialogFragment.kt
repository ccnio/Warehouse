package com.ccino.demo.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment

/**
 * Created by jianfeng.li on 2021/6/13.
 */
abstract class BaseDialogFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog?.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
            setGravity(getGravity())
            attributes.dimAmount = 0.8f
            attributes.x = getOffsetX()
            attributes.y = getOffsetY()
            attributes = attributes
            setWindowAnimations(getAnimation())
            setUpWindow(this)
        }

        return getDialogView()?.apply { (parent as? ViewGroup)?.removeView(this) }
            ?: inflater.inflate(getLayoutRes(), container).apply { bindView(this) }

    }

    open fun setUpWindow(window: Window) {

    }

    override fun onStart() {
        super.onStart()
        dialog?.run {
            setCanceledOnTouchOutside(isCanceledOnTouchOutside())
        }
        dialog?.window?.run {
            setLayout(getWidth(), getHeight())
        }
    }

    protected abstract fun bindView(view: View)
    protected open fun getWidth() = ViewGroup.LayoutParams.WRAP_CONTENT
    protected open fun getHeight() = ViewGroup.LayoutParams.WRAP_CONTENT

    @StyleRes
    protected open fun getAnimation() = 0
    protected open fun getGravity() = Gravity.CENTER
    protected open fun getOffsetX() = 0
    protected open fun getOffsetY() = 0
    protected open fun getDialogView(): View? = null

    @LayoutRes
    protected abstract fun getLayoutRes(): Int
    protected open fun isCanceledOnTouchOutside() = false
}