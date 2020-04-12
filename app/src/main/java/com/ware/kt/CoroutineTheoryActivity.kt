package com.ware.kt

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_coroutine_theory.*
import kotlinx.coroutines.*

private const val TAG = "CoroutineTheoryActivity"

class CoroutineTheoryActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope by MainScope() {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.taskView -> getArticle(Article("1100"))
            R.id.generatorView -> main()
        }
    }

    private fun getArticle(article: Article) {
        launch {
            Log.d(TAG, "getArticle before: ${Thread.currentThread().name}")
//            val token = requestToken(User("lisi"))
            val detailInfo = parseArticle("ab", article)
            Log.d(TAG, "getArticle after: ${Thread.currentThread().name}--- ${detailInfo.token}")
        }
    }
//
//    private suspend fun requestToken(user: User) = withContext(Dispatchers.IO) {
//        Log.d(TAG, "requestToken: ${Thread.currentThread().name}")
//        delay(2000)
//        "abcdefg" + user.name
//    }

    private suspend fun parseArticle(token: String, article: Article) = withContext(Dispatchers.IO) {
        Log.d(TAG, "parseArticle: ${Thread.currentThread().name}")
//        delay(1000)
        DetailInfo(article, token + "desc")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_theory)
        taskView.setOnClickListener(this)
        generatorView.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    data class Article(val id: String)

    //    data class User(val name: String)
    data class DetailInfo(val article: Article, val token: String)
}
