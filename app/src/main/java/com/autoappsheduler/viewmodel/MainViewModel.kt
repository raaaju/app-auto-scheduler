package com.autoappsheduler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoappsheduler.data.AppSchedulerRepository
import com.autoappsheduler.model.AppInfo
import com.autoappsheduler.model.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AppSchedulerRepository
) : ViewModel() {

    val schedules: StateFlow<List<Schedule>> = repository.getAllSchedules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            _installedApps.value = repository.getInstalledApps()
        }
    }

    fun deleteExpiredSchedules() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            val currentSchedules = repository.getAllSchedules().first()
            currentSchedules.forEach { schedule ->
                if (schedule.hour < currentHour || (schedule.hour == currentHour && schedule.minute <= currentMinute)) {
                    repository.deleteSchedule(schedule)
                }
            }
        }
    }

    fun scheduleApp(packageName: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.scheduleApp(packageName, hour, minute)
        }
    }

    fun getScheduleForApp(packageName: String): Schedule? {
        return schedules.value.find { it.packageName == packageName }
    }

    fun deleteSchedule(packageName: String) {
        viewModelScope.launch {
            val schedule = getScheduleForApp(packageName)
            if (schedule != null) {
                repository.deleteSchedule(schedule)
            }
        }
    }
}
