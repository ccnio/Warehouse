package com.ccnio.mdialog

import android.util.ArrayMap
import android.view.View

/**
 * Created by jianfeng.li on 2021/6/13.
 */
class DialogHolder(val view: View, private val dialog: MDialog) :
    View.OnClickListener {
    private var onClick: ((View, MDialog) -> Unit)? = null
    private val viewMap = ArrayMap<Int, View>()

    fun addClickIds(clickIds: IntArray) {
        clickIds.forEach { getView<View>(it).setOnClickListener(this) }
    }

    override fun onClick(v: View) {
        onClick?.invoke(v, dialog)
    }

    fun setOnViewClick(onClick: ((View, MDialog) -> Unit)?) {
        this.onClick = onClick
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : View> getView(id: Int): T {
        return viewMap.getOrPut(id) { view.findViewById(id) } as T
    }
}