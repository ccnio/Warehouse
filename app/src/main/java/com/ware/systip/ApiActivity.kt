package com.ware.systip

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import java.io.File
import java.nio.channels.FileChannel
import java.nio.channels.FileLock

private const val TAG = "ApiActivity"

/**
 * 1. FileOutputStream,新流写文件默认是覆盖,同一个流的写入是追加.
 */
class ApiActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource)
        findViewById<View>(R.id.channelView).setOnClickListener(this)
    }

    /** FileLock可以控制不同程序(JVM)对同一文件的并发访问，实现进程间文件同步操作。同时他也是线程安全的,支持多线程的并发访问在,只不过比较重.
     * 1. 概念
    共享锁: 共享读操作，但只能一个写（读可以同时，但写不能）。共享锁防止其他正在运行的程序获得重复的独占锁，但是允许他们获得重复的共享锁。
    独占锁: 只有一个读或一个写（读和写都不能同时）。独占锁防止其他程序获得任何类型的锁。

    2. FileLock FileChannel.lock(long position, long size, boolean shared)
    shared的含义:是否使用共享锁,一些不支持共享锁的操作系统,将自动将共享锁改成排它锁。可以通过调用isShared()方法来检测获得的是什么类型的锁。

    3. lock()和tryLock()的区别：
    lock()阻塞的方法，锁定范围可以随着文件的增大而增加。无参lock()默认为独占锁；有参lock(0L, Long.MAX_VALUE, true)为共享锁。
    tryLock()非阻塞,当未获得锁时,返回null.

    4. FileLock的生命周期：在调用FileLock.release(),或者Channel.close(),或者JVM关闭
    5. FileLock是线程安全的
    6. 注意事项：同一进程内，在文件锁没有被释放之前，不可以再次获取。即在release()方法调用前,只能lock()或者tryLock()一次。
    7. 创建FileChannel需要区分FileOutputStream和FileInputStream,
     * 如果是用FileOutputStream创建的Channel只能进行写操作而不能惊醒读操作，
     * 否则会报NonReadableChannelException,反之用FileInputStream
     * 创建的Channel只能进行读操作，而不能进行写操作，否则会报NonWritableChannelException
     * 当然除此之外，可以使用RandomAccessFile来替换，但必须指定操作方式，就像
     * mode = "r" / "rw" 但不可以是 "w"
     * RandomAccessFile r = new RandomAccessFile(file, mode)

     *
     * case 1. 有文件在写时, lock时可以中止其写入
     * case 2. tryLock(position,size,isShare); 第三个参数为 true 时为共享锁, tryLock() 是非阻塞式的，它设法获取锁，但如果不能获得，例如因为其他一些进程已经持有相同的锁，而且不共享时，抛出文件重叠锁异常【OverlappingFileLockException】。
     * case 3. lock() 是阻塞式的，它要阻塞进程直到锁可以获得，或调用 lock() 的线程中断，或调用 lock() 的通道关闭。
     */
    private fun fileChannel() {
        val file = File(getExternalFilesDir(null)!!.absolutePath + File.separator + "channel.txt")
        if (!file.exists()) file.createNewFile()

        /* case 1
        thread {
            val outputStream = FileOutputStream(file)
            while (true) {
                outputStream.write("abc".toByteArray())
            }
        }

        SystemClock.sleep(50)
        val out = FileOutputStream(file)
        val tryLock = out.channel.tryLock()
        Log.d(TAG, "fileChannel: lock = $tryLock")
        out.write("test".toByteArray())
*/
        /* case 2
        thread {
             val outputStream = FileOutputStream(file)
             outputStream.channel.tryLock()
         }

         SystemClock.sleep(50)
         val out = FileOutputStream(file)
         val tryLock = out.channel.tryLock() //OverlappingFileLockException
         Log.d(TAG, "fileChannel: lock = $tryLock")*/
    }

    private fun waitFileLock(fileChannel: FileChannel, timeoutInMilli: Long): FileLock? {
        var fileLock: FileLock? = null
        var waitTime: Long = 0
        while (fileLock == null) {
            if (waitTime >= timeoutInMilli) {
                Log.w(TAG, "wait timeout")
                return null
            }
            try {
                fileLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true)
                if (fileLock == null) {
                    Log.w(TAG, "file lock and wait")
                    val sleepTime = if (timeoutInMilli - waitTime > 100) 100 else timeoutInMilli - waitTime
                    Thread.sleep(sleepTime)
                    waitTime += sleepTime
                }
            } catch (e: Exception) {
                Log.w(TAG, "waitFileLock", e)
                return null
            }
        }
        return fileLock
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.channelView -> fileChannel()
        }
    }
}