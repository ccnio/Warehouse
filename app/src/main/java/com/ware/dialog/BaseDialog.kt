package com.moji.dialog.specific

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.ware.R
import com.ware.dialog.DialogViewHolder

open class MJSpecificDialog : DialogFragment() {
    private var mClickIds = mutableSetOf<Int>()
    private var mLayoutRes: Int = 0
    private var mWidth: Int = DEFAULT_SIZE
    private var mHeight: Int = DEFAULT_SIZE
    private var mBindViewListener: OnBindViewListener? = null
    private var mDismissListener: OnDismissListener? = null

    fun setViewClick(@IdRes vararg ids: Int): MJSpecificDialog {
        mClickIds.addAll(ids.toMutableSet())
        return this
    }

    open fun getViewClick(): MutableSet<Int> {
        return mClickIds
    }

    interface OnBindViewListener {
        fun bindView(viewHolder: DialogViewHolder)
    }

    fun setOnBindViewListener(listener: OnBindViewListener): MJSpecificDialog {
        mBindViewListener = listener
        return this
    }

    open fun getBindViewListener(): OnBindViewListener? {
        return mBindViewListener
    }

    interface OnDismissListener {
        fun onDismiss(dialog: DialogInterface?)
    }

    fun setOnDismissListener(listener: OnDismissListener): MJSpecificDialog {
        mDismissListener = listener
        return this
    }

    open fun getDismissListener(): OnDismissListener? {
        return mDismissListener
    }

    interface OnViewClickListener {
        fun onClick(viewHolder: DialogViewHolder, view: View, dialog: MJSpecificDialog)
    }

    private var mOnClickListener: OnViewClickListener? = null

    fun setOnClickListener(listener: OnViewClickListener): MJSpecificDialog {
        mOnClickListener = listener
        return this
    }

    fun getClickListener(): OnViewClickListener? {
        return mOnClickListener
    }

    open fun getOnClickListener(): OnViewClickListener? {
        return mOnClickListener
    }

    fun cancelable(cancel: Boolean): MJSpecificDialog {
        isCancelable = cancel
        return this
    }


    fun setLayoutRes(@LayoutRes id: Int): MJSpecificDialog {
        mLayoutRes = id
        return this
    }


    @LayoutRes
    open fun getLayoutRes(): Int {
        return mLayoutRes
    }


    open fun setWidth(width: Int): MJSpecificDialog {
        mWidth = width
        return this
    }

    open fun getWidth(): Int {
        return mWidth
    }


    open fun setHeight(height: Int): MJSpecificDialog {
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
        const val TAG = "MJSpecificDialog"

    }
}
