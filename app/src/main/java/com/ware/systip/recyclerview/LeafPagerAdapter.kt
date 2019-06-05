package com.ware.systip.recyclerview


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.moji.redleaves.fragment.CnLeafFragment
import com.moji.redleaves.fragment.JpLeafFragment

class LeafPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {
    private val mFragments = mutableListOf<androidx.fragment.app.Fragment>()

    init {
        mFragments.add(CnLeafFragment())
        mFragments.add(JpLeafFragment())
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return mFragments[position]
    }


    override fun getCount(): Int {
        return mFragments.size
    }

}
