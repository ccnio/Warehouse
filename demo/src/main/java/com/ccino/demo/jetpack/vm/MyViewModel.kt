package com.ccino.demo.jetpack.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

private const val TAG = "MyViewModel"

sealed interface PostAction {
    data class Clicked(val id: String) : PostAction
    data class LikeClicked(val id: String) : PostAction
    data class ShareClicked(val id: String) : PostAction
}

sealed interface NewsAction {
    data class Clicked(val news: Any) : NewsAction
    data class LikeClicked(val news: Any) : NewsAction
    data class BookmarkClicked(val news: Any) : NewsAction
}

class MyViewModel(private val saveStateHandle: SavedStateHandle) : BaseViewModel(), PostActionHandler, NewsActionHandler {
    override val viewModel = this

    // repo 或者放在 BaseViewModel 中
    override val postRepo = PostRepository()
    override val newsRepo = NewsRepository()

    // ui 界面调用 handleAction 触发各个事件
    override suspend fun handleLikeClick(id: String) {
        super.handleLikeClick(id)
        viewModelScope.launch {
            postRepo.likeAdd(id)
            newsRepo.likeAdd(id)
            showSnackbar("Post Liked")
        }
    }

    val info = saveStateHandle.get<String>("KEY_INFO") // 取值
    val infoFlow = saveStateHandle.getStateFlow("KEY_INFO", "") // 以 flow 形式

    fun setInfo(info: String) { // 设置值
        saveStateHandle.set("KEY_INFO", info)
    }
}