package com.ccnio.ware.third.koin

import android.app.Application
import com.google.gson.Gson
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * module { // module content } ：创建一个 Koin 模块。
 * factory { //definition } ：创建注入类的实例。
 * single { //definition } ：与 factory 功能一致，只不过创建的是单例。

bind() ：为注入类增加类型绑定，因为默认注入类只能对应一个类型。
binds() ：功能与上述 bind() 一致，一次性提供多个类型绑定。
scope { // scope group } ：为下述 scoped 定义一个合理的组，作用是控制注入类的生命周期。
scoped { //definition } ：与上述 scope 配合使用，定义内放注入类的实例，表示只在 scope 的范围内存在。
named() ：如果遇到相同类型需要两个或以上的注入，可以通过这个函数给注入进行命名，然后在注入处只要指定好名称即可获取正确的注入。
 */
val vmModules = module {
    viewModelOf(::KoViewModel)
}

val apiModule = module {
    factoryOf(::Repository)//等价于直接 new 一个对象
    factoryOf(::Repository2)//等价于直接 new 一个对象
    singleOf(::Gson)//单例

    single { ArrayList<Int>().apply { add(2) } }
    single(named("ints")) {
        ArrayList<Int>().apply {
            add(3)
            add(4)
        }
    }

    //绑定接口实现
    singleOf(::WeatherApiImplCN) { bind<WeatherApi>() }
    //同一对象同时注入多个接口实现
    factory(named<GasEngine>()) { GasEngine() }//方式一
    factory(named("electric")) { ElectricEngine() }//方式二

//    interface LetterClassifier
//    interface NumberClassifier
//    class Classifier() : LetterClassifier, NumberClassifier
//    single { Classifier() } binds arrayOf(LetterClassifier::class, NumberClassifier::class)


    //不同情形的构造函数
    factoryOf(::User)//User(val name: String) 使用时必须传参
    factory { User2(get()) } //get 方法会自动查找注入对象
    factory { (driver: Driver) -> Car(driver) }//由外部传参 or
    //factory { params -> Car(params.get()) }

    //context 获取、注入
    factoryOf(::AppData)
    //factory { MyPresenter(androidContext().resources.getString(R.string.mystring)) }

    //多构造函数对象的注入
    factory(named("nameAnum")) { NormalData("曹老板", 12) }
    factory(named("app")) { NormalData(get<Application>()) }
    factory(named("appData")) { NormalData(get<AppData>()) }

    //fragment 比较麻烦
    factory { KoinFragment(get()) }

}

val appModules = listOf(vmModules, apiModule)