package com.ware.device

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.invoke
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.EnvironmentCompat
import androidx.lifecycle.Observer
import com.ware.R
import com.ware.component.BaseActivity
import com.ware.component.permissionutil.PermissionResult
import com.ware.component.permissionutil.PermissionUtils
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "CameraActivity"

class CameraActivity : BaseActivity(R.layout.activity_camera), View.OnClickListener {
    private val permission by lazy { PermissionUtils(this) }
    private val CAMERA_REQUEST_CODE = 0x00000010

    /**
     * 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
     */
    private var mCameraImagePath: String? = null

    /**
     * 用于保存拍照图片的uri
     */
    private var mCameraUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        captureView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.captureView -> {
                permission.request(android.Manifest.permission.CAMERA).observe(this, Observer {
                    when (it) {
                        PermissionResult.Grant -> openCamera() // takePicture()
                    }
                })
            }
        }
    }

    private val takePicture = prepareCall(ActivityResultContracts.TakePicture()) { result ->
//        photo.setImageBitmap(result)
        imageView.setImageBitmap(result)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.d("CameraActivity", "onActivityResult: $mCameraUri")
                    // Android 10 使用图片uri加载
                    imageView.setImageURI(mCameraUri)
                } else {
                    // 使用图片路径加载
                    imageView.setImageBitmap(BitmapFactory.decodeFile(mCameraImagePath))
                    Log.d("CameraActivity", "onActivityResult222: ${Uri.fromFile(File(mCameraImagePath))}")
                }
            } else {
                Toast.makeText(this, "取消", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openCamera() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            var photoUri: Uri? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                photoUri = createImageUri();
            } else {
                photoFile = createImageFile()
                if (photoFile != null) {
                    mCameraImagePath = photoFile.absolutePath;
                    photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        FileProvider.getUriForFile(this, "$packageName.provider", photoFile)
                    } else {
                        Uri.fromFile(photoFile);
                    }
                }
            }

            mCameraUri = photoUri
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private fun createImageFile(): File? {
        val imageName: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir!!.exists()) storageDir.mkdir()

        val tempFile = File(storageDir, imageName)
        return if (Environment.MEDIA_MOUNTED != EnvironmentCompat.getStorageState(tempFile)) null
        else tempFile
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     * @return 图片的uri
     */
    private fun createImageUri(): Uri? {
        val status = Environment.getExternalStorageState()
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        return if (status == Environment.MEDIA_MOUNTED) {
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
        } else {
            contentResolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, ContentValues())
        }
    }
}