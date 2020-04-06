package com.ware.device

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.component.BaseActivity
import kotlinx.android.synthetic.main.activity_bluetooth_boned.*
import java.io.InputStream
import java.io.OutputStream
import java.util.*

private const val REQUEST_ENABLE_BT = 0x1
private const val TAG = "BluetoothActivity"
const val KEY_DATA = "key_data"

class BonedListActivity : BaseActivity(R.layout.activity_bluetooth_boned), View.OnClickListener, BluetoothListAdapter.OnclickCallback {
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.openView -> openIfNeed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val list = intent.getParcelableArrayListExtra<BluetoothDevice>(KEY_DATA)
        Log.d(TAG, "onCreate: ${list.size}")
        val adapter = BluetoothListAdapter(this, list)
        recyclerView.adapter = adapter
        adapter.setClickCallback(this)
    }

    companion object {
        fun start(context: Context, data: ArrayList<BluetoothDevice>) {
            val intent = Intent(context, BonedListActivity::class.java).apply { putParcelableArrayListExtra(KEY_DATA, data) }
            context.startActivity(intent)
        }
    }

    override fun onClick(device: BluetoothDevice) {
        Log.d(TAG, "onClick: ${device.name}")
        ConnectThread(device).start()
    }
}

class ConnectThread(val device: BluetoothDevice) : Thread() {
    private val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("UUID"))
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    override fun run() {
        socket.use {
            it.connect()
            inputStream = socket.inputStream
            outputStream = socket.outputStream
            Log.d(TAG, "connect: input = $inputStream; output = $outputStream")
        }

        // Do work to manage the connection (in a separate thread)
//        manageConnectedSocket(mmSocket)
    }

    public fun cancel() {
        socket.close()
    }
}

