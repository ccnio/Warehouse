package com.ware.dialog

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.moji.dialog.specific.FigureDialog
import com.moji.dialog.specific.MJSpecificDialog

import com.ware.R

/**
 * DialogFragment 是 Fragment 的子类，有着和 Fragment 基本一样的生命周期，使用 DialogFragment 来管理对话框，当旋转屏幕和按下后退键的时候可以更好的管理其生命周期
 * 在手机配置变化导致 Activity 需要重新创建时，例如旋转屏幕，基于 DialogFragment 的对话框将会由 FragmentManager 自动重建，然而基于 Dialog 实现的对话框却没有这样的能力
 */

class DialogActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        Log.e("lijf", "activity onCreate: ")
        findViewById<View>(R.id.start_dia).setOnClickListener(this)
        findViewById<View>(R.id.start_common).setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("lijf", "activity onDestroy: ")
    }

    override fun onClick(v: View) {
        val figureDialog = FigureDialog().bindView(R.string.zodiac_not_login_temp, R.string.zodiac_yet_login, R.string.zodiac_login_desc,
                R.drawable.zodiac_dialog_top, R.string.zodiac_login)
                .setOnDismissListener(object : MJSpecificDialog.OnDismissListener {
                    override fun onDismiss(dialog: DialogInterface?) {
                    }
                })
                .setOnClickListener(object : MJSpecificDialog.OnViewClickListener {
                    override fun onClick(viewHolder: DialogViewHolder, view: View, dialog: MJSpecificDialog) {
                        when (view.id) {
                            R.id.mTipView -> {
                                dialog.dismissAllowingStateLoss()
                            }
                            R.id.mConfirmView -> {
                                dialog.dismissAllowingStateLoss()
                            }
                            R.id.mCloseView -> {
                                dialog.dismissAllowingStateLoss()
                            }
                        }
                    }
                })
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(figureDialog, null)
        transaction.commitAllowingStateLoss()
    }

    private fun startDialog() {
        CommonDialog(this).show()
    }

    companion object {

        private val TAG = "DialogActivity"
    }
}
