package com.ccino.ware.widget

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.ware.R
import com.ware.databinding.ActivityTabLayoutBinding
import com.ware.jetpack.viewbinding.viewBinding

private const val TAG = "TabLayoutActivityL"

class TabLayoutActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityTabLayoutBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_layout)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabSelected: ")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }
}