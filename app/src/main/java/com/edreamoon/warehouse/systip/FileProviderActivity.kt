package com.edreamoon.warehouse.systip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.support.v4.content.FileProvider.getUriForFile
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.edreamoon.warehouse.R
import java.io.File
import java.io.FileReader
import java.io.IOException


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
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_provider)

        mStartView.setOnClickListener(this)
    }


    /**
     * 第三方app读取数据
     */
    fun readFormUri() {
        val returnUri = Uri.parse("content://com.edreamoon.warehouse.provider/file_path/a.txt")
        val inputPFD = contentResolver.openFileDescriptor(returnUri, "r")
        val returnCursor = contentResolver.query(returnUri, null, null, null, null)
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        println("文件名:" + returnCursor.getString(nameIndex) + ", 大小:" + returnCursor.getLong(sizeIndex) + " B")
        returnCursor.close()


        //读取文件内容
        var content = ""
        var fr: FileReader? = null
        val buffer = CharArray(1024)

        try {
            val strBuilder = StringBuilder()
            fr = FileReader(inputPFD.fileDescriptor)
            while (fr!!.read(buffer) !== -1) {
                strBuilder.append(buffer)
            }
            fr!!.close()
            content = strBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (content.isNotEmpty()) {
            println(content)
        }
        try {
            inputPFD.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
