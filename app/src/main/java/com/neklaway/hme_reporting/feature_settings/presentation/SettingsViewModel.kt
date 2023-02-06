package com.neklaway.hme_reporting.feature_settings.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.backup.StartBackup
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.backup.StartRestore
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.break_time.GetBreakDurationUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.break_time.SetBreakDurationUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_auto_clear.GetIsAutoClearUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_auto_clear.SetIsAutoClearUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.SetIsIbauUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name.GetUserNameUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name.SetUserNameUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder.GetVisaReminderUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder.SetVisaReminderUseCase
import com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case.LoadBitmapUseCase
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Settings_ViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserNameUseCase: GetUserNameUseCase,
    private val setUserNameUseCase: SetUserNameUseCase,
    private val setIsIbauUseCase: SetIsIbauUseCase,
    private val getIsIbauUseCase: GetIsIbauUseCase,
    private val getBreakDurationUseCase: GetBreakDurationUseCase,
    private val setBreakDurationUseCase: SetBreakDurationUseCase,
    private val getIsAutoClearUseCase: GetIsAutoClearUseCase,
    private val setIsAutoClearUseCase: SetIsAutoClearUseCase,
    private val loadBitmapUseCase: LoadBitmapUseCase,
    private val getVisaReminderUseCase: GetVisaReminderUseCase,
    private val setVisaReminderUseCase: SetVisaReminderUseCase,
    private val startBackup: StartBackup,
    private val startRestore: StartRestore,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage

    init {
        getUserName()
        getIsIbau()
        getBreakDuration()
        getSignature()
        getIsAutoClear()
        getVisaReminder()
    }


    private fun getUserName() {
        viewModelScope.launch {
            val userName = getUserNameUseCase.invoke()
            _state.update { it.copy(userName = userName) }
        }
    }

    private fun getIsIbau() {
        viewModelScope.launch {
            val isIbau = getIsIbauUseCase.invoke()
            _state.update { it.copy(isIbauUser = isIbau) }
        }
    }

    private fun getIsAutoClear() {
        viewModelScope.launch {
            val isAutoClear = getIsAutoClearUseCase()
            _state.update { it.copy(isAutoClear = isAutoClear) }
        }
    }

    private fun getBreakDuration() {
        viewModelScope.launch {
            val breakTime = getBreakDurationUseCase()
            _state.update { it.copy(breakDuration = breakTime) }
        }
    }

    private fun getVisaReminder() {
        viewModelScope.launch {
            val visaReminder = getVisaReminderUseCase()
            _state.update { it.copy(visaReminder = visaReminder.toString()) }
        }
    }


    fun setUserName(userName: String) {
        _state.update { it.copy(userName = userName) }
        viewModelScope.launch {
            setUserNameUseCase.invoke(userName)
        }
    }

    fun setVisaReminder(reminder: String) {
        var reminderInt: Int?

        try {
            reminderInt = reminder.toInt()

            viewModelScope.launch {
                setVisaReminderUseCase(reminderInt!!)
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            reminderInt = null
            if (reminder.isNotBlank())
                viewModelScope.launch {
                    _userMessage.emit("Visa Reminder Date Error: ${e.message}")
                }
        }

        _state.update { it.copy(visaReminder = reminderInt?.toString() ?: "") }
    }

    fun setIsIbau(isIbau: Boolean) {
        _state.update { it.copy(isIbauUser = isIbau) }
        viewModelScope.launch {
            setIsIbauUseCase.invoke(isIbau)
        }
    }

    fun setAutoClear(autoClear: Boolean) {
        _state.update { it.copy(isAutoClear = autoClear) }
        viewModelScope.launch {
            setIsAutoClearUseCase.invoke(autoClear)
        }
    }

    fun breakDurationChanged(breakDuration: String) {

        var breakFloat: Float?
        Log.d(TAG, "breakDurationChanged: clicked")

        try {
            breakFloat = breakDuration.toFloat()

            viewModelScope.launch {
                breakFloat?.let {
                    Log.d(TAG, "breakDurationChanged: breakFloat sent to use case $breakFloat")
                    setBreakDurationUseCase(it).collect { resource ->
                        when (resource) {
                            is Resource.Error -> _userMessage.emit(resource.message ?: "error")
                            else -> Unit
                        }
                    }
                }
            }

        } catch (e: NumberFormatException) {
            e.printStackTrace()
            breakFloat = null
            if (breakDuration.isNotBlank()) {
                viewModelScope.launch {
                    _userMessage.emit("Error in Break Time " + e.message)
                }
            }
        }
        Log.d(TAG, "breakDurationChanged: breakFloat $breakFloat")

        _state.update { it.copy(breakDuration = if (breakFloat == null) "" else breakDuration) }

    }

    fun signatureBtnClicked() {
        _state.update { it.copy(showSignaturePad = true) }
        Log.d(TAG, "signatureBtnClicked:  show pad = ${_state.value.showSignaturePad}")
    }

    fun backupButtonClicked() {
        startBackup()
    }


    fun restoreFolderSelected(uri: Uri) {
        startRestore(uri)
    }


    private fun getSignature() {
        viewModelScope.launch {
            when (val signatureResource =
                loadBitmapUseCase.invoke("signatures", "user_signature")) {
                is Resource.Success -> _state.update { it.copy(signature = signatureResource.data?.asImageBitmap()) }
                is Resource.Error -> {
                    _userMessage.emit("${signatureResource.message}")
                    _state.update { it.copy(isLoading = false) }
                }
                is Resource.Loading -> {}
            }

        }

    }

    fun signatureScreenClosed() {
        _state.update { it.copy(showSignaturePad = false) }
        Log.d(TAG, "signatureScreenClosed: show pad = ${_state.value.showSignaturePad}")
    }

    fun updateSignature() {
        getSignature()
    }

}