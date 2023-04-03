package com.neklaway.hme_reporting

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.neklaway.hme_reporting.utils.Constants
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class HMEReportingApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        //Notification Manager
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //PDF Notification channel
        val pdfNotificationChannel = NotificationChannel(
            Constants.PDF_CHANNEL_ID,
            Constants.PDF_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(pdfNotificationChannel)

        //Expanse PDF Notification channel
        val expansePdfNotificationChannel = NotificationChannel(
            Constants.EXPANSE_PDF_CHANNEL_ID,
            Constants.EXPANSE_PDF_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(expansePdfNotificationChannel)

        //VISA Notification channel
        val visaNotificationChannel = NotificationChannel(
            Constants.VISA_CHANNEL_ID,
            Constants.VISA_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(visaNotificationChannel)

        //BACKUP Notification channel
        val backupNotificationChannel = NotificationChannel(
            Constants.BACKUP_CHANNEL_ID,
            Constants.BACKUP_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(backupNotificationChannel)

        //RESTORE Notification channel
        val restoreNotificationChannel = NotificationChannel(
            Constants.RESTORE_CHANNEL_ID,
            Constants.RESTORE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(restoreNotificationChannel)


    }

}