package com.ware.component.file

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.common.BaseActivity
import com.ware.common.Utils
import kotlinx.android.synthetic.main.activity_permission.*
import kotlinx.coroutines.*
import java.io.File


/**
 * https://juejin.im/post/5e43ab2bf265da572660f777
 *1. 内部与私有 目录不需要权限（all version）
 * getFilesDir()/getCacheDir(), getExternalFilesDir(Environment.DIRECTORY_PICTURES)/getExternalCacheDir()
 *
 *2. 其它外部存储目录
 * - android 10以下访问方式
 * a.通过Environment.getExternalStorageDirectory需要权限；
 *
 * - android 10 访问方式
 * a.通过MediaStore API，
 * I.无权限能在medial指定目录创建文件并访问自己的创建文件见save2MediaStore；
 * II.有权限能操作共享目录其它应用创建的media类型文件
 * b. 通过Storage Access Framework(非media文件)
 * 应用通过系统选择器访问 DocumentsProvider 提供文件(包含外部存储以及云端存储， 外部存储包含应用私有目录以及共享目录)，
 * SAF机制不需要申请任何存储权限, 包含Document provider、Client app、Picker
 *
 * 3. 判断兼容模式接口
 * Environment.isExternalStorageLegacy()返回值,true : 应用以兼容模式运行; false：应用以分区存储特性运行
 */
class PermissionActivity : BaseActivity(R.layout.activity_permission), View.OnClickListener, CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writeView.setOnClickListener(this)
        mediaWriteView.setOnClickListener(this)
        mediaQueryView.setOnClickListener(this)
    }

    private fun writePermission() {
        launch {
            withContext(Dispatchers.IO) {
                val bit = BitmapFactory.decodeResource(resources, R.drawable.green_girl)
                //1. below 10: need permission; 10 cannot access, even if has permission
//                val path = Environment.getExternalStorageDirectory().absolutePath + "/girl.png"//EACCES (Permission denied)

                //2. no need to request permission for all version
                //val path = externalCacheDir?.absolutePath + "/girl.png" //Android/data/com.ware/cache/girl.png,
                val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/girl.png" //Android/data/com.ware/files/Pictures/girl.png
                val ret = Utils.saveBitmap(bit, path)
                Log.d("PermissionActivity", "writePermission: ret = $ret; path = $path")
            }
        }
    }

    private fun saveMediaStore(context: Context, bitmap: Bitmap, format: CompressFormat, mimeType: String, displayName: String): Boolean {
        val relativeLocation = Environment.DIRECTORY_DOWNLOADS
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        val resolver = context.contentResolver
        val uri: Uri = resolver.insert(contentUri, contentValues) ?: return false
        Log.d("PermissionActivity", "saveMediaStore: uri = $uri")

        val stream = resolver.openOutputStream(uri) ?: return false
        val ret = stream.use { bitmap.compress(format, 100, stream) }
        if (!ret) resolver.delete(uri, null, null);
        return ret
    }

    private fun queryMediaStore(filePath: String): Bitmap? {
        val queryPathKey = MediaStore.Images.Media.RELATIVE_PATH
        val selection = "$queryPathKey=? "
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID, queryPathKey, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.DISPLAY_NAME),
                selection,
                arrayOf(filePath),
                null)
        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)) //uri的id，用于获取图片
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH));//图片的相对路径
                val type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));//图片类型
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));//图片名字
                Log.d("PermissionActivity", "queryMediaStore: relativePath = $path; type = $type; name = $name")
                //通过流转化成bitmap对象
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong())
                val inputStream = contentResolver.openInputStream(contentUri);
                val bitmap = BitmapFactory.decodeStream(inputStream);
                Log.d("PermissionActivity", "queryMediaStore: bit = ${bitmap.width}")
            }
        }
        return null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.writeView -> writePermission()
            R.id.mediaWriteView -> {
                val ret = saveMediaStore(this, BitmapFactory.decodeResource(resources, R.drawable.green_girl),
                        CompressFormat.PNG, "image/png", "girl.png")
                Log.d("PermissionActivity", "saveMediaStore ret = $ret")
            }
            R.id.mediaQueryView -> {
                val queryMediaStore = queryMediaStore(Environment.DIRECTORY_DOWNLOADS + File.separator)
                Log.d("PermissionActivity", "queryMediaStore = $queryMediaStore")
            }
        }
    }
}
