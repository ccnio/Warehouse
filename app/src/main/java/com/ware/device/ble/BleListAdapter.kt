package com.ware.device.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.*
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ware.R
import kotlinx.android.synthetic.main.layout_bluetooth.view.*

val myDevice = setOf<String>("Mi Color 3E65", "Mi Color 385B")
fun getType(type: Int): String {
    return when (type) {
        DEVICE_TYPE_CLASSIC -> "classic"
        DEVICE_TYPE_LE -> "le"
        DEVICE_TYPE_DUAL -> "dual"
        else -> "unknown"
    }
}

class BleListAdapter(val context: Context, val list: ArrayList<BluetoothDevice>) : RecyclerView.Adapter<BleListAdapter.Holder>(), View.OnClickListener {
    private val inflater = LayoutInflater.from(context)
    private var clickListener: OnclickCallback? = null

    interface OnclickCallback {
        fun onClick(device: BluetoothDevice)
    }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(device: BluetoothDevice) {
            view.tag = device
            view.nameTv.setTextColor(if (myDevice.contains(device.name)) Color.RED else Color.BLACK)
            view.nameTv.text = "name:${device.name}; bonded:${device.bondState == BOND_BONDED}"//device.name + device.bo
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = inflater.inflate(R.layout.layout_bluetooth, parent, false)
        view.setOnClickListener(this)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    override fun onClick(v: View) {
        BleInfoActivity.start(context, v.tag as BluetoothDevice)
    }
}