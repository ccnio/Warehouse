package com.ware.widget.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ware.R
import kotlinx.android.synthetic.main.layout_header.view.*
import kotlinx.android.synthetic.main.layout_recycler.view.*

/**
 * Created by jianfeng.li on 20-1-4.
 */
private const val TYPE_HEADER = 0x11

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
                holder.view.mNameView.text = mList[position]
            }
            is HeaderHolder -> {
                holder.view.mContentView.text = mList[position]
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && hasHeader) TYPE_HEADER
        else super.getItemViewType(position)
    }

    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view)
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