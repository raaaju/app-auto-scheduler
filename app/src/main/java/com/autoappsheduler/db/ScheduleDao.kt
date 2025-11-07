package com.autoappsheduler.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.autoappsheduler.model.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert
    suspend fun insert(schedule: Schedule): Long

    @Query("SELECT * FROM schedules")
    fun getAllSchedules(): Flow<List<Schedule>>

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteSchedule(id: Long)
}
