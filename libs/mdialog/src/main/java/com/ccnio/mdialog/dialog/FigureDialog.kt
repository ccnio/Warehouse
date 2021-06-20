package com.ccnio.mdialog.dialog

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ccnio.mdialog.MDialog
import com.ccnio.mdialog.R

private const val TAG = "FigureDialog"

class FigureDialog : MDialog() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.getView<TextView>(R.id.labelView).paint.flags = Paint.UNDERLINE_TEXT_FLAG.or(Paint.ANTI_ALIAS_FLAG)
    }

    private var onClick: ((Unit) -> Unit)? = null
    fun setOnLabelClick(onClick: (Unit) -> Unit) = apply { this.onClick = onClick }

    override fun params(): Params {
        return ParamBuilder().setGravity(Gravity.BOTTOM, offsetY = 300)
            .setWidth(600)
            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            .setLayoutRes(R.layout.layout_dia_fragment)
//            .setView(LayoutInflater.from(this).inflate(R.layout.layout_figure_dialog, null))
            .setOnDismiss {
                Log.d(TAG, "setOnDismiss: ")
            }
            .addClickIds(R.id.labelView)
            .setOnViewClick { view, _ ->
                when (view.id) {
                    R.id.labelView -> onClick?.invoke(Unit)
//                    R.id.mTipView -> Toast.makeText(this@DialogActivity, "tip", Toast.LENGTH_SHORT).show()
                }
            }
            .setOnViewBind { view ->
                view.findViewById<TextView>(R.id.labelView).text = "提示评论家"
            }
            .create()
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        view.mTipView.paint.flags = Paint.UNDERLINE_TEXT_FLAG.or(Paint.ANTI_ALIAS_FLAG)
//        setViewClick(R.id.mConfirmView, R.id.mTipView)
//        view.mTipView.paint.flags = Paint.UNDERLINE_TEXT_FLAG.or(Paint.ANTI_ALIAS_FLAG)
//        if (mTipRes != 0) {
//            view.mTipView.text = Utils.getStringById(mTipRes)
//        }
//
//        if (mTitleRes != 0) {
//            view.mTitleView.text = Utils.getStringById(mTitleRes)
//        }
//
//        if (mDescRes != 0) {
//            view.mDescView.text = Utils.getStringById(mDescRes)
//        }
//
//        if (mImageRes != 0) {
//            view.mImageView.setImageResource(mImageRes)
//        }
//
//        if (mConfirmRes != 0) {
//            view.mConfirmView.text = Utils.getStringById(mConfirmRes)
//        }
//        super.onViewCreated(view, savedInstanceState)
//    }

    private var mTipRes: Int = 0
    private var mTitleRes: Int = 0
    private var mDescRes: Int = 0
    private var mImageRes: Int = 0
    private var mConfirmRes: Int = 0

//    fun bindView(@StringRes tip: Int, @StringRes title: Int, @StringRes desc: Int, @DrawableRes image: Int, @StringRes confirm: Int): FigureDialog {
//        mTipRes = tip
//        mTitleRes = title
//        mDescRes = desc
//        mImageRes = image
//        mConfirmRes = confirm
//        return this
//    }

}