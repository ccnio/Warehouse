package com.ware.component

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_web.*

private const val TAG = "WebActivity"

class WebActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        unsafeView.setOnClickListener(this)
        /**
         * 必须打开调试才能在Chrome中调试
         */
        WebView.setWebContentsDebuggingEnabled(true)

        /**
         * 从Android5.0开始，WebView默认不支持同时加载Https和Http混合模式。解决办法:
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
        }

        webView.webViewClient = object : WebViewClient() {

        }
        webView.loadUrl("http://ci-miot.voice.xiaomi.srv:8080/job/VoiceSwitch/")

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            /**
             * app不设置 networkSecurityConfig 就无法加载http请求
             */
            R.id.unsafeView -> webView.loadUrl("http://tech.sina.com.cn/i/2018-02-09/doc-ifyrmfmc0425032.shtml")
        }
    }
}