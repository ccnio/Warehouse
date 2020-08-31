package com.ware.widget.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintProperties
import androidx.constraintlayout.widget.ConstraintSet
import com.ware.R

/**
 * **********************Flow*************************
 * Flow布局通过constraint_referenced_ids属性将要约束的View的id进行关联
 *
 * flow_horizontalGap/flow_verticalGap: 水平/垂直之间间隔
 *
 * flow_wrapMode属性一共有三种值
 * 「none值」：所有引用的View形成一条链，水平居中，超出屏幕两侧的View不可见。
 * 「chian值」：所引用的View形成一条链，超出部分会自动换行。
 * 「aligned值」：所引用的View形成一条链，但View会在同行同列
 *
 * 链约束当flow_wrapMode属性为aligned和chian属性时，给Flow布局添加以下属性进行不同chain约束:
 * flow_firstHorizontalStyle  约束第一条水平链，当有多条链（多行）时，只约束第一条链（第一行），其他链（其他行）不约束；
 * flow_lastHorizontalStyle 约束最后一条水平链，当有多条链（多行）时，只约束最后一条链（最后一行），其他链（其他行）不约束；
 * flow_horizontalStyle  约束所有水平链；
 * flow_firstVerticalStyle 同水平约束；
 * flow_lastVerticalStyle 同水平约束；
 * flow_verticalStyle 约束所有垂直链；
 * 以上属性，取值有：spread、spread_inside、packed
 *
 * 给Flow布局添加以下属性进行不同Align约束:
 * flow_verticalAlign 垂直方向对齐,取值有：top、bottom、center、baseline;
 * flow_horizontalAlign 水平方向对齐,取值有：start、end、center;
 *
 * 可以通过orientation属性来设置水平horizontal和垂直vertical方向，例如改为垂直方向。
 * 通过flow_maxElementsWrap属性控制每行最大的子View数量。例如：flow_maxElementsWrap=3
 * 当flow_wrapMode属性为none时，A和G被挡住了，看不到。要A或者G可见，通过设置flow_horizontalBias属性，取值在0-1之间。前提条件是flow_horizontalStyle属性为packed才会生效。
 *
 * **********************Layer********************
 * Layer也是一个约束助手ConstraintHelper常用来增加背景，或者共同动画,类似group,但group只能控制可见性，别的啥也干不了。
 * Layer可以看做是控制它所关联的View从而能形成一个伪边界。看起来就像寻常的ViewGroup包裹这些View，给ViewGroup设置背景。但是Layer并不是一个ViewGroup，且与它所关联的View处于同一个层级，
 * 因此能很好的减少一层布局。所以，在不需要对背景做整体View动画如旋转、透明度、位移的情况下，Layer和Group结合可以很好的解决背景和整体可见性的问题，这种场景下，能很大程度的减少View布局嵌套。
 * Layer还支持对关联View的位移、旋转、缩放等操作，但是使用的时候得小心，这些操作的中心点都是基于Layer的中心点
 *
 ***********************自定义ConstraintHelper*******
 * Flow和Layer都是ConstraintHelper的子类，当两者不满足需求时，可以通过继承ConstraintHelper来实现想要的约束效果。
 *
 **********************ImageFilterButton/ImageFilterView************************
 *圆角实现 <app:round="10dp" />或者app:roundPercent="1"
 *altSrc和src属性是一样的概念，altSrc提供的资源将会和src提供的资源通过crossfade属性形成交叉淡化效果。默认情况下,crossfade=0，altSrc所引用的资源不可见,取值在0-1。
 *接下来几个属性是对图片进行调节：
 * warmth色温：1=neutral自然, 2=warm暖色, 0.5=cold冷色
 * brightness亮度：0 = black暗色, 1 = original原始, 2 = twice as bright两倍亮度；这个效果不好贴图，大家自行验证；
 * saturation饱和度：0 = grayscale灰色, 1 = original原始, 2 = hyper saturated超饱和；
 * contrast对比：1 = unchanged原始, 0 = gray暗淡, 2 = high contrast高对比;
 *
 * *************************边距问题的补充**********
 * 有ConstraintLayout实践经验的朋友应该知道margin设置负值在ConstraintLayout是没有效果的。可以通过轻量级的Space来间接实现这种效果。
 *
 * 2.0还增加了ConstraintProperties类用于通过api(代码)更新ConstraintLayout子视图；
 *
 */
class ConstrainLayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_constrain_layout)


        val properties = ConstraintProperties(findViewById(R.id.image))
        properties.translationZ(32f)
                .margin(ConstraintSet.START, 43)
                .apply()

    }
}
