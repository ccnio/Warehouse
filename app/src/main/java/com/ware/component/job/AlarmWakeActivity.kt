package com.ware.component.job

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.component.BaseActivity
import com.ware.component.job.AlarmBroadcastReceiver.ACTION_SYNC
import kotlinx.android.synthetic.main.activity_alarm_wake.*

/**
 * # PendingIntent 的 reveiver 必须是在xml中静态声明的，否则不起作用
 *
 *
 * 在PendingIntent.java文件中，我们可以看到有如下几个比较常见的静态函数：
public static PendingIntent getActivity(Context context, int requestCode, Intent intent, int flags)
public static PendingIntent getBroadcast(Context context, int requestCode, Intent intent, int flags)
public static PendingIntent getService(Context context, int requestCode, Intent intent, int flags)
public static PendingIntent getActivities(Context context, int requestCode, Intent[] intents, int flags)
public static PendingIntent getActivities(Context context, int requestCode, Intent[] intents, int flags, Bundle options)

上面的getActivity()的意思其实是，获取一个PendingIntent对象，而且该对象日后激发时所做的事情是启动一个新activity。也就是说，当它异步激发时，会执行类似Context.startActivity()那样的动作。相应地，getBroadcast()和getService()所获取的PendingIntent对象在激发时，会分别执行类似Context..sendBroadcast()和Context.startService()这样的动作。至于最后两个getActivities()，用得比较少，激发时可以启动几个activity。

（1）intent就是需要启动的Activity、Service、BroadCastReceiver的intent。

（2）Flags的类型：
FLAG_ONE_SHOT：得到的pi只能使用一次，第二次使用该pi时报错
FLAG_NO_CREATE： 当pi不存在时，不创建，返回null
FLAG_CANCEL_CURRENT： 每次都创建一个新的pi
FLAG_UPDATE_CURRENT： 不存在时就创建，创建好了以后就一直用它，每次使用时都会更新pi的数据(使用较多)

在AlarmManager中的使用

Intent intent = new Intent("action", null, context, serviceClass);
//adnroid 8.0后台启动就要用PendingIntent.getForegroundService(),不想麻烦可以用receiver
PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
AlarmManager manager = (AlarmManager)probe.getSystemService(Context.ALARM_SERVICE);
manager.set(AlarmManager.RTC_WAKEUP, milis, pi);

在NotificationManager中的使用

Intent intent = new Intent();
intent.setAction("myaction");
PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
Notification n = new Notification();
n.icon = R.drawable.ic_launcher;
n.when = System.currentTimeMillis();
n.setLatestEventInfo(this,"this is title", "this is a message", pi);
nm.notify(0, n);

两个重要方法：
send()方法是用，调用PendingIntent.send()会启动包装的Intent(如启动service，activity)
cancel()方法是为了解除PendingIntent和被包装的Intent之间的关联，此时如果再调用send()方法，则会抛出CanceledException异常

PendingIntent和Intent的区别：
PendingIntent就是一个Intent的描述，我们可以把这个描述交给别的程序，别的程序根据这个描述在后面的别的时间做你安排做的事情
换种说法Intent 字面意思是意图，即我们的目的，我们想要做的事情，在activity中，我们可以立即执行它
PendingIntent 相当于对intent执行了包装，我们不一定一定要马上执行它，我们将其包装后，传递给其他activity或application
这时，获取到PendingIntent 的application 能够根据里面的intent 来得知发出者的意图，选择拦击或者继续传递或者执行。

 */
private const val TAG_L = "AlarmWakeActivity"

class AlarmWakeActivity : BaseActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mAlarmView -> registerTimerToAlarmManager(this, System.currentTimeMillis() + 1000)
            R.id.startView -> createTwoAlarm()
            R.id.cancelView -> cancelAlarm()

        }
    }

    /**
     * 同一个action前一个会被后一个覆盖
     */
    private fun createTwoAlarm() {//3s后执行
        Log.d(AlarmBroadcastReceiver.TAG, "createTwoAlarm: ")
        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000, pi(this))

        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pi(this))
    }

    private fun cancelAlarm() {
        val context: Context = this
        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        am.cancel(pi(context))
    }

    private fun pi(context: Context): PendingIntent? {
        val intent = Intent(ACTION_SYNC, null, context, Receiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.i(TAG_L, "onReceive: $action")
        }
    }

    /**
     * AlarmManager的限制：最小未来时间受系统限制，可能是5秒；周期循环间隔有限制1ｍｉｎ
     * short-period and near-future alarms are startlingly costly in battery;
     * apps that require short-period or near-future work should use other mechanisms to schedule their activity.
     */
    @SuppressLint("NewApi")
    private fun registerTimerToAlarmManager(context: Context, targetTime: Long) {
        Log.d("AlarmWakeActivity", "registerTimerToAlarmManager: ${System.currentTimeMillis()}")
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setExact(AlarmManager.RTC_WAKEUP, targetTime, "AlarmTest",
            { Log.d("AlarmWakeActivity", "onAlarm: ${System.currentTimeMillis()}") },
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    Log.d("AlarmWakeActivity", "handleMessage: ")
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_wake)
        mAlarmView.setOnClickListener(this)
        startView.setOnClickListener(this)
        cancelView.setOnClickListener(this)
    }
}
