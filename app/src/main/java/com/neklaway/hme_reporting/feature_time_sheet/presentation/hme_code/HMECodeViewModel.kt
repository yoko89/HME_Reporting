package com.neklaway.hme_reporting.feature_time_sheet.presentation.hme_code

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetAllCustomersFlowUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.DeleteHMECodeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.InsertHMECodeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.UpdateHMECodeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.GetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.SetCustomerIdUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HME Code ViewModel"

@HiltViewModel
class HMECodeViewModel @Inject constructor(
    private val getAllCustomersFlowUseCase: GetAllCustomersFlowUseCase,
    private val getHMECodeByCustomerIdUseCase: GetHMECodeByCustomerIdUseCase,
    private val updateHMECodeUseCase: UpdateHMECodeUseCase,
    private val insertHMECodeUseCase: InsertHMECodeUseCase,
    private val deleteHMECodeUseCase: DeleteHMECodeUseCase,
    private val getIsIbauUseCase: GetIsIbauUseCase,
    private val getCustomerIdUseCase: GetCustomerIdUseCase,
    private val setCustomerIdUseCase: SetCustomerIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HMECodeState())
    val state = _state.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage

    private var getHMEJob: Job? = null

    init {
        viewModelScope.launch {
            val isIbau = getIsIbauUseCase.invoke()
            _state.update {
                it.copy(isIbau = isIbau)
            }
            getCustomers()
        }
    }

    private fun getCustomers() {

        getAllCustomersFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't get customers")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    val savedCustomerId = getCustomerIdUseCase()
                    val savedSelectedCustomer = result.data?.find { it.id == savedCustomerId }
                    savedSelectedCustomer?.let {
                        customerSelected(it)
                    }
                    Log.d(
                        TAG,
                        "getCustomers: saved Customer Id is $savedCustomerId, and object is $savedSelectedCustomer"
                    )
                    _state.update {
                        it.copy(
                            customers = result.data.orEmpty(),
                            loading = false,
                            selectedCustomer = savedSelectedCustomer
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }


    fun saveHMECode() {
        val hmeCode: String = state.value.hmeCode
        val machineNumber: String = state.value.machineNumber
        val machineType: String = state.value.machineType
        val workDescription: String = state.value.workDescription


        state.value.selectedCustomer?.let { selectedCustomer ->
            insertHMECodeUseCase(
                customerId = selectedCustomer.id,
                code = hmeCode,
                machineType = machineType,
                machineNumber = machineNumber,
                workDescription = workDescription
            ).onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _userMessage.emit(result.message ?: "Can't save HME Code")
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        clearState()
                        getHMEsByCustomerId(selectedCustomer.id!!)
                    }
                }
            }.launchIn(viewModelScope)
        } ?: {
            viewModelScope.launch {
                _userMessage.emit("Please Select Customer First")
            }
        }
    }

    private fun clearState() {
        _state.update {
            it.copy(
                loading = false,
                hmeCode = "",
                machineNumber = "",
                machineType = "",
                workDescription = "",
                selectedHMECode = null
            )
        }
    }


    fun updateHMECode() {
        val hmeCode: String = state.value.hmeCode
        val machineNumber: String = state.value.machineNumber
        val machineType: String = state.value.machineType
        val workDescription: String = state.value.workDescription

        val selectedHMECode = state.value.selectedHMECode ?: return

        updateHMECodeUseCase(
            id = selectedHMECode.id!!,
            customerId = selectedHMECode.customerId,
            code = hmeCode,
            machineType = machineType,
            machineNumber = machineNumber,
            workDescription = workDescription,
            fileNumber = selectedHMECode.fileNumber,
            expanseNumber = selectedHMECode.expanseNumber,
            signerName = selectedHMECode.signerName,
            signatureDate = selectedHMECode.signatureDate
        ).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't save HME Code")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    clearState()
                    state.value.selectedCustomer?.let {
                        getHMEsByCustomerId(it.id!!)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun getHMEsByCustomerId(id: Long) {

        getHMEJob?.cancel()

        getHMEJob = viewModelScope.launch(Dispatchers.IO) {
            getHMECodeByCustomerIdUseCase(id).collect { result ->
                Log.d("TAG", "getHMEsByCustomerId: $result")
                when (result) {
                    is Resource.Error -> {
                        _userMessage.emit(result.message ?: "Can't get hme code")
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> _state.update {
                        it.copy(
                            hmeCodes = result.data ?: emptyList(),
                            loading = false
                        )
                    }

                }
            }
        }
    }

    fun customerSelected(customer: Customer) {
        _state.update { it.copy(selectedCustomer = customer) }
        getHMEsByCustomerId(customer.id!!)
        viewModelScope.launch {
            setCustomerIdUseCase(customer.id)
            Log.d(TAG, "customerSelected ID to be saved is ${customer.id}")
        }
    }

    fun deleteHMECode(hmeCode: HMECode) {

        deleteHMECodeUseCase(hmeCode).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't delete HME Code")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    _state.update {
                        it.copy(loading = false)
                    }
                    state.value.selectedCustomer?.let {
                        getHMEsByCustomerId(it.id!!)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }


    fun hmeCodeSelected(hmeCode: HMECode) {
        _state.update {
            it.copy(
                selectedHMECode = hmeCode,
                hmeCode = hmeCode.code,
                machineNumber = hmeCode.machineNumber.orEmpty(),
                machineType = hmeCode.machineType.orEmpty(),
                workDescription = hmeCode.workDescription.orEmpty()
            )
        }
    }

    fun hmeCodeChanged(hmeCode: String) {
        _state.update {
            it.copy(hmeCode = hmeCode)
        }
    }

    fun machineTypeChanged(machineType: String) {
        _state.update {
            it.copy(machineType = machineType)
        }
    }

    fun machineNumberChanged(machineNumber: String) {
        _state.update {
            it.copy(machineNumber = machineNumber)
        }
    }

    fun workDescriptionChanged(workDescription: String) {
        _state.update {
            it.copy(workDescription = workDescription)
        }
    }


}


