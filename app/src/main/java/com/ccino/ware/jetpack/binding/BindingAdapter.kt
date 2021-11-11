package com.ccino.ware.jetpack.binding

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.ware.databinding.LayoutBindingBinding

/**
 * Created by ccino on 2021/11/5.
 */
private const val TAG_L = "BindingAdapter"

class BindingAdapter : RecyclerView.Adapter<BindingAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutBindingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.textView.text = position.toString()
    }

    override fun getItemCount(): Int {
        return 20
    }

    class Holder(val binding: LayoutBindingBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            val lifecycleOwner = itemView.findViewTreeLifecycleOwner()
            val context = itemView.context
            Log.d(TAG_L, "lifecycle = $lifecycleOwner; context = $context ")
        }
    }
}