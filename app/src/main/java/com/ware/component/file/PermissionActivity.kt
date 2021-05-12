package com.ware.component.file

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
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
 * https://developer.android.com/training/data-storage/shared/media?hl=zh-cn
 * https://juejin.im/post/5e43ab2bf265da572660f777
 *1. 内部与私有 目录不需要权限（all version）,可以通过file path获取资源。
 * getFilesDir()/getCacheDir(), getExternalFilesDir(Environment.DIRECTORY_PICTURES)/getExternalCacheDir()
 *
 *2. 其它外部存储目录
 * - android 10以下访问方式
 * a.通过Environment.getExternalStorageDirectory需要权限；
 *
 * - android 10/11 访问方式
 * 不能够通过File Path路径直接访问共享目录下资源，以下接口通过File路径操作文件资源，功能会受到影响，应用需要使用MediaStore或者SAF方式访问。
 * a.通过MediaStore API，
 * I.无权限能在medial指定目录创建文件并访问自己的创建文件见save2MediaStore；如果应用需要访问或者修改其他应用的文件怎么办呢。
 * 媒体文件话的:(Pictures/Audios等)如果只是要读取，则申请 READ_EXTERNAL_STORAGE 权限后即可通过 MediaStore Api 进行读取,例如我们上述的查询自己应用的文件中的查询语句，如果申请了读取外置存储的权限后，返回的数据就会包含了其他 App 提供给 Media 的图片了
 * 如果应用需要访问 MediaStore.Downloads: 集合中某个并非由其创建的文件，您必须使用存储访问框架。(saf)
 * II.有权限能操作共享目录其它应用创建的media类型文件
 * b. 通过Storage Access Framework(非media文件)
 * 应用通过系统选择器访问 DocumentsProvider 提供文件(包含外部存储以及云端存储， 外部存储包含应用私有目录以及共享目录)，
 * SAF机制不需要申请任何存储权限, 包含Document provider、Client app、Picker. 获取uri或者fileDesption后如果再获取path使用file api访问的话就不可以了.
 *
 * 3. 判断兼容模式接口
 * Environment.isExternalStorageLegacy()返回值,true : 应用以兼容模式运行; false：应用以分区存储特性运行
 *
 * https://www.jianshu.com/p/4d74b719309f
 * android 11
 * 1. 写入权限在11中被彻底废弃了，想要写入需要通过mediaStore和SAF框架，测试下来并不需要权限就可以通过这两种API写入文件到指定目录。
 * 2. 新增管理权限 <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>该权限的功能和之前的write权限基本一致，被google归类为特殊权限，想要获得该权限必须要用户手动到应用设置里打开，类似于打开应用通知。如果应用声明了该权限并且想上play store，则一般应用是会被拒掉的，只有类似于文件管理器这种特殊应用才会被允许使用。
 * 3. 通过MediaStore写入文件, 运行在Android11上不需要权限也可以写入成功
 * 4. 修改/删除操作，这个测试下来比较特殊，如果是在公共目录里删除自己写的文件也不需要权限，如果要删除其它应用写入的文件则每次删除都会弹框提示用户。delete的时候会抛异常，捕获这个异常，
 * 如果异常类型为RecoverableSecurityException的话，则可以在Activity或Fragement中调用startIntentSenderForResult(e.getUserAction().getActionIntent().getIntentSender(),请求码，null,0,0,0)，此时系统会弹框让你授权，当用户点击确定后再次删除即可。
 *   public Intent getGalleryIntent() {return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);} 访问规则与media provider(图片)一致
 *
 * SAF框架 https://developer.android.google.cn/training/data-storage/shared/documents-files?hl=zh-cn#create-file
 * https://developer.android.com/training/data-storage/shared/documents-files?hl=zh-cn
 * 我们不能再像之前的写法那样，自己写一个文件浏览器，然后从中选取文件，而是必须要使用手机系统中内置的文件选择器。
 * 该框架会弹出一个系统级的选择器，用户需要手动操作才能完整走完读写流程，由于用户在操作的时候相当于已经授权了，所以该框架调用不需要权限。相比于MediaStore固定的几个目录，SAF可以操作的目录更自由。
 *
 *4. 使用注意点
 *共享文件操作使用时这样就有很多限制,不能通过传递File或者path方式来作为参数了. 可以使用Uri/InputStream/ParcelFileDescriptor/FileDescriptor
 *   ParcelFileDescriptor file = dMgr.openDownloadedFile(downloadId);
 *   File dbFile = getDatabasePath(Roads.DATABASE_NAME);
 *   InputStream fileStream = new FileInputStream(file.getFileDescriptor());
 *   OutputStream newDatabase = new FileOutputStream(dbFile);
 *
 * 5. 权限
 * android 10及以上没有写权限了,只有读权限
 * MediaStore没有读权限的话,只能读自己创建的文件,有的话可以所有
 */
class PermissionActivity : BaseActivity(R.layout.activity_permission), View.OnClickListener {
    private val code_saf_write = 1
    /* 删除公共文件
     private suspend fun performDeleteImage(image: MediaStoreImage) {
         withContext(Dispatchers.IO) {
             try {
                 contentResolver.delete(
                         image.contentUri,
                         "${MediaStore.Images.Media._ID} = ?",
                         arrayOf(image.id.toString())
                 )
             } catch (securityException: SecurityException) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                     val recoverableSecurityException =
                             securityException as? RecoverableSecurityException
                                     ?: throw securityException

                     // Signal to the Activity that it needs to request permission and
                     // try the delete again if it succeeds.
 //                    pendingDeleteImage = image
 //                    _permissionNeededForDelete.postValue(
                     val intentSender = recoverableSecurityException.userAction.actionIntent.intentSender
                     startIntentSenderForResult(
                             intentSender,
                             22,
                             null,
                             0,
                             0,
                             0,
                             null
                     )
                 } else {
                     throw securityException
                 }
             }
         }
     }
 */
    /**
     * media provider
     */
    /* 获取相册中的图片
    不同于过去可以直接获取到相册中图片的绝对路径，在作用域存储当中，我们只能借助MediaStore API获取到图片的Uri，示例代码如下：
      val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc")
      if (cursor != null) {
          while (cursor.moveToNext()) {
              val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
              val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
              println("image uri is $uri")
          }
          cursor.close()
      }

      获取到了Uri之后，我又该怎样将这张图片显示出来呢？这就有很多种办法了，比如使用Glide来加载图片，它本身就支持传入Uri对象来作为图片路径：
      Glide.with(context).load(uri).into(imageView)

      而如果你没有使用Glide或其他图片加载框架，想在不借助第三方库的情况下直接将一个Uri对象解析成图片，可以使用如下代码：
      val fd = contentResolver.openFileDescriptor(uri, "r")
      if (fd != null) {
          val bitmap = BitmapFactory.decodeFileDescriptor(fd.fileDescriptor)
          fd.close()
          imageView.setImageBitmap(bitmap)
      }
      */

/*
    将图片添加到相册
    fun addBitmapToAlbum(bitmap: Bitmap, displayName: String, mimeType: String, compressFormat: Bitmap.CompressFormat) {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        } else {
            values.put(MediaStore.MediaColumns.DATA, "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName")
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(compressFormat, 100, outputStream)
                outputStream.close()
            }
        }
    }*/


    /**
     * saf
     */
//    读取
//    private fun openFile() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "*/*"
//            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
//                    "application/pdf", // .pdf
//                    "image/jpeg", // .jpeg
//                    "text/plain"))
//
//            Intent的action和category都是固定不变的。而type属性可以用于对文件类型进行过滤，比如指定成image/就可以只显示图片类型的文件，这里写成/*表示显示所有类型的文件。
//            注意type属性必须要指定，否则会产生崩溃。
//            // Optionally, specify a URI for the file that should appear in the
//            // system file picker when it loads
//        }
//
//        startActivityForResult(intent, 2)
//    }
//    用户选择某个文件后会返回应用，onActivityResult中有文件的URI路径。

//    创建和写入
//    // Request code for creating a PDF document.
    private fun safWrite() {
        /**
         * 可以保存在sd卡外部非沙箱内的任务地方. uri必需是document生成的,通过file生成的不可用,所以下面代码会直接跳到根目录
         * As far as I'm aware

        You cannot specify a starting location using an arbitrary URI, e.g. Uri.fromFile, it needs to originate from a DocumentsProvider
        You cannot specify a starting location on API 25 and below
        Assuming uri was retrieved from Intent.ACTION_OPEN_DOCUMENT_TREE:

        DocumentFile file = DocumentFile.fromTreeUri(context, uri);
        intent.putExtra(EXTRA_INITIAL_URI, file.getUri());
         */
        val pickerInitialUri: Uri = Uri.fromFile(File(Environment.getRootDirectory().absolutePath + "/MiWatch/"))
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, code_saf_write)
    }
//    这个时候会弹框让用户选择是否保存，保存完后可以根据文件uri路径写入内容。


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

    private fun writeDownloadFile() {
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

    /**
     * 没有读权限的话,只能读自己创建的文件,有的话可以所有
     */
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
//                //通过流转化成bitmap对象
//                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong())
//                val inputStream = contentResolver.openInputStream(contentUri);
//                val bitmap = BitmapFactory.decodeStream(inputStream);
//                Log.d("PermissionActivity", "queryMediaStore: bit = ${bitmap.width}")
            }
        }
        return null
    }

    /**
     * 只能读自己创建的文件.
     */
    private fun queryDownloadFile(filePath: String) {
        val queryPathKey = MediaStore.Downloads.RELATIVE_PATH;//MediaStore.Downloads.RELATIVE_PATH
        val selection = "$queryPathKey=? "
        val cursor = contentResolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Downloads._ID, queryPathKey, MediaStore.Downloads.MIME_TYPE, MediaStore.Downloads.DISPLAY_NAME),
                selection,
                arrayOf(filePath),
                null)
        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Downloads._ID)) //uri的id，用于获取图片
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.RELATIVE_PATH));//图片的相对路径
                val type = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.MIME_TYPE));//图片类型
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME));//图片名字
                Log.d("PermissionActivity", "queryDownloadFile: relativePath = $path; type = $type; name = $name")
//                //通过流转化成bitmap对象
//                val contentUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id.toLong())
//                val inputStream = contentResolver.openInputStream(contentUri);
//                val bitmap = BitmapFactory.decodeStream(inputStream);
//                Log.d("PermissionActivity", "queryMediaStore: bit = ${bitmap.width}")
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.writeView -> writePermission()
            R.id.mediaWriteView -> {
                val ret = writeMediaStorePicture(this, BitmapFactory.decodeResource(resources, R.drawable.green_girl),
                        CompressFormat.PNG, "image/png", "girl.png")
                Log.d("PermissionActivity", "saveMediaStore ret = $ret")
            }
            R.id.mediaQueryView -> queryMediaStorePicture(Environment.DIRECTORY_PICTURES + File.separator)

            R.id.mediaFileView -> writeDownloadFile()
            R.id.downloadQueryView -> queryDownloadFile(Environment.DIRECTORY_DOWNLOADS + File.separator)
            R.id.safWriteView -> safWrite()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writeView.setOnClickListener(this)
        mediaWriteView.setOnClickListener(this)
        mediaQueryView.setOnClickListener(this)
        mediaFileView.setOnClickListener(this)
        downloadQueryView.setOnClickListener(this)
        safWriteView.setOnClickListener(this)
    }

}
