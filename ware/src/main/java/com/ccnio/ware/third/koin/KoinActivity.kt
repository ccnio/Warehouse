package com.ccnio.ware.third.koin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccino.business.TimeData
import com.ccnio.ware.R
import com.google.gson.Gson
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

private const val TAG = "KoinActivity"

class KoinActivity : AppCompatActivity() {
    /**
     * get() ：通过解析组件依赖，注入类实例。
     * inject() ：与 get() 功能一致，都是提供类注入，但 inject 是懒加载。
     */
    //ViewModel
    private val lazyVM: KoViewModel by viewModel() //lazy load
//    private val weatherViewModel by sharedViewModel<WeatherViewModel>()//fragment share activity

    //带参构造函数
    private val user: User by inject { parametersOf("cc") }
    private val user2: User2 by inject()
    private val car by inject<Car> { parametersOf(driver) }//构造函数传参

    //repository 是普通类，内部也使用了注入
    private val repository by inject<Repository>()
    private val repository2 by inject<Repository2>()

    private val gson by inject<Gson>() //single
    private val gson2 by inject<Gson>()
    private val driver = Driver()

    //局限：无法屏蔽实现类
    private var gasEngine = get<GasEngine>(named<GasEngine>())//方式一
    private var electricEngine = get<ElectricEngine>(named("electric"))//方式二

    //多构造函数实例化
    private val norData1 by inject<NormalData>(named("nameAnum"))//限定符1
    private val norData2: NormalData by inject(named("app"))//限定符2

    private val otherLibData by inject<TimeData>()

    private val list: ArrayList<Int> by inject()
    private val list2: ArrayList<Int> by inject(named("ints"))

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()

        super.onCreate(savedInstanceState)
        val directVM = getViewModel<KoViewModel>()//directly load
//        val repository2 = get<Repository>()
        Log.d(TAG, "lazyVM = $lazyVM, dirVM = $directVM")
        Log.d(TAG, "user = ${user.name}, ${user2.appData}")
        Log.d(TAG, "new obj: ${repository.queryName("hell")}, ${repository2.queryName("hell")}")
        Log.d(TAG, "out param: driver = $driver,driver2= ${car.driver}")//driv1 = dirv2
        Log.d(TAG, "engine $gasEngine, $electricEngine")
        Log.d(TAG, "normalData $norData1, $norData2, $otherLibData")
        Log.d(TAG, "list $list, $list2")

        setContentView(R.layout.activity_koin)

//        supportFragmentManager.beginTransaction()
//            .replace<KoinFragment>(R.id.fragment_view)
//            .commit()

        supportFragmentManager.beginTransaction()
            .replace<KoinFragment>(
                containerViewId = R.id.fragment_view,
                args = Bundle(),
                tag = "tag"
            ).commit()
    }
}
