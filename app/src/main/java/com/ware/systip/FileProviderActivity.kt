package com.ware.systip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import com.ware.R
import kotlinx.android.synthetic.main.activity_file_provider.*
import java.io.File
import java.util.*

/**
 * FilePath
 *
Environment.getDataDirectory() = /data
Environment.getDownloadCacheDirectory() = /cache
Environment.getExternalStorageDirectory() = /mnt/sdcard
Environment.getExternalStoragePublicDirectory(“test”) = /mnt/sdcard/test
Environment.getRootDirectory() = /system
getPackageCodePath() = /data/app/com.my.app-1.apk
getPackageResourcePath() = /data/app/com.my.app-1.apk
getCacheDir() = /data/data/com.my.app/cache
getDatabasePath(“test”) = /data/data/com.my.app/databases/test
getDir(“test”, Context.MODE_PRIVATE) = /data/data/com.my.app/app_test
getExternalCacheDir() = /mnt/sdcard/Android/data/com.my.app/cache
getExternalFilesDir(“test”) = /mnt/sdcard/Android/data/com.my.app/files/test
getExternalFilesDir(null) = /mnt/sdcard/Android/data/com.my.app/files
getFilesDir() = /data/data/com.my.app/files

SDCard的根目录下文件夹，应用被卸载后，这些数据还保留在SDCard中，
Context.getExternalFilesDir()获取到的SDCard/Android/data/包名/files/ 目录卸载后会被删除
Context.getExternalCacheDir() 获取到 SDCard/Android/data/包名/cache/目录，一般存放临时缓存数据.对应清除缓存。

 */

class FileProviderActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.mStartView -> {
//                val currentHomePackage = resolveInfo.activityInfo.packageName
                val imagePath = filesDir
                val newFile = File(imagePath, "a.txt")
                if (!newFile.exists()) {
                    newFile.createNewFile()
                }
                var bmpUri = getUriForFile(this, "$packageName.provider", newFile)
                grantUriPermission("com.edreamoon.eye", bmpUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                /**
                 * 公共区域的文件，不需要授权，可直接使用
                 */
//                val path = Environment.getExternalStorageDirectory().absolutePath + "/tessat.jpg" -----或者下面 path
                val path = "/storage/emulated/0/Android/data/com.edreamoon.eye/files/tessat.jpg"

                val file = File(path)
                val uri = Uri.fromFile(file)
//                mHolderView.setImageBitmap(BitmapFactory.decodeFile(path))
                val intent = Intent("com.ware")
                intent.putExtra("abc", uri)
                startActivity(intent)

                mHolderView.setImageURI(uri)
            }

            R.id.mPathView -> {
                Log.d("FileProviderActivity", "path  Environment.getRootDirectory = ${Environment.getRootDirectory().absolutePath};  " +
                        "Environment.getDataDirectory() = ${Environment.getDataDirectory().absolutePath}; " +
                        "context.getExternalFilesDir = ${getExternalFilesDir(null)}; filesDir = $filesDir; " +
                        "cacheDir = $cacheDir; dataDir = $dataDir")

                val instance = Calendar.getInstance()
                val min = instance.get(Calendar.MINUTE)
                instance.set(Calendar.MINUTE, min + 1)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_provider)

        mStartView.setOnClickListener(this)
        mPathView.setOnClickListener(this)
    }


    /**
     * 第三方app读取数据
     */
    fun readFormUri() {
//        val returnUri = Uri.parse("content://com.ware.provider/file_path/a.txt")
//        val inputPFD = contentResolver.openFileDescriptor(returnUri, "r")
//        val returnCursor = contentResolver.query(returnUri, null, null, null, null)
//        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
//        returnCursor.moveToFirst()
//        println("文件名:" + returnCursor.getString(nameIndex) + ", 大小:" + returnCursor.getLong(sizeIndex) + " B")
//        returnCursor.close()
//
//
//        //读取文件内容
//        var content = ""
//        var fr: FileReader? = null
//        val buffer = CharArray(1024)
//
//        try {
//            val strBuilder = StringBuilder()
//            fr = FileReader(inputPFD.fileDescriptor)
//            while (fr!!.read(buffer) !== -1) {
//                strBuilder.append(buffer)
//            }
//            fr!!.close()
//            content = strBuilder.toString()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        if (content.isNotEmpty()) {
//            println(content)
//        }
//        try {
//            inputPFD.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }
}
