package com.ware.component.inflater

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R

private const val TAG = "InflaterActivity"

/**
 * # LayoutInflater.from(this/app) 得到的实例不一样. xml layout解析用的inflaterActivity
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
        factory()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inflater)
        instance()
    }

    private fun factory() {
        val inflater = LayoutInflater.from(this)
        val factoryActivity = inflater.factory2 //AppCompatDelegateImpl@8971
        val factoryApp = LayoutInflater.from(applicationContext).factory2 //null
        //factoryActivity==factoryApp:false
        Log.d(TAG, "onCreate: factoryActivity==factoryApp:${factoryActivity == factoryApp}")

        inflater.factory2 = object : LayoutInflater.Factory2 {
            override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
                //onCreateView: 1111 name = androidx.constraintlayout.widget.ConstraintLayout
                //onCreateView: 1111 name = TextView
                Log.d(TAG, "onCreateView: 1111 name = $name")
                //val n = attrs.attributeCount //view的属性
                //for (i in 0 until n) Log.e(TAG, attrs.getAttributeName(i).toString() + " , " + attrs.getAttributeValue(i))


//                //appcompat 创建view代码
//                val delegate: AppCompatDelegate = delegate
//                val view: View = delegate.createView(parent, name, context, attrs)
//                return view

                return null
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