package com.ware.widget

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieFrameInfo
import com.airbnb.lottie.value.SimpleLottieValueCallback
import kotlinx.android.synthetic.main.widget_activity_lottie.*

private val COLORS = arrayOf(0xff5a5f, 0x008489, 0xa61d55)
private val EXTRA_JUMP = arrayOf(0f, 20f, 50f)

private const val TAG = "LottieActivity"

class LottieActivity : AppCompatActivity() {
    private var speed = 1
    private var colorIndex = 0
    private var extraJumpIndex = 0

    private val shirt = KeyPath("Shirt", "Group 5", "Fill 1")
    private val leftArm = KeyPath("LeftArmWave", "LeftArm", "Group 6", "Fill 1")
    private val rightArm = KeyPath("RightArm", "Group 6", "Fill 1")
    private val body = KeyPath("Body")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_lottie)

        colorButton.setOnClickListener {
            colorIndex = (colorIndex + 1) % COLORS.size
        }

        speedButton.setOnClickListener {
            speed = ++speed % 4
            updateButtonText()
        }

        jumpHeight.setOnClickListener {
            extraJumpIndex = (extraJumpIndex + 1) % EXTRA_JUMP.size
            updateButtonText()
        }

        /**
         *KeyPath用于定位将要更新的特定内容或一组内容,KeyPath由与原始动画中After Effects内容的层次结构相对应的字符串列表指定。
         * KeyPaths可以包含内容或通配符的特定名称：
         *  '*': 通配符匹配键路径中任何单个内容名称
         *  '**': 匹配零个或多个层次
         */
        animationView.addLottieOnCompositionLoadedListener {
            animationView.resolveKeyPath(KeyPath("**")).forEach {
                Log.d(TAG, "key path ${it.keysToString()}")
            }
        }

        setupValueCallbacks()
        updateButtonText()
    }

    /**
     * 运行中更新属性，你需要做三件事：
     * KeyPath/LottieProperty/LottieValueCallback
     */
    private fun setupValueCallbacks() {
        //property color
        animationView.addValueCallback(shirt, LottieProperty.COLOR, object : SimpleLottieValueCallback<Int> {
            override fun getValue(frameInfo: LottieFrameInfo<Int>?): Int {
                return COLORS[colorIndex]
            }
        })
        animationView.addValueCallback(leftArm, LottieProperty.COLOR) { COLORS[colorIndex] }
        animationView.addValueCallback(rightArm, LottieProperty.COLOR) { COLORS[colorIndex] }


        //property translate
        val point = PointF()
        animationView.addValueCallback(body, LottieProperty.TRANSFORM_POSITION, object : SimpleLottieValueCallback<PointF> {
            override fun getValue(frameInfo: LottieFrameInfo<PointF>): PointF {
                val startX = frameInfo.startValue.x
                var startY = frameInfo.startValue.y
                var endY = frameInfo.endValue.y

                if (startY > endY) { //向上
                    startY += EXTRA_JUMP[extraJumpIndex]
                } else if (endY > startY) {//向下
                    endY += EXTRA_JUMP[extraJumpIndex]
                }
                point.set(startX, lerp(startY, endY, frameInfo.interpolatedKeyframeProgress))
                return point
            }
        })

        //speed property
        animationView.addValueCallback(KeyPath("LeftArmWave"), LottieProperty.TIME_REMAP) { frameInfo ->
            2 * speed.toFloat() * frameInfo.overallProgress
        }
    }

    private fun lerp(a: Float, b: Float, @FloatRange(from = 0.0, to = 1.0) percentage: Float): Float {
        return a + percentage * (b - a)
    }

    private fun updateButtonText() {
        speedButton.text = "Wave: ${speed}x Speed"
        jumpHeight.text = "Extra jump height ${EXTRA_JUMP[extraJumpIndex]}"
    }
}
