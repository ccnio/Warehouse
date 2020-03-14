package com.ware.widget.adpater

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ware.R
import kotlinx.android.synthetic.main.layout_pager2.view.*

/**
 * Created by jianfeng.li on 2020/3/14.
 */
class Pager2Adapter(val context: Context) : RecyclerView.Adapter<Pager2Adapter.Holder>() {
    private val mList = mutableListOf(Color.BLUE, Color.GREEN, Color.GRAY, Color.CYAN)
    private val mInflater = LayoutInflater.from(context);

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = mInflater.inflate(R.layout.layout_pager2, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.setBackgroundColor(mList[position])
        holder.view.textView.text = position.toString()
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}