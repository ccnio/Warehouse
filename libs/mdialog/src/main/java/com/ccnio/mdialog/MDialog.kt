package com.ccnio.mdialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Created by jianfeng.li on 2021/6/13.
 */
private const val KEY_SAVED_INSTANCE = "dialog_instance"
private const val KEY_SAVED_INSTANCE2 = "dialog_"
private const val TAG = "MDialog"

/**
 * View,回调本身不可序列化，但在 onSaveInstanceState 居然可以恢复(serializable,parcelable都可以)
 * 原因可能是非跨进程操作。跨进程操作就不允许这样操作
 */
class MDialog : DialogFragment() {
    private var controller: DialogController = DialogController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.getSerializable(KEY_SAVED_INSTANCE)?.let { controller = it as DialogController }
        val serializable = savedInstanceState?.getParcelable(KEY_SAVED_INSTANCE2) as SeriaBean?
        Log.d(TAG, "onCreate: ${serializable?.notSeria?.desc}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_SAVED_INSTANCE, controller)
        outState.putParcelable(KEY_SAVED_INSTANCE2, SeriaBean("test name", SeriaBean.NotSeria("desc")))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.run {
            requestFeature(Window.FEATURE_NO_TITLE)
            setGravity(getGravity())
            attributes.x = getGravityOffsetX()
            attributes.y = getGravityOffsetY()
            attributes = attributes
            setWindowAnimations(getAnimationStyle())
        }
        dialog?.setOnDismissListener(getOnDismissListener())
        return controller.createView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(getWidth(), getHeight())
        }
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        val holder = DialogHolder(view, this)
        getClickIds()?.let { holder.addClickIds(it) }
        holder.setOnViewClick(getOnClick())
        getOnViewBind()?.invoke(view)
    }

    open fun getWidth() = controller.width
    open fun getHeight() = controller.height
    open fun getGravity() = controller.gravity
    open fun getGravityOffsetX() = controller.offsetX
    open fun getGravityOffsetY() = controller.offsetY
    open fun getOnDismissListener() = controller.onDismissListener
    open fun getAnimationStyle() = controller.animationStyle
    open fun getClickIds() = controller.clickIds
    open fun getOnClick() = controller.onViewClick
    open fun getOnViewBind() = controller.onViewBind

    fun show() = controller.fragmentManager.beginTransaction().apply {
        add(this@MDialog, controller.tag)
        commitAllowingStateLoss()
    }

    class Builder(fragmentManager: FragmentManager) {
        private val param = DialogController.Params(fragmentManager)

        open fun setLayoutRes(@LayoutRes layoutRes: Int) = apply { param.layoutRes = layoutRes }
        open fun setWidth(width: Int) = apply { param.width = width }
        open fun setHeight(height: Int) = apply { param.height = height }
        open fun setAnimationRes(@StyleRes res: Int) = apply { param.animationStyle = res }

        @JvmOverloads
        fun setGravity(gravity: Int, offsetX: Int = 0, offsetY: Int = 0) = apply {
            param.gravity = gravity
            param.offsetX = offsetX
            param.offsetY = offsetY
        }

        fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) =
            apply { param.onDismissListener = onDismissListener }

        fun create(): MDialog {
            val dialog = MDialog()
            param.apply(dialog.controller)
            return dialog
        }

        fun show() = create().apply { show() }
        fun setView(view: View) = apply { param.view = view }
        fun setOnViewClick(onViewClick: (View, MDialog) -> Unit) = apply { param.onViewClick = onViewClick }
        fun addClickIds(vararg clickIds: Int) = apply { param.clickIds = clickIds }
        fun setOnViewBind(onViewBind: (View) -> Unit) = apply { param.onViewBind = onViewBind }
    }

    fun <T : View> View.getView(id: Int): T {
        return findViewById<T>(id)
    }
}