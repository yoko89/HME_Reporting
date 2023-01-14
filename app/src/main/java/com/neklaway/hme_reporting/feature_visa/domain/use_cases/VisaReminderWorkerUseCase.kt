package com.neklaway.hme_reporting.feature_visa.domain.use_cases

import android.app.Application
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.neklaway.hme_reporting.feature_visa.data.worker.VisaNotificationWorker
import com.neklaway.hme_reporting.utils.Constants
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private const val TAG = "VisaReminderWorkerUseCase"

class VisaReminderWorkerUseCase @Inject constructor(
    val app: Application,
) {

    private val workManager = WorkManager.getInstance(app)

    operator fun invoke() {

        Log.d(TAG, "invoke: Visa reminder use case started")
        val visaWorkRequest =
            PeriodicWorkRequestBuilder<VisaNotificationWorker>(1, TimeUnit.DAYS).build()

        workManager.enqueueUniquePeriodicWork(
            Constants.Visa_WORKER_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            visaWorkRequest
        )
    }
}