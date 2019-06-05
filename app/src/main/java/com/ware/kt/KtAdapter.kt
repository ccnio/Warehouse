package com.ware.kt

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ware.R
import kotlinx.android.synthetic.main.kt_adapter.view.*

class KtAdapter(val context: Context, private val type: String) : Adapter<KtAdapter.Holder>() {

    private val TAG = "KtAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        Log.d(TAG, "onCreateViewHolder: $type")
        return Holder(LayoutInflater.from(context).inflate(R.layout.kt_adapter, parent, false))
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Log.d(TAG, "onBindViewHolder **  : $type     $position")
        holder.view.mContentView.text = "adapber bind view 测试 $position"
    }


    class Holder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}