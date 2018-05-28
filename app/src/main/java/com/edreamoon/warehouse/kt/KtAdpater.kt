package com.edreamoon.warehouse.kt

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edreamoon.warehouse.R

class KtAdpater(val context: Context) : Adapter<KtAdpater.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.kt_adapter, parent, false))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view) {

    }
}