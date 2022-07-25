package com.ccnio.ware.jetpack.nav

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

private const val TAG = "NavActivity"

/**
 * https://mp.weixin.qq.com/s/1URoDU0zgoYlSQM8zYqx9w
 * 生命周期：
 * 1.fragment共享一个activity
 * 2.First跳转Sec再点击返回键(First没调 onDestroy，但不显示时会调onDestroyView;)：
NavSecondFragment: onCreate:
NavFirstFragment: onDestroyView:
NavFirstFragment: onCreateView:
NavSecondFragment: onDestroyView:
NavSecondFragment: onDestroy:(从栈中消失时会 destroy)
 * 3. 返回
 * 返回调用也不会走Fragment的onDestroy
 * NavController 有 navigateUp() 和 popBackStack() 都可以返回上一级，有什么区别？
 * popBackStack() 如果当前的返回栈是空的就会报错，navigateUp() 则不会
 * popBackStack(R.id.nav_second_frag, true)//true时会跳转到sec的前一个界面，即first、
 *
 * popUpTo 和 popUpToInclusive
设置popUpTo和popUpToInclusive在导航过程中弹出页面：直到弹出到某个界面
假设有A,B,C 3个页面，跳转顺序是 A to B，B to C，C to A。依次执行几次跳转后，栈中的顺序是A>B>C>A>B>C>A。
此时如果用户按返回键，会发现反复出现重复的页面，此时用户的预期应该是在A页面点击返回，应该退出应用。
此时就需要在C到A的action中设置popUpTo="@id/a". 这样在C跳转A的过程中会把B,C出栈。但是还会保留上一个A的实例，加上新创建的这个A的实例，就会出现2个A的实例.
此时就需要设置 popUpToInclusive=true. 这个配置会把上一个页面的实例也弹出栈，只保留新建的实例。
下面再分析一下设置成false的场景。跳转顺序A to B，B to C. 此时在B跳C的action中设置 popUpTo=“@id/a”, popUpToInclusive=false. 跳到C后，此时栈中的顺序是AC。B被出栈了。
如果设置popUpToInclusive=true. 此时栈中的保留的就是C。AB都被出栈了。
<action
android:id="@+id/c_to_a"
app:popUpTo="@id/a"
app:destination="@id/a"
app:popUpToInclusive="true" />
 *
 * 4. 动态加载
 * 实现时要在NavHostFragment去除 app:navGraph="@navigation/nav_simple"，graph的start可以不用个性
 * //1. 动态加载 nav graph 2. 动态设置 graph 中的启动界面
 *
 *
 * 5. 所有坑的中心 https://mp.weixin.qq.com/s/XdPYjC_6NP-0rmp8Y3hNpg
 * Navigation 相关的坑，都有个中心。一般情况下，Fragment 就是一个 View，View 的生命周期就是 Fragment 的生命周期，
 * 但是在 Navigation 的架构下，Fragment 的生命周期和 View 的生命周期是不一样的。当 navigate 到新的 UI，被覆盖的 UI，View 被销毁，
 * 但是保留了 fragment 实例（未被 destroy），当这个 fragment 被 resume 的时候，View 会被重新创建。这是“罪恶”之源。
 */
class NavActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        Log.d(TAG, "onCreate: navActivity = $this")
        /*val navController: NavController = Navigation.findNavController(this, R.id.frag_nav_simple)
        //1. 动态加载 nav graph
        val navGraph: NavGraph = navController.navInflater.inflate(R.navigation.nav_simple)
        //2. 动态设置 graph 中的启动界面
        navGraph.setStartDestination(R.id.nav_second_frag)
        //3. 绑定启动
        navController.setGraph(
            navGraph,
            SecondFragmentArgs.Builder(true, "from dynamic").build().toBundle()
        )*/
    }
}