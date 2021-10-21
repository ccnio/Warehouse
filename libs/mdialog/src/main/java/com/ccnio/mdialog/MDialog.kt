package com.ccnio.mdialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentManager

/**
 * Created by jianfeng.li on 2021/6/13.
 */
private const val SAVED_INSTANCE = "saved_instance"
private const val KEY_SAVED_INSTANCE2 = "dialog_"
private const val TAG = "MDialog"

/**
 * # 通过建造者模式来构造Dialog时，需要外部禁止直接通过构造函数创建.如何保证？
 * # View/各种Listener 本身不可序列化，但在 onSaveInstanceState 居然可以恢复(serializable,parcelable都可以)，原因可能是非跨进程操作。跨进程操作就不允许这样操作
 */
open class MDialog : BaseDialogFragment() {
    private var controller: DialogController = DialogController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.getParcelable<DialogController>(SAVED_INSTANCE)?.let { controller = it }
        val serializable = savedInstanceState?.getParcelable(KEY_SAVED_INSTANCE2) as SeriaBean?
        Log.d(TAG, "onCreate: ${serializable?.notSeria?.desc}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVED_INSTANCE, controller)
        outState.putParcelable(KEY_SAVED_INSTANCE2, SeriaBean("test name", SeriaBean.NotSeria("desc")))
        Log.d(TAG, "onSaveInstanceState: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun bindView(view: View) {
        val holder = DialogHolder(view, this)
        getClickIds()?.let { holder.addClickIds(it) }
        holder.setOnViewClick(getOnViewClick())
        getOnViewBind()?.invoke(view)
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
    @LayoutRes override fun getLayoutRes() = controller.layoutRes
    override fun isCanceledOnTouchOutside() = controller.isCanceledOnTouchOutside

    protected open fun getOnDismissListener() = controller.onDismissListener
    protected open fun getOnCancelListener() = controller.onCancelListener
    protected open fun getClickIds() = controller.clickIds
    protected open fun getOnViewClick() = controller.onViewClick
    protected open fun getOnViewBind() = controller.onViewBind
    protected open fun getFragmentTag() = controller.tag

    class Builder {
        private val param = DialogController.Params()

        fun setLayoutRes(@LayoutRes layoutRes: Int) = apply { param.layoutRes = layoutRes }
        fun setWidth(width: Int) = apply { param.width = width }
        fun setHeight(height: Int) = apply { param.height = height }
        fun setAnimationRes(@StyleRes res: Int) = apply { param.animationStyle = res }
        fun isCanceledOnTouchOutside(cancel: Boolean) = apply { param.isCanceledOnTouchOutside = cancel }
        fun setTag(tag: String) = apply { param.tag = tag }

        @JvmOverloads
        fun setGravity(gravity: Int, offsetX: Int = 0, offsetY: Int = 0) = apply {
            param.gravity = gravity
            param.offsetX = offsetX
            param.offsetY = offsetY
        }

        fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) = apply { param.onDismissListener = onDismissListener }
        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener) = apply { param.onCancelListener = onCancelListener }
        fun setView(view: View) = apply { param.view = view }
        fun setOnViewClick(onViewClick: (View, MDialog) -> Unit) = apply { param.onViewClick = onViewClick }
        fun addClickIds(vararg clickIds: Int) = apply { param.clickIds = clickIds }
        fun setOnViewBind(onViewBind: (View) -> Unit) = apply { param.onViewBind = onViewBind }
        fun create() = MDialog().apply { param.apply(controller) }
    }
}