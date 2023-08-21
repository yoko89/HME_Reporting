package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_pdf_worker_use_case

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.*
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.feature_expanse_sheet.data.worker.ExpenseSheetPDFCreatorWorker
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense
import com.neklaway.hme_reporting.utils.Constants
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ExpansePDFWorkerUseCase @Inject constructor(
    val app: Application,
) {
    private val workManager = WorkManager.getInstance(app)

    operator fun invoke(
        timeSheets: List<TimeSheet>,
        expenseList: List<Expense>
    ): Flow<Resource<Unit>> {
        val serializedSelectedTimeSheets = Json.encodeToString(
            TimeSheet.listSerializer,
            timeSheets
        )
        val serializedSelectedExpenseList = Json.encodeToString(
            Expense.listSerializer,
            expenseList
        )

        val expansePdfWorkRequest =
            OneTimeWorkRequestBuilder<ExpenseSheetPDFCreatorWorker>().setInputData(
                workDataOf(
                    ExpenseSheetPDFCreatorWorker.TIME_SHEET_LIST_KEY to serializedSelectedTimeSheets,
                    ExpenseSheetPDFCreatorWorker.EXPENSE_LIST_KEY to serializedSelectedExpenseList
                )
            ).setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

        workManager.beginUniqueWork(
            Constants.EXPANSE_WORKER_TAG,
            ExistingWorkPolicy.KEEP,
            expansePdfWorkRequest
        ).enqueue()

        val result =
            workManager.getWorkInfosForUniqueWorkLiveData(Constants.EXPANSE_WORKER_TAG).asFlow()

        return flow {
            result.collect { workInfoList ->
                return@collect when (workInfoList.find { it.id == expansePdfWorkRequest.id }?.state) {
                    WorkInfo.State.SUCCEEDED -> emit(Resource.Success(Unit))
                    WorkInfo.State.FAILED -> emit(Resource.Error("Expanse PDF Error"))
                    else -> emit(Resource.Loading())
                }
            }
        }
    }
}


fun <T> LiveData<T>.asFlow(): Flow<T> = callbackFlow {
    val observer = Observer<T> { value -> this.trySend(value) }
    observeForever(observer)
    awaitClose {
        removeObserver(observer)
    }
}.flowOn(Dispatchers.Main.immediate)