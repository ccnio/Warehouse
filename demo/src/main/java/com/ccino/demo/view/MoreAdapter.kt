package com.ccino.demo.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.R

/**
 * Created by jianfeng.li on 20-1-4.
 */
private const val TYPE_HEADER = 0x11
private const val TAG = "MoreAdapter"

class MoreAdapter(val context: Context, val hasHeader: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mList = mutableListOf<String>()
    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = mInflater.inflate(R.layout.layout_header, parent, false)
                HeaderHolder(view)
            }

            else -> {
                val view = mInflater.inflate(R.layout.layout_recycler, parent, false)
                ItemHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemHolder -> {
                Log.d(TAG, "onBindViewHolder: $position")
                holder.bind(mList[position], position)
            }

            is HeaderHolder -> {
                holder.view.findViewById<TextView>(R.id.mContentView).text = mList[position]
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is ItemHolder) {
            Log.d(TAG, "onViewRecycled: ${holder.view.tag}")
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        Log.d(TAG, "onViewDetachedFromWindow: ${holder.itemView.tag}")
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        Log.d(TAG, "onViewAttachedToWindow: ${holder.itemView.tag}")
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && hasHeader) TYPE_HEADER
        else super.getItemViewType(position)
    }

    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(string: String, position: Int) {
            view.findViewById<TextView>(R.id.mNameView).text = string
            view.tag = "ItemHolder: $position"
        }

    }

    class HeaderHolder(val view: View) : RecyclerView.ViewHolder(view)

    fun setData(data: List<String>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(list: List<String>) {
        val pos = mList.size
        mList.addAll(list)
        notifyItemRangeInserted(pos, list.size)
    }
}