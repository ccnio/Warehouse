package com.edreamoon.warehouse.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.edreamoon.warehouse.R
import kotlinx.android.synthetic.main.view_group.view.*


class PaneShareView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        initView()
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.view_group, this, true)
        view.mGridView.adapter = MGridAdapter(this.context)
    }
}
