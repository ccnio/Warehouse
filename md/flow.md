# SharedFlow

- 多次发送同一对象还是会发送出去:

```kotlin
//多次发送 user 对象，接收端依旧能收到
sharedFlow.emit(user)
```

- sharedFlow.distinctUntilChanged() 可以达到与 stateFlow 效果一致（包含 data class）

```kotlin
data class User(var name: String, val age: Int, val hobby: List<String> = emptyList())

val sharedFlow = MutableSharedFlow<User>()
sharedFlow.emit(User("mm", 12))//每次都创建 user 对象
sharedFlow.distinctUntilChanged().collect { Log.d(TAG, "sharedFlow distinctUntilChanged2: $it") }
```
