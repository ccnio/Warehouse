package com.ccino.demo.dialog

import android.view.View
import androidx.compose.ui.platform.ComposeView

class ComposeDialog : MDialog() {
    private var composeView: ComposeView? = null

    override fun getView(): View? {
        val ctx = context
        if (ctx == null) return null

        if (composeView == null) composeView = ComposeView(ctx)
        return composeView
    }
}