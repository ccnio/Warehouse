package com.edreamoon.warehouse.kt

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
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
        mRecyclerView.adapter

    }

    companion object {
        const val TAG: String = "KtActivity"
    }
}
