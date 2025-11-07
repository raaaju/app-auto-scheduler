package com.autoappsheduler.worker

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.autoappsheduler.MyAccessibilityService

class OpenAppWorker(private val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val packageName = inputData.getString("PACKAGE_NAME")

        return if (packageName != null) {
            val intent = Intent(MyAccessibilityService.ACTION_OPEN_APP).apply {
                putExtra(MyAccessibilityService.EXTRA_PACKAGE_NAME, packageName)
            }
            appContext.sendBroadcast(intent)
            Result.success()
        } else {
            Result.failure()
        }
    }
}
