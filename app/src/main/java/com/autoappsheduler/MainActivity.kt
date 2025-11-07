package com.autoappsheduler

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberAsyncImagePainter
import com.autoappsheduler.model.AppInfo
import com.autoappsheduler.theme.AppSchedulerTheme
import com.autoappsheduler.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppSchedulerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isAccessibilityServiceEnabled by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }
    val installedApps by viewModel.installedApps.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessibilityServiceEnabled = isAccessibilityServiceEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (!isAccessibilityServiceEnabled) {
        AccessibilityPermissionDialog(
            onConfirm = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
            },
            onDismiss = {}
        )
    } else {
        AppList(apps = installedApps, modifier = modifier, viewModel = viewModel)
    }
}

@Composable
fun AppList(apps: List<AppInfo>, modifier: Modifier = Modifier, viewModel: MainViewModel) {
    LazyColumn(modifier = modifier) {
        items(apps) { app ->
            AppListItem(appInfo = app, viewModel = viewModel)
        }
    }
}

@Composable
fun AppListItem(appInfo: AppInfo, viewModel: MainViewModel) {
    val context = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                viewModel.scheduleApp(appInfo.packageName, hour, minute)
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = appInfo.icon),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(text = appInfo.appName)
            Text(text = appInfo.packageName)
        }
        Button(onClick = { showTimePicker = true }) {
            Text("Schedule")
        }
    }
}


@Composable
fun AccessibilityPermissionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Accessibility Permission Required") },
        text = { Text("This app requires accessibility permission to open other apps. Please enable it in settings.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun isAccessibilityServiceEnabled(context: android.content.Context): Boolean {
    val accessibilityServiceId = "${context.packageName}/${MyAccessibilityService::class.java.canonicalName}"
    val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServicesSetting?.let {
        val stringColonSplitter = TextUtils.SimpleStringSplitter(':')
        stringColonSplitter.setString(it)
        while (stringColonSplitter.hasNext()) {
            val componentName = stringColonSplitter.next()
            if (componentName.equals(accessibilityServiceId, ignoreCase = true)) {
                return true
            }
        }
        false
    } ?: false
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppSchedulerTheme {
        MainScreen()
    }
}
