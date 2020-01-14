package com.ware.dialog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.common.Utils
import com.ware.systip.ThirdActivity
import kotlinx.android.synthetic.main.activity_popup.*

class PopupActivity : AppCompatActivity(), View.OnClickListener {
    private var mPopWindow: PopupWindow? = null
    private var mAnchorView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup)
        findViewById<View>(R.id.show).setOnClickListener(this)
        findViewById<View>(R.id.dismiss).setOnClickListener(this)
        mAnchorView = findViewById(R.id.anchor)
        mToastView1.setOnClickListener(this)
        mToastView2.setOnClickListener(this)
    }

    var measuredHeight = 0 //测量得到的textview的高
    var measuredWidth = 0 //测量得到的textview的宽
    private fun showPopupWindow() {
        var contentView: View? = null
        if (mPopWindow == null) {
            contentView = LayoutInflater.from(this).inflate(R.layout.layout_redleaves_city_tip, null)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            contentView.measure(widthSpec, heightSpec)
            measuredHeight = contentView.measuredHeight
            measuredWidth = contentView.measuredWidth
            //一定要指定 PopupWindow 的宽高，否则无法显示
            mPopWindow = PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mPopWindow!!.contentView = contentView
        }
        //        mPopWindow.showAtLocation(mAnchorView, Gravity.NO_GRAVITY, 500, 500);
        Log.d(TAG, "$measuredHeight:$measuredWidth")
        mPopWindow!!.showAsDropDown(mAnchorView, (-Utils.dp2px(216f)).toInt(), -mAnchorView!!.height - measuredHeight) //(int) Utils.dp2px(46)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.show -> {
                //                showPopupWindow();
//                try {
//                    Log.e("lijf", "onClick: " + DESUtil.encrypt("48113805"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                val intent = Intent(this, ThirdActivity::class.java)
                startActivity(intent)
            }
            R.id.dismiss -> {
                mPopWindow!!.dismiss()
                try {
                    Log.e("lijf", "onClick: " + DESUtil.decode("8fbc96KEFongUzo1qGl2wg=="))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.mToastView1 -> {
                showNewToastEveryTime()
            }
            R.id.mToastView2 -> {
                showSameToastEveryTime()
            }
        }
    }

    private fun showNewToastEveryTime() {
        Toast.makeText(this, System.currentTimeMillis().toString(), Toast.LENGTH_SHORT).show()
    }

    private val mToast by lazy { Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT) }
    private fun showSameToastEveryTime() {
        mToast.setText(System.currentTimeMillis().toString())
        mToast.show()
//        Toast.makeText(this, System.currentTimeMillis().toString(), Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: pop")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d(TAG, "onWindowFocusChanged pop: $hasFocus")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: $requestCode: $resultCode:$data")
    }

    companion object {
        const val TAG = "PopupActivity"
    }
}