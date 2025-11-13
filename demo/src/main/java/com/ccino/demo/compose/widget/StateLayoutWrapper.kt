package com.ccino.demo.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ccino.demo.widget.PageStateLayout

enum class PageState {
    LOADING,
    CONTENT,
    EMPTY,
    ERROR
}

@Composable
fun StateLayoutWrapper(
    state: PageState,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
    onErrorClick: () -> Unit = {},
    onEmptyClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    AndroidView(
        factory = { context ->
            val pageStateLayout = PageStateLayout(context)
            val contentView = ComposeView(context).apply {
                setContent {
                    content()
                }
            }
            pageStateLayout.inject(contentView)
            pageStateLayout
        },
        update = { pageStateLayout ->
            when (state) {
                PageState.LOADING -> pageStateLayout.showLoading()
                PageState.CONTENT -> pageStateLayout.showContent()
                PageState.EMPTY -> pageStateLayout.showEmpty(action = onEmptyClick)
                PageState.ERROR -> pageStateLayout.showError(action = onErrorClick)
            }
        },
        modifier = modifier
    )
}


@Composable
fun StateLayoutCase(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth()
    ) {
        StateLayoutWrapper(state = PageState.LOADING, modifier = Modifier.weight(1f).fillMaxHeight().background(Color.DarkGray)) {}

        StateLayoutWrapper(state = PageState.CONTENT, modifier = Modifier.weight(1f).fillMaxHeight().background(Color.LightGray)) {
            Text("This is the content")
        }
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun StateLayoutWrapperLoadingPreview() {
    StateLayoutWrapper(state = PageState.LOADING) {}
}

@Preview(showBackground = true, name = "Content State")
@Composable
fun StateLayoutWrapperContentPreview() {
    StateLayoutWrapper(state = PageState.CONTENT) {
        Text("This is the content")
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun StateLayoutWrapperEmptyPreview() {
    StateLayoutWrapper(state = PageState.EMPTY, onEmptyClick = {}) {}
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun StateLayoutWrapperErrorPreview() {
    StateLayoutWrapper(state = PageState.ERROR, onErrorClick = {}) {}
}
