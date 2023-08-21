package com.neklaway.hme_reporting.feature_settings.data.worker

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.neklaway.hme_reporting.common.domain.model.*
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.InsertCustomerListUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.InsertHMECodeListUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.InsertIBAUCodeListUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.InsertTimeSheetListUseCase
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases.InsertCarMileageListUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.InsertCurrencyExchangeListUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.InsertExpanseListUseCase
import com.neklaway.hme_reporting.feature_settings.data.broadcast.RestartReceiver
import com.neklaway.hme_reporting.feature_visa.domain.model.Visa
import com.neklaway.hme_reporting.feature_visa.domain.use_cases.InsertVisaListUseCase
import com.neklaway.hme_reporting.utils.Constants
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.copyFiles
import com.neklaway.hmereporting.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

private const val TAG = "RestoreWorker"

@HiltWorker
class RestoreWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParameters: WorkerParameters,
    val insertCustomerListUseCase: InsertCustomerListUseCase,
    val insertHMECodeListUseCase: InsertHMECodeListUseCase,
    val insertIBAUCodeListUseCase: InsertIBAUCodeListUseCase,
    val insertTimeSheetListUseCase: InsertTimeSheetListUseCase,
    val insertVisaListUseCase: InsertVisaListUseCase,
    val insertCarMileageListUseCase: InsertCarMileageListUseCase,
    val insertExpanseListUseCase: InsertExpanseListUseCase,
    val insertCurrencyExchangeListUseCase: InsertCurrencyExchangeListUseCase
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(Constants.BACKUP_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, Constants.RESTORE_CHANNEL_ID)
            .setSmallIcon(R.drawable.hb_logo)
            .setContentTitle("Restore on going")
            .setContentText("Restore is under going")
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun doWork(): Result {
        val uriString = inputData.getString("folder_uri") ?: return Result.failure()
        val uri = Uri.parse(uriString)
        val folder = DocumentFile.fromTreeUri(applicationContext, uri)
        val filesRestored = mutableMapOf(
            "customer" to false,
            "hme" to false,
            "ibau" to false,
            "timesheet" to false,
            "visa" to false,
            "car_mileage" to false,
            "currency_Exchange" to false,
            "expanse" to false
        )


        Log.d(TAG, "Started for ${folder?.listFiles()}")


        folder?.listFiles()?.forEach { files ->
            Log.d(TAG, "doWork: ${files.name}")

            val backupInputStream =
                applicationContext.contentResolver.openInputStream(files.uri) ?: return@forEach
            when (files.name) {
                "01_customers.json" -> {
                    val customers = Json.decodeFromStream(
                        ListSerializer(Customer.serializer()),
                        backupInputStream
                    )
                    Log.d(TAG, "doWork:Deserialized customers $customers")
                    insertCustomerListUseCase(customers).collect { resource ->
                        when (resource) {
                            is Resource.Success -> filesRestored["customer"] = true
                            else -> Unit
                        }
                    }
                }

                "02_hme.json" -> {
                    val hmes = Json.decodeFromStream(
                        ListSerializer(HMECode.serializer()),
                        backupInputStream
                    )
                    Log.d(TAG, "doWork:Deserialized hmes $hmes")
                    insertHMECodeListUseCase(hmes).collect { resource ->
                        when (resource) {
                            is Resource.Success -> filesRestored["hme"] = true
                            else -> Unit
                        }
                    }
                }

                "03_ibau.json" -> {
                    val ibaus = Json.decodeFromStream(
                        ListSerializer(IBAUCode.serializer()),
                        backupInputStream
                    )
                    Log.d(TAG, "doWork:Deserialized ibaus $ibaus")
                    insertIBAUCodeListUseCase(ibaus).collect { resource ->
                        when (resource) {
                            is Resource.Success -> filesRestored["ibau"] = true
                            else -> Unit
                        }
                    }
                }

                "04_timesheet.json" -> {
                    val timesheets = Json.decodeFromStream(
                        ListSerializer(TimeSheet.serializer()),
                        backupInputStream
                    )
                    Log.d(TAG, "doWork:Deserialized timesheets $timesheets")
                    insertTimeSheetListUseCase(timesheets).collect { resource ->
                        when (resource) {
                            is Resource.Success -> filesRestored["timesheet"] = true
                            else -> Unit
                        }
                    }
                }

                "05_visa.json" -> {
                    val visas = Json.decodeFromStream(
                        ListSerializer(Visa.serializer()),
                        backupInputStream
                    )
                    Log.d(TAG, "doWork:Deserialized Visas $visas")
                    insertVisaListUseCase(visas).collect { resource ->
                        when (resource) {
                            is Resource.Success -> filesRestored["visa"] = true
                            else -> Unit
                        }
                    }
                }

                "06_car_mileage.json" -> {
                    val carMileageList = Json.decodeFromStream(
                        ListSerializer(CarMileage.serializer()),
                        backupInputStream
                    )
                    Log.d(TAG, "doWork:Deserialized Car Mileage $carMileageList")
                    insertCarMileageListUseCase(carMileageList).collect { resource ->
                        when (resource) {
                            is Resource.Success -> filesRestored["car_mileage"] = true
                            else -> Unit
                        }
                    }
                }

                "07_currency_exchange.json" -> {
                    val currencyList = Json.decodeFromStream(
                        ListSerializer(CurrencyExchange.serializer()),
                        backupInputStream
                    )
                    Log.d(TAG, "doWork:Deserialized Expanse $currencyList")
                    insertCurrencyExchangeListUseCase(currencyList).collect { resource ->
                        when (resource) {
                            is Resource.Success -> filesRestored["currency_Exchange"] = true
                            else -> Unit
                        }
                    }
                }

                "08_expanse.json" -> {
                    val expenseList = Json.decodeFromStream(
                        ListSerializer(Expense.serializer()),
                        backupInputStream
                    )
                    Log.d(TAG, "doWork:Deserialized Expanse $expenseList")
                    insertExpanseListUseCase(expenseList).collect { resource ->
                        when (resource) {
                            is Resource.Success -> filesRestored["expanse"] = true
                            else -> Unit
                        }
                    }
                }

                else -> {
                    if (files.name == null) return@forEach
                    val internalStorageFiles = applicationContext.filesDir.listFiles()
                    val backedFolder = File(applicationContext.filesDir, files.name!!)
                    Log.d(TAG, "doWork: $backedFolder")
                    internalStorageFiles?.let {
                        if (files.isDirectory) {
                            if (!internalStorageFiles.any { it.name == files.name })
                                backedFolder.mkdir()
                        }
                    }
                    Log.d(TAG, "doWork: ${files.listFiles().map { it.name }}")
                    files.listFiles().forEach listForeach@{ file ->

                        val backupInputStreamFile =
                            applicationContext.contentResolver.openInputStream(file.uri)
                                ?: return@listForeach
                        val restoreFile = File(backedFolder, file.name!!)
                        Log.d(TAG, "doWork: ${file.name}")

                        copyFiles(backupInputStreamFile, restoreFile)
                    }
                }

            }
        }

        Log.d(TAG, "doWork: $filesRestored")

        // Notification when done

        // Intent
        val intent = Intent(appContext, RestartReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(appContext, 123456, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, Constants.RESTORE_CHANNEL_ID)
                .setSmallIcon(R.drawable.hb_logo)
                .setContentTitle("Restore is done")
                .addAction(R.drawable.hb_logo, "Restart App", pendingIntent)
                .setAutoCancel(true)


        var notificationText = if (filesRestored.values.any { !it }) {
            filesRestored.filter {it ->
                !it.value
            }.toString()
        } else {
            "Restore Successful"
        }
        notificationText += "\n App must restart"

        notificationBuilder.setContentText(notificationText)

        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(applicationContext)
                .notify(Constants.RESTORE_NOTIFICATION_ID, notificationBuilder.build())
        }


        Log.d(TAG, "doWork: closed")




        return if (filesRestored.values.any {
                !it
            }) Result.failure() else Result.success()
    }
}