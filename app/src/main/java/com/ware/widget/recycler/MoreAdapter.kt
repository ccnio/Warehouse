package com.ware.widget.recycler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.ware.R
import kotlinx.android.synthetic.main.layout_header.view.*
import kotlinx.android.synthetic.main.layout_recycler.view.*

/**
 * Created by jianfeng.li on 20-1-4.
 */
private const val TYPE_HEADER = 0x11
private const val TYPE_FOOTER = 0x12

class MoreAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mListener: LoadListener? = null
    private val mList = mutableListOf<String>()
    private val mInflater = LayoutInflater.from(context)
    private var mStatus = Status.MORE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        Log.d("MoreAdapter", "onCreateViewHolder: ")
        return when (viewType) {
            TYPE_HEADER -> {
                val view = mInflater.inflate(R.layout.layout_header, parent, false)
                HeaderHolder(view)
            }
            TYPE_FOOTER -> {
                val view = mInflater.inflate(R.layout.layout_footer, parent, false)
                FooterHolder(view)
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
        Log.d("MoreAdapter", "onBindViewHolder: pos = $position")
        when (holder) {
            is ItemHolder -> {
                holder.view.mNameView.text = mList[position]
            }
            is HeaderHolder -> {
                holder.view.mContentView.text = mList[position]
            }
            is FooterHolder -> {
                (holder.view as FooterView).updateState(mStatus)
            }
        }
    }

    val mOnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            val layoutManager = recyclerView.layoutManager
            layoutManager?.let {
                if (it is GridLayoutManager) {
                    val mLastVisibleItemPosition = it.findLastCompletelyVisibleItemPosition()
                    if (newState == SCROLL_STATE_IDLE && hasMore(mStatus) && mLastVisibleItemPosition + 1 == it.itemCount) {
                        updateStatus(Status.LOADING)
                        mListener?.onLoad()
                    }
                }
            }
        }
    }

    fun setLoadListener(listener: LoadListener) {
        mListener = listener
    }

    interface LoadListener {
        fun onLoad()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else if (position == itemCount - 1) TYPE_FOOTER else super.getItemViewType(position)
    }

    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view)
    class HeaderHolder(val view: View) : RecyclerView.ViewHolder(view)
    class FooterHolder(val view: View) : RecyclerView.ViewHolder(view)

    fun setData(data: List<String>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    fun updateStatus(status: Status) {
        mStatus = status
        notifyItemChanged(itemCount - 1)
    }

    fun addData(list: List<String>) {
        val pos = mList.size
        val size = list.size
        mList.addAll(list)
        mStatus = Status.MORE
        notifyItemRangeInserted(pos - 1, size)
//        notifyItemInserted(pos - 1)
    }
}