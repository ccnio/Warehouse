package com.ccnio.sec

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.remoteActivity).setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.remoteActivity -> goRemoteActivity()
        }
    }

    /**
     *  定义权限: <permission android:name="com.ware.permission.WARE" android:protectionLevel="dangerous" />
     *  权限分为若干个保护级别，normal, dangerous, signature等。normal就是正常权限，该权限并不会给用户或者设备的隐私带来风险；dangerous就是危险权限，该级别的权限通常会给用户的数据或设备的隐私带来风险；signature指的是，只有相同签名的应用才能使用该权限
     *
     *  使用/申请权限: <uses-permission android:name="com.ware.permission.WARE" />
     *
     *  app定义了权限且AcActivity 声明了 permission 属性:
     *    <activity android:name=".component.AcActivity"
     *           android:label="@string/component_activity"
     *           android:permission="com.ware.permission.WARE">
     *   所以跳转到AcActivity时,需要secondapp 申请对应权限,否则无法跳转
     *
     * 在manifest文件中uses-permission 前提下: signature,限制同签名应用才可以请求；normal都可以请求；
     * dangerous与camera这些权限一样,需要运行时申请权限(在虚拟机上没问题,小米最新miui有问题,烦人miui).
     */
    private fun goRemoteActivity() {
        val permissionName = "com.ware.permission.WARE" //要检测的权限名称
//
        val res: Int = ContextCompat.checkSelfPermission(this, permissionName)
        if (res == PackageManager.PERMISSION_GRANTED) {
            //获得了权限
            val intent = Intent()
            intent.action = "com.ware.ac"
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            startActivity(intent)
        } else if (res == PackageManager.PERMISSION_DENIED) {
            //未获得权限
            ActivityCompat.requestPermissions(this, arrayOf(permissionName), 11)//黑屏
        }
        Log.d(TAG, "goRemoteActivity: res = $res")

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: $requestCode, ret = ${grantResults.contentToString()}")
    }
}