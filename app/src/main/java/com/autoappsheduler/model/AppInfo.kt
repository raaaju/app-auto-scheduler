package com.autoappsheduler.model

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Stable

@Stable
data class AppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable
)
