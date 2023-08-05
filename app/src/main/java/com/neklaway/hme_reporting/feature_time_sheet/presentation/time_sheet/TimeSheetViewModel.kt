package com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetAllCustomersFlowUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.UpdateHMECodeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.GetIBAUCodeByHMECodeIdUseCase
import com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.time_sheet_pdf_worker_use_case.PDFWorkerUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.GetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.SetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.GetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.SetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.ibau_id.GetIBAUIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.ibau_id.SetIBAUIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.GetTimeSheetByHMECodeIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.GetTimeSheetByIBAUCodeIdUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case.LoadBitmapUseCase
import com.neklaway.hme_reporting.utils.Constants
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

private const val TAG = "TimeSheetViewModel"

@HiltViewModel
class TimeSheetViewModel @Inject constructor(
    private val getAllCustomersFlowUseCase: GetAllCustomersFlowUseCase,
    private val getHMECodeByCustomerIdUseCase: GetHMECodeByCustomerIdUseCase,
    private val getIBAUCodeByHMECodeIdUseCase: GetIBAUCodeByHMECodeIdUseCase,
    private val getTimeSheetByHMECodeIdUseCase: GetTimeSheetByHMECodeIdUseCase,
    private val getTimeSheetByIBAUCodeIdUseCase: GetTimeSheetByIBAUCodeIdUseCase,
    private val getIsIBAUUseCase: GetIsIbauUseCase,
    private val getCustomerIdUseCase: GetCustomerIdUseCase,
    private val setCustomerIdUseCase: SetCustomerIdUseCase,
    private val getHmeIdUseCase: GetHMEIdUseCase,
    private val setHMEIdUseCase: SetHMEIdUseCase,
    private val getIBAUIdUseCase: GetIBAUIdUseCase,
    private val setIBAUIdUseCase: SetIBAUIdUseCase,
    private val loadBitmapUseCase: LoadBitmapUseCase,
    private val updateHMECodeUseCase: UpdateHMECodeUseCase,
    private val pdfWorkerUseCase: PDFWorkerUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TimeSheetState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<TimeSheetUiEvents>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var isIbau: Boolean = false


    init {
        getCustomers()
        viewModelScope.launch {
            isIbau = getIsIBAUUseCase()
        }
    }

    private fun getCustomers() {
        getAllCustomersFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    sendEvent(
                        TimeSheetUiEvents.UserMessage(
                            result.message ?: "Can't get customers"
                        )
                    )
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    val savedCustomerId = getCustomerIdUseCase()
                    val savedSelectedCustomer = result.data?.find { it.id == savedCustomerId }
                    _state.update {
                        it.copy(
                            customers = result.data.orEmpty(),
                            loading = false,
                            selectedCustomer = savedSelectedCustomer,
                            isIbau = isIbau
                        )
                    }
                    savedSelectedCustomer?.let {
                        customerSelected(it)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun customerSelected(customer: Customer) {
        _state.update {
            TimeSheetState(
                customers = it.customers,
                selectedCustomer = customer
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            setCustomerIdUseCase(customer.id!!)

            getHMECodeByCustomerIdUseCase(customer.id).onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        sendEvent(
                            TimeSheetUiEvents.UserMessage(
                                result.message ?: "Can't get HME codes"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        val savedSelectedHmeId = getHmeIdUseCase()
                        val savedSelectedHme = result.data?.find { it.id == savedSelectedHmeId }
                        _state.update {
                            TimeSheetState(
                                customers = it.customers,
                                selectedCustomer = it.selectedCustomer,
                                hmeCodes = result.data.orEmpty(),
                                selectedHMECode = savedSelectedHme,
                                isIbau = isIbau
                            )
                        }
                        savedSelectedHme?.let {
                            hmeSelected(it)
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun hmeSelected(hmeCode: HMECode) {


        _state.update {
            TimeSheetState(
                customers = it.customers,
                selectedCustomer = it.selectedCustomer,
                hmeCodes = it.hmeCodes,
                selectedHMECode = hmeCode,
            )

        }

        viewModelScope.launch(Dispatchers.IO) {
            hmeCode.id?.let { id ->
                val result = loadBitmapUseCase.invoke(Constants.SIGNATURES_FOLDER, id.toString())
                Log.d(TAG, "hmeSelected: Signature available = $result")
                if (result is Resource.Success) _state.update { it.copy(signatureAvailable = true) }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            setHMEIdUseCase(hmeCode.id!!)

            if (getIsIBAUUseCase()) {
                getIBAUCodeByHMECodeIdUseCase(hmeCode.id).collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            sendEvent(
                                TimeSheetUiEvents.UserMessage(
                                    result.message ?: "Can't get IBAU codes"
                                )
                            )
                            _state.update { it.copy(loading = false) }
                        }
                        is Resource.Loading -> _state.update { it.copy(loading = true) }
                        is Resource.Success -> {
                            val savedSelectedIbauId = getIBAUIdUseCase()
                            val savedSelectedIbau =
                                result.data?.find { it.id == savedSelectedIbauId }
                            _state.update {
                                TimeSheetState(
                                    customers = it.customers,
                                    selectedCustomer = it.selectedCustomer,
                                    hmeCodes = it.hmeCodes,
                                    selectedHMECode = hmeCode,
                                    ibauCodes = result.data.orEmpty(),
                                    loading = false,
                                    selectedIBAUCode = savedSelectedIbau,
                                    isIbau = isIbau,
                                    signatureAvailable = it.signatureAvailable
                                )
                            }
                            savedSelectedIbau?.let {
                                ibauSelected(it)
                            }
                        }
                    }
                }
            } else {
                getTimeSheetByHMECodeIdUseCase(hmeCode.id).collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            sendEvent(
                                TimeSheetUiEvents.UserMessage(
                                    result.message ?: "Can't get Time Sheets"
                                )
                            )
                            _state.update { it.copy(loading = false) }
                        }
                        is Resource.Loading -> _state.update { it.copy(loading = true) }
                        is Resource.Success -> {
                            Log.d(TAG, "get timesheet by HME: success ")
                            result.data?.let { timeSheetList ->
                                val timeSheetsListSorted = timeSheetList.sortedWith(
                                    compareBy({ it.date },
                                        { it.travelStart })
                                )
                                Log.d(TAG, "get timesheet by HME: collect $timeSheetList ")
                                val timeSheetListWithOverlapping =
                                    updateTimesheetListWithOverlapping(timeSheetsListSorted)

                                allCheckBoxSelected(timeSheetList)
                                _state.update {
                                    it.copy(
                                        timeSheets = timeSheetListWithOverlapping,
                                        loading = false,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun ibauSelected(ibauCode: IBAUCode) {
        _state.update {
            it.copy(
                selectedIBAUCode = ibauCode
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            setIBAUIdUseCase(ibauCode.id!!)

            getTimeSheetByIBAUCodeIdUseCase(ibauCode.id).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        sendEvent(
                            TimeSheetUiEvents.UserMessage(
                                result.message ?: "Can't get time sheet"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        Log.d(TAG, "get timesheet by ibau: success ")
                        result.data?.collect { timeSheetList ->
                            val timeSheetsListSorted =
                                timeSheetList.sortedWith(compareBy({ it.date }, { it.travelStart }))
                            Log.d(TAG, "get timesheet by ibau: collect $timeSheetList ")

                            val timeSheetListWithOverlapping =
                                updateTimesheetListWithOverlapping(timeSheetsListSorted)

                            allCheckBoxSelected(timeSheetList)

                            _state.update {
                                it.copy(
                                    timeSheets = timeSheetListWithOverlapping,
                                    loading = false,
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    private fun sheetSelectedChanged(timeSheet: TimeSheet, selected: Boolean) {
        val index = _state.value.timeSheets.indexOf(timeSheet)
        if (index != -1) {
            val newTimeSheets = state.value.timeSheets.toMutableList()
            newTimeSheets[index] = newTimeSheets[index].copy(selected = selected)
            _state.update { it.copy(timeSheets = newTimeSheets) }
        }
        allCheckBoxSelected(state.value.timeSheets)

    }

    private fun timesheetClicked(timeSheet: TimeSheet) {
        timeSheet.id?.let {
            viewModelScope.launch {
                sendEvent(TimeSheetUiEvents.NavigateToTimeSheetUi(it))
            }
        }
    }

    private fun selectAll(selected: Boolean) {
        val timeSheets = state.value.timeSheets.map {
            it.copy(selected = selected)
        }
        _state.update { it.copy(timeSheets = timeSheets, selectAll = selected) }
    }

    private fun showMoreFABClicked() {
        _state.update { it.copy(fabVisible = !it.fabVisible) }
    }

    private fun openTimeSheets() {
        _state.update { it.copy(showFileList = true) }
    }

    private fun createTimeSheet() {
        viewModelScope.launch {
            pdfWorkerUseCase(state.value.timeSheets.filter { it.selected }).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _state.update { it.copy(loading = false) }
                        delay(1000)
                        sendEvent(TimeSheetUiEvents.UserMessage(resource.message?:"PDF Creation Error"))
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        sendEvent(TimeSheetUiEvents.UserMessage("PDF Created"))
                        _state.update { it.copy(loading = false) }
                    }
                }
                Log.d(TAG, "createTimeSheet: $resource")
            }
        }
    }

    private fun sign() {
        _state.update { it.copy(showSignaturePad = true, fabVisible = false) }
    }

    private suspend fun sendEvent(event: TimeSheetUiEvents) {
        _uiEvent.send(event)
    }

    private fun signatureDone(signerName: String?) {
        _state.update { it.copy(showSignaturePad = false) }

        Log.d(TAG, "signatureDone: ")
        state.value.selectedHMECode?.let { hmeCode ->

            Log.d(TAG, "signatureDone: hme selected")
            viewModelScope.launch {
                updateHMECodeUseCase.invoke(
                    id = hmeCode.id!!,
                    customerId = hmeCode.customerId,
                    code = hmeCode.code,
                    machineType = hmeCode.machineType,
                    machineNumber = hmeCode.machineNumber,
                    workDescription = hmeCode.workDescription,
                    fileNumber = hmeCode.fileNumber,
                    expanseNumber = hmeCode.expanseNumber,
                    signerName = signerName,
                    signatureDate = Calendar.getInstance()
                ).collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            sendEvent(TimeSheetUiEvents.UserMessage("can't save Signer Name"))
                            _state.update { it.copy(loading = false, signatureAvailable = false) }
                        }
                        is Resource.Loading -> _state.update { it.copy(loading = true) }
                        is Resource.Success -> {
                            _state.update { it.copy(loading = false, signatureAvailable = true) }
                        }
                    }
                    Log.d(TAG, "signatureDone: $resource")
                }
            }

        }

    }

    private fun signatureCanceled() {
        _state.update { it.copy(showSignaturePad = false) }
    }

    private fun fileSelected(file: File) {
        _state.update { it.copy(showFileList = false) }
        viewModelScope.launch {
            sendEvent(TimeSheetUiEvents.ShowFile(file))
        }
    }

    private fun fileSelectionCanceled() {
        _state.update { it.copy(showFileList = false) }
    }

    private fun updateTimesheetListWithOverlapping(timeSheetList: List<TimeSheet>): List<TimeSheet> {
        val timeSheetCollectedByDate = timeSheetList.groupBy { it.date }

        timeSheetList.forEach {
            if ((timeSheetCollectedByDate[it.date]?.size ?: 1) > 1) {
                it.overLap = true
            }
        }

        return timeSheetList
    }

    private fun allCheckBoxSelected(timeSheetList: List<TimeSheet>) {
        val checkBoxAllSelected = timeSheetList.all { it.selected }
        _state.update { it.copy(selectAll = checkBoxAllSelected) }
    }

    fun userEvents(event:TimeSheetUserEvents){
        when(event){
            TimeSheetUserEvents.Sign -> sign()
            TimeSheetUserEvents.CreateTimeSheet -> createTimeSheet()
            is TimeSheetUserEvents.CustomerSelected -> customerSelected(event.customer)
            is TimeSheetUserEvents.HmeSelected -> hmeSelected(event.hmeCode)
            is TimeSheetUserEvents.IbauSelected -> ibauSelected(event.ibauCode)
            TimeSheetUserEvents.OpenTimeSheets -> openTimeSheets()
            is TimeSheetUserEvents.SelectAll -> selectAll(event.checked)
            is TimeSheetUserEvents.SheetSelectedChanged -> sheetSelectedChanged(event.timeSheet,event.checked)
            TimeSheetUserEvents.ShowMoreFABClicked -> showMoreFABClicked()
            TimeSheetUserEvents.SignatureCanceled -> signatureCanceled()
            is TimeSheetUserEvents.SignatureDone -> signatureDone(event.signerName)
            is TimeSheetUserEvents.FileSelected -> fileSelected(event.file)
            TimeSheetUserEvents.FileSelectionCanceled -> fileSelectionCanceled()
            is TimeSheetUserEvents.TimesheetClicked -> timesheetClicked(event.timeSheet)
        }
    }


}