package com.ccino.ware.kt

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.flowWithLifecycle
import com.ccino.ware.retrofit.RetrofitActivity
import com.ware.R
import com.ware.component.BaseActivity
import com.ware.databinding.ActivityFlowBinding
import com.ware.jetpack.viewbinding.viewBinding
import kotlinx.android.synthetic.main.activity_flow.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

private const val TAG = "FlowActivyL"
private const val TAG_L = "FlowActivityL"

/**
 * # LiveData / StateFlow 不同：
 * 1. StateFlow多次设置相同的值只会回调一次，LiveData则会每次都回调
 * 2. 当 View 变为 STOPPED 状态时，LiveData会自动取消注册，即不会收到值，而从 StateFlow 或任何其他数据流收集数据则不会取消注册使用方。
 * 3. StateFlow 必须有初始值，LiveData 不需要。
 *
 * # 对于 StateFlow 在界面销毁的时仍处于活跃状态，有两种解决方法：
 * 1. 使用 ktx 将 Flow 转换为 LiveData。
 * 2. 在界面销毁的时候，手动取消（这很容易被遗忘）,见onDestroy。
 *
 * # ShareFlow / StateFlow 均是热流，Flow 是冷流
 * StateFlow 和 SharedFlow 提供了在 Flow 中使用 LiveData 式更新数据的能力，但是如果要在 UI 层使用，需要注意生命周期的问题。
 * StateFlow 和 SharedFlow 相比，StateFlow 需要提供初始值，SharedFlow 配置灵活，可提供旧数据同步和缓存配置的功能。
 *
 * https://www.jianshu.com/p/0d0ee5fd4931
 * https://juejin.cn/post/6978829247917850654#heading-17(buffer)
 * # sequence/list
 * 1. funList(),无论调没调用result.first()后续都会有map/filter操作，并且执行过程是执行完所有元素的map操作再执行filter操作
 * 2. funSequence(),没调用result.first()，result的map和filter都不会执行，只有result被使用的时候才会执行，而且执行是惰性的，即map取出一个元素交给filter，而不是map对所有元素都处理过户再交给filter，
 *    而且，当满足条件后就不会往下执行，由结果可以看出，没有对Sequence的4进行map和filter操作，因为3已经满足了条件
 * 像List这种实现Iterable接口的集合类，每调用一次函数就会生成一个新的Iterable，下一个函数再基于新的Iterable执行，每次函数调用产生的临时Iterable会导致额外的内存消耗，
 * 而Sequence在整个流程只有一个，且不改变原sequence。因此，Sequence这种数据类型可以在数据量较大或数据量未知的时候，作为流式处理的解决方案.一个走完再走下个流程
 *
 * # flow
 *  Flow 其内部是按照顺序执行的，这一点跟 Sequences 很类似
 * Sequence是同步完成这些操作的，那有没有办法使用异步完成那些map和filter等操作符呢，答案就是Flow
 * 1. Flow也是cold stream,也就是直到你调用terminal operator(比如collect{})，Flow才会执行，而且如果重复调用collect，则调用会得到同样的结果
 * 2. Flow提供了很多操作符，比如map，filter，scan，groupBy等，它们都是cold stream的，我们可以利用这些操作符完成异步代码。
 *
 * ## terminal operator
 * terminal operator 就是仅当你调用它的时候才会去得到结果，和sequence使用的时候才会执行，Rxjava调用subscribe后才会执行类似，
 * Flow中的terminal operator是suspend函数，其他的terminal operator有toList,toSet;first(),reduce(),flod()等
 *
 * ## 取消 Cancellation
 * 在协程作用内你完全不用考虑这些，因为只会在作用域内执行，作用域外会自动取消
 *
 * ## Errors 处理异常
 * ### 捕获异常
 * 同样Flow有类似的方法catch{}，如果你不使用此方法，那你的应用会抛出异常或者崩溃，你可以像之前一样使用try catch或者catch{}来处理错误
 * ### 异常恢复resume
 * 异常时，希望使用默认数据或者完整的备份来恢复数据流，在Flow中可以使用catch{},但是需要在catch{}代码块里使用emit()来一个一个的发送备份数据，甚至如果我们愿意，可以使用emitAll()可以产生一个新的Flow，
 *
 * # 上下文切换[flowOn]
 * 1. 默认情况下Flow数据会运行在调用者的上下文(线程)中。
 * 2. 使用flowOn()来改变上游的上下文, flowOn 仅影响操作符之前没有自己上下文的操作符。
 * 3. 根据（2）, 即使中间使用 flowOn 切换上下文，collect 执行的线程还是在 flow 创建的线程中执行的。
 *
 * For example:
 * ```
 * withContext(Dispatchers.Main) {
 *     val singleValue = intFlow // will be executed on IO if context wasn't specified before
 *         .map { ... } // Will be executed in IO
 *         .flowOn(Dispatchers.IO)
 *         .filter { ... } // Will be executed in Default
 *         .flowOn(Dispatchers.Default)
 *         .single() // Will be executed in the Main
 * }
 * ```
 *
 * # StateFlow
 * StateFlow只有值变化后才会释放新的值，和distinctUntilChanged类似
 * collect对于它不是必需的，StateFlow创建的时候就能开始释放值
 *
 * # [toLiveData]
 * # [flowLifecycle]
 * - 会使用 flow 变成热流，所以注意代码执行顺序
 * - 当生命周期反复时，flow 也会反复执行
 * [callback]
 * - callbackFlow：将基于回调的 API 转换为数据流
 * - 使用 awaitClose 来保持流运行，否则在块完成时通道将立即关闭。
 * - awaitClose 参数在流消费者取消流收集cancel()或基于回调的 API 手动调用 SendChannel.close() 时调用或外部的协程被取消，通常用于在完成后清理资源。
 */

class FlowActivity : BaseActivity(R.layout.activity_flow), View.OnClickListener {
    private val binding by viewBinding(ActivityFlowBinding::bind)
    private val viewModel by lazy { ViewModelProvider(this).get(FlowViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sequenceView.setOnClickListener(this)
        listView.setOnClickListener(this)
        flowView.setOnClickListener(this)
        flowDelayView.setOnClickListener(this)
        flowErrorView.setOnClickListener(this)
        flowResumeView.setOnClickListener(this)
        flowOnView.setOnClickListener(this)
        flow2LiveDataView.setOnClickListener(this)
//        toLiveData()
        Log.d(TAG_L, "onCreate: ")
        binding.stateFlowView.setOnClickListener(this)
        stateFlow()
        binding.flowLifecycleView.setOnClickListener { flowLifecycle() }
        binding.goOtherView.setOnClickListener { startOtherActivity() }
        binding.callbackFlowView.setOnClickListener { callback() }
    }


    private fun TextView.textWatcherFlow(): Flow<String> = callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    val success = trySend(s.toString()).isSuccess // 发送值
                    Log.d(TAG_L, "callback:afterTextChanged ret = $success")
                } catch (e: Throwable) {
                    // flow consumers will stop collecting and the coroutine will resume
                    close(e) //异常时关闭
                }
            }
        }
        addTextChangedListener(textWatcher)
        awaitClose { removeTextChangedListener(textWatcher) } //会一直监听
    }.buffer(Channel.CONFLATED)
        .debounce(300L)

    private fun callback() {
        launch {
            binding.callbackFlowView.textWatcherFlow().collect {
//                viewModel.getArticles(it)
            }
        }
    }

    private fun flowLifecycle() {
        launch {
            flow {
                emit("a") // 先打印 doSth before; 再打印 a
                doSth("flowLifecycle")
                emit("b")
            }.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { Log.d(TAG_L, "flowLifecycle: $it") }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sequenceView -> funSequence()
            R.id.listView -> funList()
            R.id.flowView -> funFlow()
            R.id.flowDelayView -> funFlowDelay()
            R.id.flowErrorView -> flowError()
            R.id.flowResumeView -> flowResume()
            R.id.flowOnView -> flowOn()
            R.id.flow2LiveDataView -> toLiveData()
            R.id.state_flow_view -> binding.stateFlowView.postDelayed({ changeStateFlow() }, 3000)
        }
    }

    private fun toLiveData() {
        /**
         * flow 在 active 时执行 collect，inactive 时
         * - 超过 timeout 且没有执行完， flow 会取消任务；当再次 active 时【整个任务】会重新执行。
         * - 未超过 timeout 时，flow 会继续执行，再次 active 时会发送 liveData 数据
         * If the LiveData becomes inactive (LiveData.onInactive) while the flow has not completed, the flow collection will be cancelled after timeoutInMs milliseconds unless the LiveData becomes active again before that timeout (to gracefully handle cases like Activity rotation).
        After a cancellation, if the LiveData becomes active again, the upstream flow collection will be re-executed.
        If the upstream flow completes successfully or is cancelled due to reasons other than LiveData becoming inactive, it will not be re-collected even after LiveData goes through active inactive cycle.
         */
        launch {
            val asLiveData = flow {
                emit("a")
                doSth("toLiveData")
                emit("b")
            }.asLiveData()
            asLiveData.observe(this@FlowActivity) {
                Log.d(TAG_L, "toLiveData: $it")
            }
        }
    }

    private suspend fun doSth(tag: String) {
        Log.d(TAG_L, "$tag doSth: before")
        delay(5000)
        Log.d(TAG_L, "$tag doSth: after")
    }

    private fun startOtherActivity() {
        startActivity(Intent(this, RetrofitActivity::class.java))
    }

    private fun changeStateFlow() {
        launch { viewModel.changeCount() }
    }

    private var stateFlowJob: Job? = null

    //因为有初始值，所以界面一打开就有log
    private fun stateFlow() {
        stateFlowJob = launch {
            viewModel.stateFlow.collect { Log.d(TAG, "stateFlow: collect $it") }
            //同一个launch里只有第一个Flow收到数据，因为collect是挂起函数，可能没有结束
            //viewModel.stateFlow2.collect { Log.d(TAG, "stateFlow 222: collect $it") }
        } //collect is suspend
        launch { viewModel.stateFlow2.collect { Log.d(TAG, "stateFlow 222: $it") } }

        viewModel.liveData.observe(this, { Log.d(TAG, "stateFlow: liveData $it") })
    }

    override fun onStart() {
        super.onStart()
        stateFlowJob = launch { viewModel.stateFlow2.collect { Log.d(TAG, "stateFlow 222: $it") } }
    }

    override fun onStop() {
        super.onStop()
        stateFlowJob?.cancel()
    }


    private fun funList() {
        val list = listOf<Int>(1, 2, 3, 4)
        val result: List<Int> = list.map { i ->
            Log.d(TAG, "funList: Map $i")
            i * 2
        }.filter { i ->
            Log.d(TAG, "funList:  Filter $i")
            i % 3 == 0
        }
/*
        操作符的每个处理实现过程会创建额外的集合来保存过程中产生的中间结果
        2020-06-30 11:42:50.814 20326-20326/com.ware D/FlowActivity: funList: Map 1
        2020-06-30 11:42:50.814 20326-20326/com.ware D/FlowActivity: funList: Map 2
        2020-06-30 11:42:50.814 20326-20326/com.ware D/FlowActivity: funList: Map 3
        2020-06-30 11:42:50.814 20326-20326/com.ware D/FlowActivity: funList: Map 4
        2020-06-30 11:42:50.815 20326-20326/com.ware D/FlowActivity: funList:  Filter 2
        2020-06-30 11:42:50.815 20326-20326/com.ware D/FlowActivity: funList:  Filter 4
        2020-06-30 11:42:50.815 20326-20326/com.ware D/FlowActivity: funList:  Filter 6
        2020-06-30 11:42:50.815 20326-20326/com.ware D/FlowActivity: funList:  Filter 8*/
//        println(result.first())
    }

    private fun funSequence() {
        val sequence = sequenceOf(1, 2, 3, 4)
        val result: Sequence<Int> = sequence.map { i ->
            Log.d(TAG, "funSequence: Map $i")
            i * 2
        }.filter { i ->
            Log.d(TAG, "funSequence: Filter $i")
            i % 3 == 0
        }
        println(result.first()) //不调用就没有输出

        /*

         可以看到是逐个执行所有操作，但性能提升也是相对。
惰性,序列操作分为两个过程：中间操作、末端操作。中间操作全部都是惰性操作：如果没有执行末端操作，中间操作都不会被执行。
           | ----- 中间操作 ----- |
sequence.map { ... }.filter { ... }.forEach{}
                                  |-末端操作-|
          2020-06-30 11:50:36.137 20828-20828/com.ware D/FlowActivity: funSequence: Map 1
          2020-06-30 11:50:36.137 20828-20828/com.ware D/FlowActivity: funSequence: Filter 2
          2020-06-30 11:50:36.137 20828-20828/com.ware D/FlowActivity: funSequence: Map 2
          2020-06-30 11:50:36.137 20828-20828/com.ware D/FlowActivity: funSequence: Filter 4
          2020-06-30 11:50:36.137 20828-20828/com.ware D/FlowActivity: funSequence: Map 3
          2020-06-30 11:50:36.137 20828-20828/com.ware D/FlowActivity: funSequence: Filter 6*/
    }


    /**
     * 1. list转成Flow并在最后调用collect{},会产生一个编译错误，这是因为Flow是基于协程构建的，默认具有异步功能，因此你只能在协程里使用它
     * 2. 打印结果跟sequence不同，过滤到对 i % 3 == 0 后不会停止
     */
    private fun funFlow() {
        launch {
            val list = listOf(1, 2, 3, 4, 5)
            list.asFlow().map {
                Log.d(TAG, "funFlow: Map $it; thread = ${Thread.currentThread().name}")
                it * 2
            }.filter {
                Log.d(TAG, "funFlow: Filter $it;  thread = ${Thread.currentThread().name}")
                it % 3 == 0
            }.collect {
                Log.d(TAG, "funFlow: received $it;  thread = ${Thread.currentThread().name}")
            }
            Log.d(TAG_L, "funFlow: after")
        }
        /* 2020-06-30 14:27:30.514 27051-27051/com.ware D/FlowActivity: funFlow: Map 1; thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: Filter 2;  thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: Map 2; thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: Filter 4;  thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: Map 3; thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: Filter 6;  thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: received 6;  thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: Map 4; thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: Filter 8;  thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: Map 5; thread = main
        2020-06-30 14:27:30.515 27051-27051/com.ware D/FlowActivity: funFlow: Filter 10;  thread = main*/
    }

    private fun funFlowDelay() {
        launch {
            flowOf("A", "Ba", "ora", "pear", "fruit")
                .map { stringToLength(it) }
                .collect { Log.d(TAG, "funFlowDelay: receive $it; thread = ${Thread.currentThread().name}") }//会依次打印 12345
        }
    }

    private suspend fun stringToLength(it: String): Int {
        delay(1000)
        Log.d(TAG, "stringToLength: $it")
        return it.length
    }

    private fun flowOfAnimeCharacters() = flow {
        emit("Madara")
        emit("Kakashi")
        //throwing some error
        throw IllegalStateException() // collect 时会打印 kotlin.Unit
        emit("Jiraya")
        emit("Itachi")
        emit("Naruto")
    }

    private fun flowError() {
        launch {
            /*  try {
                  flowOfAnimeCharacters()
                          .map { stringToLength(it) }
                          .collect { Log.d(TAG, "flowError: receive $it") }
              } catch (e: Exception) {
                  Log.d(TAG, "flowError: ${e.printStackTrace()}")//虽然有异常，但是我们对异常做了处理，不会导致应用崩溃
              } finally {
                  Log.d(TAG, "flowError: finally")
              }
              *//*2020-06-30 14:49:37.665 3224-3224/com.ware D/FlowActivity: stringToLength: Madara
            2020-06-30 14:49:37.666 3224-3224/com.ware D/FlowActivity: flowError: receive 6
            2020-06-30 14:49:38.668 3224-3224/com.ware D/FlowActivity: stringToLength: Kakashi
            2020-06-30 14:49:38.668 3224-3224/com.ware D/FlowActivity: flowError: receive 7
            2020-06-30 14:49:38.670 3224-3224/com.ware W/System.err:     at com.ware.kt.FlowActivity$flowOfAnimeCharacters$1.invokeSuspend(FlowActivity.kt:131)
            2020-06-30 14:49:38.671 3224-3224/com.ware D/FlowActivity: flowError: kotlin.Unit
            2020-06-30 14:49:38.671 3224-3224/com.ware D/FlowActivity: flowError: finally*//*
            */ //or
            flowOfAnimeCharacters()
                .map { stringToLength(it) }
                .catch { Log.d(TAG, "flowError: ${it.printStackTrace()}") }//还是实验性质的api，catch{}必须在terminal operator之前
                .collect { Log.d(TAG, "flowError: receive $it") }
        }
    }

    private fun flowResume() {
        launch {
            flowOfAnimeCharacters()
                .catch {
                    emitAll(flowOf("Minato", "Hashirama"))
                    emit("single")
                }
                .collect { Log.d(TAG, "flowResume: $it") }
        }
    }

    private fun flowOn() {
        launch {
            withContext(Dispatchers.Main) {
                val list = listOf(1, 2, 3, 4, 5).asFlow()
                list // will be executed on IO if context wasn't specified before
                    .map {
                        Log.d(TAG, "flowOn: map $it; thread = ${Thread.currentThread().name}")
                        it * 2
                    } // Will be executed in IO
                    .flowOn(Dispatchers.IO)
                    .filter {
                        Log.d(TAG, "flowOn: filter $it; thread = ${Thread.currentThread().name}")
                        it % 3 == 0
                    } // Will be executed in Default
                    .flowOn(Dispatchers.Default)
                    .collect { Log.d(TAG, "flowOn: collect $it; thread = ${Thread.currentThread().name}") } // Will be executed in the Main
            }
        }
        /* 2020-06-30 15:47:48.259 32043-32225/com.ware D/FlowActivity: flowOn: map 1; thread = DefaultDispatcher-worker-1
         2020-06-30 15:47:48.260 32043-32225/com.ware D/FlowActivity: flowOn: map 2; thread = DefaultDispatcher-worker-1
         2020-06-30 15:47:48.260 32043-32225/com.ware D/FlowActivity: flowOn: map 3; thread = DefaultDispatcher-worker-1
         2020-06-30 15:47:48.260 32043-32225/com.ware D/FlowActivity: flowOn: map 4; thread = DefaultDispatcher-worker-1
         2020-06-30 15:47:48.260 32043-32225/com.ware D/FlowActivity: flowOn: map 5; thread = DefaultDispatcher-worker-1
         2020-06-30 15:47:48.262 32043-32226/com.ware D/FlowActivity: flowOn: filter 2; thread = DefaultDispatcher-worker-2
         2020-06-30 15:47:48.262 32043-32226/com.ware D/FlowActivity: flowOn: filter 4; thread = DefaultDispatcher-worker-2
         2020-06-30 15:47:48.262 32043-32226/com.ware D/FlowActivity: flowOn: filter 6; thread = DefaultDispatcher-worker-2
         2020-06-30 15:47:48.262 32043-32226/com.ware D/FlowActivity: flowOn: filter 8; thread = DefaultDispatcher-worker-2
         2020-06-30 15:47:48.262 32043-32226/com.ware D/FlowActivity: flowOn: filter 10; thread = DefaultDispatcher-worker-2
         2020-06-30 15:47:48.264 32043-32043/com.ware D/FlowActivity: flowOn: collect 6; thread = main
 */
    }
}
