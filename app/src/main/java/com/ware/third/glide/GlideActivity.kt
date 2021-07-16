package com.ware.third.glide

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ware.R
import com.ware.face.DisplayUtil
import kotlinx.android.synthetic.main.activity_web_img.*

private const val TAG = "WebImgActivity"

class GlideActivity : AppCompatActivity() {
    private val size = DisplayUtil.dip2px(100f)
    private val radius = DisplayUtil.dip2px(10f)
    private val gifUrl = "http://cdn.cnbj1.fds.api.mi-img.com/plato-article/developer_487d13ca287187a9ca79a4139ad8ecd0.gif?GalaxyAccessKeyId=AKVGLQWBOVIRQ3XLEW&Expires=9223372036854775807&Signature=XYFyqUY7sInYPBcUVsrQm390Vms="
    private val commonUrl = "http://cdn.cnbj1.fds.api.mi-img.com/plato-article/developer_bcdcd393b07c01cf8da1ad8bf0f7d8f1.jpg?GalaxyAccessKeyId=AKVGLQWBOVIRQ3XLEW&Expires=9223372036854775807&Signature=AE+31b0lqL4Aor1XPmiPvdFttgg="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_img)

        //glide: gif available
        Glide.with(this).load(gifUrl)
            .override(600, 600)//not work for gif
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(glideGifView)

        //glide common img
        Glide.with(this).load(commonUrl)
            .error(0)
            .override(size, size)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Log.d(TAG, "onLoadFailed: ${Thread.currentThread().name}")
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    Log.d(TAG, "onResourceReady: ${Thread.currentThread().name}")
                    return false
                }

            })
            .apply(RequestOptions.bitmapTransform(RoundedCorners(radius)))
            .into(imgView)

//        Glide.with(this).asGif().load(gifUrl).override(20,20).transform(RoundedTransform)
        Glide.with(this).load(R.drawable.dynamic).into(cropGifView)
    }
}