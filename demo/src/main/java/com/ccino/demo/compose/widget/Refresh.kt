package com.ccino.demo.compose.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val TAG = "Refresh"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PullToRefreshSample() {
    var itemCount by remember { mutableIntStateOf(15) }
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    val onRefresh = remember {
        {
            isRefreshing = true
            coroutineScope.launch {
                delay(5000)
                itemCount += 5
                isRefreshing = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Title") },
                // Provide an accessible alternative to trigger refresh.
                actions = {
                    IconButton(onClick = { onRefresh() }) {
//                        Icon(Icons.Filled.Refresh, "Trigger Refresh")
                    }
                },
            )
        }
    ) {
        PullToRefreshBox(
            modifier = Modifier.padding(it),
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = { onRefresh() },
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(itemCount) { ListItem({ Text(text = "Item ${itemCount - it}") }) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PullToRefreshViewModelSample() {
    val viewModel = remember {
        object : ViewModel() {
            private val refreshRequests = Channel<Unit>(1)
            var isRefreshing by mutableStateOf(false)
                private set

            var itemCount by mutableIntStateOf(15)
                private set

            init {
                viewModelScope.launch {
                    for (r in refreshRequests) {
                        isRefreshing = true
                        try {
                            itemCount += 5
                            delay(5000) // simulate doing real work
                        } finally {
                            isRefreshing = false
                        }
                    }
                }
            }

            fun refresh() {
                refreshRequests.trySend(Unit)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Title") },
                // Provide an accessible alternative to trigger refresh.
                actions = {
                    IconButton(
                        enabled = !viewModel.isRefreshing,
                        onClick = { viewModel.refresh() },
                    ) {
//                        Icon(Icons.Filled.Refresh, "Trigger Refresh")
                    }
                },
            )
        }
    ) {
        PullToRefreshBox(
            modifier = Modifier.padding(it),
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refresh() },
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                if (!viewModel.isRefreshing) {
                    items(viewModel.itemCount) {
                        ListItem({ Text(text = "Item ${viewModel.itemCount - it}") })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PullToRefreshScalingSample() {
    var itemCount by remember { mutableIntStateOf(15) }
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    val onRefresh = remember {
        {
            isRefreshing = true
            coroutineScope.launch {
                // fetch something
                delay(5000)
                itemCount += 5
                isRefreshing = false
            }
        }
    }

    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(state.distanceFraction).coerceIn(0f, 1f)
    }

    Scaffold(
        modifier =
            Modifier.pullToRefresh(
                state = state,
                isRefreshing = isRefreshing,
                onRefresh = { onRefresh() },
            ),
        topBar = {
            TopAppBar(
                title = { Text("TopAppBar") },
                // Provide an accessible alternative to trigger refresh.
                actions = {
                    IconButton(onClick = { onRefresh() }) {
//                        Icon(Icons.Filled.Refresh, "Trigger Refresh")
                    }
                },
            )
        },
    ) {
        Box(Modifier.padding(it)) {
            LazyColumn(Modifier.fillMaxSize()) {
                if (!isRefreshing) {
                    items(itemCount) { ListItem({ Text(text = "Item ${itemCount - it}") }) }
                }
            }
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        scaleX = scaleFraction()
                        scaleY = scaleFraction()
                    }
            ) {
                PullToRefreshDefaults.Indicator(state = state, isRefreshing = isRefreshing)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PullToRefreshLinearProgressIndicatorSample() {
    var itemCount by remember { mutableIntStateOf(15) }
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    val onRefresh = remember {
        {
            isRefreshing = true
            coroutineScope.launch {
                // fetch something
                delay(5000)
                itemCount += 5
                isRefreshing = false
            }
        }
    }

    Scaffold(
        modifier =
            Modifier.pullToRefresh(
                state = state,
                isRefreshing = isRefreshing,
                onRefresh = { onRefresh() },
            ),
        topBar = {
            TopAppBar(
                title = { Text("TopAppBar") },
                // Provide an accessible alternative to trigger refresh.
                actions = {
                    IconButton(onClick = { onRefresh() }) {
//                        Icon(Icons.Filled.Refresh, "Trigger Refresh")
                    }
                },
            )
        },
    ) {
        Box(Modifier.padding(it)) {
            LazyColumn(Modifier.fillMaxSize()) {
                if (!isRefreshing) {
                    items(itemCount) { ListItem({ Text(text = "Item ${itemCount - it}") }) }
                }
            }
            if (isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { state.distanceFraction },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PullToRefreshSampleCustomState() {
    var itemCount by remember { mutableIntStateOf(15) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val onRefresh = remember {
        {
            isRefreshing = true
            coroutineScope.launch {
                // fetch something
                delay(5000)
                itemCount += 5
                isRefreshing = false
            }
        }
    }

    val state = remember {
        object : PullToRefreshState {
            private val anim = Animatable(0f, Float.VectorConverter)

            override val distanceFraction
                get() = anim.value

            override val isAnimating: Boolean
                get() = anim.isRunning

            override suspend fun animateToThreshold() {
                anim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
            }

            override suspend fun animateToHidden() {
                anim.animateTo(0f)
            }

            override suspend fun snapTo(targetValue: Float) {
                anim.snapTo(targetValue)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TopAppBar") },
                // Provide an accessible alternative to trigger refresh.
                actions = {
                    IconButton(onClick = { onRefresh() }) {
//                        Icon(Icons.Filled.Refresh, "Trigger Refresh")
                    }
                },
            )
        }
    ) {
        PullToRefreshBox(
            modifier = Modifier.padding(it),
            isRefreshing = isRefreshing,
            onRefresh = { onRefresh() },
            state = state,
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                if (!isRefreshing) {
                    items(itemCount) { ListItem({ Text(text = "Item ${itemCount - it}") }) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PullToRefreshTextSample() {
    var itemCount by remember { mutableIntStateOf(15) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val onRefresh = remember {
        {
            isRefreshing = true
            coroutineScope.launch {
                delay(2000)
                itemCount += 5
                isRefreshing = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Text Refresh Header") }
            )
        }
    ) {
        PullToRefreshLayout(
            modifier = Modifier.padding(it),
            isRefreshing = isRefreshing,
            onRefresh = { onRefresh() },
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(itemCount) { ListItem({ Text(text = "Item ${itemCount - it}") }) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PullToRefreshAndLoadMoreSample() {
    var itemCount by remember { mutableIntStateOf(20) }
    var isRefreshing by remember { mutableStateOf(false) }
    var loadMoreStatus by remember { mutableStateOf(LoadMoreStatus.Default) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Refresh logic
    val onRefresh = remember {
        {
            isRefreshing = true
            loadMoreStatus = LoadMoreStatus.Default // Reset load more status
            coroutineScope.launch {
                delay(2000)
                itemCount = 20
                isRefreshing = false
            }
        }
    }

    // Load More logic
    val onLoadMore = remember {
        {
            if (loadMoreStatus == LoadMoreStatus.Default && !isRefreshing) {
                loadMoreStatus = LoadMoreStatus.Loading
                coroutineScope.launch {
                    delay(2000)
                    if (itemCount >= 60) {
                        loadMoreStatus = LoadMoreStatus.NoMore
                    } else {
                        // Simulate random fail
                        if (System.currentTimeMillis() % 3 == 0L) {
                            loadMoreStatus = LoadMoreStatus.Fail
                        } else {
                            itemCount += 20
                            loadMoreStatus = LoadMoreStatus.Default
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Refresh & LoadMore") }) }
    ) { padding ->
        PullToRefreshLayout(
            modifier = Modifier.padding(padding),
            isRefreshing = isRefreshing,
            onRefresh = { onRefresh() },
            enabled = loadMoreStatus == LoadMoreStatus.Default || loadMoreStatus == LoadMoreStatus.Fail || loadMoreStatus == LoadMoreStatus.NoMore
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                items(itemCount) {
                    ListItem({ Text(text = "Item $it") })
                }

                // Footer
                item {
                    LoadMoreFooter(
                        status = loadMoreStatus,
                        onRetry = { onLoadMore() }
                    )
                }
            }

            // Buffer = 0 (底部触发), needRelease = true (松手触发), enabled = !isRefreshing
            listState.OnBottomReached(
                buffer = 0,
                needRelease = true,
                enabled = !isRefreshing
            ) {
                onLoadMore()
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Reusable Components
// -----------------------------------------------------------------------------------------

/**
 * 通用的下拉刷新布局，带有自定义的文本Header。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLayout(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    enabled: Boolean = true,
    refreshThreshold: Dp = 80.dp,
    headerHeight: Dp = 50.dp,
    content: @Composable () -> Unit
) {
    val thresholdPx = with(LocalDensity.current) { refreshThreshold.toPx() }
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }

    Box(
        modifier = modifier
            .pullToRefresh(
                state = state,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                enabled = enabled
            )
    ) {
        // Content Layer
        // 使用 Box 包裹 content，并应用位移。
        // 注意：这里 content() 的重组取决于 content lambda 自身的稳定性。
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = state.distanceFraction * thresholdPx
                }
        ) {
            content()
        }

        // Header Layer
        // 提取 Header 到单独组件，减少重组范围。
        RefreshHeader(
            state = state,
            isRefreshing = isRefreshing,
            headerHeight = headerHeight,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = state.distanceFraction * thresholdPx - headerHeightPx
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RefreshHeader(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    headerHeight: Dp,
    modifier: Modifier = Modifier
) {
    // 只有文字逻辑在这里，避免 PullToRefreshLayout 因为 distanceFraction 变化而重组 text
    val text by remember(isRefreshing) {
        derivedStateOf {
            when {
                isRefreshing -> "刷新中"
                state.distanceFraction > 1f -> "松手刷新"
                else -> "下拉刷新"
            }
        }
    }

    Box(
        modifier = modifier.height(headerHeight),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

enum class LoadMoreStatus {
    Default,
    Loading,
    Fail,
    NoMore
}

/**
 * 通用的上拉加载更多 Footer 组件
 */
@Composable
fun LoadMoreFooter(
    status: LoadMoreStatus,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(enabled = status == LoadMoreStatus.Fail, onClick = onRetry),
        contentAlignment = Alignment.Center
    ) {
        when (status) {
            LoadMoreStatus.Default -> Text("上滑加载更多")
            LoadMoreStatus.Loading -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("加载中...")
                }
            }
            LoadMoreStatus.Fail -> Text("加载失败，请重试")
            LoadMoreStatus.NoMore -> Text("已加载全部")
        }
    }
}

/**
 * 监听列表滚动到底部
 *
 * @param buffer 触发加载的提前量（倒数第 buffer 个 item）
 * @param needRelease 是否需要松手后才触发 (true: 松手且到底部; false: 只要到底部)
 * @param enabled 是否启用监听
 */
@Composable
fun LazyListState.OnBottomReached(
    buffer: Int = 0,
    needRelease: Boolean = false,
    enabled: Boolean = true,
    onLoadMore: () -> Unit
) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val isDragged by interactionSource.collectIsDraggedAsState()

    LaunchedEffect(this, buffer, needRelease, enabled) {
        if (!enabled) return@LaunchedEffect

        snapshotFlow {
            val layoutInfo = layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty() || totalItems == 0) return@snapshotFlow false

            val lastVisibleItem = visibleItemsInfo.last()

            val reached = lastVisibleItem.index >= totalItems - 1 - buffer
            if (needRelease) {
                reached && !isDragged
            } else {
                reached
            }
        }
            .distinctUntilChanged()
            .collect { shouldLoad ->
                if (shouldLoad) {
                    currentOnLoadMore()
                }
            }
    }
}
