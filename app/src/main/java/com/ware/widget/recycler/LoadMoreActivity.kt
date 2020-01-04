package com.ware.widget.recycler

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.ware.R
import com.ware.common.BaseActivity
import com.ware.systip.recyclerview.RecyclerDecor
import kotlinx.android.synthetic.main.activity_load_more.*

class LoadMoreActivity : BaseActivity() {

    private val mAdapter by lazy { MoreAdapter(this) }
    private var mDataIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_more)
        val layoutManager = GridLayoutManager(this, 3)
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.addItemDecoration(RecyclerDecor(10, 10, false))
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0 || position == layoutManager.itemCount - 1) 3 else 1
            }
        }

        mRecyclerView.adapter = mAdapter

        mRecyclerView.addOnScrollListener(mAdapter.mOnScrollListener)
        mAdapter.setLoadListener(object : MoreAdapter.LoadListener {
            override fun onLoad() {
                mRecyclerView.postDelayed({ generateData(false) }, 2000)
            }

        })
        generateData(true)
    }

    fun generateData(isSet: Boolean) {
        val list = mutableListOf<String>()
        for (i in 0..46) {
            mDataIndex += 1
            Log.d("LoadMoreActivity", "generateData: $mDataIndex")
            list.add(mDataIndex.toString())
        }
        if (isSet) {
            mAdapter.setData(list)
        } else {
            mAdapter.addData(list)
        }
    }
}
