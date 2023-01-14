package com.neklaway.hme_reporting.feature_visa.data.worker

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder.GetVisaReminderUseCase
import com.neklaway.hme_reporting.common.domain.visa_use_cases.GetAllVisasUseCase
import com.neklaway.hme_reporting.utils.Constants
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.toDate
import com.neklaway.hmereporting.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.temporal.ChronoUnit
import java.util.*


private const val TAG = "VisaNotificationWorker"

@HiltWorker
class VisaNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    val getAllVisasUseCase: GetAllVisasUseCase,
    val getVisaReminderUseCase: GetVisaReminderUseCase
) : CoroutineWorker(appContext, workerParameters) {



    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(Constants.VISA_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, Constants.VISA_CHANNEL_ID)
            .setSmallIcon(R.drawable.hb_logo)
            .setContentTitle("Visa Reminder")
            .setContentText("Visa reminder Checking")
            .build()
    }

    override suspend fun doWork(
    ): Result {

        Log.d(TAG, "doWork: Notification reminder started")

        var result: Result? = null

        val currentDay = Calendar.getInstance()
        currentDay.set(Calendar.HOUR_OF_DAY, 0)
        currentDay.set(Calendar.MINUTE, 0)
        currentDay.set(Calendar.SECOND, 0)
        currentDay.set(Calendar.MILLISECOND, 0)

        val visaReminderDays = getVisaReminderUseCase()

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        getAllVisasUseCase().apply {

            when (val resource = this) {
                is Resource.Error -> result = Result.failure()
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    Log.d(TAG, "Visa Data: collected Successfully")
                    val visaList = resource.data
                    visaList?.forEach { visa ->
                        if (visa.selected) {
                            val days =
                                ChronoUnit.DAYS.between(
                                    currentDay.toInstant(),
                                    visa.date.toInstant()
                                )
                            var massage: String? = null
                            if (days <= 0) {
                                massage =
                                    "Visa for ${visa.country} expired on ${visa.date.toDate()}"
                            } else if (days <= visaReminderDays) {
                                massage =
                                    "Visa for ${visa.country} will expire on ${visa.date.toDate()}"
                            }
                            Log.d(TAG, "${visa.country}: should trigger")
                            massage?.let {
                                val notification = NotificationCompat.Builder(
                                    applicationContext,
                                    Constants.VISA_CHANNEL_ID
                                )
                                    .setSmallIcon(R.drawable.hb_logo)
                                    .setContentTitle("Visa Reminder")
                                    .setContentText(massage)
                                    .build()
                                notificationManager.notify(visa.id!!.toInt(), notification)
                            }
                        }
                    }


                    result = Result.success()
                }
            }

        }

        Log.d(TAG, "doWork: finished")
        return result ?: Result.failure()

    }
}