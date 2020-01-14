package com.ware.widget.recycler

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.ware.R
import com.ware.common.BaseActivity
import com.ware.systip.recyclerview.RecyclerDecor
import kotlinx.android.synthetic.main.activity_load_more.*

class LoadMoreActivity : BaseActivity() {

    //    private val mAdapter by lazy { CommonMoreAdapter(this) }
    private val mAdapter by lazy { MoreAdapter(this) }
    private var mDataIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_more)

        /**
         * GridLayoutManager
        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
        return if (position == 0) 3 else 1
        }
        }
        mRecyclerView.addItemDecoration(RecyclerDecor(10, 10, false))
         */


        /**
         * LinearLayoutManager
         */
        val layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
//        mRecyclerView.setEnableMore(false)
        mRecyclerView.addItemDecoration(RecyclerDecor(10))


        mRecyclerView.setAdapter(mAdapter)
        mRecyclerView.setLoadListener(object : MoreRecyclerView.LoadListener {
            override fun loadMore() {
                mRecyclerView.postDelayed({ generateData(false) }, 1000)
            }
        })

        generateData(true)
    }

    private fun generateData(isSet: Boolean) {
        val list = mutableListOf<String>()
        for (i in 0..54) {
            mDataIndex += 1
            Log.d("LoadMoreActivity", "generateData: $mDataIndex")
            list.add(mDataIndex.toString())
        }
        if (isSet) {
            mAdapter.setData(list)
        } else {
            mRecyclerView.updateStatus(Status.MORE, false)
            mAdapter.addData(list)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRecyclerView.removeCallbacks(null)
    }
}
