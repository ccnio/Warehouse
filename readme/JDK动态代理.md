## 代理模式
代理模式的特征是代理类与委托类有同样的接口。代理类与委托类之间通常会存在关联关系，在访问实际对象时，是通过代理对象来访问的，代理对象可以附加额外操作。

代理	分为静态代理和动态代理两种：

- 静态代理：在编译时就已经将接口，被代理类，代理类等确定下来。在程序运行之前，代理类的.class文件就已经生成。
- 动态代理：**程序运行期间根据需要动态的创建代理类及其实例**来完成具体的功能。
## 静态代理

```java
public interface Movie { // 接口
    void play();
}

public class RealMovie implements Movie { // 被代理类
    @Override
    public void play() {
        System.out.println("您正在观看电影 《肖申克的救赎》");
    }
}

public class Cinema implements Movie {//代理类，同被代理类一个接口
    RealMovie movie;
    public Cinema(RealMovie movie) {
        super();
        this.movie = movie;
    }
    
    @Override
    public void play() {
        guanggao(true); // 代理类的增强处理
        movie.play(); // 代理类把具体业务委托给目标类，并没有直接实现
        guanggao(false); // 代理类的增强处理
    }
    
    public void guanggao(boolean isStart){
        if ( isStart ) {
            System.out.println("电影马上开始了，爆米花、可乐、口香糖9.8折，快来买啊！");
        } else {
            System.out.println("电影马上结束了，爆米花、可乐、口香糖9.8折，买回家吃吧！");
        }
    }
}

public class ProxyTest {
    public static void main(String[] args) {
        RealMovie realmovie = new RealMovie();
        Movie movie = new Cinema(realmovie);//将被代理对象传递进去
        movie.play();
    }
}
```

Cinema(代理类)还对“播放电影”这个行为进行进一步增强，即增加了额外的处理，同时不影响RealMovie(目标类)的实现。值得注意的是，代理类和被代理类应该**共同实现一个接口**，或者是**共同继承某个类**。
## JDK动态代理
### 使用

```java
public interface Subject {
    public void doSomething();
}

public class RealSubject implements Subject {
    public void doSomething() {
        System.out.println("call doSomething()");
    }
}

/**
* Title: InvocationHandler 的实现 
* 每个代理的实例都有一个与之关联的 InvocationHandler实现类
* 如果代理的方法被调用，那么代理便会通知和转发给内部的 InvocationHandler 实现类，由它调用invoke()去处理。
*/
public class ProxyHandler implements InvocationHandler {
    private Object proxied; // 被代理对象
    
    public ProxyHandler(Object proxied) {
        this.proxied = proxied;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 在转调具体目标对象之前，可以执行一些功能处理
        System.out.println("前置增强处理： yoyoyo...");
        // 转调具体目标对象的方法(三要素：实例对象 + 实例方法 + 实例方法的参数)
        Object obj = method.invoke(proxied, args);
        // 在转调具体目标对象之后，可以执行一些功能处理
        System.out.println("后置增强处理：hahaha...");
        return obj;
    }
}
```

在实现了InvocationHandler接口后，我们就可以创建代理对象了。在Java的动态代理机制中，我们使用Proxy类的静态方法newProxyInstance创建代理对象，如下：

```java
public class Test {
    public static void main(String args[]) {
        // 真实对象real
        Subject real = new RealSubject();
        // 生成real的代理对象
        Subject proxySubject = (Subject) Proxy.newProxyInstance(
            Subject.class.getClassLoader(), new Class[] { Subject.class },
            new ProxyHandler(real));
        
        proxySubject.doSomething();
        System.out.println("代理对象的类型 ： " + proxySubject.getClass().getName());
        System.out.println("代理对象所在类的父类型 ： " + proxySubject.getClass().getGenericSuperclass());
    }
}/** Output
前置增强处理： yoyoyo...
call doSomething()
后置增强处理：hahaha...
代理对象的类型 ： com.sun.proxy.$Proxy0
代理对象所在类的父类型 ： class java.lang.reflect.Proxy
 **/
```
到此为止，我们给出了完整的基于JDK动态代理机制的代理模式的实现。我们从上面的实例中可以看到:

- JDK 动态代理有一个最致命的问题是只能代理实现了接口的类。
- 代理对象proxySubject的类型为”com.sun.proxy.$Proxy0”，继承自java.lang.reflect.Proxy类。
- 接口的实现类 RealSubject 不是必要的，看具体的使用场景，下面这个例子就未有接口的实现类。
### 实现原理
我们写了一个接口**未定义接口的实现**，**但通过代理就能产生一个该接口的对象**，然后我们还能拦截它的方法。
```java
interface IUserApi {
   fun login(id: String, password: String): Boolean
}

class ProxyCase {
    fun dynamicProxy() {
        val proxyInstance = Proxy.newProxyInstance(IUserApi::class.java.classLoader, arrayOf(IUserApi::class.java), object : InvocationHandler {
            override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
                // 此处打印 proxy 会报 StackOverflowError
                // 因为打印 $proxy/$proxyInstance 就是调用代理对象的 toString() 方法，所以会一直调用 invoke()
                Log.d(TAG_L, "proxy invoke: proxy = , method = $method, args = $args")
                return getBasicValue(method.returnType)
            }
        }) as IUserApi
        val ret = proxyInstance.login("cc", "123") //proxy invoke: proxy = , method = public java.lang.String java.lang.Object.toString(), args = null
        Log.d(TAG_L, "dynamicProxy: instance = ${proxyInstance::class.java}, ret = $ret") //$Proxy1 Unit
    }

    fun getBasicValue(clazz: Class<*>): Any? {
        return when (clazz) {
            Int::class.java, Int::class.javaPrimitiveType -> 0
            Boolean::class.java, Boolean::class.javaPrimitiveType -> false
            Float::class.java, Float::class.javaPrimitiveType -> 0f
            Unit::class.java -> Unit
            else -> null
        }
    }
}
```

打印生成的实例是 jdk.proxy1.$Proxy0，是 jvm 帮我们创建了 $Proxy0 并实现了 IUserApi 接口。由于 $Proxy0.class 是在内存中的，所以我们需要写到本地。在调用动态代理的main方法中加上：
System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true") 即可（jdk1.8及以上）。

然后用 jad 查看 class 文件,省略了一些方法。这样就知道为什么每调用一个函数会被拦截了
```java
public final class $Proxy0 extends Proxy implements IUserApi {
    private static final Method m2;//toString
    private static final Method m3;//login
    
    public $Proxy0(InvocationHandler param1) {
        super(var1);
    }
    //h->InvocationHandler
    public final String toString() {
        return (String)super.h.invoke(this, m2, (Object[])null);
    }
    //h->InvocationHandler; this->$Proxy0
    public final Boolean login(String var1, String var2) {
        return (Boolean)super.h.invoke(this, m3, new Object[]{var1, var2});
    }
    
    static {
        m2 = Class.forName("java.lang.Object").getMethod("toString");
        m3 = Class.forName("com.demo.IUserApi").getMethod("login", Class.forName("java.lang.String"), Class.forName("java.lang.String"));
    }
}
```

**代理类并不是在Java代码中定义的，而是在运行时根据我们在Java代码中的“指令”动态生成的**。从 JVM 角度来说，动态代理是在**运行时**动态生成类字节码，并加载到 JVM 中的。

**代理类的生成与创建**
Proxy类中的newProxyInstance这个函数入手
```java
public static Object newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h){
    //所有被实现的业务接口
    final Class<?>[] intfs = interfaces.clone();
    //寻找或生成指定的代理类
    Class<?> cl = getProxyClass0(loader, intfs);
    //通过反射类中的Constructor获取其所有构造方法
    final Constructor<?> cons = cl.getConstructor(constructorParams);
    //通过Constructor返回代理类的实例
    return cons.newInstance(new Object[]{h});
}
```

先看看这三个参数loader，interfaces，h；ClassLoader是一个抽象类，作用是将字节码文件加载进虚拟机并生成相应的class（注意是小写的），这里得到的loader是其子类AppClassLoader（负责加载应用层字节码）的一个实例，interfaces就是被实现的那些业务接口，h是InvocationHandler接口的实例，具体代理操作就被放在这个InvocationHandler的invoke函数中。

接下来看看生成业务代理类的getProxyClass0（loader，intfs）的实现
```java
private static Class<?> getProxyClass0(ClassLoader loader, Class<?>... interfaces) {
    // proxyClassCache会缓存所有的代理类，如果缓存中有这个业务代理类，则会从缓存中取出，否则从ProxyClassFactory中生成
    return proxyClassCache.get(loader, interfaces);
}
// ProxyClassFactory是Proxy中的内部类，缓存中如果没有这个代理类则会调用ProxyClassFactory中的apply方法生成

private static final class ProxyClassFactory
    implements BiFunction<ClassLoader, Class<?>[], Class<?>> {
    // 这两个常量就是代理类名字的由来
    private static final String proxyClassNamePrefix = "$Proxy";
    private static final AtomicLong nextUniqueNumber = new AtomicLong();
    
    @Override
    public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {
        //这里就生成了我们要的字节码形式的代理类
        byte[] proxyClassFile = ProxyGenerator.generateProxyClass(proxyName, interfaces, accessFlags);
        //defineClass0是个native方法        
        return defineClass0(loader, proxyName, proxyClassFile, 0, proxyClassFile.length);
    }
    
}
```

到这里业务代理类就生成了，我们再回到newProxyInstance方法中，它会将InvocationHandler的实例h传入这个业务代理类实例中: return cons.newInstance(new Object[]{h});
### 案例

1. 注意 InvocationHandler各参数与返回值类型
2. invoke的返回值需要根据具体类型去适配

下面的demo，是如下场景的使用：
远程api的准备需要一定的时间，在api未准备妥当调用各种服务时是无效的。想法是，把这些无效调用先缓存起来，等api准备好后再把这些无效调用重新走一遍。
```kotlin
private var mRemoteCall: Api? = null
private val api: Api
private val awaitCalls: Deque<AwaitCall> = LinkedList() // 缓存调用信息

init {
    val classLoader = Api::class.java.classLoader
    api = Proxy.newProxyInstance(classLoader, arrayOf(Api::class.java), ApiCallInvocationHandler()) as Api
    
    if (WearServiceConnector.get().isConnected) {
        setApi()
    }
    WearServiceConnector.get().addServiceConnectListener(mac, object : WearServiceConnector.ServiceListener {
        override fun onServiceConnected() {
            LogUtil.d(TAG, "onServiceConnected: ")
            setHuamiApi()
            flushApiCallDeque()
        }
        
        override fun onServiceDisconnected() {
            awaitCalls.clear()
        }
    })
}

private fun flushApiCallDeque() {
    while (true) {
        val call = awaitCalls.pollFirst() ?: break
        LogUtil.d(TAG, "flushApiCallDeque: method = ${call.method?.name}")
        call.method.invoke(mRemoteCall, *(call.args ?: arrayOfNulls<Any>(0)))
    }
}


private fun setApi() {
    mRemoteCall = WearServiceConnector.get().getRemoteApi(mac)
    Log.d(TAG, "setApi: $mRemoteCall")
}

private inner class ApiCallInvocationHandler : InvocationHandler {
    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        if (WearServiceConnector.get().isConnected && mRemoteCall != null) {
            LogUtil.d(TAG, "ApiCallInvocationHandler invoke-> method = " + method.name)
            return method.invoke(mRemoteCall, *(args ?: emptyArray()))
        } else {
            LogUtil.d(TAG, "ApiCallInvocationHandler not connect: method = " + method.name)
            val awaitCall = AwaitCall(0, method, args)
            awaitCalls.add(awaitCall)
        }
        LogUtil.d(TAG, "invoke: ret type = ${method.returnType}; Int = ${Int::class.java}")
        return when (method.returnType) {
            String::class.java -> ""
            Int::class.java -> -1
            Boolean::class.java -> false
            else -> null
        }
    }
}

fun setAuthKey(var1: String) {
    api.setAuthKey(var1)
}
```
