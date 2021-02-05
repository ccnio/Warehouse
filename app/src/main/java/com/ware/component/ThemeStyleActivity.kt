package com.ware.component

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.ware.R
import com.ware.component.permissionutil.PermissionResult
import com.ware.component.permissionutil.PermissionUtils
import kotlinx.android.synthetic.main.activity_theme_style.*

/**
 * 字体国际化：不同国家用不同字体
 * 1. 通过style方式. 跟多语言同理，在不同地区的values文件夹下定义不同字体style,例如
 * <style name="LantingBold"> <item name="android:fontFamily">@font/ml_lanting_bold</item> </style>; 如果使用系统字体则指定null或不设置
 * 问题：
 * a.xml中每个控件只允许一个style，这样的话使用自定义字体控件的style必须继承所用字体
 * b.自定义View中获取Theme的字体:
 *  boolean resolveAttribute = getContext().getTheme().resolveAttribute(R.attr.myFont, typedValue, true);
 *  Log.d(TAG, "StyleView: resolveAttribute " + resolveAttribute);
 *   int resourceId = typedValue.resourceId;
 *  if (resourceId > 0) {
 *   Typeface font = ResourcesCompat.getFont(context, resourceId);
 *  paint.setTypeface(font);
 *  }
 *
 * c.自定义View的自定义属性:typedArrays.getResourceId(R.styleable.BarChartRecyclerView_xAxisLabelFont, -1);
 */
class ThemeStyleActivity : BaseActivity(R.layout.activity_theme_style) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Day) //must before setContentView
        super.onCreate(savedInstanceState)

//        container.addView((StyleView(this)))

        PermissionUtils(this).request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
                .observe(this, Observer {
                    when (it) {
                        PermissionResult.Grant -> {

                        }
                        is PermissionResult.Deny -> {

                        }
                        else -> {
                        }

                    }
                })
    }
}
