package com.ccnio.sec

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
    和应用安装顺序的关系(所以除了signature,这个功能没啥用途)
    场景：App A中声明了权限PermissionA，App B中使用了权限PermissionA。


    情况一：PermissionA的保护级别是normal或者dangerous
    App B先安装，App A后安装，此时App B无法获取PermissionA的权限，从App B打开App A会报权限错误。
    App A先安装，App B后安装，从App B打开App A一切正常。


    情况二：PermissionA的保护级别是signature或者signatureOrSystem
    App B先安装，App A后安装，如果App A和App B是相同的签名，那么App B可以获取到PermissionA的权限。如果App A和App B的签名不同，则App B获取不到PermissionA权限。
    即，对于相同签名的app来说，不论安装先后，只要是声明了权限，请求该权限的app就会获得该权限。
    这也说明了对于具有相同签名的系统app来说，安装过程不会考虑权限依赖的情况。安装系统app时，按照某个顺序（例如名字排序，目录位置排序等）安装即可，等所有app安装完了，所有使用权限的app都会获得权限。
     */
    private fun goRemoteActivity() {
        val permissionName = "com.ware.permission.WARE" //要检测的权限名称
//
//        val res: Int = ContextCompat.checkSelfPermission(this, permissionName)
//        if (res == PackageManager.PERMISSION_GRANTED) {
//            //获得了权限
//
//        } else if (res == PackageManager.PERMISSION_DENIED) {
//            //未获得权限
//            ActivityCompat.requestPermissions(this, arrayOf(permissionName), 11)//黑屏
//        }
//        Log.d(TAG, "goRemoteActivity: res = $res")
        val intent = Intent()
        intent.setAction("com.ware.ac")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: $requestCode")
    }
}