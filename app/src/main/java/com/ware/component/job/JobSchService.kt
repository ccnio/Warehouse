package com.ware.component.job

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * Created by jianfeng.li on 20-10-28.
 */
private const val TAG = "JobSchService"

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class JobSchService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: ${params?.jobId}")
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    companion object {
        fun scheduleJob(context: Context) {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(JobInfo.Builder(11,
                    ComponentName(context, JobSchService::class.java))
//                    .setPeriodic(com.xiaomi.wearable.app.keepalive.KeepAliveJobSchedulerService.INTERVAL.toLong())
//                    .setPeriodic(5000)
                    .setMinimumLatency(5000)
                    .setPersisted(true)
                    .build())
        }
    }

}