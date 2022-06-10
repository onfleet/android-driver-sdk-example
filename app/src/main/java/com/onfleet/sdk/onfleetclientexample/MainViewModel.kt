package com.onfleet.sdk.onfleetclientexample

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.onfleet.sdk.dataModels.*
import com.onfleet.sdk.managers.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)
    private var cachedPhoneNumber = "" //for simplification - don't cache sensitive data
    private var cachedPassword = "" //for simplification - don't cache sensitive data

    @Parcelize
    data class State(
        val isLoading: Boolean = false,
        val isAuthenticated: Boolean = false,
        val isOnDuty: Boolean = false,
        val isList: Boolean = false,
        val isDetails: Boolean = false,
        val tasks: List<Task> = emptyList(),
        val selectedTask: Task? = null,
    ) : Parcelable

    sealed class Event {
        data class OnDutyClicked(val status: Boolean) : Event()
        data class TaskClicked(val task: Task) : Event()
        data class CompleteTaskClicked(val task: Task) : Event()
        data class StartTaskClicked(val task: Task) : Event()
        data class SelfAssignClicked(val task: Task) : Event()
        data class Login(val phone: String, val password: String) : Event()
        object PermissionsAcquired : Event()
    }

    init {
        //In this example driver by default accepts all pending invitations
        viewModelScope.launch {
            SessionManager.getInstance().getAccountsFlow().collect { accounts ->
                for (account in accounts) {
                    if (account.isPending()) {
                        SessionManager.getInstance().respondToInvitation(account, true)
                    }
                }
            }
        }
        viewModelScope.launch {
            SessionManager.getInstance().getActiveAccountFlow().collect { account ->
                _state.update { it.copy(isAuthenticated = account != null) }
            }
        }
        viewModelScope.launch {
            TasksManager.getInstance().getTasksFlow().collect { tasks ->
                val selectedTask = tasks.find { it.id == _state.value.selectedTask?.id }
                _state.update { it.copy(tasks = tasks, selectedTask = selectedTask) }
            }
        }
        viewModelScope.launch {
            DriverManager.getInstance().getDriverFlow().collect { driver ->
                if (driver != null) {
                    _state.update { it.copy(isOnDuty = driver.isOnDuty, isAuthenticated = true) }
                } else {
                    _state.update { it.copy(isAuthenticated = false) }
                }
            }
        }
        viewModelScope.launch {
            CoreManager.getInstance().sdkErrorFlow().collect { sdkError ->
                when (sdkError.type) {
                    SDKErrorType.LOCATION_ERROR -> {
                        sdkError.description?.let {
                            Timber.e(it)
                        }
                        sdkError.resolvableApiException?.let {
                            //resolve in activity
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            SessionManager.getInstance().loginEventsFlow().collect { status ->
                when (status) {
                    LoginStatus.SUCCESS -> {
                        _state.update { it.copy(isLoading = false, isList = true) }
                    }
                    LoginStatus.SET_NEW_PASSWORD_SUCCESS, LoginStatus.PROVISIONING_COMPLETED_LOG_IN -> {
                        SessionManager.getInstance().login(cachedPhoneNumber, cachedPassword)
                    }
                    LoginStatus.WAIT_PROVISIONING, LoginStatus.WAIT_ADMIN_VERIFICATION, LoginStatus.WAIT_SMS_VERIFICATION, LoginStatus.RECEIVED_SMS_VERIFICATION -> {
                        Timber.d(status.toString())
                    }
                    LoginStatus.SET_NEW_PASSWORD -> {
                        cachedPassword = "abcde"
                        SessionManager.getInstance().setNewPassword("abcde")
                    }
                    else -> {
                        Timber.e(status.toString())
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: com.google.android.gms.tasks.Task<String?> ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
                OnfleetFcmManager.getInstance().handleTokenRefresh(task.result!!)
            }
    }

    fun event(event: Event) {
        when (event) {
            is Event.OnDutyClicked -> onOnDutyClicked(event.status)
            Event.PermissionsAcquired -> onPermissionsAcquired()
            is Event.CompleteTaskClicked -> onCompleteTaskClicked(event.task)
            is Event.Login -> onLogin(event.phone, event.password)
            is Event.StartTaskClicked -> onStartTaskClicked(event.task)
            is Event.SelfAssignClicked -> onSelfAssignClicked(event.task)
            is Event.TaskClicked -> {
                _state.update { it.copy(isDetails = true, isList = false, selectedTask = event.task) }
            }
        }
    }

    private fun onStartTaskClicked(task: Task) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        when (val response = TasksManager.getInstance().startTask(task.id)) {
            StartTaskResponse.SUCCESS -> {}
            else -> {
                Timber.e(response.toString())
            }
        }
        _state.update { it.copy(isLoading = false) }
    }

    private fun onSelfAssignClicked(task: Task) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        when (val response = TasksManager.getInstance().selfAssignTask(task.id)) {
            SelfAssignResponse.SUCCESS -> {}
            else -> {
                Timber.e(response.toString())
            }
        }
        _state.update { it.copy(isLoading = false) }
    }

    private fun onCompleteTaskClicked(task: Task) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        when (val response = TasksManager.getInstance()
            .completeTask(
                task.id,
                completionDetails = TaskCompletionDetails(
                    success = true,
                    attachments = emptyList(),
                    barcodes = emptyList(),
                    completionNotes = null,
                    failureNotes = null,
                    failureReason = null,
                    signatureText = null
                )
            )) {
            CompleteTaskResponse.SUCCESS -> {}
            else -> {
                Timber.e(response.toString())
            }
        }
        _state.update { it.copy(isLoading = false, isDetails = false, isList = true) }
    }

    private fun onPermissionsAcquired() = viewModelScope.launch {
        if (!CoreManager.getInstance().isInitialized()) {
            _state.update { it.copy(isLoading = true) }
            when (SessionManager.getInstance().resumeSession()) {
                ResumeResponse.SUCCESS -> {
                    _state.update { it.copy(isList = true) }
                }
                else -> {}
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun onOnDutyClicked(status: Boolean) =
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (DriverManager.getInstance().setDriverOnDutyStatus(status)) {
                OnDutyResponse.SUCCESS -> {}
                else -> {
                    _state.update { it.copy(isOnDuty = !status) }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }


    private fun onLogin(phoneNumber: String, password: String) =
        viewModelScope.launch {
            cachedPhoneNumber = phoneNumber
            cachedPassword = password
            _state.update { it.copy(isLoading = true) }
            SessionManager.getInstance().login(phoneNumber, password)
        }

}