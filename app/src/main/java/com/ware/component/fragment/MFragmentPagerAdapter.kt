package com.ware.component.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by jianfeng.li on 2020/3/14.
 * 懒加载新方案
 * 1. viewpager+fragment 查看setPrimaryItem源码
 *过去使用setUserVisibleHint(now is BEHAVIOR_SET_USER_VISIBLE_HINT)来控制Fragment懒加载，在最新版的FragmentPagerAdapter里有新思路:
 *切换到BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT模式，在Fragment onResume里判断，更符合显示逻辑，只需要在onResume加一个isLoad变量即可:
 *
 * 2.ViewPager2 ViewPager2默认关闭了预加载，此时只需将请求放到onStart中即可。设置了offScreenPageLimit(1)的呢？
 * 预加载的Fragment的生命周期仅仅执行到了onStart。在FragmentStateAdapter中设置了setMaxLifecycle(fragment, STARTED)，因此，
 * 此时处理懒加载问题其实和ViewPager的懒加载新方案如出一辙了，仅仅需要添加一个boolean值即可。
 */
class MFragmentPagerAdapter(fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {//BEHAVIOR_SET_USER_VISIBLE_HINT Deprecated
    //FragmentPagerAdapter setPrimaryItem中有如下
    /*if (mCurrentPrimaryItem != null) {
        mCurrentPrimaryItem.setMenuVisibility(false);
        if (mBehavior == BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.setMaxLifecycle(mCurrentPrimaryItem, Lifecycle.State.STARTED);
        } else {
            mCurrentPrimaryItem.setUserVisibleHint(false);//Deprecated
        }
    }
    fragment.setMenuVisibility(true);
    if (mBehavior == BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
    } else {
        fragment.setUserVisibleHint(true);//Deprecated
    }
    mCurrentPrimaryItem = fragment;*/

    /* viewpager+fragment懒加载
    private var isFirstLoad = true
    override fun onResume() {
        super.onResume()
        if (isFirstLoad) {
        	isFirstLoad = false
            loadData()
        }
    }*/


    private val mList = mutableListOf<Fragment>()
    override fun getItem(position: Int): Fragment {
        return mList[position]
    }

    override fun getCount(): Int {
        return mList.size
    }
}