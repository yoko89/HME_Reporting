package com.neklaway.hme_reporting.feature_settings.data.worker

import android.Manifest
import android.app.Notification
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetAllCustomersUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetAllHMECodesUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.GetAllIBAUCodesUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.GetAllTimeSheetUseCase
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases.GetAllCarMileageUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetAllCurrencyExchangeUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.GetAllExpansesUseCase
import com.neklaway.hme_reporting.feature_visa.domain.model.Visa
import com.neklaway.hme_reporting.feature_visa.domain.use_cases.GetAllVisasUseCase
import com.neklaway.hme_reporting.utils.Constants
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.toDate
import com.neklaway.hme_reporting.utils.toTime
import com.neklaway.hmereporting.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.Calendar

private const val TAG = "BackupWorker"

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    val getAllCustomersUseCase: GetAllCustomersUseCase,
    val getAllHMECodesUseCase: GetAllHMECodesUseCase,
    val getAllIBAUCodesUseCase: GetAllIBAUCodesUseCase,
    val getAllTimeSheetUseCase: GetAllTimeSheetUseCase,
    val getAllVisasUseCase: GetAllVisasUseCase,
    val getAllCarMileage: GetAllCarMileageUseCase,
    val getAllCurrencyExchangeUseCase: GetAllCurrencyExchangeUseCase,
    val getAllExpansesUseCase: GetAllExpansesUseCase,
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(Constants.BACKUP_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(appContext, Constants.BACKUP_CHANNEL_ID)
            .setSmallIcon(R.drawable.hb_logo)
            .setContentTitle("BackUp on going")
            .setContentText("BackUp is under preparation")
            .build()
    }

    override suspend fun doWork(): Result {
        val resultError: MutableList<String> = mutableListOf()
        val calendar = Calendar.getInstance()
        val folderPath = Pair(
            MediaStore.Files.FileColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DOCUMENTS + "/hme_${calendar.toDate()}_${calendar.toTime()}"
        )

        val collection = MediaStore.Files.getContentUri("external")


        Log.d(TAG, "Started")

        getAllCustomersUseCase().run {
            val resource = this
            Log.d(TAG, "getCustomers: $resource")
            when (resource) {
                is Resource.Error -> resultError.add(resource.message ?: "Can't load Customer List")
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val serializedCustomer = Json.encodeToString(
                        ListSerializer(Customer.serializer()),
                        resource.data.orEmpty()
                    )


                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "01_customers")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: Customer file writing started")
                        appContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                appContext.contentResolver.openOutputStream(uri)
                                    .use { outputStream ->
                                        outputStream?.write(serializedCustomer.toByteArray())
                                        Log.d(TAG, "customer serialized: $serializedCustomer")
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                Log.d(TAG, "doWork: resolver use closed")
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        resultError.add("Customer " + e.message)
                        Log.d(TAG, "Customer: failed ${e.message}")
                    }

                    Log.d(TAG, "writing: stopped")

                }
            }
            Log.d(TAG, "get all customers")
        }

        getAllHMECodesUseCase().collect { resource ->

            Log.d(TAG, "getHme: $resource")
            when (resource) {
                is Resource.Error -> resultError.add(resource.message ?: "Can't load HME Code List")
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val serializedHme = Json.encodeToString(
                        ListSerializer(HMECode.serializer()),
                        resource.data.orEmpty()
                    )


                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "02_hme")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: HME file writing started")
                        appContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                appContext.contentResolver.openOutputStream(uri)
                                    .use { outputStream ->
                                        outputStream?.write(serializedHme.toByteArray())
                                        Log.d(TAG, "HME serialized: $serializedHme")
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                Log.d(TAG, "doWork: resolver use closed")
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        resultError.add("HME " + e.message)
                        Log.d(TAG, "HME: failed ${e.message}")
                    }
                    Log.d(TAG, "writing: stopped")
                }
            }
            Log.d(TAG, "get all hme")
        }

        getAllIBAUCodesUseCase().collect { resource ->

            Log.d(TAG, "getIbau: $resource")
            when (resource) {
                is Resource.Error -> resultError.add(resource.message ?: "Can't load IBAU List")
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val serializedIbau = Json.encodeToString(
                        ListSerializer(IBAUCode.serializer()),
                        resource.data.orEmpty()
                    )


                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "03_ibau")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: IBAU file writing started")
                        appContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                appContext.contentResolver.openOutputStream(uri)
                                    .use { outputStream ->
                                        outputStream?.write(serializedIbau.toByteArray())
                                        Log.d(TAG, "Ibau serialized: $serializedIbau")
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                Log.d(TAG, "doWork: resolver use closed")
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        resultError.add("IBAU " + e.message)
                        Log.d(TAG, "IBAU: failed ${e.message}")
                    }

                    Log.d(TAG, "writing: stopped")

                }
            }
            Log.d(TAG, "get all ibau")
        }

        getAllTimeSheetUseCase().apply {
            val resource = this

            Log.d(TAG, "getTimeSheet: $resource")
            when (resource) {
                is Resource.Error -> resultError.add(
                    resource.message ?: "Can't load TimeSheet List"
                )

                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val serializedTimeSheet = Json.encodeToString(
                        ListSerializer(TimeSheet.serializer()),
                        resource.data.orEmpty()
                    )


                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "04_timesheet")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: TimeSheet file writing started")
                        appContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                appContext.contentResolver.openOutputStream(uri)
                                    .use { outputStream ->
                                        outputStream?.write(serializedTimeSheet.toByteArray())
                                        Log.d(TAG, "Ibau serialized: $serializedTimeSheet")
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                Log.d(TAG, "doWork: resolver use closed")
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        resultError.add("TimeSheet " + e.message)
                        Log.d(TAG, "TimeSheet: failed ${e.message}")
                    }

                    Log.d(TAG, "writing: stopped")

                }
            }
            Log.d(TAG, "get all TimeSheet")
        }

        getAllVisasUseCase().apply {
            val resource = this

            Log.d(TAG, "getVisas: $resource")
            when (resource) {
                is Resource.Error -> resultError.add(resource.message ?: "Can't load Visa List")
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val serializedVisa = Json.encodeToString(
                        ListSerializer(Visa.serializer()),
                        resource.data.orEmpty()
                    )


                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "05_visa")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: Visa file writing started")
                        appContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                appContext.contentResolver.openOutputStream(uri)
                                    .use { outputStream ->
                                        outputStream?.write(serializedVisa.toByteArray())
                                        Log.d(TAG, "Visa serialized: $serializedVisa")
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                Log.d(TAG, "doWork: resolver use closed")
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        resultError.add("Visa " + e.message)
                        Log.d(TAG, "Visa: failed ${e.message}")
                    }

                    Log.d(TAG, "writing: stopped")

                }
            }
            Log.d(TAG, "get all Visa")
        }


        getAllCarMileage().let { resource ->

            Log.d(TAG, "getCarMileage: $resource")
            when (resource) {
                is Resource.Error -> resultError.add(
                    resource.message ?: "Can't load CarMileage List"
                )

                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val serializedCarMileage = Json.encodeToString(
                        ListSerializer(CarMileage.serializer()),
                        resource.data.orEmpty()
                    )


                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "06_car_mileage")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: Car Mileage file writing started")
                        appContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                appContext.contentResolver.openOutputStream(uri)
                                    .use { outputStream ->
                                        outputStream?.write(serializedCarMileage.toByteArray())
                                        Log.d(TAG, "HME serialized: $serializedCarMileage")
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                Log.d(TAG, "doWork: resolver use closed")
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        resultError.add("Car Mileage: " + e.message)
                        Log.d(TAG, "carMileage: failed ${e.message}")
                    }
                    Log.d(TAG, "writing: stopped")
                }
            }
            Log.d(TAG, "get all Car Mileage")
        }

        getAllCurrencyExchangeUseCase().let { resource ->

            Log.d(TAG, "getExpanse: $resource")
            when (resource) {
                is Resource.Error -> resultError.add(
                    resource.message ?: "Can't load Currency Exchange List"
                )

                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val serializedCurrencyExchange = Json.encodeToString(
                        ListSerializer(CurrencyExchange.serializer()),
                        resource.data.orEmpty()
                    )


                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "07_currency_exchange")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: Currency Exchange file writing started")
                        appContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                appContext.contentResolver.openOutputStream(uri)
                                    .use { outputStream ->
                                        outputStream?.write(serializedCurrencyExchange.toByteArray())
                                        Log.d(
                                            TAG,
                                            "Currency Exchange serialized: $serializedCurrencyExchange"
                                        )
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                Log.d(TAG, "doWork: resolver use closed")
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        resultError.add("Currency Exchanged: " + e.message)
                        Log.d(TAG, "Currency Exchanged: failed ${e.message}")
                    }
                    Log.d(TAG, "writing: stopped")
                }
            }
            Log.d(TAG, "get Currency Exchanged")
        }

        getAllExpansesUseCase().let { resource ->

            Log.d(TAG, "getExpanse: $resource")
            when (resource) {
                is Resource.Error -> resultError.add(resource.message ?: "Can't load Expanse List")
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    val serializedExpense = Json.encodeToString(
                        ListSerializer(Expense.serializer()),
                        resource.data.orEmpty()
                    )


                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "08_expanse")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: Expanse file writing started")
                        appContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                appContext.contentResolver.openOutputStream(uri)
                                    .use { outputStream ->
                                        outputStream?.write(serializedExpense.toByteArray())
                                        Log.d(TAG, "Expanse serialized: $serializedExpense")
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                Log.d(TAG, "doWork: resolver use closed")
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        resultError.add("Expanse: " + e.message)
                        Log.d(TAG, "Expanse: failed ${e.message}")
                    }
                    Log.d(TAG, "writing: stopped")
                }
            }
            Log.d(TAG, "get Expanse")
        }


        val internalStorageFiles = appContext.filesDir.listFiles()

        //Backup signatures and datastore
        internalStorageFiles?.filter {
            it.name.equals(Constants.SIGNATURES_FOLDER) or it.name.equals(
                "datastore"
            )
        }
            ?.onEach { files ->
                files.listFiles()?.onEach { file ->

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, file.name)
                        when (files.name) {
                            Constants.SIGNATURES_FOLDER -> {
                                put(MediaStore.Files.FileColumns.MIME_TYPE, "image/png")
                                put(
                                    folderPath.first,
                                    folderPath.second + "/" + Constants.SIGNATURES_FOLDER
                                )
                            }

                            "datastore" -> {
                                put(MediaStore.Files.FileColumns.MIME_TYPE, "*/*")
                                put(folderPath.first, folderPath.second + "/datastore")
                            }
                        }

                    }

                    try {
                        Log.d(TAG, "backup: File writing started")
                        appContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                appContext.contentResolver.openOutputStream(uri)
                                    .use { outputStream ->
                                        outputStream?.write(file.readBytes())
                                        Log.d(TAG, "File: ${file.name}")
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                Log.d(TAG, "doWork: resolver use closed")
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        resultError.add("File " + e.message)
                        Log.d(TAG, "File: failed ${file.name} ${e.message}")
                    }
                }

                Log.d(TAG, "writing: stopped")

            }

        //Backup rest of files and folders
        internalStorageFiles?.filter {
            !it.name.equals(Constants.SIGNATURES_FOLDER) or !it.name.equals(
                "datastore"
            )
        }
            ?.onEach { files ->
                files.listFiles()?.onEach { file ->

                    if (!file.isDirectory) {
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Files.FileColumns.DISPLAY_NAME, file.name)

                            put(MediaStore.Files.FileColumns.MIME_TYPE, "*/*")
                            put(
                                folderPath.first,
                                folderPath.second + "/" + files.name
                            )
                        }
                        try {
                            Log.d(TAG, "backup: File writing started")
                            appContext.contentResolver.insert(collection, contentValues)
                                ?.also { uri ->
                                    appContext.contentResolver.openOutputStream(uri)
                                        .use { outputStream ->
                                            outputStream?.write(file.readBytes())
                                            Log.d(TAG, "File: ${file.name}")
                                            outputStream?.flush()
                                            outputStream?.close()
                                        }
                                    Log.d(TAG, "doWork: resolver use closed")
                                }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            resultError.add("File " + e.message)
                            Log.d(TAG, "File: failed ${file.name} ${e.message}")
                        }
                    } else {
                        file.listFiles()?.onEach { expanseFile ->
                            val contentValues = ContentValues().apply {
                                put(MediaStore.Files.FileColumns.DISPLAY_NAME, expanseFile.name)

                                put(MediaStore.Files.FileColumns.MIME_TYPE, "*/*")
                                put(
                                    folderPath.first,
                                    folderPath.second + "/" + files.name + "/" + file.name
                                )
                            }
                            try {
                                Log.d(TAG, "backup: File writing started")
                                appContext.contentResolver.insert(collection, contentValues)
                                    ?.also { uri ->
                                        appContext.contentResolver.openOutputStream(uri)
                                            .use { outputStream ->
                                                outputStream?.write(expanseFile.readBytes())
                                                Log.d(TAG, "File: ${expanseFile.name}")
                                                outputStream?.flush()
                                                outputStream?.close()
                                            }
                                        Log.d(TAG, "doWork: resolver use closed")
                                    }
                            } catch (e: IOException) {
                                e.printStackTrace()
                                resultError.add("File " + e.message)
                                Log.d(TAG, "File: failed ${expanseFile.name} ${e.message}")
                            }
                        }
                    }
                }

                Log.d(TAG, "writing: stopped")

            }

        // Notification when done
        val notificationBuilder =
            NotificationCompat.Builder(appContext, Constants.BACKUP_CHANNEL_ID)
                .setSmallIcon(R.drawable.hb_logo)
                .setContentTitle("Backup is done")
                .setAutoCancel(true)

        if (resultError.isNotEmpty()) {
            notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(resultError.toString()))
        } else {
            notificationBuilder.setContentText("Backup Successful")

        }

        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(appContext)
                .notify(Constants.BACKUP_NOTIFICATION_ID, notificationBuilder.build())
        }


        Log.d(TAG, "doWork: closed")

        return if (resultError.isNotEmpty()) Result.failure() else Result.success()
    }
}