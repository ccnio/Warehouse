package com.ware.jetpack

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.ware.common.BaseViewModel
import com.ware.http.HttpHelper
import com.ware.http.data.FeedArticle
import com.ware.http.data.FriendSite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by jianfeng.li on 19-6-24.
 */
class MViewModel : BaseViewModel() {
    val mArticleLiveData = MutableLiveData<FeedArticle>()
    val mSiteLiveData = MutableLiveData<FriendSite>()
    val mCompositeLiveData = MediatorLiveData<CompositeData>()
    private val mComposeData = CompositeData()

    val myLiveData = MyLiveData()

    init {
        mCompositeLiveData.addSource(mArticleLiveData) {
            Log.d("MViewModel", "compose article: ")
            mComposeData.mFeedArticle = it
            mCompositeLiveData.value = mComposeData
        }

        mCompositeLiveData.addSource(mSiteLiveData) {
            Log.d("MViewModel", "compose site: ")
            mComposeData.mFriendSite = it
            mCompositeLiveData.value = mComposeData
        }
    }

    fun requestSite() {
        val friendCall = HttpHelper.getFriendSites()
        friendCall.enqueue(object : Callback<FriendSite> {
            override fun onFailure(call: Call<FriendSite>, t: Throwable) {
                Log.d("MViewModel", "FriendSites onFailure: 2222")
            }

            override fun onResponse(call: Call<FriendSite>, response: Response<FriendSite>) {
                Log.d("MViewModel", "FriendSites onResponse: 22222")
                mSiteLiveData.value = response.body()
            }
        })
    }

    fun requestFeed() {
        val call = HttpHelper.getFeedArticleList()
        call.enqueue(object : Callback<FeedArticle> {
            override fun onFailure(call: Call<FeedArticle>, t: Throwable) {
                Log.d("MViewModel", "FeedArticle onFailure: ")
            }

            override fun onResponse(call: Call<FeedArticle>, response: Response<FeedArticle>) {
                Log.d("MViewModel", "FeedArticle onResponse: ")
                mArticleLiveData.value = response.body()
            }
        })
    }

    private val list = arrayListOf<Activity>()
    fun testLeak(activity: Activity) {
        Log.d("MViewModel", "testLeak: $activity")
        list.add(activity)
    }

    fun testLeak2(activity: Activity) {
        Log.d("MViewModel", "testLeak2: $activity")
        Thread {
            while (true) {
                Log.d("MViewModel", "testLeak2: $activity")
            }
        }.start()
    }


    fun testLeak3() {
        Thread {
            while (true) {
                myLiveData.postValue(null)
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MViewModel", "onCleared: ")
    }


}