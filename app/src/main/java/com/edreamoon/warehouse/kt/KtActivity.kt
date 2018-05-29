package com.edreamoon.warehouse.kt

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.edreamoon.warehouse.R
import kotlinx.android.synthetic.main.activity_kt.*

class KtActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kt)

        //Activity findView
        mLabelView.text = "测试 findView !!"

        //Fragment findView
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mFragment, ContentFragment())
        transaction.commit()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.adapter = KtAdapter(this)

        val nullSafe = NullSafe.getNullSafe()
        //b?.length
        //表示的意义：若b为非空变量，就会返回b.length，否则就返回null.该式的完整表达式：
        //栗子：
        //如果Bob，一个雇员，可被分配给一个部门（或不），这反过来又可以获得 Bob 的部门负责人的名字（如果有的话），我们这么写：
        //bob?.department?.head?.name
        //如果任意一个属性（环节）为空，这个链式调用就会返回 null{: .keyword }。
        //if(nullSafe == null || nullSafe.value == null)
        if (nullSafe?.value == null) {
            Log.d(TAG, "null: ")
        }


        //如果该变量为非空时，我们使用它 （操作该变量的属性或者方法）；否则使用一个非空的值：
        var b: String? = null
        // val l: Int = if (b != null) b.length else -1
        //我们可以换成Elvis操作符进行表示：
        val l: Int = b?.length ?: -1


        //b!!, 这样就会返回一个不可以为空的b的值，如果b为空，这是就会抛出NPE异常。
        var a = "abc"
        val len: Int = a!!.length


        //安全转型 转型的时候，可能会经常出现 ClassCastException 。 可以使用安全转型，当转型不成功的时候，它会返回null
        val aInt: Int? = a as? Int

        var sa: String? = null
        var sb: String? = "abc"

        if (sa == sb) {
            Log.d(TAG, "null equals: ")
        } else {
            Log.d(TAG, "null not equals: ")
        }

    }

    companion object {
        const val TAG: String = "KtActivity"
    }
}
