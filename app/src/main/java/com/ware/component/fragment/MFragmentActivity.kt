package com.ware.component.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import com.ware.R
import com.ware.component.BaseActivity
import kotlinx.android.synthetic.main.activity_fragmnet.*

/**
 * 1. setMaxLifecycle：设置最大生命周期，替代setUserVisibleHint并且可以修改当前生命周期
 * 2. setMaxLifecycle带来了生命周期设置，FragmentPagerAdapter中也据此进行了适配 见：MFragmentPagerAdapter
 */
class MFragmentActivity : BaseActivity(R.layout.activity_fragmnet), View.OnClickListener {
    private val frag = ContentFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //1.add配合setMaxLifecycle(Lifecycle.State.CREATED)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment, frag);
        fragmentTransaction.setMaxLifecycle(frag, Lifecycle.State.RESUMED)//print ContentFragment: onCreate/onResume
        //fragmentTransaction.setMaxLifecycle(frag, Lifecycle.State.CREATED)//print ContentFragment: onCreate(no onResume,no view show)
        fragmentTransaction.commit()

        lifeCycleBt.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.lifeCycleBt -> {
                //2. 对正在Resume的Fragment setMaxLifecycle
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.setMaxLifecycle(frag, Lifecycle.State.CREATED) //ContentFragment: onPause/onStop(view cannot see)
                //fragmentTransaction.setMaxLifecycle(frag, Lifecycle.State.STARTED) //ContentFragment: onViewCreated/onStart
                fragmentTransaction.commit()
            }
        }
    }
}
