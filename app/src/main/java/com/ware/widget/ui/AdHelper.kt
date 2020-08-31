package com.ware.widget.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewAnimationUtils
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Created by jianfeng.li on 20-8-31.
 * 那么如何使用Helper来帮助我们管理View呢？答案就是在Helper提供的几个updateXX，updateXX与Helper的绘制生命周期所关联，因此自定义的Helper想要达到效果，必须触发绘制过程或者绘制过程的对应阶段。
 */
class AdHelper : ConstraintHelper {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context?, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)


    override fun updatePostLayout(container: ConstraintLayout?) {
        super.updatePostLayout(container)
        val views = getViews(container)
        views.forEach {
            val anim = ViewAnimationUtils.createCircularReveal(it, 0, 0, 0f, it.width.toFloat())
            anim.duration = 5000
            anim.start()
        }
    }
}