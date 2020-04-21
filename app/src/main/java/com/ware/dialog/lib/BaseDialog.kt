package com.ware.dialog.lib

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ware.R

abstract class BaseDialog : DialogFragment() {
    private var mOffsetY: Int = 0
    private var mOffsetX: Int = 0
    private var mGravity: Int = Gravity.BOTTOM
    private var mClickIds = mutableSetOf<Int>()
    private var mLayoutRes: Int = 0
    private var mWidth: Int = DEFAULT_SIZE
    private var mHeight: Int = DEFAULT_SIZE
    private var mBindViewListener: OnBindViewListener? = null
    private var mDismissListener: OnDismissListener? = null
    protected val tController: TController = TController()


//    @LayoutRes
//    abstract fun getLayoutRes(): Int
//
//    protected abstract fun bindView(view: View)
//
//    protected abstract fun getDialogView(): View?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val window = dialog?.window
        window?.let {
            window.setGravity(mGravity)

            // after that, setting values for x and y works "naturally"
            val params = window.attributes
            params.x = mOffsetX
            params.y = mOffsetY
            window.attributes = params
        }

//        val view = getDialogView() ?: inflater.inflate(getLayoutRes(), container)
//        bindView(view)
        return view
    }

    fun getOnViewClickListener(): OnViewClickListener? {
        return tController.onViewClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setCanceledOnTouchOutside(isCancelableOutside())
            if (dialog!!.window != null && getDialogAnimationRes() > 0) {
                dialog!!.window!!.setWindowAnimations(getDialogAnimationRes())
            }
            if (getOnKeyListener() != null) {
                dialog!!.setOnKeyListener(getOnKeyListener())
            }
        }
        super.onViewCreated(view, savedInstanceState)

//        initView(view)
    }

    protected open fun getOnKeyListener(): DialogInterface.OnKeyListener? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(constrainSize(getWidth()), ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(R.color.transparent)
        }
    }

    open fun getGravity() = Gravity.CENTER


    fun setViewClick(@IdRes vararg ids: Int): BaseDialog {
        mClickIds.addAll(ids.toMutableSet())
        return this
    }

    open fun getViewClick(): MutableSet<Int> {
        return mClickIds
    }

    fun setOnBindViewListener(listener: OnBindViewListener): BaseDialog {
        mBindViewListener = listener
        return this
    }

    fun setGravity(gravity: Int, x: Int, y: Int): BaseDialog {
        mGravity = gravity
        mOffsetX = x
        mOffsetY = y
        return this
    }

    fun show(manager: FragmentManager) {
        val transaction = manager.beginTransaction()
        transaction.add(this, null)
        transaction.commitAllowingStateLoss()
    }

    protected open fun isCancelableOutside(): Boolean {
        return true
    }

    //获取弹窗显示动画,子类实现
    protected open fun getDialogAnimationRes(): Int {
        return 0
    }

    open fun getBindViewListener(): OnBindViewListener? {
        return mBindViewListener
    }

    interface OnDismissListener {
        fun onDismiss(dialog: DialogInterface?)
    }

    fun setOnDismissListener(listener: OnDismissListener): BaseDialog {
        mDismissListener = listener
        return this
    }

    open fun getDismissListener(): OnDismissListener? {
        return mDismissListener
    }
//
//    interface OnViewClickListener {
//        fun onClick(viewHolder: DialogViewHolder, view: View, dialog: BaseDialog)
//    }

    private var mOnClickListener: OnViewClickListener? = null


    fun setOnClickListener(listener: OnViewClickListener): BaseDialog {
        mOnClickListener = listener
        return this
    }


    fun getClickListener(): OnViewClickListener? {
        return mOnClickListener
    }


    open fun getOnClickListener(): OnViewClickListener? {
        return mOnClickListener
    }

    fun cancelable(cancel: Boolean): BaseDialog {
        isCancelable = cancel
        return this
    }


    fun setLayoutRes(@LayoutRes id: Int): BaseDialog {
        mLayoutRes = id
        return this
    }

    open fun setWidth(width: Int): BaseDialog {
        mWidth = width
        return this
    }

    open fun getWidth(): Int {
        return mWidth
    }

    open fun setHeight(height: Int): BaseDialog {
        mHeight = height
        return this
    }


    open fun getHeight(): Int {
        return mHeight
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        getDismissListener()?.onDismiss(dialog)
    }

    open fun initView(view: View) {
//        val viewHolder = DialogViewHolder(view, this)

        //大部分对话框右上角有一个关闭按钮，为减少调用代码，在此共同处理，但viewID要为 mCloseView
//        view.findViewById<View>(R.id.mCloseView).setOnClickListener(viewHolder)

//        for (id in getViewClick()) {
//            viewHolder.addOnClickListener(id)
//        }

//        getBindViewListener()?.bindView(viewHolder)
//        getBindViewListener()?.bindView(view)
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }

    private fun constrainSize(size: Int) = if (size == DEFAULT_SIZE) DEFAULT_SIZE else size

//
//    class Builder() {
//        private val params: TController.TParams = TController.TParams()
//
//        fun create() = with(BaseDialog()) {
//            params.apply(tController)
//            this
//        }
//
//        fun setLayoutRes(@LayoutRes layoutRes: Int) = apply { params.mLayoutRes = layoutRes }
//        fun setDialogView(view: View) = apply { params.mDialogView = view }
//        fun setWidth(width: Int) = apply { params.mWidth = width }
//        fun setHeight(height: Int) = apply { params.mHeight = height }
//        fun setGravity(gravity: Int) = apply { params.mGravity = gravity }
//        fun setCancelableOutside(cancelable: Boolean) = apply { params.mIsCancelableOutside = cancelable }
//        fun setCancelable(cancelable: Boolean) = apply { params.mIsCancelableOutside = cancelable }
//        fun setOnDismissListener(dismissListener: DialogInterface.OnDismissListener?) = apply { params.mOnDismissListener = dismissListener }
//        fun setOnBindViewListener(listener: OnBindViewListener?) = apply { params.bindViewListener = listener }
//        fun addOnClickListener(vararg ids: Int) = apply { params.ids = ids }
//        fun setOnViewClickListener(listener: OnViewClickListener?) = apply { params.mOnViewClickListener = listener }
//        fun setDialogAnimationRes(animationRes: Int) = apply { params.mDialogAnimationRes = animationRes }
//    }


    companion object {
        const val DEFAULT_SIZE = ViewGroup.LayoutParams.WRAP_CONTENT
        const val TAG = "BaseDialog"
    }
}
