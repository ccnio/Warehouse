package com.ware.widget.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ware.R

/**
 * Created by jianfeng.li on 19-6-14.
 */
class FlexAdapter : RecyclerView.Adapter<FlexAdapter.FlexHolder>(), View.OnClickListener {
    override fun onClick(v: View) {
//        val tag = v.tag
        v.isSelected = !(v.isSelected)
    }

    private val mList = mutableListOf("硬件连接", "产品优化", "功能优化", "门禁相关", "跑步相关", "锻炼相关", "其它类型")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlexHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flex_item, parent, false)
        view.setOnClickListener(this)
        return FlexHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: FlexHolder, position: Int) {
        (holder.itemView as TextView).text = mList[position]
    }

    class FlexHolder(view: View) : RecyclerView.ViewHolder(view)
}