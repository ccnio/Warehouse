package com.edreamoon.warehouse.systip.recyclerview


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.moji.redleaves.fragment.CnLeafFragment
import com.moji.redleaves.fragment.JpLeafFragment

class LeafPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val mFragments = mutableListOf<Fragment>()

    init {
        mFragments.add(CnLeafFragment())
        mFragments.add(JpLeafFragment())
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }


    override fun getCount(): Int {
        return mFragments.size
    }

}
