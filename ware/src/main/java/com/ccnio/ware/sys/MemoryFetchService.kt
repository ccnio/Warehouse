package com.ccnio.ware.sys

import android.app.Service
import android.content.Intent
import android.os.*
import android.system.OsConstants
import android.util.Log
import com.ccnio.ware.IMemoryAidlInterface
import com.ccnio.ware.app
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.lang.reflect.Method
import java.nio.ByteBuffer

class MemoryFetchService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return MemoryFetchStub()
    }

    class MemoryFetchStub : IMemoryAidlInterface.Stub() {
        override fun getParcelFileDescriptor(): ParcelFileDescriptor? {
            val memoryFile = MemoryFile(SHM_FILE_NAME, 1024)
            memoryFile.outputStream.write(byteArrayOf(1, 2, 3, 4, 5))
            val method: Method = MemoryFile::class.java.getDeclaredMethod("getFileDescriptor")
            val des: FileDescriptor = method.invoke(memoryFile) as FileDescriptor
            return ParcelFileDescriptor.dup(des)
        }

        /**
         * api >= 27
         */
        override fun getSharedMemory(): SharedMemory? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                val pathname = app.getExternalFilesDir(null).toString() + File.separator + "memory.text"
                Log.d("SysApiActivity", "getParcelFileDescriptor: $pathname")
                val jpegFile = File(pathname)
                jpegFile.writeText("abcdefg")
                // 1、使用文件流把文件读入到内存
                val inputStream = FileInputStream(jpegFile);
                val bytes = ByteArray(jpegFile.length().toInt())
                inputStream.read(bytes)
                // 2、创建sharedMemory跨进程传输
                val sharedMemory = SharedMemory.create("SharedMemory", bytes.size)
                // 3、mapReadWrite获取ByteBuffer
                val buffer: ByteBuffer = sharedMemory.mapReadWrite()
                // 4、put数据
                buffer.put(bytes)
                // 5、把sharedMemory权限设置为只读，create默认是读写权限都有，这里可以避免客户端更改数据
                sharedMemory.setProtect(OsConstants.PROT_READ)

                // 6、使用完需要unmap
                SharedMemory.unmap(buffer)
                return sharedMemory
            }
            return null
        }
    }

    companion object {
        private const val TAG = "MemoryFetchService"
        private const val SHM_FILE_NAME = "test_memory"
    }
}