package com.ware.device.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.common.BaseActivity
import kotlinx.android.synthetic.main.activity_ble.*


private const val REQUEST_ENABLE_BT = 0x1
private const val TAG = "BleActivity"

class BleActivity : BaseActivity(R.layout.activity_ble), View.OnClickListener {
    private val devices = arrayListOf<BluetoothDevice>()
    private val bluetoothManager by lazy { getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    private val bluetoothAdapter by lazy { bluetoothManager.adapter }
    private val scanner by lazy { bluetoothAdapter.bluetoothLeScanner }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.scanView -> scanDevice()
        }
    }

    private fun scanDevice() {
        Log.d(TAG, "scanDevice: state = ${bluetoothAdapter.state}")
        devices.clear()
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                Log.d(TAG, "onScanResult: type = $callbackType")
                result?.apply {
                    Log.d(TAG, "onScanResult: ${device.name}; ${device.address}")
                    devices.add(device)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.d(TAG, "onScanFailed: $errorCode")
            }
        }
        scanner.startScan(callback)

        Handler().postDelayed({
            scanner.stopScan(callback)
            DeviceListActivity.start(this, devices)
        }, 10000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanView.setOnClickListener(this)
//        bondView.setOnClickListener(this)
//        findView.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(receiver)
    }
}