package com.ware.component.permissionutil

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData

/**
 * Created by jianfeng.li on 2020/3/22.
 */
private const val TAG = "permissions"

class PermissionUtils {
    constructor(activity: FragmentActivity) {
        liveFragment = getInstance(activity.supportFragmentManager)
    }

    constructor(fragment: Fragment) {
        liveFragment = getInstance(fragment.childFragmentManager)
    }

    @Volatile
    private var liveFragment: LiveFragment? = null

    private fun getInstance(fragmentManager: FragmentManager) =
            liveFragment ?: synchronized(this) {
                liveFragment
                        ?: if (fragmentManager.findFragmentByTag(TAG) == null) LiveFragment().run {
                            fragmentManager.beginTransaction().add(this, TAG).commitNow()
                            this
                        } else fragmentManager.findFragmentByTag(TAG) as LiveFragment
            }

    fun request(vararg permissions: String): MutableLiveData<PermissionResult> {
        return this.requestArray(permissions)
    }

    fun requestArray(permissions: Array<out String>): MutableLiveData<PermissionResult> {
        liveFragment!!.requestPermissions(permissions)
        return liveFragment!!.liveData
    }
}