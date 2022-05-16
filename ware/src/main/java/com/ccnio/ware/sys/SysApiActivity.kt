package com.ccnio.ware.sys

import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.SharedMemory
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.IMemoryAidlInterface
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivitySysApiBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import java.io.FileDescriptor
import java.io.FileInputStream
import java.nio.ByteBuffer


private const val TAG = "SysApiActivity"

class SysApiActivity : AppCompatActivity(R.layout.activity_sys_api) {
    private val bind by viewBinding(ActivitySysApiBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind.dndView.setOnClickListener { dnd() }
        bind.shareMemoryView.setOnClickListener { sharedMemory() }
    }

    //勿扰模式
    private fun dnd() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val curFilter = manager.currentInterruptionFilter
        val notificationPolicy = manager.notificationPolicy
        Log.d(TAG, "dnd: filter = $curFilter, notifyPolicy = $notificationPolicy")
    }

    private fun sharedMemory() {
        val intent = Intent(this, MemoryFetchService::class.java)
        bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val remoteService = IMemoryAidlInterface.Stub.asInterface(service)
                //api<27 时
                val content = ByteArray(100)
                val parcelFileDescriptor = remoteService.parcelFileDescriptor
                val descriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                val fileInputStream = FileInputStream(descriptor)
                val read = fileInputStream.read(content)
                Log.d(TAG, "onServiceConnected: read == $read")


                //api>=27时
                // 1、通过aidl拿到SharedMemory
                val sharedMemory = remoteService.sharedMemory
                Log.d(TAG, "onServiceConnected: shared memory = $sharedMemory")
                // 2、mapReadOnly获取到存了数据的ByteBuffer
                val byteBuffer: ByteBuffer = sharedMemory.mapReadOnly()
                val len: Int = byteBuffer.limit() - byteBuffer.position()
                val bytes = ByteArray(len)
                // 3、借助byteBuffer获取到数据
                byteBuffer.get(bytes)
                val string = String(bytes)
                Log.d(TAG, "onServiceConnected: shared memory ret = $string")
                SharedMemory.unmap(byteBuffer)
                sharedMemory.close()

            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }

        }, Service.BIND_AUTO_CREATE)

    }
}
