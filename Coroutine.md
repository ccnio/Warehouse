# handler、dispatcher、executor 关系

```kotlin
val workThread = HandlerThread("ThreadName").apply { start() }
//通过 handlerThread 快速创建一个子线程 handler
val workHandler: Handler = Handler(workThread.looper)


val workDispatcher: CoroutineDispatcher = workHandler.asCoroutineDispatcher("")
var workExecutor: Executor = workDispatcher.asExecutor()
```

# Dispatchers
## Dispatchers.Main.immediate、Dispatchers.Main 区别
immediate 也是在主线程执行，但不经过 handler 消息派发，直接在主线程中执行。
```kotlin
 fun dispatcher() {
    launch(Dispatchers.Main.immediate) {
        Log.d(TAG, "dispatcher: immediate")//1
    }
    Log.d(TAG, "dispatcher: outer")//2
}
```
Dispatchers.Main 打印顺序: 2、1
Dispatchers.Main.immediate 打印顺序: 1、2