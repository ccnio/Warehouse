package com.ccnio.mdialog

import android.view.View

/**
 * Created by jianfeng.li on 2021/6/13.
 */
internal class DialogHolder(private val view: View, private val dialog: MDialog) : View.OnClickListener {
    private var onClick: ((View, MDialog) -> Unit)? = null
    fun addClickIds(clickIds: IntArray) {
        clickIds.forEach { view.findViewById<View>(it).setOnClickListener(this) }
    }

    override fun onClick(v: View) {
        onClick?.invoke(v, dialog)
    }

    fun setOnViewClick(onClick: ((View, MDialog) -> Unit)?) {
        this.onClick = onClick
    }
}