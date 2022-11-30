package com.neklaway.hme_reporting.feature_settings.data.worker

import android.app.Notification
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.Customer
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.HMECode
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.IBAUCode
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.TimeSheet
import com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.customer_use_cases.GetAllCustomersUseCase
import com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.hme_code_use_cases.GetAllHMECodesUseCase
import com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.ibau_code_use_cases.GetAllIBAUCodesUseCase
import com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.time_sheet_use_cases.GetAllTimeSheetUseCase
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
import java.util.*

private const val TAG = "BackupWorker"

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    val getAllCustomersUseCase: GetAllCustomersUseCase,
    val getAllHMECodesUseCase: GetAllHMECodesUseCase,
    val getAllIBAUCodesUseCase: GetAllIBAUCodesUseCase,
    val getAllTimeSheetUseCase: GetAllTimeSheetUseCase,
    val getAllVisasUseCase: GetAllVisasUseCase,
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(Constants.BACKUP_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, Constants.BACKUP_CHANNEL_ID)
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

                    val collection = MediaStore.Files.getContentUri("external")

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "customers")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: Customer file writing started")
                        applicationContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                applicationContext.contentResolver.openOutputStream(uri)
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

                    val collection = MediaStore.Files.getContentUri("external")

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "hme")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: HME file writing started")
                        applicationContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                applicationContext.contentResolver.openOutputStream(uri)
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

                    val collection = MediaStore.Files.getContentUri("external")

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "ibau")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: IBAU file writing started")
                        applicationContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                applicationContext.contentResolver.openOutputStream(uri)
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
                        Log.d(TAG, "Ibau: failed ${e.message}")
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

                    val collection = MediaStore.Files.getContentUri("external")

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "timesheet")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: TimeSheet file writing started")
                        applicationContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                applicationContext.contentResolver.openOutputStream(uri)
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

                    val collection = MediaStore.Files.getContentUri("external")

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "visa")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(folderPath.first, folderPath.second)
                    }
                    try {
                        Log.d(TAG, "backup: Visa file writing started")
                        applicationContext.contentResolver.insert(collection, contentValues)
                            ?.also { uri ->
                                applicationContext.contentResolver.openOutputStream(uri)
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
                        Log.d(TAG, "Visa: failed ${e.message}")
                    }

                    Log.d(TAG, "writing: stopped")

                }
            }
            Log.d(TAG, "get all Visa")
        }


        //TODO("Not yet implemented")
        Log.d(TAG, "doWork: closed")
        //val data = workDataOf(Pair("error",resultError))
        return if (resultError.isNotEmpty()) Result.failure() else Result.success()
    }
}