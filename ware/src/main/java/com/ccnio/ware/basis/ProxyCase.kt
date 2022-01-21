package com.ccnio.ware.basis

import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by ccino on 2022/1/21.
 */
interface IUserApi {
    fun login(id: String, password: String)
}

private const val TAG_L = "Proxy"

/**
 * 我们写了一个接口未定义接口的实现，但通过代理就能产生一个该接口的对象，然后我们还能拦截它的方法。
 * 下面的代码实验是在 idea 中普通 java 工程实验的
 * 打印生成的实例是 jdk.proxy1.$Proxy0，是 jvm 帮我们创建了 $Proxy0 并实现了 IUserApi 接口。
 * 由于 $Proxy0.class 是在内存中的，所以我们需要写到本地。在调用动态代理的main方法中加上：
 * System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true") 即可（jdk1.8及以上）。
 * 然后用 jad 查看 class 文件,省略了一些方法。这样就知道为什么每调用一个函数会被拦截了
 */
/*
public final class $Proxy0 extends Proxy implements IUserApi {
    private static final Method m2;
    private static final Method m3;

    public $Proxy0(InvocationHandler param1) {
        super(var1);
    }

    public final String toString() {
         return (String)super.h.invoke(this, m2, (Object[])null);
    }

    public final Boolean login(String var1, String var2) {
         return (Boolean)super.h.invoke(this, m3, new Object[]{var1, var2});
    }

    static {
        m2 = Class.forName("java.lang.Object").getMethod("toString");
        m3 = Class.forName("com.demo.IUserApi").getMethod("login", Class.forName("java.lang.String"), Class.forName("java.lang.String"));
    }

    private static Lookup proxyClassLookup(Lookup var0) throws IllegalAccessException {
        if (var0.lookupClass() == Proxy.class && var0.hasFullPrivilegeAccess()) {
            return MethodHandles.lookup();
        } else {
            throw new IllegalAccessException(var0.toString());
        }
    }
}
代理类并不是在Java代码中定义的，而是在运行时根据我们在Java代码中的“指令”动态生成的。相比于静态代理， 动态代理的优势在于可以很方便的对代理类的函数进行统一的处理，而不用修改每个代理类的函数。
从 JVM 角度来说，动态代理是在运行时动态生成类字节码，并加载到 JVM 中的。

动态代理实现方式
JDK 动态代理有一个最致命的问题是只能代理实现了接口的类。

CGLIB是一个基于ASM的字节码生成库，它允许我们在运行时对字节码进行修改和动态生成。CGLIB 通过继承方式实现代理。
例如 Spring 中的 AOP 模块中：如果目标对象实现了接口，则默认采用 JDK 动态代理，否则采用 CGLIB 动态代理。
CGLIB是针对类来实现代理的，他的原理是对代理的目标类生成一个子类，并覆盖其中方法实现增强，因为底层是基于创建被代理类的一个子类，所以它避免了JDK动态代理类的缺陷。
通过“继承”可以继承父类所有的公开方法，然后可以重写这些方法，在重写时对这些方法增强，这就是CGLIB的思想。根据里氏代换原则（LSP），父类需要出现的地方，子类可以出现，所以CGLIB实现的代理也是可以被正常使用的。
但因为采用的是继承，所以不能对final修饰的类进行代理。final修饰的类不可继承。
 */
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