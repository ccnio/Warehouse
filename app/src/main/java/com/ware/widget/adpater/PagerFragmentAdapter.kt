package com.ware.widget.adpater

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ware.component.fragment.ContentFragment

/**
 * Created by jianfeng.li on 2020/3/14.
 */
class PagerFragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments = mutableListOf<Fragment>()

    init {
        for (i in 0..3) {
            val frag = ContentFragment.newInstance(i.toString())
            fragments.add(frag)
        }
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}