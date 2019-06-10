package com.moji.dialog.specific

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.edreamoon.Utils
import com.ware.R
import kotlinx.android.synthetic.main.layout_figure_dialog.view.*

class FigureDialog : MJSpecificDialog() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.mTipView.paint.flags = Paint.UNDERLINE_TEXT_FLAG.or(Paint.ANTI_ALIAS_FLAG)
        setViewClick(R.id.mConfirmView, R.id.mTipView)
        view.mTipView.paint.flags = Paint.UNDERLINE_TEXT_FLAG.or(Paint.ANTI_ALIAS_FLAG)
        if (mTipRes != 0) {
            view.mTipView.text = Utils.getStringById(mTipRes)
        }

        if (mTitleRes != 0) {
            view.mTitleView.text = Utils.getStringById(mTitleRes)
        }

        if (mDescRes != 0) {
            view.mDescView.text = Utils.getStringById(mDescRes)
        }

        if (mImageRes != 0) {
            view.mImageView.setImageResource(mImageRes)
        }

        if (mConfirmRes != 0) {
            view.mConfirmView.text = Utils.getStringById(mConfirmRes)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private var mTipRes: Int = 0
    private var mTitleRes: Int = 0
    private var mDescRes: Int = 0
    private var mImageRes: Int = 0
    private var mConfirmRes: Int = 0

    fun bindView(@StringRes tip: Int, @StringRes title: Int, @StringRes desc: Int, @DrawableRes image: Int, @StringRes confirm: Int): FigureDialog {
        mTipRes = tip
        mTitleRes = title
        mDescRes = desc
        mImageRes = image
        mConfirmRes = confirm
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.layout_figure_dialog
    }
}