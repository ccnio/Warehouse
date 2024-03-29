## Runtime.gc、System.gc区别

### 两者实现

System.gc() 内部 调用 Runtime.gc()

```
// Runtime
public void gc() {
    BlockGuard.getThreadPolicy().onExplicitGc();
    nativeGc();
}

// System
public static void gc() {
    boolean shouldRunGC;
    synchronized (LOCK) {
        shouldRunGC = justRanFinalization;
        if (shouldRunGC) {
            justRanFinalization = false;
        } else {
            runGC = true;
        }
    }
    if (shouldRunGC) {
        Runtime.getRuntime().gc();
    }
}
```

leakcandary 触发 gc 使用的是 Runtime，注意其中的注释：

```kotlin
/**
 * Default implementation of [GcTrigger].
 */
object Default : GcTrigger {
    override fun runGc() {
        // Code taken from AOSP FinalizationTest:
        // System.gc() does not garbage collect every time. Runtime.gc() is
        // more likely to perform a gc.
        Runtime.getRuntime().gc()
        enqueueReferences()
        System.runFinalization() //注意这里的调用
    }
}
```

### 结论

先说结论。

- 4.2.2源码： System.gc()的实现就是调用Runtime.getRuntime().gc()，所以两者是等价的。
- 5.0的源码不一样了：（看上述源码）单纯调用System.gc()是不会触发Runtime.getRuntime().gc()
  的。但是会把这次尝试纪录下来，等到下次调用System.runFinalization()时，会先执行这个Runtime.getRuntime().gc()。这样改后的影响至少有两点：
    - 单纯调用System.gc()是不会触发Runtime.getRuntime().gc()的，直到调用了System.runFinalization()
    - System.gc() -> System.gc() -> … -> System.gc() ->System.runFinalization()，最终只会调用一次Runtime.getRuntime()
      .gc()

所以说只是单纯的调用System.gc()，在Android5.0以上什么也不会发生，可以这样调用：

1. System.gc()和System.runFinalization()同时调用 或者
2. 直接调用Runtime.getRuntime().gc()

### 验证

代码验证见下面引用介绍的中的 demo.

## 强、软、弱、虚引用

### 概念

注意措辞：引用、对象是两码事，val obj = Any(), obj 是引用，Any() 是对象。这里描述的四个引用其实用引用关系描述比较合适。比如：弱引用意思是，变量与对象的引用关系是弱引用，val
obj = Weakerence(obj)。

- 弱引用：只具有弱引用的对象拥有更短暂的生命周期。gc 时一旦发现了只具有弱引用的对象(这里的含义参考下面的demo)
  ，不管当前内存空间足够与否，都会回收它的内存。
- 强引用：强引用是使用最普遍的引用。如果一个对象具有强引用，那垃圾回收器绝不会回收它
- 软引用：如果一个对象只具有软引用，则内存空间充足时，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。
- 虚引用：与其他几种引用都不同，虚引用并不会决定对象的生命周期。get方法返回null，不能获取值。虚引用主要用来跟踪对象被垃圾回收器回收的活动。但
  leakcandary 中使用的是弱引用来跟踪对象被回收的

引用队列：软、弱、虚引用创建时可以指定一个引用队列 ReferenceQueue，当引用的对象被回收时，这个引用(注意这里不是被引用指向的对象)会被加入到队列中。

### 代码验证

```kotlin
var obj: Any? = Any() // 这里是强引用关系。引用：obj, 对象: any
val weakReference = WeakReference(obj)
Log.d(TAG, "reference: value=${weakReference.get()}")

obj = null //这样 any 没有强引用了，只有 weakReference 的弱引用。
delay(1000)

Runtime.getRuntime().gc() // 手动触发垃圾回收, 使用 System.gc 不会生效 
delay(2000) // 因为 any 只有弱引用, 所以 any 会被回收，weakReference.get 就返回空。
Log.d(TAG, "reference: after gc obj=${weakReference.get()}")
// 打印结果：
// reference: value=java.lang.Object@667e64b
// reference: after gc obj=null


// 虚引用验证
val queue = ReferenceQueue<Any>()
val reference = PhantomReference(obj!!, queue)
Log.d(TAG, "reference before: value=${reference.get()}, queue=${queue.poll()}, refer=$reference")
obj = null
Runtime.getRuntime().gc()
delay(1000)
Log.d(TAG, "reference after: ${reference.get()}, queue=${queue.poll()}")
//打印结果
//reference before: value=null, queue=null, refer=java.lang.ref.PhantomReference@e4c9c27
//reference after: null, queue=java.lang.ref.PhantomReference@e4c9c27
```

上述代码如果把 obj = null 注掉，any 对象因为还有 obj 的强引用，这样 weakReference.get 还能获取。
