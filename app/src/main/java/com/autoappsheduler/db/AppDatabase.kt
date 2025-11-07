package com.autoappsheduler.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.autoappsheduler.model.Schedule

@Database(entities = [Schedule::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}
