package com.neklaway.hme_reporting.feature_time_sheet.presentation.ibau_code

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetAllCustomersFlowUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.DeleteIBAUCodeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.GetIBAUCodeByHMECodeIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.InsertIBAUCodeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.UpdateIBAUCodeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.GetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.SetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.GetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.SetHMEIdUseCase
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IBAUCodeViewModel @Inject constructor(
    private val getAllCustomersFlowUseCase: GetAllCustomersFlowUseCase,
    private val getHMECodeByCustomerIdUseCase: GetHMECodeByCustomerIdUseCase,
    private val getIBAUCodeByHMECodeIdUseCase: GetIBAUCodeByHMECodeIdUseCase,
    private val updateIBAUCodeUseCase: UpdateIBAUCodeUseCase,
    private val insertIBAUCodeUseCase: InsertIBAUCodeUseCase,
    private val deleteIBAUCodeUseCase: DeleteIBAUCodeUseCase,
    private val getCustomerIdUseCase: GetCustomerIdUseCase,
    private val setCustomerIdUseCase: SetCustomerIdUseCase,
    private val getHmeIdUseCase: GetHMEIdUseCase,
    private val setHMEIdUseCase: SetHMEIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(IBAUCodeState())
    val state = _state.asStateFlow()

    private val _userMessage = Channel<String>()
    val userMessage = _userMessage.receiveAsFlow()

    private var getHMEJob: Job? = null
    private var getIBAUJob: Job? = null

    init {
        getCustomers()

    }

    private fun getCustomers() {

        getAllCustomersFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't get customers")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    val savedCustomerId = getCustomerIdUseCase()
                    val savedSelectedCustomer = result.data?.find { it.id == savedCustomerId }
                    savedSelectedCustomer?.let {
                        customerSelected(it)
                    }
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


    private fun saveIBAUCode() {
        val ibauCode: String = state.value.ibauCode
        val machineNumber: String = state.value.machineNumber
        val machineType: String = state.value.machineType
        val workDescription: String = state.value.workDescription

        insertIBAUCodeUseCase(
            state.value.selectedHMECode?.id!!,
            ibauCode,
            machineType,
            machineNumber,
            workDescription
        ).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't save IBAU Code")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update {
                    it.copy(loading = true)
                }

                is Resource.Success -> {
                    clearState()
                    state.value.selectedHMECode?.let {
                        getIBAUsByHMECodeId(it.id!!)

                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun clearState() {
        _state.update {
            it.copy(
                loading = false,
                ibauCode = "",
                machineNumber = "",
                machineType = "",
                workDescription = "",
                selectedIBAUCode = null
            )
        }
    }

    private fun updateIBAUCode() {
        val ibauCode: String = state.value.ibauCode
        val machineNumber: String = state.value.machineNumber
        val machineType: String = state.value.machineType
        val workDescription: String = state.value.workDescription

        val selectedIBAUCode = state.value.selectedIBAUCode
        if (selectedIBAUCode != null) {
            updateIBAUCodeUseCase(
                selectedIBAUCode.id!!,
                selectedIBAUCode.HMEId,
                ibauCode,
                machineType,
                machineNumber,
                workDescription
            ).onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _userMessage.send(result.message ?: "Can't save IBAU Code")
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
    }

    private fun getHMEsByCustomerId(id: Long) {

        getHMEJob?.cancel()

        getHMEJob = viewModelScope.launch(Dispatchers.IO) {
            getHMECodeByCustomerIdUseCase(id).collect { result ->
                Log.d("TAG", "getHMEsByCustomerId: $result")
                when (result) {
                    is Resource.Error -> {
                        _userMessage.send(result.message ?: "Can't get hme code")
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Loading -> _state.update {
                        it.copy(loading = true)
                    }

                    is Resource.Success -> {
                        val savedHmeId = getHmeIdUseCase()
                        val savedHmeCode = result.data?.find { it.id == savedHmeId }
                        savedHmeCode?.let {
                            hmeCodeSelected(it)
                        }
                        _state.update {
                            it.copy(
                                hmeCodes = result.data ?: emptyList(),
                                loading = false,
                                selectedHMECode = savedHmeCode
                            )
                        }
                    }
                }
            }
        }

    }

    private fun getIBAUsByHMECodeId(id: Long) {

        getIBAUJob?.cancel()

        getIBAUJob = viewModelScope.launch(Dispatchers.IO) {
            getIBAUCodeByHMECodeIdUseCase(id).collect { result ->
                Log.d("TAG", "getIBAUsByCustomerId: $result")
                when (result) {
                    is Resource.Error -> {
                        _userMessage.send(result.message ?: "Can't get IBAU code")
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Loading -> _state.update {
                        it.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                ibauCodes = result.data ?: emptyList(),
                                loading = false
                            )
                        }
                    }
                }
            }
        }

    }

    private fun customerSelected(customer: Customer) {
        _state.update {
            it.copy(
                selectedCustomer = customer
            )
        }
        viewModelScope.launch {
            setCustomerIdUseCase(customer.id!!)
        }
        getHMEsByCustomerId(customer.id!!)
    }


    private fun deleteIBAUCode(ibauCode: IBAUCode) {
        deleteIBAUCodeUseCase(ibauCode).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't delete IBAU Code")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update {
                    it.copy(loading = true)
                }

                is Resource.Success -> {
                    _state.update {
                        it.copy(loading = false)
                    }
                    state.value.selectedHMECode?.let {
                        getIBAUsByHMECodeId(it.id!!)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun hmeCodeSelected(hmeCode: HMECode) {
        _state.update {
            it.copy(
                selectedHMECode = hmeCode,
            )
        }
        viewModelScope.launch {
            setHMEIdUseCase(hmeCode.id!!)
        }
        getIBAUsByHMECodeId(hmeCode.id!!)
    }

    private fun ibauCodeSelected(ibauCode: IBAUCode) {
        _state.update {
            it.copy(
                selectedIBAUCode = ibauCode,
                ibauCode = ibauCode.code,
                machineNumber = ibauCode.machineNumber,
                machineType = ibauCode.machineType,
                workDescription = ibauCode.workDescription
            )
        }
    }

    private fun ibauCodeChanged(ibauCode: String) {
        _state.update {
            it.copy(ibauCode = ibauCode)
        }
    }

    private fun machineTypeChanged(machineType: String) {
        _state.update {
            it.copy(machineType = machineType)
        }
    }

    private fun machineNumberChanged(machineNumber: String) {
        _state.update {
            it.copy(machineNumber = machineNumber)
        }
    }

    private fun workDescriptionChanged(
        workDescription: String
    ) {
        _state.update {
            it.copy(workDescription = workDescription)
        }
    }

    fun userEvent(event: IbauCodeUserEvent) {
        when (event) {
            is IbauCodeUserEvent.CustomerSelected -> customerSelected(event.customer)
            is IbauCodeUserEvent.DeleteIBAUCode -> deleteIBAUCode(event.ibauCode)
            is IbauCodeUserEvent.HmeCodeSelected -> hmeCodeSelected(event.hmeCode)
            is IbauCodeUserEvent.IbauCodeChanged -> ibauCodeChanged(event.ibauCode)
            is IbauCodeUserEvent.IbauCodeSelected -> ibauCodeSelected(event.ibauCode)
            is IbauCodeUserEvent.MachineNumberChanged -> machineNumberChanged(event.machineNumber)
            is IbauCodeUserEvent.MachineTypeChanged -> machineTypeChanged(event.machineType)
            IbauCodeUserEvent.SaveIBAUCode -> saveIBAUCode()
            IbauCodeUserEvent.UpdateIBAUCode -> updateIBAUCode()
            is IbauCodeUserEvent.WorkDescriptionChanged -> workDescriptionChanged(event.description)
        }
    }
}