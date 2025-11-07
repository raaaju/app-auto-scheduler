package com.autoappsheduler

import android.app.Application
import androidx.work.Configuration
import com.autoappsheduler.worker.AppWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AppSchedulerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: AppWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
