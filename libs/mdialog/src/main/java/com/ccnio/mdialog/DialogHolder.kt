package com.ccnio.mdialog

import android.view.View

/**
 * Created by jianfeng.li on 2021/6/13.
 */
internal class DialogHolder(private val view: View, private val dialog: MDialog_Builder) : View.OnClickListener {
    private var onClick: ((View, MDialog_Builder) -> Unit)? = null
    fun addClickIds(clickIds: IntArray) {
        clickIds.forEach { view.findViewById<View>(it).setOnClickListener(this) }
    }

    override fun onClick(v: View) {
        onClick?.invoke(v, dialog)
    }

    fun setOnViewClick(onClick: ((View, MDialog_Builder) -> Unit)?) {
        this.onClick = onClick
    }
}