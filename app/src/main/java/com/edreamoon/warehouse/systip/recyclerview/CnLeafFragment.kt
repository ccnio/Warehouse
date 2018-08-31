package com.moji.redleaves.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edreamoon.warehouse.R
import com.edreamoon.warehouse.systip.recyclerview.LeafFragment
import com.edreamoon.warehouse.systip.recyclerview.TouchedAdapter
import kotlinx.android.synthetic.main.fragment_cn_leaf.*
import java.util.*


class CnLeafFragment : LeafFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val strings = ArrayList<String>()
        for (i in 0..31) {
            strings.add("pos: $i")
        }
        mRecyclerView.adapter = TouchedAdapter(strings)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cn_leaf, container, false)
    }

}
