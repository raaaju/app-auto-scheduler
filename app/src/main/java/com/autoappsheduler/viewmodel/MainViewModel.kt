package com.autoappsheduler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoappsheduler.data.AppSchedulerRepository
import com.autoappsheduler.model.AppInfo
import com.autoappsheduler.model.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AppSchedulerRepository
) : ViewModel() {

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules

    init {
        loadInstalledApps()
        loadSchedules()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            _installedApps.value = repository.getInstalledApps()
        }
    }

    private fun loadSchedules() {
        repository.getAllSchedules().onEach {
            _schedules.value = it
        }.launchIn(viewModelScope)
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
