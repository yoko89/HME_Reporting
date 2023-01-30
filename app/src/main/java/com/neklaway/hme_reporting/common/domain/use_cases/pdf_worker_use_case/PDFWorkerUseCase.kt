package com.neklaway.hme_reporting.common.domain.use_cases.pdf_worker_use_case

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.*
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.feature_time_sheet.data.worker.PDFCreatorWorker
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

class PDFWorkerUseCase @Inject constructor(
    val app: Application,
) {
    private val workManager = WorkManager.getInstance(app)

    operator fun invoke(timeSheets: List<TimeSheet>): Flow<Resource<Unit>> {
        val serializedSelectedTimeSheets = Json.encodeToString(
            TimeSheet.listSerializer,
            timeSheets
        )
        val pdfWorkRequest = OneTimeWorkRequestBuilder<PDFCreatorWorker>().setInputData(
            workDataOf(
                PDFCreatorWorker.TIME_SHEET_LIST_KEY to serializedSelectedTimeSheets
            )
        ).setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.beginUniqueWork(
            Constants.PDF_WORKER_TAG,
            ExistingWorkPolicy.KEEP,
            pdfWorkRequest
        ).enqueue()

        val result =
            workManager.getWorkInfosForUniqueWorkLiveData(Constants.PDF_WORKER_TAG).asFlow()

        return flow {
            result.collect { workInfoList ->
                return@collect when (workInfoList.find { it.id == pdfWorkRequest.id }?.state) {
                    WorkInfo.State.SUCCEEDED -> emit(Resource.Success(Unit))
                    WorkInfo.State.FAILED -> emit(Resource.Error("PDF Error"))
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