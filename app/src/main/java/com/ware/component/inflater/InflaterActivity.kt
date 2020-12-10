package com.ware.component.inflater

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.LayoutInflaterCompat
import com.ware.R

private const val TAG = "InflaterActivity"

/**
 * LayoutInflater.md
 * # Factory
作用: ** factory可用于拦截view创建,onCreateView返回非空时将会替换要创建的view **.
```

```
LayoutInflater 提供两个设置 Factory 的方法.
- setFactory(Factory factory)
- setFactory2(Factory2 factory): SDK>=11以后引入的
```
public interface Factory {
    View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs);
}

public interface Factory2 extends Factory {
    View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs);
}
```



setFactory2:
 *
 * .from(this/app) 得到的实例不一样. xml layout解析用的inflaterActivity
 *
 * # factory可用于拦截view创建。
 *   1.onCreateView返回非空时将会替换要创建的view
 *   2.添加自己的factory时必须在oncreate()之前，否则“A factory has already been set on this LayoutInflater”
 *   3.activity里已经有一个factory拦截view创建并替换，用于对一些控件（AppCompatTextView,AppCompatImageView等）做兼容
 *   4.添加自定义的factory时会替换掉系统的factory，如何不影响系统的兼容？ 系统的factory最终是调用：AppCompatDelegateImpl->createView()完成appcompat中view的创建，我们可以在自定义factory中调用这个方法即可:
 *    //appcompat 创建view代码
 *    AppCompatDelegate delegate = getDelegate();
 *    View view = delegate.createView(parent, name, context, attrs);
 *    return view;
 * # inflaterApp.inflate(res,root,attachToRoot) 参数
 *   root为空时，res里的布局属性跟root没关系，可能会失效
 *   root不空，res测量时受root约束
 *   attach true时如果root不空，将直接添加到root下，不用手动addView
 *
 *
 *
 */


class InflaterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        factory() // must before oncreate
        super.onCreate(savedInstanceState)
//        forceSetFactory2(layoutInflater) //must after oncreate
        setContentView(R.layout.activity_inflater)
        instance()
    }

    private fun forceSetFactory2(inflater: LayoutInflater) {
        val compatClass = LayoutInflaterCompat::class.java
        val inflaterClass = LayoutInflater::class.java
        try {
            val sCheckedField = compatClass.getDeclaredField("sCheckedField")
            sCheckedField.isAccessible = true
            sCheckedField.setBoolean(inflater, false)
            val mFactory = inflaterClass.getDeclaredField("mFactory")
            mFactory.isAccessible = true
            val mFactory2 = inflaterClass.getDeclaredField("mFactory2")
            mFactory2.isAccessible = true
            val factory = BackgroundFactory()
            if (inflater.factory2 != null) {
                factory.setInterceptFactory2(inflater.factory2)
            } else if (inflater.factory != null) {
                factory.setInterceptFactory(inflater.factory)
            }
            mFactory2[inflater] = factory
            mFactory[inflater] = factory
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }

    private fun factory() {
        val inflater = LayoutInflater.from(this)
        val factoryActivity = inflater.factory2 //AppCompatDelegateImpl@8971
        val factoryApp = LayoutInflater.from(applicationContext).factory2 //null
        //factoryActivity==factoryApp:false
        Log.d(TAG, "onCreate: factoryActivity==factoryApp:${factoryActivity == factoryApp}")

//        inflater.factory2 = LayoutFactory(this)

        inflater.factory2 = object : LayoutInflater.Factory2 {
            override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
                /**
                 **** onCreateView with parent: name = com.ware.component.inflater.CustomTextView ****
                attrName: layout_width; attrValue = -2
                attrName: layout_height; attrValue = -2
                attrName: text; attrValue = 自定义 TextView
                 **** onCreateView with parent: name = TextView ****
                attrName: layout_width; attrValue = -2
                attrName: layout_height; attrValue = -2
                attrName: text; attrValue = Hi
                 */
                Log.d(TAG, "**** onCreateView with parent: name = $name ****")
                val attrsCount = attrs.attributeCount
                for (i in 0 until attrsCount) {
                    Log.d(TAG, "attrName: ${attrs.getAttributeName(i)}; attrValue = ${attrs.getAttributeValue(i)}")
                }
                //appcompat 创建view代码
                val delegate: AppCompatDelegate = delegate
                val view = delegate.createView(parent, name, context, attrs)
                Log.d(TAG, "onCreateView: ret view = $view")
                return view
            }

            override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
                Log.d(TAG, "onCreateView: 2222")
                return null
            }
        }
    }

    private fun instance() {
        val inflaterActivity = LayoutInflater.from(this)
        val inflaterApp = LayoutInflater.from(applicationContext)
        val inflaterGet = layoutInflater

        //ac==app:false; ac==get:true; get==app:false
        Log.d(TAG, "onCreate: ac==app:${inflaterActivity == inflaterApp}; ac==get:${inflaterActivity == inflaterGet}; get==app:${inflaterGet == inflaterApp}")
    }
}