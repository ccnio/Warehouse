package com.ware.kt

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_kt.*
import kotlinx.coroutines.*
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private const val TAG = "KtActivity"

/**
 * # 委托模式是有两个对象参与处理同一个请求，接受请求的对象将请求委托给另一个对象来处理。 https://www.runoob.com/kotlin/kotlin-delegated.html
 * ## 类委托 即一个类中定义的方法实际是调用另一个类的对象的方法来实现的。
 * a. CoroutineScope by MainScope(): KtActivity的协程域CoroutineScope(接口)通过MainScope实现
 * b. 见 delegateClass()
 * ## 属性委托 一个类的某个属性值不是在类中直接进行定义，而是将其托付给一个代理类，从而实现对该类的属性统一管理。
 * a. 属性委托语法格式：val/var <属性名>: <类型> by <表达式>
 * b. 表达式：委托代理类; 属性的 get() 方法(以及set() 方法)将被委托给这个[对象]的 getValue() 和 setValue() 方法。属性委托不必实现任何接口, 但必须提供 getValue() 函数(对于 var属性,还需要 setValue() 函数)。
 * ## 标准委托 Kotlin 的标准库中已经内置了很多工厂方法来实现[属性]的委托
 * a. by lazy. lazy() 是一个[函数], 接受一个Lambda表达式作为参数, 返回一个 Lazy <T> 实例的函数，返回的实例可以作为实现延迟属性的委托： 第一次调用 get() 会执行已传递给 lazy() 的 lamda 表达式并记录结果，
 *    后续调用 get() 只是返回记录的结果。默认 LazyThreadSafetyMode.SYNCHRONIZED 线程安全.
 * b. by Delegates.observable. 用于实现观察者模式,Delegates.observable() 函数接受两个参数: 第一个是初始化值, 第二个是属性值变化事件的响应器(handler)。
 * c. by Delegates.vetoable. vetoable 与 observable一样，可以观察属性值的变化，不同的是，vetoable可以通过处理器函数来决定属性值是否生效。
 * d. 局部委托属性 可以将局部变量声明为委托属性,变量只会在第一次访问时计算
 * e. Kotlin标准库中提供了[ReadOnlyProperty] [ReadWriteProperty],方便Delegate的set/get写法,delegate只需要实现对应的接口即可 var-> ReadWrite,val->ReadOnly
 */
class KtActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope by MainScope() {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.delegateClassView -> delegateClass()
            R.id.delegateFieldView -> delegateField()
//            R.id.mCoroutineModeView -> coroutineMode()
//            R.id.mCoroutineDispatcherView -> coroutineDispatcher()
//            R.id.mCoroutineAsyncView -> coroutineAsync()
            R.id.equalView -> equalOp()
        }
    }

    private var delegateString: String by Delegate()
    private val viewModel by lazy {
        Log.d(TAG, "computed!")  // 第一次调用输出，第二次调用不执行
        ArrayList<String>()
    }
    private var name: String by Delegates.observable("初始值", { property, oldValue, newValue -> Log.d(TAG, "Delegates.observable: property = $property, old = $oldValue, new = $newValue") })
    private var vetoableProp: Int by Delegates.vetoable(0) { _, oldValue, newValue ->
        // 如果新的值大于旧值，则生效
        newValue > oldValue
    }

    class Delegate {
        operator fun getValue(thisRef: KtActivity, property: KProperty<*>): String {
            Log.d(TAG, "$thisRef, 这里委托了 ${property.name} 属性")
            return "origin str"
        }

        operator fun setValue(thisRef: KtActivity, property: KProperty<*>, value: String) {
            Log.d(TAG, "$thisRef 的 ${property.name} 属性赋值为 $value")
        }
    }

    class Delegate2 : ReadWriteProperty<KtActivity, String> {
        override fun getValue(thisRef: KtActivity, property: KProperty<*>): String {
            TODO("Not yet implemented")
        }

        override fun setValue(thisRef: KtActivity, property: KProperty<*>, value: String) {
            TODO("Not yet implemented")
        }
    }

    private fun delegateField() {
        //属性委托
        Log.d(TAG, "delegateField: $delegateString") // 访问该属性，调用 Delegate getValue() 函数
        delegateString = "modified str"   // 调用 Delegate setValue() 函数
        Log.d(TAG, "delegateField: $delegateString") //注意 这里打印的还是origin str, 因为访问的是getValue()返回值

        println("vetoableProp=$vetoableProp")
        vetoableProp = 10
        println("vetoableProp=$vetoableProp")
        vetoableProp = 5
        println("vetoableProp=$vetoableProp")//10 -> 5 的赋值没有生效。


        //局部委托属性
        /* fun example(computeFoo: () -> Foo) {
                val memoizedFoo by lazy(computeFoo)

                if (someCondition && memoizedFoo.isValid()) {
                    memoizedFoo.doSomething()
                }
           }
           memoizedFoo 变量只会在第一次访问时计算。 如果 someCondition 失败，那么该变量根本不会计算。
         */
    }

    interface Base {
        fun print() // 创建接口
    }

    class BaseImpl(val x: Int) : Base { // 实现此接口的被委托的类
        override fun print() {
            print(x)
        }
    }

    class Derived(b: Base) : Base by b  // 通过关键字 by 建立委托类
//    class Derived() : Base by BaseImpl(10) // 通过关键字 by 建立委托类

    private fun delegateClass() {
        val b = BaseImpl(10)
        Derived(b).print() // 输出 10
//           在 Derived 声明中，by 子句表示，将 b 保存在 Derived 的对象实例内部，而且编译器将会生成继承自 Base 接口的所有方法, 并将调用转发给 b。
    }

    /**
     * Note that the compiler only uses the properties defined inside the primary constructor for the automatically generated functions.
     * To exclude a property from the generated implementations, declare it inside the class body.our example has to look like this:
     * data class Product( val name: String, val manufacturer: String) {
     *      val id: Int
     * }
     */
    private fun equalOp() {
        val user = User("ab", 23)
        val user2 = User("ab", 24)
        Log.d(TAG, "user == user2: ${user == user2}") //true   结构比较
        Log.d(TAG, "user equals user2: ${user.equals(user2)}") //true  结构比较
        Log.d(TAG, "user === user2: ${user === user2}") //false 引用比
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kt)
        delegateClassView.setOnClickListener(this)
        delegateFieldView.setOnClickListener(this)
        mCoroutineAsyncView.setOnClickListener(this)
        equalView.setOnClickListener(this)
        launch { }
    }

}
