package com.ware.performance

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.ware.R
import com.ware.common.Utils
import com.ware.component.BaseActivity
import kotlinx.android.synthetic.main.activity_memory.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import kotlin.concurrent.thread


/**
 *handler 内存泄露的解决办法，具体情况具体分析:https://github.com/Moosphan/Android-Daily-Interview/issues/1
 *1. 把 handler 定义为静态内部类，对外部 activity 的引用使用弱引用的方式
＊2. 在 activity 里的 onDestroy 回调方法中，调用 handler的removeCallbacksAndMessages（null）方法，清除消息队列中message
＊3. 把 handler 单独定义成一个类，不作为非静态内部类存在
 */
private const val TAG = "MemoryActivity"

class MemoryActivity : BaseActivity(), View.OnClickListener {
    //    private lateinit var bitmapTemp: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        leakView.setOnClickListener(this)
        bitView.setOnClickListener(this)
    }

    /**
     * kotlin 默认是静态内部，如果想指定非静态的需要显示加 inner
     */
    class WeakHandler(activity: Activity) : Handler() {
        private val weakReference = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            weakReference.get()?.let {
                // doSomeThings
            }
        }
    }

    private var num = 0
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.leakView -> leak()
            R.id.bitView -> loadBit()
        }
    }

    private fun loadBit() {
//        bitmapTemp = BitmapFactory.decodeResource(resources, R.drawable.green_girl)
        //内存大小跟ImageView尺寸没有关系
        Log.d(TAG, "loadBit: density = ${resources.displayMetrics.density}; screenW = ${Utils.getScreenWidth()}")
        //同本地的newer是同一张图片，只不过是远程加载。图片是xxhdpi规格切的
        val url = "https://cdn.cnbj1.fds.api.mi-img.com/plato-product/p_1603941266972BJyYS5JM.jpg?GalaxyAccessKeyId=AKVGLQWBOVIRQ3XLEW&Expires=9223372036854775807&Signature=/tIDAc1lchgdStUpZHQ8JRlg3Mk="
        /**
         * 以下测试条件density=3,1080*1920屏幕
         *
         * ## scaleType = FIT_CENTER（默认）
         * 1. localView 图片片显示大小600×300，但Bitmap尺寸就是图片原始尺寸1000×500，内存也是按此尺寸算的
         * 2. Glide.with(this).load(url).into(glideView) 显示效果同localView,但glide内部裁剪成Bitmap为600×300，所以内存相对于localView也小
         *
         * ## scaleType = CENTER_CROP
         * 1. localView 图片片显示完全填充控件并裁剪，但Bitmap尺寸依旧是图片原始尺寸1000×500
         * 2. Glide.with(this).load(url).into(glideView)显示效果同localView,内存里有两个Bitmap,一个缩放后的1080*540,另一个用于展示的Bitmap 1080*300,具体需要分析源码.
         * 3. Glide.with(this).load(url).transform(CenterCrop()).into(glideView)同 2
         *
         * ## scaleType = FIT_XY
         * 只有一个显示大小的Bitmap
         *
         * ## other
         * Glide.with(this).load(url).override(DisplayUtil.screenWidth, DisplayUtil.dip2px(50f)).into(glideView) 显示为override size,bitmap也是此尺寸,缩放遵从scaleType
         */
//        Glide.with(this).load(url).downsample(DownsampleStrategy.AT_MOST).into(glideView)
//        Glide.with(this).asBitmap().load(url).into(BitmapImageViewTarget(glideView))
        launch {
            var futureBitmap = withContext(Dispatchers.IO) {
                Glide.with(glideView)
                        .asBitmap()
                        .load(url)
                        .submit().get()
            }
            glideView.setImageBitmap(futureBitmap)
        }
    }


    private fun leak() {
        Utils.testLeak(this)
    }
}
