package com.autoappsheduler.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.autoappsheduler.db.ScheduleDao
import javax.inject.Inject

class AppWorkerFactory @Inject constructor(
    private val scheduleDao: ScheduleDao
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            OpenAppWorker::class.java.name -> {
                OpenAppWorker(appContext, workerParameters)
            }
            else -> null
        }
    }
}
