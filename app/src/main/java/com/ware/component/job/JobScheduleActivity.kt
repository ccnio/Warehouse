package com.ware.component.job

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ware.R
import kotlinx.android.synthetic.main.activity_job_schedule.*

class JobScheduleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_schedule)
        scheduleView.setOnClickListener { JobSchService.scheduleJob(this) }
    }
}