package com.ware.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by jianfeng.li on 19-12-9.
 */
//private const val
class MoreAdapter : RecyclerView.Adapter<MoreAdapter.MHolder>() {

    private val mData = mutableListOf<String>()

    class MHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MHolder {
        return MHolder(parent)
    }

    override fun getItemCount(): Int {
        return if (mData.isEmpty()) 0 else mData.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) 1 else 1
    }

    override fun onBindViewHolder(holder: MHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}