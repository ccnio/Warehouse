package com.ccino.demo.media.list

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.ccino.demo.R
import com.ccino.demo.databinding.ActivityPlayerListBinding
import com.ccino.demo.media.videoList

class PlayerListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerListBinding
    private val playDetector by lazy { ListDetector("PlayerListMain", this, binding.recyclerView) }
    private val adapter by lazy { PlayerListAdapter(playDetector) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PreloadManager.getCacheFactory()
        binding = ActivityPlayerListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }


    private fun initView() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recyclerView.adapter = adapter
        adapter.setData(videoList)
    }
}