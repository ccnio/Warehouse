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
import com.ware.common.Utils
import com.ware.component.BaseActivity
import kotlinx.android.synthetic.main.activity_permission.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
 *
 * https://www.jianshu.com/p/4d74b719309f
 * android 11
 * 1. 写入权限在11中被彻底废弃了，想要写入需要通过mediaStore和SAF框架，测试下来并不需要权限就可以通过这两种API写入文件到指定目录。
 * 2. 新增管理权限 <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>该权限的功能和之前的write权限基本一致，被google归类为特殊权限，想要获得该权限必须要用户手动到应用设置里打开，类似于打开应用通知。如果应用声明了该权限并且想上play store，则一般应用是会被拒掉的，只有类似于文件管理器这种特殊应用才会被允许使用。
 *
 *
 */
class PermissionActivity : BaseActivity(R.layout.activity_permission), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writeView.setOnClickListener(this)
        mediaWriteView.setOnClickListener(this)
        mediaQueryView.setOnClickListener(this)
        mediaFileView.setOnClickListener(this)
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

    /**
     * DIRECTORY_DOWNLOADS会报:Primary directory Download not allowed for content://media/external/images/media; allowed directories are [DCIM, Pictures]
     */
    private fun writeMediaStorePicture(context: Context, bitmap: Bitmap, format: CompressFormat, mimeType: String, displayName: String): Boolean {
        val relativeLocation = Environment.DIRECTORY_PICTURES
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

    private fun writeMediaStoreFile() {
        val uri = getUri(this, "a.log", "text/plain", Environment.DIRECTORY_DOWNLOADS, MediaStore.Downloads.EXTERNAL_CONTENT_URI)
                ?: return
        val stream = contentResolver.openOutputStream(uri) ?: return
        stream.use { stream.write("abc".toByteArray()) }
    }

    /**
     * 第一步，配置文件名称
     * 第二步，配置文件类型，每个文件都应该有一个类型描述，这样，后续查找时，就可以根据这个类型去查找出同一类型的文件，如:查找相册，此属性是可选的，如果不配置，后续就无法根据类型查找到这个文件
     *
     * 第三步，配置存储目录，这个是相对路径，总共有10个目录可选，如下：
     * Environment.DIRECTORY_DOCUMENTS 对应路径：/storage/emulated/0/Documents/
     * Environment.DIRECTORY_DOWNLOADS 对应路径：/storage/emulated/0/Download/
     * Environment.DIRECTORY_DCIM 对应路径：/storage/emulated/0/DCIM/
     * Environment.DIRECTORY_PICTURES 对应路径：/storage/emulated/0/Pictures/
     * Environment.DIRECTORY_MOVIES 对应路径：/storage/emulated/0/Movies/
     * Environment.DIRECTORY_ALARMS 对应路径：/storage/emulated/0/Alrams/
     * Environment.DIRECTORY_MUSIC 对应路径：/storage/emulated/0/Music/
     * Environment.DIRECTORY_NOTIFICATIONS 对应路径：/storage/emulated/0/Notifications/
     * Environment.DIRECTORY_PODCASTS 对应路径：/storage/emulated/0/Podcasts/
     * Environment.DIRECTORY_RINGTONES 对应路径：/storage/emulated/0/Ringtones/
     * 如果需要在以上目录下，创建子目录，则传入的时候，直接带上即可: values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/RxHttp");
     *
     * 第四步，插入到对应的表中，总共有5张表可选，如下：
     * 存储图片：MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     * 存储视频：MediaStore.Video.Media.EXTERNAL_CONTENT_URI
     * 存储音频：MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
     * 存储任意文件：MediaStore.Downloads.EXTERNAL_CONTENT_URI
     * 存储任意文件：MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
     * 需要特殊说明下，以上5张表中，只能存入对应文件类型的信息，如我们不能将音频文件信息，插入到MediaStore.Images.Media.EXTERNAL_CONTENT_URI图片表中，插入时，系统会直接抛出异常
     */
    private fun getUri(context: Context, displayName: String, mimeType: String, relativePath: String, externalContentUri: Uri): Uri? {
        val values = ContentValues()
        //1、配置文件名
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        //2、配置文件类型
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        //3、配置存储目录
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        //4、将配置好的对象插入到某张表中，最终得到Uri对象
        return context.contentResolver.insert(externalContentUri, values)
    }

    private fun queryMediaStorePicture(filePath: String): Bitmap? {
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
                val ret = writeMediaStorePicture(this, BitmapFactory.decodeResource(resources, R.drawable.green_girl),
                        CompressFormat.PNG, "image/png", "girl.png")
                Log.d("PermissionActivity", "saveMediaStore ret = $ret")
            }
            R.id.mediaQueryView -> {
                val queryMediaStore = queryMediaStorePicture(Environment.DIRECTORY_PICTURES + File.separator)
                Log.d("PermissionActivity", "queryMediaStore = $queryMediaStore")
            }
            R.id.mediaFileView -> writeMediaStoreFile()
        }
    }
}
