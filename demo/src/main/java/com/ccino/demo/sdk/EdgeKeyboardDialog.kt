package com.ccino.demo.sdk

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.ccino.demo.R
import com.ccino.demo.dialog.BaseDialogFragment


class EdgeKeyboardDialog : BaseDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 全屏注意：全屏时需要设置，非全屏可以不设置，不然无法全屏
        setStyle(STYLE_NO_TITLE, R.style.DialogFull)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 处理键盘弹起时的布局调整
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val barInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeAndBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())

            // 只对内容容器应用底部内边距，标题栏保持固定
            v.setPadding(
                imeAndBarInsets.left,
                barInsets.top, // 顶部不设置ime padding，让标题栏固定在顶部
                imeAndBarInsets.right,
                imeAndBarInsets.bottom // 底部根据键盘高度调整
            )
            insets
        }
    }

    override fun getWidth(): Int {
        return ViewGroup.LayoutParams.MATCH_PARENT
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
        return R.layout.dialog_keyboard
    }
}