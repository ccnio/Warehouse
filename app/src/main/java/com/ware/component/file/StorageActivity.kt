package com.ware.component.file

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.ware.R
import com.ware.common.Utils
import com.ware.component.BaseActivity
import com.ware.util.FileUtil
import kotlinx.android.synthetic.main.activity_permission.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


/**
 * https://developer.android.com/training/data-storage/shared/media?hl=zh-cn
 *
 * # 注意点
 * a. 外部存储访问: 10最好用mediastore/saf,10以下用file或者saf,因为mediastore有api限制
 * b. 共享文件操作时File或者path方式有很多限制,可以考虑Uri/InputStream/ParcelFileDescriptor/FileDescriptor
 *      val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
 *      val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
 *      parcelFileDescriptor.close()
 *
 * # 权限(见dirPermission())
 * a. 所有版本 内部与私有目录不需要权限,可以通过file api操作。getFilesDir()/getCacheDir(), getExternalFilesDir(Environment.DIRECTORY_PICTURES)/getExternalCacheDir()
 * b. 其余外部目录: 10以下操作只跟权限有关, 有权限其它app下的外部空间也可以操作；10一般通过media/saf/FileProvider操作, 通过File操作文件会受限,有无权限会有不同影响。).Environment.getExternalStorageDirectory().absolutePath + "/girl.png"
 * c. 10及以上没有写权限了,只有读权限. 增加了 MANAGE_EXTERNAL_STORAGE 所有文件访问权限，被google归类为特殊权限，想要获得该权限必须要用户手动到应用设置里打开。
 *    如果应用声明了该权限并且想上play store，则一般应用是会被拒掉的，只有类似于文件管理器这种特殊应用才会被允许使用。
 *
 * # file api操作(见fileApi(). 10以下只跟权限有关,所以下面只针对10) "/storage/emulated/0/Pictures/girl (1).png"
 * ## 自己创建的文件 有无权限均可以各种操作
 * ## 其它文件
 * a. 无权限, 可用: exists(), dir.listFiles()； 不可用: 读写均不可以(canRead/canWrite 均为false)
 * b. 有权限, 只能读；delete不会报错但不会执行.
 *
 * # MediaStore 操作. 媒体文件(图片、音频文件、视频)与下载文件
 * ## media 读取媒体文件(mediaReadImg)
 * a. 没有权限: 只能读取自己创建的图片, 无法读取其它应用创建的图.
 * b. 有权限: 可以读取目录下所有图片
 * ## media 写媒体文件
 * a. 插入时,有无权限均可以,如果有重名的图片,新保存的图片名字以数字递增为后缀,如 test(2).jpg(见mediaCreateImg)
 * b. 删除时,自己的可以, 非自己即使有权限也会报SecurityException
 * c. 更新或删除其它应用文件(mediaWriteOther()): 可通过捕获平台抛出的 RecoverableSecurityException 来征得用户同意修改文件。然后，您可以请求用户授予您的应用对此特定内容的写入权限
 *
 * # Storage Access Framework
 * 应用通过系统选择器访问 DocumentsProvider 提供文件(包含外部存储以及云端存储， 外部存储包含应用私有目录以及共享目录)，
 * SAF机制不需要申请任何存储权限, 包含Document provider、Client app、Picker. 获取uri或者fileDesption后如果再获取path使用file api访问的话就不可以了.
 * a. 读(见safOpenFile())和写(见safWriteFile())是分开的,读的话非自己的只能读操作.
 *
 * # 对外分享文件使用FileProvider
 * 由于权限的限制,其它app无法再访问本应用外部存储,这时就需要使用FileProvider了,且接收方要处理好才能接收.微信已经支持通过了FileProvider形式进行分享了, 微信分享:
 * a. FileProvider绝对大数App在适配 Android7(>=N) 的文件存储的时候就已经做了,注意版本适配
 * b. 见 shareToWechat()
 *
 * # 清除数据 或者 卸载重装后,原先公共目录创建的数据就不再归属此app了.
 * # getGalleryIntent 图库选择图片 new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);}同saf. 不需要权限就可以选择所有公共图片.选择后的图片:
 * # 媒体位置权限 如果应用使用分区存储，您需要在应用的清单中声明 ACCESS_MEDIA_LOCATION 权限，然后在运行时请求此权限，应用才能从照片中检索未编辑的 Exif 元数据。
 * # 加载文件缩略图val thumbnail: Bitmap = applicationContext.contentResolver.loadThumbnail(content-uri, Size(640, 480), null)
 */
private const val CODE_GALLERY = 0x22
private const val TAG = "PermissionActivity"
private const val CODE_SAF_WRITE = 1
private const val CODE_SAF_OPEN = 2
private const val CODE_DELETE_OTHER = 3

class StorageActivity : BaseActivity(R.layout.activity_permission), View.OnClickListener {

    private fun shareToWechat(context: Context) {
        // 该filePath对应于xml/file_provider_paths里的配置才可被共享
        val filePath = context.getExternalFilesDir(null)!!.absolutePath + "/shareData/test.png";
        val file = File(filePath)
        val uriStr: String? = getFileUri(context, file)
        // 使用 uriStr 作为文件路径进行分享
        // ...
    }

    private fun getFileUri(context: Context, file: File): String? {
        if (!file.exists()) return null

        val contentUri: Uri = FileProvider.getUriForFile(context, "com.example.app.fileprovider", file)  // 要与`AndroidManifest.xml`里配置的`authorities`一致
        // 授权给微信访问路径
        context.grantUriPermission("com.tencent.mm",  // 这里填微信包名
                contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return contentUri.toString()   // contentUri.toString() 即是以"content://"开头的用于共享的路径
    }

    private fun dirPermission() {
        launch {
            withContext(Dispatchers.IO) {
                val bit = BitmapFactory.decodeResource(resources, R.drawable.green_girl)
                //1. below 10: need permission; 10 cannot access, even if has permission
//                val path = Environment.getExternalStorageDirectory().absolutePath + "/girl.png"//EACCES (Permission denied)

                //2. no need to request permission for all version
                //val path = externalCacheDir?.absolutePath + "/girl.png" //Android/data/com.ware/cache/girl.png,
//                val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/girl.png" //Android/data/com.ware/files/Pictures/girl.png
                val path = "/storage/emulated/0/Android/data/com.ccnio.sec/files/girl.png" //其它app下的外部空间也可以操作
                val ret = Utils.saveBitmap(bit, path)
                Log.d("PermissionActivity", "writePermission: ret = $ret; path = $path")
            }
        }
    }

    private fun fileApi() {
        val dir = File("/storage/emulated/0/Pictures/")
        dir.listFiles().forEach { Log.d(TAG, "fileApi iter: $it") } //不受限

        val publicExternalPath = "/storage/emulated/0/Pictures/girl (1).png"
        val file = File(publicExternalPath)
        Log.d(TAG, "fileApi: exists = ${file.exists()}; canRead = ${file.canRead()}; canWrite = ${file.canWrite()}")
        file.delete()
    }

    private fun safOpenFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            //It's possible to limit the types of files by mime-type.
            /**mime type: [MediaType.java] **/
            type = "text/plain"

            //ensure use [ContentResolver.openFileDescriptor] to read the data of whatever file is picked
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        /*   或者如下 action: 只是选择界面表现形式不一样
         val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = IMAGE_MIME_TYPE }

         ACTION_OPEN_DOCUMENT_TREE intent 操作，它支持用户授予应用对整个目录树的访问权限。见官网
         */

        startActivityForResult(intent, CODE_SAF_OPEN)
    }

    private fun safCreateFile() {
        /**
         * 可以保存在sd卡外部非沙箱内的任何地方. uri必需是document生成的,通过file生成的不可用,所以下面代码会直接跳到根目录
         * As far as I'm aware, You cannot specify a starting location using an arbitrary URI, e.g. Uri.fromFile, it needs to originate from a DocumentsProvider
         */
        val pickerInitialUri: Uri = Uri.fromFile(File(Environment.getRootDirectory().absolutePath + "/MiWatch/"))
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")

            // Optionally, specify a URI for the directory that should be opened in the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        //    这个时候会弹框让用户选择是否保存，保存完后可以根据文件uri路径写入内容。
        startActivityForResult(intent, CODE_SAF_WRITE)
    }

    private fun mediaDeleteImg() {
        val uri = Uri.parse("content://media/external/images/media/442277") //可通过mediaCreateImg/mediaReadImg获取
        val delete = contentResolver.delete(uri, null, null) //非自己即使有权限也会报SecurityException
        Log.d(TAG, "mediaDeleteImg: $delete")
        refreshMediaStore()
    }

    /**
     * 写错位置如DIRECTORY_DOWNLOADS,会报:Primary directory Download not allowed for content://media/external/images/media; allowed directories are [DCIM, Pictures]
     */
    private fun mediaCreateImg(context: Context, bitmap: Bitmap, format: CompressFormat, mimeType: String, displayName: String) {
        val uri = getUri(context, displayName, mimeType, Environment.DIRECTORY_PICTURES, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val path = FileUtil.getPath(uri)

        //真正写入
        val stream = contentResolver.openOutputStream(uri!!)
        val ret = stream.use { bitmap.compress(format, 100, stream) }
        if (!ret) contentResolver.delete(uri, null, null);

        Log.d(TAG, "mediaWriteImg: uri = $uri; path = $path")
        refreshMediaStore()
    }

    //没有读权限的话,只能读自己创建的文件,有的话可以所有;
    //数据库的记录不一定与图库里一致,比如删除一张图片,数据库还未来得及刷新,这时 contentResolver.openInputStream(contentUri)就会报FileNotFoundException
    private fun mediaReadImg(filePath: String): Bitmap? {
        val queryPathKey = MediaStore.Images.Media.RELATIVE_PATH
        val selection = "$queryPathKey=? "
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID, queryPathKey, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.DISPLAY_NAME),
                selection, arrayOf(filePath), null)
        Log.d(TAG, "mediaReadImg: 读取到${cursor?.count}张图片如下:")
        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)) //uri的id，用于获取图片
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH));//图片的相对路径
                val type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));//图片类型
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));//图片名字
                //通过流转化成bitmap对象
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong())
                try {
                    val inputStream = contentResolver.openInputStream(contentUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    Log.d(TAG, "mediaReadImg: relativePath = $path; type = $type; name = $name; uri = $contentUri; width = ${bitmap.width}")
                } catch (e: FileNotFoundException) {
                    Log.e(TAG, "mediaReadImg: file not found, uri = $contentUri")
                }
            }
        }
        return null
    }

    private fun mediaWriteOther() {
        val uri = Uri.parse("content://media/external/images/media/366548") //可通过mediaCreateImg/mediaReadImg获取
        try {
            val delete = contentResolver.delete(uri, null, null)
            Log.d(TAG, "mediaDeleteImg: $delete")
        } catch (securityException: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recoverableSecurityException = securityException as? RecoverableSecurityException
                        ?: throw RuntimeException(securityException.message, securityException)

                val intentSender = recoverableSecurityException.userAction.actionIntent.intentSender
                intentSender?.let { startIntentSenderForResult(intentSender, CODE_DELETE_OTHER, null, 0, 0, 0, null) }
            } else {
                throw RuntimeException(securityException.message, securityException)
            }
        }
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
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)//1、配置文件名
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType) //2、配置文件类型
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)//3、配置存储目录
        return context.contentResolver.insert(externalContentUri, values)//4、将配置好的对象插入到某张表中，最终得到Uri对象
    }

    private fun mediaWriteDownloadFile() {
        val uri = getUri(this, "a.log", "text/plain", Environment.DIRECTORY_DOWNLOADS, MediaStore.Downloads.EXTERNAL_CONTENT_URI)
        val stream = contentResolver.openOutputStream(uri!!) ?: return
        stream.use { stream.write("abc".toByteArray()) }
    }

    private fun mediaReadDownloadFile() {
        val queryPathKey = MediaStore.Downloads.RELATIVE_PATH
        val filePath = Environment.DIRECTORY_DOWNLOADS + File.separator
        val selection = "$queryPathKey=? "
        val cursor = contentResolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Downloads._ID, queryPathKey, MediaStore.Downloads.MIME_TYPE, MediaStore.Downloads.DISPLAY_NAME),
                selection, arrayOf(filePath), null)
        Log.d(TAG, "mediaReadImg: 读取到${cursor?.count}个文件如下:")
        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Downloads._ID)) //uri的id，用于获取图片
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.RELATIVE_PATH));//图片的相对路径
                val type = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.MIME_TYPE));//图片类型
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME));//图片名字
                val contentUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id.toLong())
                Log.d(TAG, "mediaReadDownloadFile: relativePath = $path; type = $type; name = $name; uri = $contentUri")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: req = $requestCode; ret = $resultCode")
        if (resultCode != RESULT_OK) return

        val uri = data?.data!!
        val path = FileUtil.getPath(uri)
        when (requestCode) {
            CODE_GALLERY -> {
                Glide.with(this).load(path).into(imgView)
                Log.d(TAG, "onActivityResult: uri = $uri; path = $path")
            }
            CODE_SAF_OPEN -> {
                val file = File(path)
                Log.d(TAG, "onActivityResult SAF_OPEN: uri = $uri; path = $path; canRead = ${file.canRead()}; canWrite = ${file.canWrite()}")//canRead: false; canWrite: false;

                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use { Log.d(TAG, "content = ${it.bufferedReader().readText()} ") } //ok

                val openOutputStream = contentResolver.openOutputStream(uri)
                openOutputStream?.use { it.bufferedWriter().write("hello") }// not ok

                val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                fileDescriptor?.use {
                    val readText = FileInputStream(it.fileDescriptor).bufferedReader().readText() //ok
                    Log.d(TAG, "fileDescriptor content = $readText")
                }
            }
            CODE_SAF_WRITE -> {
            }
            CODE_DELETE_OTHER -> Log.d(TAG, "media delete other: $uri")

        }
    }

    private fun openGallery() = startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), CODE_GALLERY)

    private fun refreshMediaStore() {
        // 通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(this, arrayOf(Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_PICTURES), null
            ) { path, uri -> Log.d(TAG, "onScanCompleted: path = $path; uri = $uri") }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dirView -> dirPermission()
            R.id.mediaCreateImgView -> mediaCreateImg(this, BitmapFactory.decodeResource(resources, R.drawable.green_girl), CompressFormat.PNG, "image/png", "girl.png")
            R.id.mediaReadImgView -> mediaReadImg(Environment.DIRECTORY_PICTURES + File.separator)
            R.id.mediaDeleteImgView -> mediaDeleteImg()
            R.id.mediaOtherView -> mediaWriteOther()
            R.id.fileView -> fileApi()
            R.id.mediaDownloadReadView -> mediaReadDownloadFile()
            R.id.mediaDownloadWriteView -> mediaWriteDownloadFile()
            R.id.safReadView -> safOpenFile()
            R.id.safCreateView -> safCreateFile()
            R.id.galleryView -> openGallery()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dirView.setOnClickListener(this)

        mediaReadImgView.setOnClickListener(this)
        mediaCreateImgView.setOnClickListener(this)
        mediaDeleteImgView.setOnClickListener(this)

        mediaOtherView.setOnClickListener(this)

        mediaDownloadWriteView.setOnClickListener(this)
        mediaDownloadReadView.setOnClickListener(this)

        safReadView.setOnClickListener(this)
        safCreateView.setOnClickListener(this)
        galleryView.setOnClickListener(this)
        fileView.setOnClickListener(this)
    }

}
