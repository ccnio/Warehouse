package com.ware.device.ble

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ware.R
import com.ware.component.BaseActivity
import kotlinx.android.synthetic.main.activity_bluetooth_boned.*

const val KEY_DATA = "key_data"

class DeviceListActivity : BaseActivity(R.layout.activity_bluetooth_list), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.openView -> openIfNeed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val list = intent.getParcelableArrayListExtra<BluetoothDevice>(KEY_DATA)
        val adapter = BleListAdapter(this, list!!)
        recyclerView.adapter = adapter
    }

    companion object {
        fun start(context: Context, data: ArrayList<BluetoothDevice>) {
            val intent = Intent(context, DeviceListActivity::class.java).apply { putParcelableArrayListExtra(KEY_DATA, data) }
            context.startActivity(intent)
        }
    }
}

