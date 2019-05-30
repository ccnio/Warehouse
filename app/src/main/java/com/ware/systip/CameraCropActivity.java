package com.ware.systip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.edreamoon.Utils;
import com.ware.BuildConfig;
import com.ware.R;

import java.io.File;

public class CameraCropActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_BACK_CODE = 22;
    private static final int RESULT_REQUEST_BACK_CODE = 33;
    private Uri mCropUri;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.verifyStoragePermissions(this);

        setContentView(R.layout.activity_camera_crop);
        mImageView = findViewById(R.id.holder);
        File outputImage = new File(Environment.getExternalStorageDirectory(), "camera_crop_result.jpg");
        mCropUri = Uri.fromFile(outputImage);
        findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }


    Uri imageUri;

    private void takePhoto() {
        try {
            File outputImage = new File(Environment.getExternalStorageDirectory(), "camera_crop.jpg");
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", outputImage);
            Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentFromCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentFromCapture, CAMERA_REQUEST_BACK_CODE);
            Log.e("lijf", "takePhoto: " + imageUri.toString());//content://com.edreamoon.stu.provider/my_images/camera_crop.jpg
        } catch (Exception e) {
            Log.e("lijf", "takePhoto: " + e.getMessage());
        }
    }

    int i = 0;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_BACK_CODE) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(imageUri, "image/*");
            // 设置裁剪
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 720);
            intent.putExtra("outputY", 720);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra("noFaceDetection", false);
            File outputImage = new File(Environment.getExternalStorageDirectory(), "camera_crop_result" + i + ".jpg");
            i++;
            mCropUri = Uri.fromFile(outputImage);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(intent, RESULT_REQUEST_BACK_CODE);
        } else if (requestCode == RESULT_REQUEST_BACK_CODE) {
            Log.e("lijf", "onActivityResult: ");

            try {
                //图片解析成Bitmap对象
//                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mCropUri));
//                mImageView.setImageBitmap(bitmap); //将剪裁后照片显示出来

                /**
                 * 小米手机上会有缓存问题，名字和上次不一样或者使用注释部分代码
                 */
                Log.e("lijf", "onActivityResult: " + mCropUri.toString());
                mImageView.setImageURI(mCropUri);
            } catch (Exception e) {
                Log.e("lijf", "onActivityResult: " + e.getMessage());
            }
        }
    }
}
