package com.ware.component.fragment

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

/**
 * Created by jianfeng.li on 2020/3/14.
 */
//not use
class MFactory(val arg: String) : FragmentFactory() { //Fragment 传递参数

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val name = ::ContentFragment.name
        Log.d("MFactory", "instantiate: $name ; className = $className")
        if (className == name) {
            return ContentFragment()
        }
        return super.instantiate(classLoader, className)
    }

    fun mulParams(vararg args: String) {
        args.size
    }
}