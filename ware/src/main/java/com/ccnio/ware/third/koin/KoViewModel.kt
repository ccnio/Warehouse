package com.ccnio.ware.third.koin

import android.util.Log
import androidx.lifecycle.ViewModel
import org.koin.java.KoinJavaComponent.inject

private const val TAG = "KoinViewModel"


/**
 * State Handle Injection#
Add a new property typed SavedStateHandle to your constructor to handle your ViewModel state:

class MyStateVM(val handle: SavedStateHandle, val myService : MyService) : ViewModel()
In Koin module, just resolve it with get() or with parameters:

viewModel { MyStateVM(get(), get()) }
// or
viewModel { params -> MyStateVM(params.get(), get()) }
To inject a state ViewModel in a Activity,Fragment use:

by stateViewModel() - lazy delegate property to inject state ViewModel instance into a property
getStateViewModel() - directly get the state ViewModel instance
To inject a shared state ViewModel in a Fragment use:

by sharedStateViewModel() - lazy delegate property to inject state ViewModel instance into a property
getSharedStateViewModel() - directly get the state ViewModel instance
Bundle Injection#
Passing SavedStateHandle to your ViewModel can also help you pass Bundle arguments. Just declare your ViewModel:

class MyStateVM(val handle: SavedStateHandle, val myService : MyService) : ViewModel()
// in your module
viewModel { params -> MyStateVM(params.get(), get()) }
Just call your ViewModel with following stateViewModel function and state parameter:

Copy
val myStateVM: MyStateVM by stateViewModel( state = { Bundle(...) })
 */
/**
 * repository 构造函数注入、属性注入
 */
class KoViewModel(private val repository: Repository) : ViewModel() {
    //构造函数注入
    private val repository2: Repository by inject(Repository::class.java)//属性注入
    private val weatherApi: WeatherApi by inject(WeatherApi::class.java)

    init {
        Log.d(TAG, "init: $repository, $repository2")
        Log.d(TAG, "init: ${weatherApi.getWeather()}")
    }

    fun getName(): String {
        return repository.queryName("git")
    }
}