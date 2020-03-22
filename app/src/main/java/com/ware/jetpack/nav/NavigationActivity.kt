package com.ware.jetpack.nav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ware.R

/**
 * 1. navigation的xml文件管理fragment跳转意图
 * 2. Host 布局中的Fragment name 必须指定为android:name="androidx.navigation.fragment.NavHostFragment"
 * defaultNavHost 表示是否拦截返回键，默认为false, 假如页面处于第二个fragment，这是按下返回键，会退出当前activity而不是回到上一个fragment。true则反之
 * 3. 切换fragment 从FragmentA切换到FragmentB,在fragmentA中调用: NavHostFragment.findNavController(fragmentA).navigate(R.id.action_fragmentA_to_fragmentB)
 * 假如是通过点击某个按钮进行切换的话，也可以通过以下方式,id为目标fragment的id:jumpBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.fragment_third));
 * 4. 返回时view会重建，也就是会走onCreateView； 跳到下一个时view会销毁，也就是会走onDestroyView
 */
class NavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
    }
}
