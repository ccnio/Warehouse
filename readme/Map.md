## HashMap
![image.png](https://cdn.nlark.com/yuque/0/2024/png/26218568/1704113491029-fdb3164d-c846-4c69-a16a-aac68af9f118.png#averageHue=%23f5efef&clientId=u6c52a120-ad2c-4&from=paste&height=289&id=u9496c57e&originHeight=434&originWidth=931&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=98614&status=done&style=none&taskId=u621c2e25-525a-49ab-bdd9-d5033b2727f&title=&width=620.6666666666666)

- HashMap 可以存储 null 的 key 和 value，但 null 作为键只能有一个，null 作为值可以有多个
- JDK1.8 之前 HashMap 由 数组+链表 组成的，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的（“拉链法”解决冲突）。 JDK1.8 以后的 HashMap 在解决哈希冲突时有了较大的变化，当链表长度大于等于阈值（默认为 8）（将链表转换成红黑树前会判断，如果当前数组的长度小于 64，那么会选择先进行数组扩容，而不是转换为红黑树）时，将链表转化为红黑树，以减少搜索时间。
- HashMap 默认的初始化大小为 16。之后每次扩充，容量变为原来的 2 倍， 当容量超过（加载因子*size时会扩容）。HashMap 总是使用 2 的幂作为哈希表的大小。
- 线程不安全原因：JDK1.7 中，扩容时会造成环形链或数据丢失；JDK1.8 中，插入数据时会发生数据覆盖的情况。

**1.7，1.8区别**

| 比较 | HashMap1.7 | HashMap1.8 |
| --- | --- | --- |
| 数据结构 | 数组+链表 | 数组+链表+红黑树 |
| Hash算法 | 较为复杂 | 异或hash右移16位 |
| 扩容 | 插入前扩容 | 插入后，初始化，树化时扩容 |
| 节点插入 | 头插法 | 尾插法 |
| 节点 | Entry | Node TreeNode |
| 对Null的处理 | 单独写一个putForNull()方法处理 | 以一个Hash值为0的普通节点处理 |
| 初始化 | 赋值给一个空数组，put时初始化 | 没有赋值，懒加载，put时初始化 |


**红黑树**
红黑树是一个自平衡的二叉查找树，查找效率会从链表的o(n)降低为o(logn)。
注意：不是说变成了红黑树效率就一定提高了，只有在链表的长度不小于8，而且数组的长度不小于64的时候才会将链表转化为红黑树，
为什么非要等到链表的长度大于等于8的时候，才转变成红黑树？
红黑树要比链表构造复杂，在链表的节点不多的时候，还是链表性能高些。
```java
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {
    // 序列号
    private static final long serialVersionUID = 362498820763181265L;
    // 默认的初始容量是16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    // 最大容量
    static final int MAXIMUM_CAPACITY = 1 << 30;
    // 默认的负载因子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 当桶(bucket)上的结点数大于等于这个值且时会转成红黑树
    static final int TREEIFY_THRESHOLD = 8;
    // 当桶的结点数小于等于这个值数组长度大于等于64(见MIN_TREEIFY_CAPACITY)时树转链表
    static final int UNTREEIFY_THRESHOLD = 6;
    // 桶中结构转化为红黑树对应的table的最小容量
    static final int MIN_TREEIFY_CAPACITY = 64;
    // 存储元素的数组，总是2的幂次倍
    transient Node<k,v>[] table;
    // 存放具体元素的集
    transient Set<map.entry<k,v>> entrySet;
    // 存放元素的个数，注意这个不等于数组的长度。
    transient int size;
    // 每次扩容和更改map结构的计数器
    transient int modCount;
    // 阈值(容量*负载因子) 当实际大小超过阈值时，会进行扩容
    int threshold;
    // 负载因子
    final float loadFactor;
}
```

- loadFactor 负载因子

loadFactor 负载因子是控制数组存放数据的疏密程度，loadFactor 越趋近于 1，那么 数组中存放的数据(entry)也就越多，也就越密，也就是会让链表的长度增加，loadFactor 越小，也就是趋近于 0，数组中存放的数据(entry)也就越少，也就越稀疏。
loadFactor 太大导致查找元素效率低，太小导致数组的利用率低，存放的数据会很分散。loadFactor 的默认值为 0.75f 是官方给出的一个比较好的临界值。给定的默认容量为 16，负载因子为 0.75。Map 在使用过程中不断的往里面存放数据，当数量超过了 16 * 0.75 = 12 就需要将当前 16 的容量进行扩容，而扩容这个过程涉及到 rehash、复制数据等操作，所以非常消耗性能。

- resize 方法

进行扩容，会伴随着一次重新 hash 分配，并且会遍历 hash 表中所有的元素，是非常耗时的。在编写程序中，要尽量避免 resize。resize 方法实际上是将 table 初始化和 table 扩容 进行了整合，底层的行为都是给 table 赋值一个新的数组。

- HashMap 的 Hash 规则
   1. 计算 hash 值 int hash = key.hashCode()。
   2. **与或**上 hash 值无符号右移16 位。 hash = hash ^ (hash >>> 16)。
   3. 位置计算公式 index = (n - 1) & hash ，其中 n 是容量。

结合 容量是2的幂时，key 的 hash 值然后 & (容量-1) 确定位置时**碰撞概率会比较低（**可以通过负载因子调节）
## ConcurrentHashMap
在JDK8ConcurrentHashMap内部机构：结构基本上与功能和JDK8的HashMap一样，只不过增加了线程安全性的举措。
JDK1.8 大量的利用了volatile，CAS等乐观锁技术来减少锁竞争对于性能的影响**：**

- **JDK1.8：synchronized+VOLATILE+CAS+Node**
- **JDK1.7：ReentrantLock+Segment+HashEntry**
### HashMap/ConcurrentHashMap/HashTable 区别

- HashMap 允许 null 键和 null 值，在计算哈键的哈希值时，null 键哈希值为 0。
- HashTable、ConcurrentHashMap不允许key和value为null
- ConcurrentHashMap对整个桶数组进行了分割分段(Segment)，然后在每一个分段上都用lock锁进行保护，相对于HashTable的synchronized锁的粒度更精细了一些，并发性能更好;键值对不允许有null
- **初始容量大小和每次扩充容量大小的不同 **： ①创建时如果不指定容量初始值，Hashtable 默认的初始大小为11，之后每次扩充，容量变为原来的2n+1。HashMap,ConcurrentHashMap 默认的初始化大小为16。之后每次扩充，容量变为原来的2倍。②创建时如果给定了容量初始值，那么 Hashtable 会直接使用你给定的大小，而 HashMap 会将其扩充为2的幂次方大小（HashMap 中的tableSizeFor()方法保证，下面给出了源代码）。也就是说 HashMap 总是使用2的幂作为哈希表的大小。
- Hashtable 和 JDK1.8 之前的 HashMap 的底层数据结构类似都是采用 数组+链表 的形式
-  实现线程安全的方式（重要）：HashTable 中，使用同一把锁直接在 put 和 get 方法上加上了 synchronized，效率非常低下。当一个线程访问同步方法时，其他线程也访问同步方法，可能会进入阻塞或轮询状态，如使用 put 添加元素，另一个线程不能使用 put 添加元素，也不能使用 get，竞争会越来越激烈效率越低。
### 线程安全实现

**JDK7 ConcurrentHashMap**
在JDK1.7中ConcurrentHashMap由Segment(分段锁)数组结构和HashEntry数组组成。Segment是一种可重入锁，通过继承 ReentrantLock 来进行加锁，通过每次锁住一个 segment 来降低锁的粒度。
![image.png](https://cdn.nlark.com/yuque/0/2022/png/26218568/1651045346489-cdb704f9-a498-4b9d-a2b1-ed2afc2daf04.png#averageHue=%23f6f5f8&clientId=u4834a404-557c-4&from=paste&height=272&id=uf346a059&originHeight=445&originWidth=543&originalType=binary&ratio=1&rotation=0&showTitle=false&size=205872&status=done&style=none&taskId=uba673295-f3a6-4527-aea6-9364baf37f7&title=&width=332)

**JDK8 ConcurrentHashMap**
1.8中摒弃了Segment分段锁的数据结构，基于CAS操作及使用synchronized/volatile关键字提高并发性。
```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    volatile V val;  //使用了volatile属性
    volatile Node<K,V> next;  //使用了volatile属性
    ...
}
```
使用了volatile关键字修饰value和next，保证并发的可见性。其中Node子类有：

- ForwardingNode：扩容节点，只是在扩容阶段使用的节点，主要作为一个标记，在处理并发时起着关键作用，有了ForwardingNodes，也是ConcurrentHashMap有了分段的特性，提高了并发效率
- TreeBin：TreeNode的代理节点，用于维护TreeNodes，ConcurrentHashMap的红黑树存放的是TreeBin
- TreeNode：用于树结构中，红黑树的节点（当链表长度大于8时转化为红黑树），此节点不能直接放入桶内，只能是作为红黑树的节点
- ReservationNode：保留结点

ConcurrentHashMap中查找元素、替换元素和赋值元素都是基于sun.misc.Unsafe中原子操作实现多并发的无锁化操作。
```java
static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
    return (Node<K,V>)U.getObjectAcquire(tab, ((long)i << ASHIFT) + ABASE);
}

static final <K,V> boolean casTabAt(Node<K,V>[] tab, int i, Node<K,V> c, Node<K,V> v) {
    return U.compareAndSetObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
}

static final <K,V> void setTabAt(Node<K,V>[] tab, int i, Node<K,V> v) {
    U.putObjectRelease(tab, ((long)i << ASHIFT) + ABASE, v);
}
```
**ConcurrentHashmap 不支持 key 或者 value 为 null 的原因？**
ConcurrentHashmap和hashMap不同的是，concurrentHashMap的key和value都不允许为null，
因为concurrenthashmap它们是用于多线程的，并发的 ，如果map.get(key)得到了null，不能判断到底是映射的value是null,还是因为没有找到对应的key而为空

**put()方法如何实现线程安全呢？**
ConcurrentHashMap 中存储数据采用的 Node 数组是采用了 volatile 来修饰的，但是这只能保证数组的引用在不同线程之间是可用的，并不能保证数组内部的元素在各个线程之间也是可见的。

**put时 不存在hash冲突的时**
(f = tabAt(tab, i = (n - 1) & hash)) == null中使用tabAt原子操作获取数组，并利用casTabAt(tab, i, null, new Node<K,V>(hash, key, value))CAS操作将元素插入到Hash表中
**存在hash冲突时**
先把当前节点使用关键字synchronized加锁，然后再使用tabAt()原子操作判断下有没有线程对数组进行了修改，最后再进行其他操作。
**为什么要锁住更新操作的代码块?**
因为发生了哈希冲突，当前线程正在f所在的链表上进行更新操作，假如此时另外一个线程也需要到这个链表上进行更新操作，则需要等待当前线程更新完后再执行
```java
//当前节点加锁  
synchronized (f) {  
     //这里判断下有没有线程对数组进行了修改  
     if (tabAt(tab, i) == f) {  
     ......//do something  
     }
}
```
## SparseArray/ArrayMap
HashMap内部是使用一个默认容量为16的数组来存储数据的，扩容时，int newCapacity = oldCapacity * 2; 这将对我们的内存空间造成很大消耗和浪费，并且 hashmap 不会收缩空间。我们在一些情况下可以使用SparseArray和ArrayMap来代替HashMap。
```kotlin
val sparseArray = SparseArray<UserData>()
sparseArray.get(2)//int类型为key
sparseArray.put(2, UserData())

val arrayMap = ArrayMap<String, UserData>()
arrayMap.get("Jack")//自定义类型为key
arrayMap.put("Jack", UserData())
arrayMap.put(null, UserData())
```
hashmap查找时间复杂度 O(1)，ArrayMap 查找的时间复杂度是 O(logn)），但是 ArrayMap 的空间消耗更小，它内部使用数组存储 hash 和键值对，不用花费多余的指针维护链表或树结构，扩容的时候只扩容 1.5 倍，并且元素小于一定量后还会收缩数组来回收空间。所以在数据量不大并且需要节省空间的时候可以考虑 ArrayMap。
### ArrayMap的实现
![image.png](https://cdn.nlark.com/yuque/0/2024/png/26218568/1704164667801-85633939-8394-4405-ac03-e1696746baaa.png#averageHue=%23efefef&clientId=u6c52a120-ad2c-4&from=paste&height=541&id=ue2d2420f&originHeight=812&originWidth=410&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=133429&status=done&style=none&taskId=ud6bcef3d-d4a7-4cca-82ef-c2a9da85f22&title=&width=273.3333333333333)

- int[] mHashes：保存每个元素的 hash，长度为容量大小，mHashes 是有序的，按照元素的 hash 值由小到大排序，hash 值相同的元素，先插入的排在前面。由于 mHashes 是有序的，所以使用二分法查找元素的位置。
- Object[] mArray：保存键值对，长度为容量的两倍
- hash、array 映射关系：根据 key 的 hash 在 mHashes 的存放位置 index，可以确定键值对在 mArray 的存放位置。key 存放在 index << 1 处，value 存放在 (index << 1) + 1 处
- 初始容量为 0
```java
mHashes = new int[size];   
mArray = new Object[size<<1];  
```
ArrayMap相对于HashMap，无需为每个键值对创建Node对象，并且在数组中连续存放，这就是为什么ArrayMap相对HashMap要节省空间。ArrayMap也是通过Key对象的hashCode方法返回int型hash值，通过一系列计算获取对应在数组中的下标。下面分析ArrayMap中hash->index的转换过程

- **Key对象->mArray下标转换**
   - 第一步，调用key对象的hashCode方法获取int值，如果key对象为null则为0。这里和HashMap是完全一样的。
   - 第二步，通过二分法查找获取hash在mHashes数组中的下标index。
   - 第三步，mHashes下标查找mArray键值对。mHashes中的index*2即为mArray中的Key下标，index*2+1为Value的下标。由于存在hash碰撞的情况，而二分法查找到下标可能是多个连续相同hash值中的任意一个，所以此时需要用equals比对对命中的Key对象是否相符，不相符时，从当前index先向后再向前遍历所有相同hash值。
- **存取**

由于是用数组中连续位置存放的，数组各元素中没有空余位置，空间占用更优。最好的情况时在最尾部增删，如果在中间增删则需要移动数组元素，这里和ArrayList原理相同不再细说。

- 扩容、删除

扩容会发生数据的复制，这个是会影响效率的，remove 操作时，在一定条件下，可能会发生数据的压缩，从而节省内存的使用。在插入前，判断是否要扩容，当键值对数量大于等于 mHashes 数组的长度时，进行扩容。扩容过程和 ArrayList 相似，得到新数组长度，创建新数组，并将旧数组的元素复制过去。新数组长度和旧数组长度有关：
如果旧数组长度小于 4，那么新数组长度为 4；如果旧数组长度大于等于 4 并且小于 8，那么新数组长度为 8；如果旧数组长度大于等于 8，新数组长度是原来的 1.5 倍。在删除元素后，如果发现当前容量大于 8，并且剩余的键值对数量小于容量的 1/3 时，将收缩数组，如果键值对数量小于等于 8，那么新数组长度为 8；如果键值对数量大于 8，那么新数组长度为键值对数量的 1.5 倍。

- 冲突解决

假设当前 ArayMap 数据如下图，此时再往里面插入数据key4/value4,假设4的hash值为15。
![image.png](https://cdn.nlark.com/yuque/0/2024/png/26218568/1704170623401-62b3898f-f7e2-4b3a-a92d-7211abb0330e.png#averageHue=%23f3f3f3&clientId=u6c52a120-ad2c-4&from=paste&height=270&id=uf3ab725b&originHeight=405&originWidth=1144&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=64919&status=done&style=none&taskId=u5d2cd977-206e-43f6-9b10-6736a2f9a10&title=&width=762.6666666666666)
![image.png](https://cdn.nlark.com/yuque/0/2024/png/26218568/1704170695627-21d50c0c-2de8-4e9b-b142-866a2921f306.png#averageHue=%23f2f2f2&clientId=u6c52a120-ad2c-4&from=paste&height=253&id=uc67bc0be&originHeight=379&originWidth=1032&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=65517&status=done&style=none&taskId=ua35f6cda-2ad6-4632-a1e3-5a328f2dccb&title=&width=688)
### SparseArray
SparseArray和ArrayMap的实现原理是完全一样的，都是通过二分法查找Key对象在Key数组中的下标来定位Value，SparseArray相比ArrayMap进一步优化空间提高性能，目的是专门针对基本类型做优化，Key只能是可排序的基本类型，比如int型key的SparseArray，long型key的LongSparseArray，对于Value，除了泛型Value，对每种基本类型都有单独的实现，比如SparseBooleanArray，SparseLongArray等等。

1. 无需包装。直接使用基本类型值，不需要包装成对象。
2. 无需hash，无需比对Key对象，直接使用基本类型值排序索引和判断相等，无碰撞，无需调用hashCode方法，无需equals比较。
3. 更小的内部数组，相比于ArrayMap，无需单独的hash排序数组，内部只需等长的两个数组分别存放Key和Value
4. 延迟删除，对于移除操作，SparseArray并不是在每次remove操作直接移动数组元素，而是用一个删除标记将对应key的value标记为已删除，并标记需要回收，等待下次添加、扩容等需要移动数组元素的地方统一操作，进一步提升性能。
## LinkedHashMap
LinkedHashMap 继承自HashMap ，HashMap 一切重要的概念 LinkedHashMap 都是拥有的，不同点体现在：

- LinkedHashMap 内部维护了一个双向链表，解决了 HashMap 不能随时保持遍历顺序和插入顺序一致的问题
- LinkedHashMap 元素的访问顺序也提供了相关支持，也就是我们常说的 LRU（最近最少使用）原则。

这张图可以很好的体现 LinkedHashMap 中个各个元素关系：
![](https://cdn.nlark.com/yuque/0/2022/webp/26218568/1651199625495-3b415b74-9183-45a2-9a12-ef18cd1907e1.webp#averageHue=%23fbf9f8&clientId=u21fce30b-3e0c-4&from=paste&height=402&id=u6a512295&originHeight=966&originWidth=1174&originalType=url&ratio=1&rotation=0&showTitle=false&status=done&style=none&taskId=u5b841399-e64c-4665-a297-2e64e974d51&title=&width=489)
图片中红黄箭头代表元素添加顺序，蓝箭头代表单链表各个元素的存储顺序。head 表示双向链表头部，tail 代表双向链表尾部。
HashMap 中的 Node 节点只有 next 指针，对于双向链表而言只有 next 指针是不够的，所以 LinkedHashMap 对于 Node 节点进行了拓展：
```java
static class Entry<K,V> extends HashMap.Node<K,V> {
   Entry<K,V> before, after;
   Entry(int hash, K key, V value, Node<K,V> next) {
       super(hash, key, value, next);
   }
}
```
LinkedHashMap 基本存储单元 Entry<K,V> 继承自 HashMap.Node<K,V>,并在此基础上添加了 **before 和 after** 这两个指针变量。这 before 变量在每次添加元素的时候将会链接上一次添加的元素，而上一次添加的元素的 after 变量将指向该次添加的元素，来形成双向链接。

**LinkedHashMap 维护节点访问顺序**
```java
Map<String, String> map = new LinkedHashMap<>(16, 0.75f, true);
map.put("1", "1");
map.put("2", "2");
map.put("3", "3");
map.put("4", "4");

//new add
map.get("1");
map.put("2","2");//虽然值没变，但也算是访问
//3 4 1 2
```
accessOrder 默认false，基于插入顺序； true的话基于访问顺序，get或访问一个元素后，这个元素被加到最后。
实现LRUCache:
android中的实现跟glide中的稍有差异，但都是使用 LinkedHashMap，accessOrder 设为true后，要重写移除removeEldestEntry方法，不然默认袖是不删除数据的。
```java
public class LRUCache extends LinkedHashMap {
  public LRUCache(int maxSize) {
      super(maxSize, 0.75F, true);
      maxElements = maxSize;
  }

  //一定要重写移除方法,当达到条件后就会移除最老的元素
  protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
      return size() > maxElements;
  }

  protected int maxElements;
}
```
