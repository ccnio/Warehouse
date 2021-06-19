package com.ware.dialog

//import com.ccnio.mdialog.MDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.mdialog.MDialog
import com.ccnio.mdialog.MDialog_Builder
import com.ware.R
import com.ware.face.DisplayUtil
import kotlinx.android.synthetic.main.activity_dialog.*

/**
 * DialogFragment 是 Fragment 的子类，有着和 Fragment 基本一样的生命周期，使用 DialogFragment 来管理对话框，当旋转屏幕和按下后退键的时候可以更好的管理其生命周期
 *
 *
 * # 最好每次show时都要创建Dialog,不然第二次show同一个dialog时会报内存泄露
 * # 手机配置变化时
 *  1. 配置变化会导致 Activity 需要重新创建时，例如旋转屏幕，基于 DialogFragment 的对话框将会由 FragmentManager 自动重建, 而基于 Dialog 实现的对话框却没有这样的能力
 *  2. 但view的各种状态,比如edittext里的内容需要自己去通过savedInstanceState处理"保存&恢复"
 */

class DialogActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(R.style.Dark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        dialogBuilderView.setOnClickListener(this)
        mDialogView.setOnClickListener(this)
        mDialogView.setOnClickListener(this)
        commonView.setOnClickListener(this)
//        findViewById<View>(R.id.start_dia).setOnClickListener(this)
//        findViewById<View>(R.id.start_common).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialogBuilderView -> dialogBuilder()
            R.id.mDialogView -> mDialog()
            R.id.commonView -> commonDialog()
//            R.id.extendView -> extendDialog()
        }
//        val figureDialog = FigureDialog().bindView(R.string.zodiac_not_login_temp, R.string.zodiac_yet_login, R.string.zodiac_login_desc,
//                R.drawable.zodiac_dialog_top, R.string.zodiac_login)
//                .setGravity(Gravity.BOTTOM, 0, Utils.dp2px(10f).toInt())
//                .setOnDismissListener(object : MDialog.OnDismissListener {
//                    override fun onDismiss(dialog: DialogInterface?) {
//                    }
//                })
//                .setOnClickListener(object : MDialog.OnViewClickListener {
//                    override fun onClick(viewHolder: DialogViewHolder, view: View, dialog: MDialog) {
//                        when (view.id) {
//                            R.id.mTipView -> {
//                                dialog.dismissAllowingStateLoss()
//                            }
//                            R.id.mConfirmView -> {
//                                dialog.dismissAllowingStateLoss()
//                            }
//                            R.id.mCloseView -> {
//                                dialog.dismissAllowingStateLoss()
//                            }
//                        }
//                    }
//                })
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.add(figureDialog, null)
//        transaction.commitAllowingStateLoss()
    }

    private val commonDialog by lazy { DiaFragment() }
    private fun commonDialog() {
        DiaFragment().show(supportFragmentManager, null)
    }

    private val params = MDialog.ParamBuilder()
            .setGravity(Gravity.BOTTOM, offsetY = 300)
            .setWidth(DisplayUtil.dip2px(200f))
            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            .setLayoutRes(R.layout.layout_figure_dialog)
            .setAnimationRes(R.style.dialog_animate)
//            .setView(LayoutInflater.from(this).inflate(R.layout.layout_figure_dialog, null))
            .setOnDismiss { Toast.makeText(this, "dismiss", Toast.LENGTH_SHORT).show() }
            .addClickIds(R.id.mConfirmView, R.id.mTipView)
            .setOnViewClick { view, _ ->
                when (view.id) {
                    R.id.mConfirmView -> Toast.makeText(this@DialogActivity, "confirm", Toast.LENGTH_SHORT).show()
                    R.id.mTipView -> Toast.makeText(this@DialogActivity, "tip", Toast.LENGTH_SHORT).show()
                }
            }
            .setOnViewBind { view ->
                view.findViewById<TextView>(R.id.mTipView).text = "提示评论家"
            }
            .create()
    private val mDialog by lazy { MDialog(params) }
    private fun mDialog() {
        mDialog.show(supportFragmentManager, null)
    }

    private fun dialogBuilder() {
        MDialog_Builder.Builder(supportFragmentManager)
                .setGravity(Gravity.BOTTOM, offsetY = 300)
                .setWidth(DisplayUtil.getScreenWidth())
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setLayoutRes(R.layout.layout_figure_dialog)
                .setAnimationRes(R.style.dialog_animate)
                .setView(LayoutInflater.from(this).inflate(R.layout.layout_figure_dialog, null))
                .setOnDismissListener { Toast.makeText(this, "dismiss", Toast.LENGTH_SHORT).show() }
                .addClickIds(R.id.mConfirmView, R.id.mTipView)
                .setOnViewClick { view, _ ->
                    when (view.id) {
                        R.id.mConfirmView -> Toast.makeText(this@DialogActivity, "confirm", Toast.LENGTH_SHORT).show()
                        R.id.mTipView -> Toast.makeText(this@DialogActivity, "tip", Toast.LENGTH_SHORT).show()
                    }
                }
                .setOnViewBind { view ->
                    view.findViewById<TextView>(R.id.mTipView).text = "提示评论家"
                }
                .create()
                .show()
    }
}
