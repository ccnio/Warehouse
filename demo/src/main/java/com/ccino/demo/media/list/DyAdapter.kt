package com.ccino.demo.media.list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.databinding.DyLayoutVideoBinding

private const val TAG = "DyAdapter"

class DyAdapter : RecyclerView.Adapter<DyViewHolder>() {
    private val list = mutableListOf<DyData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DyViewHolder {
        val binding = DyLayoutVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DyViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")
        holder.bind(list[position])
        // 这里不直接播放，交由 Activity/Fragment 控制
    }

    override fun onViewAttachedToWindow(holder: DyViewHolder) {
        Log.d(TAG, "onViewAttachedToWindow: ${holder.binding.root.tag}")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        Log.d(TAG, "onDetachedFromRecyclerView: ")
    }

    override fun onViewRecycled(holder: DyViewHolder) {
        Log.d(TAG, "onViewRecycled: ${holder.binding.root.tag}")
    }

    override fun getItemCount() = list.size
    fun setData(list: MutableList<DyData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}

class DyViewHolder(val binding: DyLayoutVideoBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: DyData) {
        binding.root.tag = data
        binding.titleView.text = data.title
    }


}

data class DyData(val url: String, val title: String)