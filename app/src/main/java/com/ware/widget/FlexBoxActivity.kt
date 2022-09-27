package com.ware.widget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ware.R
import com.ware.widget.adpater.FlexAdapter
import kotlinx.android.synthetic.main.activity_flex_layout.*


class FlexBoxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flex_layout)
        val flexManager = FlexboxLayoutManager(this)
        flexManager.flexWrap = FlexWrap.WRAP
//        flexManager.set.;....
        flexManager.justifyContent = JustifyContent.FLEX_START

        val decoration = FlexboxItemDecoration(this)
        decoration.setOrientation(FlexboxItemDecoration.HORIZONTAL)
//        decoration.setDrawable(resources.getDrawable(R.drawable.flexbox_divider))
        mRecyclerView.addItemDecoration(decoration)
//        mRecyclerView.addItemDecoration(RecyclerDecor(Utils.dp2px(10f).toInt(), Utils.dp2px(10f).toInt(), false)) invalid

        mRecyclerView.layoutManager = flexManager
        mRecyclerView.adapter = FlexAdapter().apply {  }
    }
}
