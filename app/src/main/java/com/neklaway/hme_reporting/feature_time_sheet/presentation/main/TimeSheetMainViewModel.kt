package com.neklaway.hme_reporting.feature_time_sheet.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.timesheet_route.GetTimeSheetRouteUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.timesheet_route.SetTimeSheetRouteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeSheetMainViewModel @Inject constructor(
    private val getIsIbauUseCase: GetIsIbauUseCase,
    private val getTimeSheetRouteUseCase: GetTimeSheetRouteUseCase,
    private val setTimeSheetRouteUseCase: SetTimeSheetRouteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TimeSheetState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = TimeSheetState(isIbau = getIsIbauUseCase(),startupRoute = getTimeSheetRouteUseCase()?:Screen.NewTimeSheetScreen.route)
        }
    }

    fun screenSelected(route:String){
        viewModelScope.launch {
            setTimeSheetRouteUseCase(route)
        }
    }
}