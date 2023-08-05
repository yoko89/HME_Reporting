package com.neklaway.hme_reporting.feature_settings.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.Get8HDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetFullDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetSavingDeductibleUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.Set8HAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.SetFullDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.SetSavingDeductibleUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.backup.StartBackup
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.backup.StartRestore
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.break_time.GetBreakDurationUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.break_time.SetBreakDurationUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.dark_theme.GetDarkThemeUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.dark_theme.SetDarkThemeUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_auto_clear.GetIsAutoClearUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_auto_clear.SetIsAutoClearUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.SetIsIbauUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.theme.GetThemeUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.theme.SetThemeUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name.GetUserNameUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name.SetUserNameUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder.GetVisaReminderUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder.SetVisaReminderUseCase
import com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case.LoadBitmapUseCase
import com.neklaway.hme_reporting.utils.DarkTheme
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.Theme
import com.neklaway.hme_reporting.utils.toFloatWithString
import com.neklaway.hme_reporting.utils.toIntWithString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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
    private val getFullDayAllowanceUseCase: GetFullDayAllowanceUseCase,
    private val get8HDayAllowanceUseCase: Get8HDayAllowanceUseCase,
    private val setFullDayAllowanceUseCase: SetFullDayAllowanceUseCase,
    private val set8HAllowanceUseCase: Set8HAllowanceUseCase,
    private val setSavingDeductibleUseCase: SetSavingDeductibleUseCase,
    private val getSavingDeductibleUseCase: GetSavingDeductibleUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val setDarkThemeUseCase: SetDarkThemeUseCase,
    private val getDarkThemeUseCase: GetDarkThemeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _userMessage = Channel<String>()
    val userMessage = _userMessage.receiveAsFlow()

    init {
        getUserName()
        getIsIbau()
        getBreakDuration()
        getSignature()
        getIsAutoClear()
        getVisaReminder()
        getFullDayAllowance()
        get8HDayAllowance()
        getSavingDeductible()
        getTheme()
        getDarkTheme()
    }


    private fun getUserName() {
        viewModelScope.launch {
            val userName = getUserNameUseCase.invoke()
            _state.update { it.copy(userName = userName) }
        }
    }

    private fun getDarkTheme() {
        viewModelScope.launch {
            val theme = getDarkThemeUseCase.invoke().first()
            _state.update { it.copy(darkTheme = theme) }
        }
    }

    private fun getTheme() {
        viewModelScope.launch {
            val theme = getThemeUseCase.invoke().first()
            _state.update { it.copy(theme = theme) }
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
            _state.update {
                it.copy(
                    breakDuration = ResourceWithString.Success(
                        breakTime.toFloat(),
                        breakTime
                    )
                )
            }
        }
    }

    private fun getVisaReminder() {
        viewModelScope.launch {
            val visaReminder = getVisaReminderUseCase()
            _state.update {
                it.copy(
                    visaReminder = ResourceWithString.Success(
                        visaReminder,
                        visaReminder.toString()
                    )
                )
            }
        }
    }

    private fun getFullDayAllowance() {
        viewModelScope.launch {
            val fullDayAllowance = getFullDayAllowanceUseCase()
            _state.update {
                it.copy(
                    fullDayAllowance = ResourceWithString.Success(
                        fullDayAllowance,
                        fullDayAllowance.toString()
                    )
                )
            }
        }
    }

    private fun get8HDayAllowance() {
        viewModelScope.launch {
            val _8HDayAllowance = get8HDayAllowanceUseCase()
            _state.update {
                it.copy(
                    _8HAllowance = ResourceWithString.Success(
                        _8HDayAllowance,
                        _8HDayAllowance.toString()
                    )
                )
            }
        }
    }

    private fun getSavingDeductible() {
        viewModelScope.launch {
            val savingDeductible = getSavingDeductibleUseCase()
            _state.update {
                it.copy(
                    savingDeductible = ResourceWithString.Success(
                        savingDeductible,
                        savingDeductible.toString()
                    )
                )
            }
        }
    }


    private fun setUserName(userName: String) {
        _state.update { it.copy(userName = userName) }
        viewModelScope.launch {
            setUserNameUseCase.invoke(userName)
        }
    }

    private fun setTheme(theme: Theme) {
        _state.update { it.copy(theme = theme) }
        viewModelScope.launch {
            setThemeUseCase.invoke(theme)
        }
    }

    private fun setDarkTheme(theme: DarkTheme) {
        _state.update { it.copy(darkTheme = theme) }
        viewModelScope.launch {
            setDarkThemeUseCase.invoke(theme)
        }
    }

    private fun setVisaReminder(reminder: String) {
        reminder.toIntWithString().let { resourceWithString ->
            when (resourceWithString) {
                is ResourceWithString.Success -> {
                    viewModelScope.launch {
                        setVisaReminderUseCase(resourceWithString.data!!)
                    }
                }

                else -> Unit
            }
            _state.update { it.copy(visaReminder = resourceWithString) }
        }
    }

    private fun setIsIbau(isIbau: Boolean) {
        _state.update { it.copy(isIbauUser = isIbau) }
        viewModelScope.launch {
            setIsIbauUseCase.invoke(isIbau)
        }
    }

    private fun setAutoClear(autoClear: Boolean) {
        _state.update { it.copy(isAutoClear = autoClear) }
        viewModelScope.launch {
            setIsAutoClearUseCase.invoke(autoClear)
        }
    }

    private fun breakDurationChanged(breakDuration: String) {
        breakDuration.toFloatWithString().let { resourceString ->
            when (resourceString) {
                is ResourceWithString.Success -> {
                    viewModelScope.launch {
                        setBreakDurationUseCase(resourceString.data!!).collect { resource ->
                            when (resource) {
                                is Resource.Error -> _userMessage.send(
                                    resource.message ?: "error"
                                )

                                else -> Unit
                            }
                        }
                    }
                }

                else -> Unit
            }
            _state.update { it.copy(breakDuration = resourceString) }
        }
    }

    private fun signatureBtnClicked() {
        _state.update { it.copy(showSignaturePad = true) }
        Log.d(TAG, "signatureBtnClicked:  show pad = ${_state.value.showSignaturePad}")
    }

    private fun backupButtonClicked() {
        startBackup()
    }


    private fun restoreFolderSelected(uri: Uri) {
        startRestore(uri)
    }


    private fun getSignature() {
        viewModelScope.launch {
            when (val signatureResource =
                loadBitmapUseCase.invoke("signatures", "user_signature")) {
                is Resource.Success -> _state.update { it.copy(signature = signatureResource.data?.asImageBitmap()) }
                is Resource.Error -> {
                    _userMessage.send("${signatureResource.message}")
                    _state.update { it.copy(isLoading = false) }
                }

                is Resource.Loading -> {}
            }

        }

    }

    private fun signatureScreenClosed() {
        _state.update { it.copy(showSignaturePad = false) }
        Log.d(TAG, "signatureScreenClosed: show pad = ${_state.value.showSignaturePad}")
    }

    private fun updateSignature() {
        getSignature()
    }

    private fun setFullDayAllowance(allowance: String) {
        allowance.toIntWithString().let { resourceWithString ->
            when (resourceWithString) {
                is ResourceWithString.Success -> {
                    viewModelScope.launch {
                        setFullDayAllowanceUseCase(resourceWithString.data!!)
                    }
                }

                else -> Unit
            }
            _state.update { it.copy(fullDayAllowance = resourceWithString) }

        }
    }

    private fun set8HAllowance(allowance: String) {
        allowance.toIntWithString().let { resourceWithString ->
            when (resourceWithString) {
                is ResourceWithString.Success -> {
                    viewModelScope.launch {
                        set8HAllowanceUseCase(resourceWithString.data!!)
                    }
                }

                else -> Unit
            }
            _state.update { it.copy(_8HAllowance = resourceWithString) }
        }
    }

    private fun setSavingDeductible(deductible: String) {
        deductible.toIntWithString().let { resourceWithString ->
            when (resourceWithString) {
                is ResourceWithString.Success -> {
                    viewModelScope.launch {
                        setSavingDeductibleUseCase(resourceWithString.data!!)
                    }
                }

                else -> Unit
            }
            _state.update { it.copy(savingDeductible = resourceWithString) }

        }
    }

    fun userEvent(event: SettingsUserEvents) {
        when (event) {
            SettingsUserEvents.BackupButtonClicked -> backupButtonClicked()
            is SettingsUserEvents.BreakDurationChanged -> breakDurationChanged(event.duration)
            is SettingsUserEvents.RestoreFolderSelected -> restoreFolderSelected(event.uri)
            is SettingsUserEvents.Set8HAllowance -> set8HAllowance(event.allowance)
            is SettingsUserEvents.SetAutoClear -> setAutoClear(event.autoClear)
            is SettingsUserEvents.SetDarkTheme -> setDarkTheme(event.darkTheme)
            is SettingsUserEvents.SetFullDayAllowance -> setFullDayAllowance(event.allowance)
            is SettingsUserEvents.SetIsIbau -> setIsIbau(event.ibau)
            is SettingsUserEvents.SetSavingDeductible -> setSavingDeductible(event.deductible)
            is SettingsUserEvents.SetTheme -> setTheme(event.theme)
            is SettingsUserEvents.SetUserName -> setUserName(event.userName)
            is SettingsUserEvents.SetVisaReminder -> setVisaReminder(event.reminder)
            SettingsUserEvents.SignatureBtnClicked -> signatureBtnClicked()
            SettingsUserEvents.SignatureScreenClosed -> signatureScreenClosed()
            SettingsUserEvents.UpdateSignature -> updateSignature()
        }
    }
}