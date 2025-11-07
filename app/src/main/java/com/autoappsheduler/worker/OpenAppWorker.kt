package com.autoappsheduler.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.autoappsheduler.MyAccessibilityService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class OpenAppWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val packageName = inputData.getString("PACKAGE_NAME")
        return if (packageName != null) {
            val intent = Intent(MyAccessibilityService.ACTION_OPEN_APP).apply {
                putExtra(MyAccessibilityService.EXTRA_PACKAGE_NAME, packageName)
            }
            applicationContext.sendBroadcast(intent)
            Result.success()
        } else {
            Result.failure()
        }
    }
}
