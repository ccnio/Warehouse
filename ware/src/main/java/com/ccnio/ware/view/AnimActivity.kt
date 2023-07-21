package com.ccnio.ware.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityAnimBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding

/**
 * # 场景切换动画 Transition和TransitionManager
 * 您可以通过自定义 Transition 来实现自定义的场景切换动画。
 * Transition 是 Android 中的一个动画类，用于在场景切换时添加动画效果，比如 Activity 转场、Fragment 切换等。
 *
 * Scene 场景 场景过渡动画就是实现View从一种状态变化到另外一种状态，
 * Scene就代表一个场景，它内部保存一个完整地视图结构，从根ViewGroup到所有子view，还有它们的所有状态信息。所以Scene最终就一个设置了不同属性特征的ViewGroup。
 */
class AnimActivity : AppCompatActivity(R.layout.activity_anim) {
    private val binding by viewBinding(ActivityAnimBinding::bind)
    private var toggle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.actionView.setOnClickListener {
//            viewTransition()
            sceneTransition()
        }
        binding.rootView.setOnClickListener { sceneTransition() }
    }

    private fun sceneTransition() {
        /*
        //使用布局作为scene
        var scene1 = Scene.getSceneForLayout(binding.container, R.layout.scene_layout_1, this)
        var scene2 = Scene.getSceneForLayout(binding.container, R.layout.scene_layout_2, this)*/

        //使用 sceneRoot中的view
        val scene1 = Scene(binding.rootView, binding.imgView)
        val scene2 = Scene(binding.rootView, binding.imgView2)
        TransitionManager.go(if (!toggle) scene1 else scene2)
        toggle = !toggle
    }

    private fun viewTransition() {
        /**
         * 执行TransitionManager.beginDelayedTransition后，系统会保存一个当前视图树状态的场景，
         * 修改view的属性信息，在下一次绘制时，系统会自动对比之前保存的视图树，然后执行一步动画。重要提醒：
         * 如果想让beginDelayedTransition有效果，必须每次改变视图属性之后，重新调用beginDelayedTransition，
         * 或者改变之前调用beginDelayedTransition，这样才能够保存当前view的状态，否则存储的属性没有改变，不会有动画效果。
         *
         * 可以利用Transition的addTarget（），removeTarget（），只对某些view做动画，或者不对某些view做动画。
         */

        /* val transition = ChangeBounds()
         transition.setDuration(1000)
         TransitionManager.beginDelayedTransition(binding.rootView, transition);
         val layoutParams1 = binding.imgView.layoutParams
         layoutParams1.height = if (toggle) 100 else 200
         layoutParams1.width = if (toggle) 100 else 200
         binding.imgView.layoutParams = layoutParams1*/


        /* val transition: Transition = ChangeImageTransform()
         transition.setDuration(1000)
         transition.addTarget(binding.imgView) //必须有 add， 否则 remove 不生效
         transition.removeTarget(binding.imgView2)
         TransitionManager.beginDelayedTransition(binding.rootView, transition)
         binding.imgView.scaleType =
             if (toggle) ImageView.ScaleType.FIT_CENTER else ImageView.ScaleType.FIT_XY
         binding.imgView2.scaleType =
             if (toggle) ImageView.ScaleType.FIT_CENTER else ImageView.ScaleType.FIT_XY*/

        TransitionManager.beginDelayedTransition(binding.rootView)
        /*  binding.imgView.isVisible = toggle
          binding.imgView2.isVisible = !toggle*/
        val layoutParams1 = binding.imgView.layoutParams
        layoutParams1.height = if (toggle) 100 else 200
        layoutParams1.width = if (toggle) 100 else 200
        binding.imgView.layoutParams = layoutParams1

        toggle = !toggle
    }
}
