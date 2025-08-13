package com.ccino.demo.sdk

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.ccino.demo.R
import com.ccino.demo.dialog.BaseDialogFragment


class EdgeDialog : BaseDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 全屏注意：全屏时需要设置，非全屏可以不设置，不然无法全屏
        setStyle(STYLE_NO_TITLE, R.style.DialogFull)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //有时需要在 onCreateView 之后设置，否则可能不生效,换着位置看效果
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            // 获取状态栏、导航栏和系统栏的 Insets
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars()) //Insets{left=0, top=81, right=0, bottom=0}
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars()) //Insets{left=0, top=0, right=0, bottom=130}
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) //Insets{left=0, top=81, right=0, bottom=130}
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun getWidth(): Int {
        return ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // 设置背景为透明，否则可能会有默认的圆角白色背景
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // 设置全屏
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.let { window ->
                WindowCompat.setDecorFitsSystemWindows(window, false) // 设置全屏
//                val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
//                   windowInsetsController.hide(WindowInsetsCompat.Type.systemBars()) // 隐藏system bars
            }

        }
    }

    override fun getHeight(): Int {
        return ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun bindView(view: View) {
    }

    override fun getLayoutRes(): Int {
        return R.layout.dialogue_bar
    }
}


class SimpleTestDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("测试")
            .setMessage("应该有蒙层")
            .create()
    }
}