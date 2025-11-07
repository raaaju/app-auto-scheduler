package com.autoappsheduler.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val hour: Int,
    val minute: Int
)
