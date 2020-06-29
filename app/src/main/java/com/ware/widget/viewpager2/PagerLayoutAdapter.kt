package com.ware.widget.viewpager2

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ware.R
import kotlinx.android.synthetic.main.layout_pager2.view.*

/**
 * Created by jianfeng.li on 2020/3/14.
 */
private const val TAG = "PagerLayoutAdapter"

class PagerLayoutAdapter(val context: Context) : RecyclerView.Adapter<PagerLayoutAdapter.Holder>() {
    private val mList = mutableListOf(Color.BLUE, Color.GREEN, Color.GRAY, Color.CYAN, Color.RED, Color.BLUE, Color.GREEN, Color.GRAY, Color.CYAN, Color.RED)
    private val mInflater = LayoutInflater.from(context)
    private val liveLayouts = HashMap<Int, Holder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = mInflater.inflate(R.layout.layout_pager2, parent, false)
        Log.d(TAG, "onCreateViewHolder: ")
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onViewRecycled(holder: Holder) {
        super.onViewRecycled(holder)
        Log.d(TAG, "onViewRecycled: ${holder.layoutPosition}")
        liveLayouts.remove(holder.layoutPosition)
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        Log.d(TAG, "onViewAttachedToWindow: ${holder.layoutPosition}")
        liveLayouts[holder.layoutPosition] = holder
    }

    override fun onViewDetachedFromWindow(holder: Holder) {
        super.onViewDetachedFromWindow(holder)
        Log.d(TAG, "onViewDetachedFromWindow: ${holder.layoutPosition}")
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.setBackgroundColor(mList[position])
        holder.view.textView.text = position.toString()
        Log.d(TAG, "onBindViewHolder: $position")
    }

    fun getCurrentPage(currentPos: Int) = liveLayouts[currentPos]

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}