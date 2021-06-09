package com.ware.media

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.widget.views.CircleProgressBar
import kotlinx.android.synthetic.main.activity_course.*

class CourseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_course)

//        CircleProgressBar(this).bgColor
        conteView.setOnClickListener {
            startActivity(Intent(this, ExoPlayerActivity::class.java))
        }
    }
}