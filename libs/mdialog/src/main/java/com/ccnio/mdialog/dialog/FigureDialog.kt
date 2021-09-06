package com.ccnio.mdialog.dialog

import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ccnio.mdialog.MDialog
import com.ccnio.mdialog.R

class FigureDialog : MDialog() {

    private var onClick: ((Unit) -> Unit)? = null
    fun setOnLabelClick(onClick: (Unit) -> Unit) = apply { this.onClick = onClick }

    private var mTipRes: Int = 0
    private var mTitleRes: Int = 0
    private var mDescRes: Int = 0
    private var mImageRes: Int = 0
    private var mConfirmRes: Int = 0

    override fun getGravity() = Gravity.BOTTOM

    override fun getLayoutRes() = R.layout.layout_figure_dialog

    fun bindView(@StringRes tip: Int, @StringRes title: Int, @StringRes desc: Int, @DrawableRes image: Int, @StringRes confirm: Int): FigureDialog {
        mTipRes = tip
        mTitleRes = title
        mDescRes = desc
        mImageRes = image
        mConfirmRes = confirm
        return this
    }

}