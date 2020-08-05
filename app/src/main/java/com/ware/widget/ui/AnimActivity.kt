package com.ware.widget.ui

import android.os.Bundle
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.anim_activity.*

class AnimActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anim_activity)
        animSet()
        txtView.postDelayed({
            txtView.animation = AnimationUtils.loadAnimation(this, R.anim.share_txt_rotate)
        }, 1000)
    }

    private fun animSet() {

        /**
         * AnimationSet 无法定义循环次数，只能在每个item里设置
         */
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.repeatMode = RotateAnimation.RESTART
        rotate.repeatCount = RotateAnimation.INFINITE
        rotate.duration = 2000

        val alpha = AlphaAnimation(0.2f, 1.0f)
        alpha.duration = 2000
        alpha.repeatMode = RotateAnimation.RESTART
        rotate.repeatCount = RotateAnimation.INFINITE

        val set = AnimationSet(true)
        set.addAnimation(rotate)
        set.addAnimation(alpha)

//        set.setRepeatMode(AnimationSet.RESTART);
//        set.setRepeatCount(10);
//        set.setDuration(2000);

        rotate_view.postDelayed({ rotate_view.startAnimation(set) }, 1000)

    }

}
