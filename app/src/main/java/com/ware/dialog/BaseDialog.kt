package com.moji.dialog.specific

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ware.R
import com.ware.dialog.DialogViewHolder

open class BaseDialog : DialogFragment() {
    private var mOffsetY: Int = 0
    private var mOffsetX: Int = 0
    private var mGravity: Int = Gravity.BOTTOM
    private var mClickIds = mutableSetOf<Int>()
    private var mLayoutRes: Int = 0
    private var mWidth: Int = DEFAULT_SIZE
    private var mHeight: Int = DEFAULT_SIZE
    private var mBindViewListener: OnBindViewListener? = null
    private var mDismissListener: OnDismissListener? = null

    fun setViewClick(@IdRes vararg ids: Int): BaseDialog {
        mClickIds.addAll(ids.toMutableSet())
        return this
    }

    open fun getViewClick(): MutableSet<Int> {
        return mClickIds
    }

    interface OnBindViewListener {
        fun bindView(viewHolder: DialogViewHolder)
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

    interface OnViewClickListener {
        fun onClick(viewHolder: DialogViewHolder, view: View, dialog: BaseDialog)
    }

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


    @LayoutRes
    open fun getLayoutRes(): Int {
        return mLayoutRes
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

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        getDismissListener()?.onDismiss(dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val window = dialog.window
        window?.setGravity(mGravity)

        // after that, setting values for x and y works "naturally"
        val params = window.attributes
        params.x = mOffsetX
        params.y = mOffsetY
        window.attributes = params
        return inflater.inflate(getLayoutRes(), container)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    open fun initView(view: View) {
        val viewHolder = DialogViewHolder(view, this)

        //大部分对话框右上角有一个关闭按钮，为减少调用代码，在此共同处理，但viewID要为 mCloseView
        view.findViewById<View>(R.id.mCloseView).setOnClickListener(viewHolder)

        for (id in getViewClick()) {
            viewHolder.addOnClickListener(id)
        }

        getBindViewListener()?.bindView(viewHolder)
//        getBindViewListener()?.bindView(view)
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(constrainSize(getWidth()), ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }

    private fun constrainSize(size: Int): Int {
        return if (size == DEFAULT_SIZE) DEFAULT_SIZE else size
    }

    companion object {
        const val DEFAULT_SIZE = ViewGroup.LayoutParams.WRAP_CONTENT
        const val TAG = "BaseDialog"

    }
}
