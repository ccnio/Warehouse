package com.ware.jetpack;

import androidx.lifecycle.ViewModel;

/**
 * ViewModel用来存储和管理UI相关的数据，可将一个Activity或Fragment组件相关的数据逻辑抽象出来，并能适配组件的生命周期，如当屏幕旋转Activity重建后，ViewModel中的数据依然有效。
 * 引入ViewModel之前，存在如下几个问题：
 * <p>
 * 1. 通常 UI controllers (Activity、Fragment) 的生命周期，由系统响应用户交互或者重建组件，用户无法操控。当组件被销毁并重建后，原来组件相关的数据也会丢失，
 * 数据类型简单，不大，可以通过onSaveInstanceState()存储数据。但如果是大量数据，不方便序列化及反序列化，
 * 2. UI controllers经常会发送很多异步请求，有可能会出现UI组件已销毁，而请求还未返回的情况，因此UI controllers需要做额外的工作以防止内存泄露。
 * <p>
 * ViewModel使用方式
 * Android架构组件提供了一个ViewModel帮助类来为UI controllers负责数据相关的工作，当配置变化组件销毁重建时，这些数据仍然可以保留。当新的组件重建后，可以立即使用之前保留的数据。下面的示例代码维护了一个User列表数据：
 * <p>
 * public class MyViewModel extends ViewModel {
 * private MutableLiveData<List<User>> users;
 * public LiveData<List<User>> getUsers() {
 * if (users == null) {
 * users = new MutableLiveData<List<Users>>();
 * loadUsers();
 * }
 * return users;
 * }
 * <p>
 * private void loadUsers() {
 * // Do an asyncronous operation to fetch users.
 * }
 * }

 * 在Activity中可以按如下方式使用ViewModel：
 * <p>
 * public class MyActivity extends AppCompatActivity {
 * public void onCreate(Bundle savedInstanceState) {
 * // Create a ViewModel the first time the system calls an activity's onCreate() method.
 * // Re-created activities receive the same MyViewModel instance created by the first activity.
 * <p>
 * MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
 * model.getUsers().observe(this, users -> {
 * // update UI
 * });
 * }
 * }
 *
 * 如果Activity销毁重建，可以立即得到一个相同的MyViewModel实例，它是由之前销毁的Activity创建的。当宿主Activity最终销毁后，系统会调用ViewModel的onCleared()方法来释放资源。
 * <p>
 * 由上面的例子可以知道，ViewModel的生命周期比特定view或LifecycleOwner（如Activity实现了LifecycleOwner接口）要长，因此ViewModel不要引用view、Lifecycle或其他引用到Activity上下文的对象。
 * <p>
 * ViewModel中可以包含LifecycleObserver，如LiveData对象。如果ViewModel需要使用Application的上下文对象，则可以通过继承AndroidViewModel，并提供一个以Application为参数的构造函数。
 * <p>
 * ViewModel的生命周期
 * ViewModel的生命周期依赖于对应的Activity或Fragment的生命周期。通常会在Activity第一次onCreate()时创建ViewModel，ViewModel的生命周期一直持续到Activity最终销毁或Frament最终detached，期间由于屏幕旋转等配置变化引起的Activity销毁重建并不会导致ViewModel重建。借用官方示意图来解释一下：
 * <p>
 * 这里写图片描述
 * <p>
 * 上图左侧为Activity的生命周期过程，期间有一个旋转屏幕的操作；右侧则为ViewModel的生命周期过程。
 * <p>
 * 一般通过如下代码初始化ViewModel：
 * <p>
 * viewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
 * 1
 * this参数一般为Activity或Fragment，因此ViewModelProvider可以获取组件的生命周期。
 * <p>
 * Activity在生命周期中可能会触发多次onCreate()，而ViewModel则只会在第一次onCreate()时创建，然后直到最后Activity销毁。
 * <p>
 * Fragment之间分享数据
 * 日常开发中，一个Activity中可能会有多个Fragment，且他们需要进行交互。例如一个Fragment展示列表，另一个Fragment展示选中列表对应的详情信息，之前我们可能会利用宿主Activity并定义几个接口来实现Fragment之间的交互，另外还得考虑Fragment是否已经创建或显示的问题。
 * <p>
 * 上述痛点，可以使用ViewModel来解决，在Fragment之间可以共享ViewModel。示例代码如下：
 * <p>
 * public class SharedViewModel extends ViewModel {
 * private final MutableLiveData<Item> selected = new MutableLiveData<Item>();
 * <p>
 * public void select(Item item) {
 * selected.setValue(item);
 * }
 * <p>
 * public LiveData<Item> getSelected() {
 * return selected;
 * }
 * }
 * <p>
 * public class MasterFragment extends Fragment {
 * private SharedViewModel model;
 * public void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
 * itemSelector.setOnClickListener(item -> {
 * model.select(item);
 * });
 * }
 * }
 * <p>
 * public class DetailFragment extends Fragment {
 * public void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * SharedViewModel model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
 * model.getSelected().observe(this, { item ->
 * // Update the UI.
 * });
 * }
 * }
 * <p>
 * 32
 * 注意到上面两个Fragment都用到了如下代码来获取ViewModel：
 * <p>
 * SharedViewModel model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
 * <p>
 * getActivity()返回的是同一个宿主Activity，因此两个Fragment之间返回的是同一个SharedViewModel对象。
 * <p>
 * Fragment间共享ViewModel的优点有：
 * <p>
 * 宿主Activity不需要做任何事情，也不需要关心Fragment间交互的内容。
 * Fragment只需要了解ViewModel的实现，而无需了解通信目标Fragment。即使一个Fragment已经销毁了，另一个Fragment也能正常工作。
 * 每一个Fragment有自己的生命周期，并不受其他Fragment影响。
 * Fragment之间解耦。
 * 总结
 * ViewModel职责是为Activity或Fragment管理、请求数据，当然具体数据请求逻辑不应该写在ViewModel中，否则ViewModel的职责会变得太重，此处需要一个引入一个Repository，负责数据请求相关工作。具体请参考 Android架构组件。
 * ViewModel可以用于Activity内不同Fragment的交互，也可以用作Fragment之间一种解耦方式。
 * ViewModel也可以负责处理部分Activity/Fragment与应用其他模块的交互。
 * ViewModel生命周期（以Activity为例）起始于Activity第一次onCreate()，结束于Activity最终finish时。
 */
public class MyViewModel extends ViewModel {
}
