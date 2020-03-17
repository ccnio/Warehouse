package com.ware.device

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ware.R
import kotlinx.android.synthetic.main.layout_bluetooth.view.*

class BluetoothListAdapter(val context: Context, val list: ArrayList<BluetoothDevice>) : RecyclerView.Adapter<BluetoothListAdapter.Holder>() {
    private val inflater = LayoutInflater.from(context)
    private var clickListener: OnclickCallback? = null

    interface OnclickCallback {
        fun onClick(device: BluetoothDevice)
    }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(device: BluetoothDevice) {
            view.nameTv.text = "name:${device.name}; bonded:${device.bondState == BluetoothDevice.BOND_BONDED}"//device.name + device.bo
            view.addressTv.text = "address:${device.address}; type = ${getType(device.type)}"
            if (device.uuids.isNullOrEmpty()) {
                view.uuidTv.text = "uuids: empty"
            } else {
                var txt = ""
                device.uuids.forEach {
                    txt += it.uuid
                    txt += "; "
                }
                view.uuidTv.text = "uuids: $txt"
            }
            view.connectBt.setOnClickListener {
                clickListener?.onClick(device)
            }
        }

        private fun getType(type: Int): String {
            return when (type) {
                DEVICE_TYPE_CLASSIC -> "classic"
                DEVICE_TYPE_LE -> "le"
                DEVICE_TYPE_DUAL -> "dual"
                else -> "unknown"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = inflater.inflate(R.layout.layout_bluetooth, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setClickCallback(callback: OnclickCallback) {
        clickListener = callback
    }
}