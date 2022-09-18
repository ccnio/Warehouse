# ViewModel

## constructor vm

```kotlin
class SimpleViewModel(val id: String, val service: SimpleService) : ViewModel()

viewModelOf(::SimpleViewModel)// { (id: String) -> SimpleViewModel(id, get()) }
viewModelOf(::SimpleViewModel) { named("vm1") } //{ (id: String) -> SimpleViewModel(id, get()) }
viewModel(named("vm2")) { (id: String) -> SimpleViewModel(id, get()) }

val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }
val vm1: SimpleViewModel by viewModel(named("vm1")) { parametersOf("vm1") }
val vm2: SimpleViewModel by viewModel(named("vm2")) { parametersOf("vm2") }
```

## shared vm

```kotlin
val shared: SimpleViewModel by sharedViewModel()// sharedViewModel { parametersOf(ID) }
```

## saved vm

```kotlin
class SavedStateViewModel(
    val handle: SavedStateHandle,
    val id: String,
    val service: SimpleService
) : ViewModel() {
    init {
        val get = handle.get<String>(id)
        println("handle: $get")
        handle.set(id, UUID.randomUUID().toString())
    }
}

viewModelOf(::SavedStateViewModel)// { params -> SavedStateViewModel(get(), params.get(), get()) }// injected params
viewModelOf(::SavedStateBundleViewModel)// { SavedStateBundleViewModel(get(), get()) }// injected params

val savedVm: SavedStateViewModel by stateViewModel { parametersOf("vm1") }
//val scopedSavedVm: SavedStateViewModel by viewModel(named("vm2")){ parametersOf("vm2") }
val state = Bundle().apply { putString("id", "vm1") }
val stateVM: SavedStateBundleViewModel by stateViewModel(state = { state })
```
