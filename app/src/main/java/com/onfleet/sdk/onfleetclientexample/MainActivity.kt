package com.onfleet.sdk.onfleetclientexample

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.NonCancellable.isActive
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : AppCompatActivity() {
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color.Magenta,
                    secondary = Color.White
                )
            ) {
                val state by viewModel.state.collectAsState()
                val locationPermissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    onPermissionsResult = { }
                )

                if (!locationPermissionsState.allPermissionsGranted) {
                    LaunchedEffect(key1 = locationPermissionsState) {
                        locationPermissionsState.launchMultiplePermissionRequest()
                    }
                } else {
                    LaunchedEffect(key1 = locationPermissionsState) {
                        viewModel.event(MainViewModel.Event.PermissionsAcquired)
                    }
                    if (state.isLoading) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text("Loading")
                        }
                    } else if (!state.isAuthenticated) {
                        var phone by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        Column(modifier = Modifier.fillMaxSize()) {
                            TextField(value = phone, label = { Text("Phone") }, onValueChange = { phone = it })
                            TextField(
                                value = password, label = { Text("Password") }, onValueChange = { password = it },
                                visualTransformation = PasswordVisualTransformation()
                            )
                            Button(
                                onClick = { viewModel.event(MainViewModel.Event.Login(phone, password)) },
                                content = {
                                    Text(
                                        "Login"
                                    )
                                })
                        }
                    } else if (state.isList) {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Switch(
                                    checked = state.isOnDuty,
                                    onCheckedChange = {
                                        viewModel.event(MainViewModel.Event.OnDutyClicked(it))
                                        if (it) {
                                            val intent = Intent(this@MainActivity, ForegroundService::class.java)
                                            intent.action = ForegroundService.ACTION_START_FOREGROUND_SERVICE
                                            startService(intent)
                                        } else {
                                            val intent = Intent(this@MainActivity, ForegroundService::class.java)
                                            intent.action = ForegroundService.ACTION_STOP_FOREGROUND_SERVICE
                                            startService(intent)
                                        }
                                    },
                                )
                            }
                            if (state.isOnDuty) {
                                items(
                                    items = state.tasks,
                                    itemContent = {
                                        Button(
                                            onClick = { viewModel.event(MainViewModel.Event.TaskClicked(it)) },
                                            content = { Text(it.shortId + if (it.isActive) " ACTIVE" else "") })
                                        Divider()
                                    })
                            }
                        }
                    } else if (state.isDetails) {
                        state.selectedTask?.let {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text("Details of " + it.shortId)
                                Button(
                                    onClick = {
                                        viewModel.event(
                                            if (it.isActive) MainViewModel.Event.CompleteTaskClicked(it)
                                            else if(it.isSelfAssignable) MainViewModel.Event.SelfAssignClicked(it) else MainViewModel.Event.StartTaskClicked(it)
                                        )
                                    },
                                    content = { Text(it.shortId + if (it.isActive) "COMPLETE" else if(it.isSelfAssignable) "SELF-ASSIGN" else "START") })
                            }
                        }
                    }
                }
            }
        }
    }
}