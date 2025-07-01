package com.ccino.demo.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.ccino.demo.R
import com.ccino.demo.databinding.ActivityRecyclerViewBinding

/**
 * # 生命周期
 * - 初始时 onBindViewHolder -> onViewAttachedToWindow。
 * - 当一个 item 刚滑出屏幕后，onViewDetachedFromWindow 就会被调用。
 * # setItemViewCacheSize
 * - RecyclerView item 除了可见区域外的缓存数量，影响 onViewRecycled 的调用时机。
 * - 在 ViewPager2 中，setItemViewCacheSize 也会起作用，但在 offscreenPageLimit 外的缓存数量。
 */
class RecyclerViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val list = mutableListOf<String>()
        for (i in 0..20) {
            list.add(i.toString())
        }
        binding.recyclerView.setItemViewCacheSize(1)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {

        })
        binding.recyclerView.adapter = MoreAdapter(this, false).apply {
            setData(list)
        }
    }
}