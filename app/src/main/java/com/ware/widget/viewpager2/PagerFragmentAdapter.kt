package com.ware.widget.viewpager2

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

/**
 * Created by jianfeng.li on 20-6-16.
 */
private const val COUNT = 10
private const val TAG = "Pager2FragmentAdapter"

class Pager2FragmentAdapter(val activity: FragmentActivity) : FragmentStateAdapter(activity) {
    /**
     * for get current fragment
     */
    private val fragments = hashMapOf<Int, PagerFragment>()

    override fun getItemCount(): Int {
        return COUNT
    }


    override fun createFragment(position: Int): PagerFragment {
        val instance = PagerFragment.instance(position)
        fragments[position] = instance
        return instance
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        Log.d(TAG, "onBindViewHolder: $position")
    }

    /**
     * 当前pager离开屏幕时调用，打印这个pager的位置. 4->5: print 4
     */
    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d(TAG, "onViewDetachedFromWindow: ${holder.layoutPosition}")
    }

    fun getCurrentPager(curPos: Int) = fragments[curPos]
}