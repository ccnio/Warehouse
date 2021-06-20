package com.ware.dialog

//import com.ccnio.mdialog.MDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.mdialog.MDialog
import com.ccnio.mdialog.MDialog_Builder
import com.ccnio.mdialog.dialog.FigureDialog
import com.ware.R
import com.ware.face.DisplayUtil
import kotlinx.android.synthetic.main.activity_dialog.*

/**
 * DialogFragment 是 Fragment 的子类，有着和 Fragment 基本一样的生命周期，使用 DialogFragment 来管理对话框，当旋转屏幕和按下后退键的时候可以更好的管理其生命周期
 *
 *
 * # 内存泄露 DialogFragment 因为 DismissListener 会存在内存泄露
 *  1. 最好每次show时都要创建Dialog,不然第二次show同一个dialog时会报内存泄露
 *  2. 手机配置变化因为会走onDismiss，也会泄露
 * # 手机配置变化时
 *  1. 配置变化会导致 Activity 重新创建(对象会重新创建)，DialogFragment 会由 FragmentManager 自动重建, 而基于 Dialog 的对话框却没有这样的能力
 *  2. 自动创建时，DialogFragment 内的成员需要自己通过savedInstanceState去处理，且view的各种状态,比如edittext里的内容也是开发者自己处理
 * # MDialog事项
 *  1. 如何限制 Params 只能 MDialog 内创建，目前只能限制在本 module 内。
 *  2. 必须手动处理配置变化时的恢复。因此Params需要支持序列化
 *  3. 通过Builder来管理参数。但为了Dialog能被继承或者直接创建，在构造函数加入了Builder，而不是直接通过Builder构建Dialog
 */
private const val TAG = "DialogActivity"

class DialogActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(R.style.Dark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        dialogBuilderView.setOnClickListener(this)
        mDialogView.setOnClickListener(this)
        mDialogView.setOnClickListener(this)
        commonView.setOnClickListener(this)
        extendView.setOnClickListener(this)
//        findViewById<View>(R.id.start_dia).setOnClickListener(this)
//        findViewById<View>(R.id.start_common).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialogBuilderView -> dialogBuilder()
            R.id.mDialogView -> mDialog()
            R.id.commonView -> commonDialog()
            R.id.extendView -> extendDialog()
        }
    }

    private fun extendDialog() {
        FigureDialog()
            .setOnLabelClick {
                Toast.makeText(this, "label click", Toast.LENGTH_SHORT).show()

            }
            .show(supportFragmentManager)
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
    private val mDialog by lazy {
        Log.d(TAG, "mDialog lazy")
        MDialog(params)
    }

    private fun mDialog() {
        Log.d(TAG, "mDialog: show mDialog=$mDialog")
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }
}
