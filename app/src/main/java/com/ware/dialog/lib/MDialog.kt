package com.ware.dialog.lib

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ware.R

open class MDialog private constructor() : DialogFragment() {
    protected val tController: TController = TController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setGravity(getGravity())
            attributes.x = getOffsetX()
            attributes.y = getOffsetY()
            setWindowAnimations(getDialogAnimationRes())
        }
        return getDialogView() ?: inflater.inflate(getLayoutRes(), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.apply {
            setCanceledOnTouchOutside(cancelableOutside())
            setOnKeyListener(getOnKeyListener())
        }
        super.onViewCreated(view, savedInstanceState)
        if (getViewClick() != null || getBindViewListener() != null) {
            initView(view)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(getWidth(), getHeight())
            setBackgroundDrawableResource(R.color.transparent)
        }
    }

    open fun initView(view: View) {
        val viewHolder = BindViewHolder(view, this)
        getViewClick()?.let {
            for (id in it) {
                viewHolder.addOnClickListener(id)
            }
        }
        getBindViewListener()?.bindView(viewHolder)
    }

    fun show(manager: FragmentManager) {
        val transaction = manager.beginTransaction()
        transaction.add(this, null)
        transaction.commitAllowingStateLoss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        getDismissListener()?.onDismiss(dialog)
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }

    fun getOnViewClickListener(): OnViewClickListener? = tController.onViewClickListener
    override fun isCancelable() = tController.cancelable
    protected fun getLayoutRes(): Int = tController.layoutRes
    protected fun cancelableOutside() = tController.isCancelableOutside
    protected fun getDialogView() = tController.dialogView
    protected open fun getOnKeyListener(): DialogInterface.OnKeyListener? = tController.onKeyListener
    fun getWidth() = tController.width
    fun getHeight() = tController.height
    open fun getGravity() = tController.gravity
    open fun getOffsetX() = tController.dx
    open fun getOffsetY() = tController.dy
    open fun getDismissListener(): DialogInterface.OnDismissListener? = tController.onDismissListener
    protected open fun getDialogAnimationRes() = tController.dialogAnimationRes
    open fun getViewClick(): IntArray? = tController.ids
    open fun getBindViewListener(): OnBindViewListener? = tController.onBindViewListener
//    fun getClickListener(): OnViewClickListener? = tController.onViewClickListener

    class Builder {
        private val params: TController.TParams = TController.TParams()

        fun create() = with(MDialog()) {
            params.apply(tController)
            this
        }

        fun setLayoutRes(@LayoutRes layoutRes: Int) = apply { params.mLayoutRes = layoutRes }
        fun setDialogView(view: View) = apply { params.mDialogView = view }
        fun setWidth(width: Int) = apply { params.mWidth = width }
        fun setHeight(height: Int) = apply { params.mHeight = height }
        fun setGravity(gravity: Int, dx: Int = 0, dy: Int = 0) = apply {
            params.mGravity = gravity
            params.dx = dx
            params.dy = dy
        }

        fun setCancelableOutside(cancelable: Boolean) = apply { params.mIsCancelableOutside = cancelable }
        fun setCancelable(cancelable: Boolean) = apply { params.mIsCancelableOutside = cancelable }
        fun setOnDismissListener(dismissListener: DialogInterface.OnDismissListener?) = apply { params.mOnDismissListener = dismissListener }
        fun setOnBindViewListener(listener: OnBindViewListener?) = apply { params.bindViewListener = listener }
        fun addOnClickListener(vararg ids: Int) = apply { params.ids = ids }
        fun setOnViewClickListener(listener: OnViewClickListener?) = apply { params.mOnViewClickListener = listener }
        fun setDialogAnimationRes(animationRes: Int) = apply { params.mDialogAnimationRes = animationRes }
    }


    companion object {
        const val DEFAULT_SIZE = ViewGroup.LayoutParams.WRAP_CONTENT
        const val TAG = "BaseDialog"
    }
}
