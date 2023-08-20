package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.expanse_sheet_route.GetExpanseSheetRouteUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.expanse_sheet_route.SetExpanseSheetRouteUseCase
import com.neklaway.hme_reporting.common.presentation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpanseSheetMainViewModel @Inject constructor(
    private val getExpanseSheetRouteUseCase: GetExpanseSheetRouteUseCase,
    private val setExpanseSheetRouteUseCase: SetExpanseSheetRouteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ExpanseSheetState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = ExpanseSheetState(
                startupRoute = getExpanseSheetRouteUseCase() ?: Screen.ExpanseSheet.route
            )
        }
    }

    private fun screenSelected(route: String) {
        viewModelScope.launch {
            setExpanseSheetRouteUseCase(route)
        }
    }

    fun userEvent(event: ExpanseSheetMainUserEvent) {
        when (event) {
            is ExpanseSheetMainUserEvent.ScreenSelected -> screenSelected(event.route)
        }
    }
}