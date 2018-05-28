package com.edreamoon.warehouse.kt

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edreamoon.warehouse.R
import kotlinx.android.synthetic.main.kt_adapter.view.*

class KtAdapter(val context: Context) : Adapter<KtAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.kt_adapter, parent, false))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.mContentView.text = "adapber bind view 测试 $position"
    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}