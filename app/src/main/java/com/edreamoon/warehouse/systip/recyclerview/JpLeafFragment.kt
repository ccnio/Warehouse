package com.moji.redleaves.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edreamoon.warehouse.R
import com.edreamoon.warehouse.systip.recyclerview.LeafFragment

class JpLeafFragment : LeafFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_jp_leaf, container, false)
        return view
    }
}
