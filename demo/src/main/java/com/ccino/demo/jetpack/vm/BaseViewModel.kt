package com.ccino.demo.jetpack.vm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

interface NewsActionHandler {

    val viewModel: BaseViewModel
    val newsRepo: NewsRepository

    fun handleAction(action: NewsAction) = when (action) {
        is NewsAction.Clicked -> handleNewsClick("action")
        is NewsAction.BookmarkClicked -> handleNewsBookmark("action.id")
        is NewsAction.LikeClicked -> Log.d("TAG", "handleAction: ")
    }

    fun handleNewsClick(id: String) {
        viewModel.navigate()
    }

    fun handleNewsBookmark(id: String) {
//        newsRepo.bookmark(id)
//        viewModel.showSnackBar("News Bookmarked")
    }
}

interface PostActionHandler {

    val viewModel: BaseViewModel
    val postRepo: PostRepository

    fun handleAction(action: PostAction) = viewModel.viewModelScope.launch {
        when (action) {
            is PostAction.Clicked -> handlePostClick(action.id)
            is PostAction.LikeClicked -> handleLikeClick(action.id)
            else -> {}
        }
    }

    suspend fun handlePostClick(id: String) {
        viewModel.navigate()
    }

    suspend fun handleLikeClick(id: String) {
//        postRepo.like(id)
        viewModel.showSnackbar("Post Liked")
    }

}

abstract class BaseViewModel : ViewModel() {

    var showShackBar by mutableStateOf("")
    var showBottomSheet by mutableStateOf("")

    fun navigate() {
        //Implementation
    }

    fun showSnackbar(message: String) {
        showShackBar = message
    }
}