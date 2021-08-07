https://blog.csdn.net/rikkatheworld/article/details/105650504
http://wenqin231.com/2020/04/13/Glide%20%E7%BC%93%E5%AD%98%E6%9C%BA%E5%88%B6/
https://www.jianshu.com/p/133adedd8860

- bitmap的内存是如何分配的，需要手动回收吗（glide里是主动调用的）
- bitmap获取内存占用多少？com.bumptech.glide.util.Util.getBitmapByteSize(android.graphics.Bitmap)
- 加载的图片大小对过程的影响
- Glide磁盘缓存策略分为四种,默认的是RESULT(默认值这一点网上很多文章都写错了,但是这一点很重要):
  1.ALL:缓存原图(SOURCE)和处理图(RESULT)
  2.NONE:什么都不缓存
  3.SOURCE:只缓存原图(SOURCE)
  4.RESULT:只缓存处理图(RESULT)      ---默认值
# NavigableMap
# LinkedHashMap 实现
# Glide 默认会依据传入的 View 的宽高来裁剪图片的宽高，那是怎么拿到 View 的宽高值的呢
https://blog.csdn.net/f409031mn/article/details/91348546
1. Glide 优先通过 View 的 LayoutParams 来获取宽高值， 其次是 View.getWidth()/Height() 方法
2. 如果宽高值设置为 WRAP_CONTENT ，那么将会返回设备屏幕宽高中的最大值，不利于节省内存
3. 如果计算得到的宽高值小于等于 0 时， Glide 会通过给 View 设置 **OnPreDrawListener** 来监听获取具体的宽高值，这一点在 LinearLayout 的权重，ConstraintLayout 设置 View 的比例尺会经常用到
4. 获取展示的宽高后才会进行图片的请求
因此，我们平时使用 Glide 应该尽可能在 xml 中或在加载图片之前使用 Java 代码设置好 LayoutParams 具体的宽高值，达到最好的使用效果；Glide对WRAP_CONTENT的支持并不好，所以尽量不要用。
```
ViewTreeObserver observer=view.getViewTreeObserver();
//注册观察者，监听变化
observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
   @Override
   public boolean onPreDraw() {
       int viewWidth=view.getMeasuredWidth();
       int viewHeight=view.getMeasuredHeight();
       return true; //true 代表执行接下来的绘制
   }
});
```
# 内存缓存
* <ul>
*   <li>Check the current set of actively used resources, return the active resource if present,
*       and move any newly inactive resources into the memory cache.
*   <li>Check the memory cache and provide the cached resource if present.
*   <li>Check the current set of in progress loads and add the cb to the in progress load if one
*       is present.
*   <li>Start a new load.
* </ul>
宽高确定后开始执行数据请求，加载数据是在SingleRequest#onSizeReady方法中被触发，这里面是调用了Engine#load方法，网络执行前执行了一些缓存查询。
Glide在内存缓存中做出的优化是，加入了弱引用缓存，LRUCache缓存和复用池（bitmapArrayPool，byteArrayPool
## 活跃缓存策略(弱引用缓存)
1. 回收线程如何唤醒并执行清除操作的？
2. ReferenceQueue的性质，在此处的作用？
https://www.jianshu.com/p/8de72cf25847
https://www.jianshu.com/p/f45938865cd0
ActiveResources是活跃内存缓存的实现，其核心就是维护一个Map，这个map就是维系图片包装类弱引用的：
```
Executor monitorClearedResourcesExecutor; //核心线程，一直运行监视弱引用队列中的引用对象被gc回收掉
final Map<Key, ResourceWeakReference> activeEngineResources = new HashMap<>();
private final ReferenceQueue<EngineResource<?>> resourceReferenceQueue = new ReferenceQueue<>();
final Map<Key, ResourceWeakReference> activeEngineResources = new HashMap<>();

ActiveResources(boolean isActiveResourceRetentionAllowed, Executor monitorClearedResourcesExecutor) {
    this.isActiveResourceRetentionAllowed = isActiveResourceRetentionAllowed;
    this.monitorClearedResourcesExecutor = monitorClearedResourcesExecutor;

    monitorClearedResourcesExecutor.execute(
        new Runnable() {
          @Override
          public void run() {
            cleanReferenceQueue();
          }
    });
}

//ResourceWeakReference继承了WeakReference.
static final class ResourceWeakReference extends WeakReference<EngineResource<?>> {
    final Key key;//关联的key
    final boolean isCacheable;//是否可缓存
    Resource<?> resource;//关联的图片资源
}
```
在构造的时候会启动一个核心线程，一直运行监视弱引用队列中的引用对象被gc回收掉，被回收掉的对象就调用cleanupActiveReference方法，从缓存中移除引用，并且调用 listener.onResourceReleased(ref.key, newResource);

说明一点：这里缓存管理的都是Resource<T>对象，该对象是对原生资源（baitmap,drawable,gif）的一种包装
EngineResource<T>实现了Resource接口，对资源的引用计算，采用引用计数法，当资源被引用的时候计数器+1，当引用不在使用的时候计数器-1，当计数=0的时候，回调释放资源,会从弱引用缓存中删除，加入lru cache中

既然是维系Map，那么从活跃缓存中取图片资源对应的就是get，将图片资源加入活跃缓存中对于的就是put。
我们先看下put的操作：
```
void activate(Key key, EngineResource<?> resource) {
    //将图片资源和关联的key使用弱引用进行包装关联
    ResourceWeakReference toPut =
        new ResourceWeakReference(key, resource, getReferenceQueue(), isActiveResourceRetentionAllowed);
    //将包装之后的图片资源弱引用加入map中
    ResourceWeakReference removed = activeEngineResources.put(key, toPut);
    if (removed != null) {
      //移除之前就的图片资源弱引用
      removed.reset();
    }
  }
```
get操作：
```
EngineResource<?> get(Key key) {
    //根据key从map中取出图片资源弱引用
    ResourceWeakReference activeRef = activeEngineResources.get(key);
    if (activeRef == null) {
      //如果图片资源资源弱引用为空，返回空
      return null;
    }
    //从弱引用中获取图片资源
    EngineResource<?> active = activeRef.get();
    if (active == null) {
      //如果图片资源已经释放，则调用cleanupActiveReference清除处理
      cleanupActiveReference(activeRef);
    }
    //返回图片资源
    return active;
}
``
get的操作其实就是从map取出弱引用对象，再从弱引用对象中取出图片资源。这里有个关键的地方cleanupActiveReference：
```
void cleanupActiveReference(@NonNull ResourceWeakReference ref) {
    //从map中移除key对应的图片资源弱引用对象
    activeEngineResources.remove(ref.key);

    if (!ref.isCacheable || ref.resource == null) {
      //如果图片不可缓存或者图片资源为空，则结束
      return;
    }
    //图片可缓存并且图片资源不为空
   //重新包装成EngineResource图片资源对象
    EngineResource<?> newResource =
        new EngineResource<>(ref.resource, /*isCacheable=*/ true, /*isRecyclable=*/ false);
    newResource.setResourceListener(ref.key, listener);
    //回调告知图片资源已经从活跃缓存中移除释放
    listener.onResourceReleased(ref.key, newResource);
}
```
这里的listener就是图片加载引擎Engine对象，我们看下图片加载引擎Engine对象的onResourceReleased方法是做什么处理的：
```
public void onResourceReleased(Key cacheKey, EngineResource<?> resource) {
    //从活跃缓存中移除指定key对应的图片资源
    activeResources.deactivate(cacheKey);
    if (resource.isCacheable()) { //图片资源可缓存
      //将活跃缓存移除的图片资源加入内存缓存中进行缓存
      cache.put(cacheKey, resource);
    } else {
      //图片资源不可缓存，执行释放操作
      resourceRecycler.recycle(resource);
    }
}
synchronized void deactivate(Key key) {
    //remove会触发回收线程的唤醒并执行清除操作
    ResourceWeakReference removed = activeEngineResources.remove(key);
    if (removed != null) {
      removed.reset();
    }
}
```

综合上述解析，其实活跃缓存策略没什么复杂的，它的核心：
- 使用弱引用对图片资源和key进行包装
- 用HashMap进行管理，key是与图片资源关联的Key，value是经过包装的图片资源弱引用
- put操作就是讲图片资源包装成图片资源弱引用对象，然后放入HashMap中
- get操作就是根据key从HashMap中取出对应的图片资源弱引用对象，再从图片资源弱引用对象中取出图片资源，当弱引用中图片资源为空，如果可缓存则将图片资源缓存到内存缓存中，否则进行回收释放操作

那么什么时候图片会加入活跃缓存中的呢？大体就是图片从网络上加载之后，放入磁盘，再从磁盘中经过转换之后就会放入活跃缓存中。
## LRUCache缓存
https://blog.islinjw.cn/2021/02/08/Glide源码探究-二-内存缓存/#LRUCache
- 下面代码用LinkedHashMap实现LRU算法缓存,android中的实现跟下面的实现还是不一样的，glide中的也不一样，但都是使用此集合。
```
public class LRUCache extends LinkedHashMap {
  public LRUCache(int maxSize) {
      super(maxSize, 0.75F, true);
      maxElements = maxSize;
  }

  protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
      return size() > maxElements;
  }

  private static final long serialVersionUID = 1L;
  protected int maxElements;
}
```
- 如何根据设备来分配内存缓存的大小
MemorySizeCalculator 这个类是用来计算 BitmapPool 、ArrayPool 以及 MemoryCache 大小的。
activityManager.getMemoryClass(); isLowMemoryDevice
- bitmap内存大小的获取方式
在弱引用缓存中，被回收掉的弱引用使用listener.onResourceReleased(ref.key, newResource);处理时会调用MemoryCache（LRUCache）缓存相关的put操作：
```
public void onResourceReleased(Key cacheKey, EngineResource<?> resource) {
    activeResources.deactivate(cacheKey);
    //回调释放资源,会从弱引用缓存中删除，加入lru cache中
    if (resource.isMemoryCacheable()) {
      cache.put(cacheKey, resource);
    } else {
      resourceRecycler.recycle(resource, /*forceNextFrame=*/ false);
    }
}
```
内存缓存的接口是MemoryCache，接口的实现类有两个，一个是MemoryCacheAdapter适配不适用内存缓存的时候，一个是LruResourceCache内存缓存。这里的LRUCache跟android系统自带的LRUCache是有区别的。

从LRUCache中get资源时，会从其保存的队列中【移出】并添加到弱引用缓存中；弱引用回收时又会添加到LRU中；这样就形成了一个依赖关系。
LRU中的put操作：
```
public synchronized Y put(@NonNull T key, @Nullable Y item) {
    final int itemSize = getSize(item);
    if (itemSize >= maxSize) {
      onItemEvicted(key, item); //如果此资源特别大，直接跳过LRU,然后回收
      return null;
    }

    if (item != null) {
      currentSize += itemSize;
    }
    @Nullable final Y old = cache.put(key, item);
    if (old != null) {
      currentSize -= getSize(old);

      if (!old.equals(item)) {
        onItemEvicted(key, old);
      }
    }
    evict();

    return old;
}
```
onItemEvicted 经过层层调用最终到各个具体的资源（BitmapResource/BitmapDrawableResource/DrawableResource等）中如下代码
```
public void recycle() {
   bitmapPool.put(bitmap);//这就引入了缓冲池
}
```
MemoryCache被回收掉的bitmap放到了复用池中。内存缓存中当内存溢出的时候，会清理资源腾出空间，以满足其他资源的加入，清理掉的资源会被放入复用池中。
## BitmapPool复用池（LruBitmapPool）
- bitmap如何复用内存
- NavigableMap用于查找匹配的bitmap
- keyPool缓存实现
Glide在解码Bitmap的时候采用了BitmapPool复用池的方式，达到高效利用内存，减少创建内存的开销，就是拿旧图片的bitmap给新图片去循环利用。未使用复用的情况下，每次解码都会申请一块新的内存，
如果使用复用bitmap对象，解码的时候会去池子中找出合适大小的bitmap，使用这个bitmap对象的内存。bitmap复用并不会减少内存大小，而是减少了内存分配和回收带来的内存抖动导致页面卡顿，以及内存溢出问题。
从Android 3.0 (API Level 11)开始，引进了BitmapFactory.Options.inBitmap字段。如果这个值被设置了，decode方法会在加载内容的时候去reuse已经存在的bitmap. 这意味着bitmap的内存是被reused的，这样可以提升性能, 并且减少了内存的allocation与de-allocation.
能够复用要求如下：
1. android4.4以上被复用的bitmap内存大小必须大于等于要解码的bitmap的内存大小，才可以复用bitmap
2. 4.4以下3.0以上要解码的bitmap必须是jpeg或者png格式，而且和复用的bitmap大小一样，inSampleSize=1，另外复用的bitmap必须要设置inPreferredConfig
BitmapPool接口 实现类有一个BitmapPoolAdapter适配器适配不使用复用的时候，另外一个就是LruBitmapPool，采用LRU算法管理复用池。
LruBitmapPool主要做一些缓存大小配置、日志记录等操作。主要的缓存实现是交给LruPoolStrategy来完成的。由于实际开发的时候两张图片资源尺寸完全一样的情况不多(尤其在不同页面)，会导致复用的命中率比较低。而安卓4.4之后如果config相同只需要旧图片Bitmap的内存大小大于新图片需要的内存大小就能拿来复用了，这样就能提高复用的命中率:SizeConfigStrategy
将Bitmap的字节大小和Config一起作为key，将这个Key与Bitmap按照K-V的方式存入GroupedLinkedMap中。还有一个HashMap对象sortedSizes，记录每个Bitmap的size对应在当前缓存中的个数，put 时加一，get 时减一。
GroupedLinkedMap链表是为了支持 LRU 算法，最常使用的 Bitmap 都会移动到链表的前端，使用次数越少就越靠后，当调用 removeLast 方法时就直接调用链表最后一个元素的 removeLast 方法移除元素。
SizeConfigStrategy 使用 size（图片的像素总数） 和 config 作为唯一标识。当获取的时候会先找出 cofig 匹配的 Bitmap（一般就是 config 相同），然后保证该 Bitmap 的 size 大于我们期望的 size 并且小于期望 size 的 8 倍即可复用
```
/** An interface for a pool that allows users to reuse {@link android.graphics.Bitmap} objects. */
public interface BitmapPool {
  void put(Bitmap bitmap);

  /**
   * Returns a {@link android.graphics.Bitmap} of exactly the given width, height, and
   * configuration, and containing only transparent pixels.
   *
   * <p>Because this method erases all pixels in the {@link Bitmap}, this method is slightly slower
   * than {@link #getDirty(int, int, android.graphics.Bitmap.Config)}. If the {@link
   * android.graphics.Bitmap} is being obtained to be used in {@link android.graphics.BitmapFactory}
   * or in any other case where every pixel in the {@link android.graphics.Bitmap} will always be
   * overwritten or cleared, {@link #getDirty(int, int, android.graphics.Bitmap.Config)} will be
   * faster.
   *
   * <pre>
   *     Implementations can should clear out every returned Bitmap using the following:
   *
   * {@code
   * bitmap.eraseColor(Color.TRANSPARENT);
   * }
   */
  Bitmap get(int width, int height, Bitmap.Config config);//仅包含透明的像素

  /**
   * Identical to {@link #get(int, int, android.graphics.Bitmap.Config)} except that any returned
   * {@link android.graphics.Bitmap} may <em>not</em> have been erased and may contain random data
   */
  Bitmap getDirty(int width, int height, Bitmap.Config config);//
}
```
# 加载 Bitmap
Glide 的 Bitmap 加载流程位于 Downsampler 类中。当从其他渠道，比如网络或者磁盘中获取到一个输入流 InputStream 之后就可以进行图片加载了。下面是 Downsampler 的 decodeFromWrappedStreams 方法
# 资源加载
资源加载开始于Engine.load()方法
* <p>The flow for any request is as follows:
* <ul>
*   <li>Check the current set of actively used resources, return the active resource if present,
*       and move any newly inactive resources into the memory cache.
*   <li>Check the memory cache and provide the cached resource if present.
*   <li>Check the current set of in progress loads and add the cb to the in progress load if one
*       is present.
*   <li>Start a new load.
* </ul>

Stage.RESOURCE_CACHE: 从磁盘中缓存的资源中获取数据,ResourceCacheGenerator
Stage.DATA_CACHE: 从磁盘中缓存的源数据中获取数据,DataCacheGenerator
Stage.SOURCE: 重新请求数据,SourceGenerator

