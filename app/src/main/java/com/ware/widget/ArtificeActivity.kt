package com.ware.widget

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_artificy.*

/**
# MaterialButton (局限:selector还是得xml)
## theme
使用MaterialButton可能会出现闪退的问题，原因就是使用了MD控件，但是未将them设置为MaterialComponents。
方法一：AndroidManifest里application节点下配置，作用域为整个应用
方法二：只在当前activity配置，作用域为当前activity
方法三：为每个在使用到MD控件的地方配置，作用域只针对当前控件:android:theme="@style/Theme.MaterialComponents.Light.NoActionBar"
## background
如果未设置自定义背景，则 MaterialShapeDrawable 仍将用作默认背景。如果按钮背景是纯色，可以通过app:backgroundTint指定；如果按钮背景是渐变色，则需要自己定义drawable，然后通过android:background设置。
注意：如果要使用android:background设置背景，则需要将backgroundTint设置为@empty，否则background不会生效。
## insetTop、insetBottom
MaterialButton默认在style指定了insetTop和insetBottom为6dp，使得height看起来并没有Button实际设置值一样高，可以在xml将MaterialButton的insetTop和insetBottom都设置为0dp，这样MaterialButton的高度就和实际设置的高度一致了。
## 关于阴影
MD组件默认都是自带阴影的，MaterialButton也不例外。但是有时候我们并不想要按钮有阴影，那么这时候可以指定style为style="@style/Widget.MaterialComponents.Button.UnelevatedButton"，这样就能去掉阴影，让视图看起来扁平化。

# ShapeableImageView继承自ImageView，可以为image添加描边大小、颜色，以及圆角、裁切等
imageView?.shapeAppearanceModel = ShapeAppearanceModel.builder()
.setAllCorners(CornerFamily.ROUNDED,20f)
.setTopLeftCorner(CornerFamily.CUT,RelativeCornerSize(0.3f))
.setTopRightCorner(CornerFamily.CUT,RelativeCornerSize(0.3f))
.setBottomRightCorner(CornerFamily.CUT,RelativeCornerSize(0.3f))
.setBottomLeftCorner(CornerFamily.CUT,RelativeCornerSize(0.3f))
.setAllCornerSizes(ShapeAppearanceModel.PILL)
.setTopLeftCornerSize(20f)
.setTopRightCornerSize(RelativeCornerSize(0.5f))
.setBottomLeftCornerSize(10f)
.setBottomRightCornerSize(AbsoluteCornerSize(30f))
.build()

这里还有个CornerFamily，它表示处理的方式，有ROUNDED和CUT两种，ROUNDED是圆角，CUT是直接将圆角部分裁切掉。setAllCornerSizes(ShapeAppearanceModel.PILL)可以直接实现圆形效果。

## 关于Stroke
ShapeableImageView指定strokeWidth描边的时候，其描边会被覆盖掉一半，如strokeWidth=4dp，上下左右会被覆盖，实际的效果是只有2dp被显示。如图，


 */
private const val TAG = "ArtificeActivity"

class ArtificeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artificy)
        materialButton.setOnClickListener { Log.d(TAG, "onCreate: ") }
    }
}