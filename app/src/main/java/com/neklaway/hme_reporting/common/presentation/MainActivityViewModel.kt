package com.neklaway.hme_reporting.common.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.dark_theme.GetDarkThemeUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.theme.GetThemeUseCase
import com.neklaway.hme_reporting.utils.DarkTheme
import com.neklaway.hme_reporting.utils.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivityViewModel"
@HiltViewModel
class MainActivityViewModel @Inject constructor(
private val getDarkThemeUseCase: GetDarkThemeUseCase,
private val getThemeUseCase: GetThemeUseCase
) :ViewModel(){
 private val _themeState = MutableStateFlow (Theme.Auto)
    val themeState = _themeState.asStateFlow()
 private val _darkThemeState = MutableStateFlow (DarkTheme.Auto)
    val darkThemeState = _darkThemeState.asStateFlow()

    init {
        viewModelScope.launch {
            getThemeUseCase.invoke().collect {theme ->
                _themeState.update { theme }
            }
        }

        viewModelScope.launch {
            getDarkThemeUseCase.invoke().collect{darkTheme->
                _darkThemeState.update {
                    darkTheme}
                Log.d(TAG, "dark theme: ${darkTheme.name}")
            }
        }

    }

}