package com.ware.device.ble

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.common.BaseActivity
import kotlinx.android.synthetic.main.activity_ble_info.*


private const val TAG = "BleInfoActivity"
private const val KEY_DEVICE = "key_device"

/**
 * https://www.jianshu.com/p/a274e17fc66a
 * https://juejin.im/post/5cc1ca775188252dd9184b03#heading-12
手环手边之类的周边设备有这样的一个特性：一旦建立了GATT连接就不再被扫描到

 * 每个GATT由完成不同功能的Service组成；
 * 每个Service由不同的Characteristic组成；
 * 每个Characteristic由一个value和一个或者多个Descriptor组成；
Service、Characteristic相当于标签（Service相当于他的类别，Characteristic相当于它的名字），而value才真正的包含数据，Descriptor是对这个value进行的说明和描述，当然我们可以从不同角度来描述和说明，因此可以有多个Descriptor.
这样子理解可能不够准确，下面我们来举一个简单的例子进行说明：

常见的小米手环是一个BLE设备，（假设）它包含三个Service,分别是提供设备信息的Service、提供步数的Service、检测心率的Service;
而设备信息的service中包含的characteristic包括厂商信息、硬件信息、版本信息等；而心率Service则包括心率characteristic等，而心率characteristic中的value则真正的包含心率的数据，而descriptor则是对该value的描述说明，比如value的单位啊，描述啊，权限啊等。

任何BLE功能的实现都要对characteristic进行操作，主要包括有三种：
 * 读取特征值
 * 写入特征值
 * 特征值的变化通知


BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(mCharacteristicUUID);//获取指定uuid的Characteristic


//通过Gatt对象读取特定特征（Characteristic）的特征值
bluetoothGatt.readCharacteristic(gattCharacteristic);
//当成功读取特征值时，会触发BluetoothGattCallback#onCharacteristicRead()回调。
@Override
public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
Log.d(TAG, "onCharacteristicRead UUID : " + characteristic.getUuid());
byte[] data = characteristic.getValue(); //获取读取到的特征值
}


//写入你需要传递给外设的特征值（即传递给外设的信息）
gattCharacteristic.setValue(bytes);
bluetoothGatt.writeCharacteristic(gattCharacteristic);//通过GATT实体类将，特征值写入到外设中。
//回调中获取写的结果
@Override
public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
Log.d(TAG, "onCharacteristicWrite UUID: " + characteristic.getUuid() + "state : " + status);
characteristic.getValue()//获取写入到外设的特征值
}


//设置订阅notificationGattCharacteristic值改变的通知
bluetoothGatt.setCharacteristicNotification(notificationGattCharacteristic, true);
//获取其对应的通知Descriptor
BluetoothGattDescriptor descriptor = notificationGattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
if (descriptor != null){
//设置通知值
descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
boolean descriptorResult = bluetoothGatt.writeDescriptor(descriptor);
}
//当写入完特征值后，外设修改自己的特征值进行回复时，手机端会触发BluetoothGattCallback#onCharacteristicChanged()方法，获取到外设回复的值，从而实现双向通信。
@Override
public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
String value = characteristic.getValue()//获取外设修改的特征值
}

 */
class BleInfoActivity : BaseActivity(R.layout.activity_ble_info), View.OnClickListener {
    private lateinit var device: BluetoothDevice
    override fun onClick(v: View) {
        when (v.id) {
            R.id.connectView -> connectDevice()
        }
    }

    private fun connectDevice() {
        device.connectGatt(this, false, object : BluetoothGattCallback() {
            //Callback indicating when GATT client has connected/disconnected to/from a remote GATT server.
            //newState: Returns the new connection state. BluetoothProfile#STATE_DISCONNECTED->0 or BluetoothProfile#STATE_CONNECTED->2
            //Status of the connect or disconnect operation. BluetoothGatt#GATT_SUCCESS->0 if the operation succeeds.
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                Log.d(TAG, "onConnectionStateChange: gatt = $gatt; status = $status; newState = $newState")
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt?.discoverServices()
                }
            }

            //Callback invoked when the list of remote services, characteristics and descriptors for the remote device have been updated, ie new services have been discovered.
            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                Log.d(TAG, "onServicesDiscovered: status = $status")
                var valueStr = ""
                gatt?.services?.apply {
                    forEach { service ->
                        val serviceStr = "service:" + "\n" + service.uuid + "\n"
                        valueStr += serviceStr


                        valueStr += "characteristics: " + "\n"

                        service.characteristics.forEach { character ->
                            valueStr += character.uuid
                            valueStr += "\n"

                            valueStr += "descriptor: " + "\n"
                            character.descriptors.forEach { descriptor ->
                                valueStr += descriptor.uuid
                                valueStr += "\n"
                            }
                        }

                        valueStr += "\n"
                    }
                    runOnUiThread {
                        serviceView.text = valueStr
                    }
                }
            }

            override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
                super.onReadRemoteRssi(gatt, rssi, status)
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                super.onCharacteristicRead(gatt, characteristic, status)
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                super.onCharacteristicWrite(gatt, characteristic, status)
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
                super.onCharacteristicChanged(gatt, characteristic)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        device = intent.getParcelableExtra(KEY_DEVICE)!!
        infoView.text = "name: ${device.name}  address: ${device.address}  bonded: ${device.bondState == BluetoothDevice.BOND_BONDED}  type = ${getType(device.type)}"
        connectView.setOnClickListener(this)
    }

    companion object {
        fun start(context: Context, device: BluetoothDevice) {
            context.startActivity(Intent(context, BleInfoActivity::class.java).putExtra(KEY_DEVICE, device))
        }
    }
}