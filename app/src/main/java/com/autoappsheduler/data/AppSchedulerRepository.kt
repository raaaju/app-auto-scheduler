package com.autoappsheduler.data

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.autoappsheduler.db.ScheduleDao
import com.autoappsheduler.model.AppInfo
import com.autoappsheduler.model.Schedule
import com.autoappsheduler.worker.OpenAppWorker
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AppSchedulerRepository @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val context: Context
) {

    fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(0)
        return apps.mapNotNull { appInfo ->
            if (pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                val appName = appInfo.loadLabel(pm).toString()
                val icon = appInfo.loadIcon(pm)
                AppInfo(appName, appInfo.packageName, icon)
            } else {
                null
            }
        }
    }

    fun getAllSchedules(): Flow<List<Schedule>> {
        return scheduleDao.getAllSchedules()
    }

    suspend fun scheduleApp(packageName: String, hour: Int, minute: Int) {
        val schedule = Schedule(packageName = packageName, hour = hour, minute = minute)
        val scheduleId = scheduleDao.insert(schedule)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val delay = calendar.timeInMillis - System.currentTimeMillis()

        val data = Data.Builder()
            .putString("PACKAGE_NAME", packageName)
            .putLong("SCHEDULE_ID", scheduleId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<OpenAppWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    suspend fun deleteSchedule(schedule: Schedule) {
        scheduleDao.deleteSchedule(schedule.id)
    }
}
