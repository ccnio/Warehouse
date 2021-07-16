package com.ware.kt

import android.os.Bundle
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.component.BaseActivity
import kotlinx.android.synthetic.main.activity_flow.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "FlowActivity"

/**
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
 * 每次设置Rxjava订阅时，我们都必须考虑合适取消这些订阅，以免发生内存溢出或者，在生命周期结束后依然在后台执行任务(expired task working in background),调用subscribe后，
 * Rxjava会s给我们返回一个Disposable对象，在需要时利用它的disposable方法可以取消订阅，如果你有多个订阅，你可以把这些订阅放在CompositeDisposable,并在需要的时候调用它的clear()方法或者dispose方法
 * 但是在协程作用内你完全不用考虑这些，因为只会在作用域内执行，作用域外会自动取消
 *
 * ## Errors 处理异常
 * ### 捕获异常
 * Rxjava最有用的功能之一是处理错误的方式，你可以在onError里处理所有的异常。
 * 同样Flow有类似的方法catch{}，如果你不使用此方法，那你的应用会抛出异常或者崩溃，你可以像之前一样使用try catch或者catch{}来处理错误
 * ### 异常恢复resume
 * 异常时，我们希望使用默认数据或者完整的备份来恢复数据流，在Rxjava中我们可以是使用 onErrorResumeNext()或者 onErrorReturn()，
 * 在Flow中我们依然可以使用catch{},但是我们需要在catch{}代码块里使用emit()来一个一个的发送备份数据，甚至如果我们愿意，可以使用emitAll()可以产生一个新的Flow，
 *
 * ## 上下文切换flowOn()
 * 默认情况下Flow数据会运行在调用者的上下文(线程)中，如果你想随时切换线程比如像Rxjava的observeOn(),你可以使用flowOn()来改变上游的上下文，
 * 这里的上游是指调用flowOn之前的所有操作符，官方文档有很好的说明
 *
 * Changes the context where this flow is executed to the given [context].--改变Flow执行的上下文
 * This operator is composable and affects only preceding operators that do not have its own context.
 * ---这个操作符是可以多次使用的，它仅影响操作符之前没有自己上下文的操作符
 * This operator is context preserving: [context] **does not** leak into the downstream flow.
 * --这个操作符指定的上下文不会污染到下游，它会保留默认的上下文，例如下面例子中最后的操作符single()使用的是默认的上下文而不是上游指定的Dispatchers.Default
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
 */

class FlowActivity : BaseActivity(R.layout.activity_flow), View.OnClickListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sequenceView.setOnClickListener(this)
        listView.setOnClickListener(this)
        flowView.setOnClickListener(this)
        flowDelayView.setOnClickListener(this)
        flowErrorView.setOnClickListener(this)
        flowResumeView.setOnClickListener(this)
        flowOnView.setOnClickListener(this)
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
        }
    }
}
