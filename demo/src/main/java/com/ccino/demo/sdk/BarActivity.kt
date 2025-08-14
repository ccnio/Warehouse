package com.ccino.demo.sdk

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ccino.demo.R
import com.ccino.demo.databinding.ActivityBarBinding
import com.ccino.demo.util.debounceClick

private const val TAG = "BarActivity"

class BarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarBinding
    private var isLight = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 启用全屏模式，允许内容延伸到状态栏和导航栏
        enableEdgeToEdge()
        setNavigation()

        binding = ActivityBarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSafeArea()
        binding.switchButton.debounceClick { switchMode() }
        binding.dialogueBtn.debounceClick {
            EdgeDialog().show(supportFragmentManager, "EdgeDialog")
//            SimpleTestDialog().show(supportFragmentManager, "SimpleTestDialog")

        }
        binding.keyboardBtn.debounceClick {  EdgeKeyboardDialog().show(supportFragmentManager, "keyboard") }
        switchMode()
    }

    private fun setSafeArea() {
        // 确保内容显示在安全区域
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // 获取状态栏、导航栏和系统栏的 Insets
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars()) //Insets{left=0, top=81, right=0, bottom=0}
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars()) //Insets{left=0, top=0, right=0, bottom=130}
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) //Insets{left=0, top=81, right=0, bottom=130}
            Log.d(TAG, "setSafeArea: $systemBars, $statusBars, $navigationBars")
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setNavigation() {
        // 设置导航栏透明: 在一些手机上设置 xml 可能不生效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    private fun switchMode() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (isLight) {
            window.decorView.setBackgroundColor(Color.WHITE)
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
            binding.switchButton.text = "当前浅色"
        } else {
            window.decorView.setBackgroundResource(R.color.color2)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
            binding.switchButton.text = "当前深色"
        }
        isLight = !isLight
    }
}