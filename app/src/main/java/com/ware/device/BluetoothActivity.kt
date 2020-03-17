package com.ware.device

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.ware.R
import com.ware.common.BaseActivity
import kotlinx.android.synthetic.main.activity_bluetooth.*

/**
 * 蓝牙可分为经典蓝牙、低功耗蓝牙、双模蓝牙三大类。
 * 2009 年发布的蓝牙 3.0 及之前的蓝牙版本包括 BR、EDR 和 HS(AMP) 三种蓝牙技术，统称为经典蓝牙技术，只支持经典蓝牙技术的蓝牙称为经典蓝牙。
 * 2010 年 SIG 联盟合并了 Wibree 联盟，并把 Wibree 联盟提出的低功耗无线技术重新命名为低功耗蓝牙技术（BLE）。
 * 2010 年发布的蓝牙 4.0 规格就同时包含经典蓝牙和低功耗蓝牙，只支持低功耗蓝牙技术的蓝牙称为低功率蓝牙，同时支持经典蓝牙和低功率蓝牙技术的蓝牙称为双模蓝牙。
 *
 * 低功耗蓝牙与经典蓝牙技术是不兼容的，所以低功耗蓝牙和经典蓝牙两者之间是不能相互通信的。
 */
private const val REQUEST_ENABLE_BT = 0x1
private const val TAG = "BluetoothActivity"

class BluetoothActivity : BaseActivity(R.layout.activity_bluetooth), View.OnClickListener {
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val bondedDevices = ArrayList<BluetoothDevice>()
    private val foundDevices = mutableListOf<BluetoothDevice>()

//    private fun checkPermission(permission: Array<String>, action: (String) -> Unit) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            action("")
//        } else {
//            ActivityCompat.requestPermissions(this,permission,)
//        }
//    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.openView -> openIfNeed()
            R.id.bondView -> getBondedDevice()
            R.id.findView -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) findDevice()
                else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 2)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    foundDevices.add(device)
                    Log.d(TAG, "find device: ${device.name} -- ${device.address}")
                }
            }
        }
    }

    private fun findDevice() {
        bluetoothAdapter.startDiscovery()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }


    private fun getBondedDevice() {
        val bonded = bluetoothAdapter.bondedDevices
        if (!bonded.isNullOrEmpty()) {
            bonded.forEach {
                Log.d(TAG, "getBondedDevices: ${it.name} -- ${it.address}")
                bondedDevices.add(it)
            }
            BonedListActivity.start(this, bondedDevices)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openView.setOnClickListener(this)
        bondView.setOnClickListener(this)
        findView.setOnClickListener(this)
    }

    private fun openIfNeed() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            Log.d(TAG, "onActivityResult: $resultCode")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}