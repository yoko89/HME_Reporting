package com.neklaway.hme_reporting.feature_expanse_sheet.data.worker

import android.app.Notification
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.neklaway.hme_reporting.common.data.entity.AllowanceType
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetCustomerByIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.UpdateHMECodeUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetCurrencyExchangeByIdUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.Get8HDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetFullDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetSavingDeductibleUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name.GetUserNameUseCase
import com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case.LoadBitmapUseCase
import com.neklaway.hme_reporting.utils.*
import com.neklaway.hmereporting.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

private const val TAG = "ExpansePDFCreatorWorker"
//Times new roman font

private const val HEADER_TEXT_SIZE = 20f
private const val HEADER_DATA_TEXT_SIZE = 12f
private const val NORMAL_TEXT_SIZE = 10f

// Page Size
private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842

//Invoice locations
private const val HME_CODE_X = 30f
private const val HME_CODE_Y = 30f
private const val INVOICE_DESCRIPTION_X = 250f
private const val INVOICE_DESCRIPTION_Y = 30f
private const val INVOICE_AMOUNT_AND_CURRENCY_X = 30f
private const val INVOICE_AMOUNT_AND_CURRENCY_Y = 50f
private const val INVOICE_AMOUNT_AED_X = 250f
private const val INVOICE_AMOUNT_AED_Y = 50f

//Invoice Image Location
private const val INVOICE_IMAGE_TOP_POSITION = 150f
private const val INVOICE_IMAGE_LEFT_POSITION = 10f

//Invoice Image max size
private const val IMAGE_MAX_WIDTH = PAGE_WIDTH - (2 * INVOICE_IMAGE_LEFT_POSITION)
private const val IMAGE_MAX_HEIGHT = PAGE_HEIGHT - (2 * INVOICE_IMAGE_TOP_POSITION)

// Title position
private const val TITLE_X = 10f
private const val TITLE_Y = 5f

// Boarder
private const val LEFT_BORDER = 5f
private const val TOP_BORDER = 5f
private const val RIGHT_BORDER = 590f
private const val BOTTOM_BORDER = 838f
private const val ROUND_X_BORDER = 5f
private const val ROUND_Y_BORDER = 5f


// LINES Constants

private const val THICK_LINE_STROKE_WIDTH = 3f
private const val THICK_LINE_COLOR = Color.BLUE

private const val NORMAL_LINE_COLOR = Color.BLUE

private const val TABLE_THICK_BORDER_WIDTH = 1f

//Table Header Shift Constants
private const val Y_TABLE_TOP = 210f
private const val X_TABLE_LEFT = 15f
private const val TABLE_WIDTH = 560f
private const val SUMMARY_COLUMN_SHIFT = TABLE_WIDTH / 8
private const val DETAILS_COLUMN_SHIFT = TABLE_WIDTH / 5
private const val Expanse_COLUMN_SHIFT = TABLE_WIDTH / 7


//Y position start initialization

//HME Logo and data
private const val LOGO_X_START = 420f
private const val LOGO_X_END = 580f
private const val LOGO_Y_START = 10f
private const val LOGO_Y_END = 83f
private const val DATA_Y = LOGO_Y_END + 2f

//Data part
private const val LEFT_DATA_START = 10f
private const val LEFT_DATA_END = 410f
private const val RIGHT_DATA_START = 220f
private const val DATA_TOP = 50f


//Fill Data Shifts
private const val SERVICE_ENGINEER_SHIFT = 82
private const val CUSTOMER_SHIFT = 55
private const val DEPARTURE_SHIFT = 55
private const val HME_CODE_SHIFT = 95
private const val ARRIVAL_SHIFT = 45
private const val CITY_SHIFT = 60
private const val COUNTRY_SHIFT = 80

// PDF Tables Shifts
private const val PARTICULARS_HEADER_SHIFT = 25
private const val CATEGORY_HEADER_SHIFT = 5
private const val LESS24H_HEADER_SHIFT = 30
private const val LESS24H_SHIFT = 5
private const val FULL24H_SHIFT = 5
private const val FULL24H_HEADER_SHIFT = 30
private const val NO_ALLOWANCE_HEADER_SHIFT = 25
private const val LESS24H_MARK_SHIFT = X_TABLE_LEFT + 3 * DETAILS_COLUMN_SHIFT + 50
private const val FULL24H_MARK_SHIFT = X_TABLE_LEFT + 2 * DETAILS_COLUMN_SHIFT + 50
private const val NO_ALLOWANCE_MARK_SHIFT = X_TABLE_LEFT + 4 * DETAILS_COLUMN_SHIFT + 50
private const val SUB_TOTAL_SHIFT = 5
private const val DAYS_COUNT_SHIFT = 25
private const val NO_OF_DAYS_HEADER_SHIFT = 5
private const val DAY_SHIFT = 5
private const val TOTAL_DAILY_ALLOWANCE_HEADER_SHIFT = 25
private const val RATE_HEADER_SHIFT = 25
private const val RATE_SHIFT = 25
private const val AMOUNT_HEADER_SHIFT = 15
private const val EXPANSE_AMOUNT_HEADER_SHIFT = 5
private const val EXPANSE_AMOUNT_SHIFT = 5
private const val AMOUNT_SHIFT = 25
private const val DATE_SHIFT = 2
private const val ACCRUED_SAVING_HEADER_SHIFT = 25
private const val AMOUNT_PAYABLE_HEADER_SHIFT = 25
private const val EXPANSE_SUMMERY_HEADER_SHIFT = 25
private const val TOTAL_PAYABLE_AMOUNT_SHIFT = 20
private const val INVOICE_NUMBER_HEADER_SHIFT = 5
private const val INVOICE_NUMBER_SHIFT = 2
private const val DESCRIPTION_HEADER_SHIFT = 2
private const val DESCRIPTION_SHIFT = 2
private const val CURRENCY_HEADER_SHIFT = 2
private const val CURRENCY_SHIFT = 2
private const val AMOUNT_AED_HEADER_SHIFT = 2
private const val AMOUNT_AED_SHIFT = 2


//Create Signature and date
private const val ENGINEER_SIGN_SHIFT = 480f
private const val ENGINEER_SIGNATURE_IMG_SHIFT = ENGINEER_SIGN_SHIFT - 20f
private const val ENGINEER_SIGNATURE_DATE_SHIFT = ENGINEER_SIGN_SHIFT + 30f
private const val ENGINEER_SIGN_WIDTH = 150f
private const val SIGNATURE_BOTTOM = 815f
private const val SIGNATURE_LINE_SHIFT = -5f
private const val ENGINEER_LINE_LENGTH = 90f

@HiltWorker
class ExpanseSheetPDFCreatorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getHMECodeByIdUseCase: GetHMECodeByIdUseCase,
    private val updateHMECodeUseCase: UpdateHMECodeUseCase,
    private val getCustomerByIdUseCase: GetCustomerByIdUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val loadBitmapUseCase: LoadBitmapUseCase,
    private val getFullDayAllowanceUseCase: GetFullDayAllowanceUseCase,
    private val get8HDayAllowanceUseCase: Get8HDayAllowanceUseCase,
    private val getSavingDeductibleUseCase: GetSavingDeductibleUseCase,
    private val calculateAllowance: CalculateAllowance,
    private val getCurrencyExchangeByIdUseCase: GetCurrencyExchangeByIdUseCase,
) : CoroutineWorker(appContext, workerParameters) {


    lateinit var hmeCode: HMECode
    lateinit var customer: Customer

    /***  PDF SETUP  ***/
    //Font
    private val timesFontFamily: Typeface = applicationContext.resources.getFont(R.font.times)
    private val timesBold = Typeface.create(timesFontFamily, Typeface.BOLD)

    //Expanse Sheet Header
    private val paintHeader = Paint()

    // Normal Text
    private val paintText = Paint()

    //Row Height
    private val textRowHeight = paintText.descent() - paintText.ascent()

    // Bold Text
    private val paintBoldText = Paint()

    // Thick Line for border
    private val paintThickLineTableBoarderLine = Paint()

    private val paintBlue = Paint()

    // Thick blue for Table outline
    private val paintThickLineTableBorder = Paint()

    // Paint to Fill pdf Header Data
    private val paintHeaderData = Paint()

    // Bitmap for Signatures
    private val bitmapOptions = BitmapFactory.Options()

    /*** PDF Setup End ***/

    //create PDF
    private val pdfDocument = PdfDocument()

    //Create page description List
    private val pageInfo = mutableListOf<PdfDocument.PageInfo>()

    // Page List
    private val page = mutableListOf<PdfDocument.Page>()

    // Canvas List
    private val canvas = mutableListOf<Canvas>()

    //Page counter
    private var currentPageCount = 0
    private var lastPageCreated = 0

    private lateinit var userSign: Deferred<Bitmap?>

    companion object {
        const val TIME_SHEET_LIST_KEY = "time_sheet_list"
        const val EXPANSE_LIST_KEY = "expanse_list"
    }


    // Global variables
    private var userSignHeight = 100f


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(Constants.EXPANSE_PDF_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, Constants.EXPANSE_PDF_CHANNEL_ID)
            .setSmallIcon(R.drawable.hb_logo)
            .setContentTitle("Expanse PDF Creation on going")
            .setContentText("Expanse PDF is under preparation")
            .build()
    }

    override suspend fun doWork(
    ): Result {

        // Get passed Data
        val timeSheetsSerialized =
            inputData.getString(TIME_SHEET_LIST_KEY) ?: return Result.failure()
        val timeSheets = Json.decodeFromString(TimeSheet.listSerializer, timeSheetsSerialized)

        val expanseSerialized =
            inputData.getString(EXPANSE_LIST_KEY) ?: return Result.failure()
        val expanses = Json.decodeFromString(Expanse.listSerializer, expanseSerialized)


        //Fetching required data

        getHMECodeByIdUseCase(timeSheets.first().HMEId).collect { resource ->
            when (resource) {
                is Resource.Success -> hmeCode = resource.data ?: return@collect
                else -> Unit
            }
        }

        if (!this::hmeCode.isInitialized) return Result.failure()

        getCustomerByIdUseCase(hmeCode.customerId).collect { resource ->
            when (resource) {
                is Resource.Success -> customer = resource.data ?: return@collect
                else -> Unit
            }
        }

        if (!this::customer.isInitialized) return Result.failure()

        val userName = withContext(Dispatchers.Default) {
            async {
                getUserNameUseCase()
            }
        }

        userSign = withContext(Dispatchers.IO) {
            async {
                val signature =
                    loadBitmapUseCase(Constants.SIGNATURES_FOLDER, Constants.USER_SIGNATURE)
                if (signature is Resource.Success) {
                    Log.d(TAG, "Expanse PDF worker doWork: userSignature ${signature.data}")
                    signature.data
                } else {
                    Log.d(TAG, "Expanse PDF worker doWork: userSignature $signature")
                    null
                }
            }
        }

        // Adjust Setup
        paintHeader.textSize = HEADER_TEXT_SIZE
        paintHeader.typeface = timesBold

        paintText.textSize = NORMAL_TEXT_SIZE
        paintText.typeface = timesFontFamily

        paintBoldText.textSize = NORMAL_TEXT_SIZE
        paintBoldText.typeface = timesBold

        paintThickLineTableBoarderLine.strokeWidth = THICK_LINE_STROKE_WIDTH
        paintThickLineTableBoarderLine.color = THICK_LINE_COLOR
        paintThickLineTableBoarderLine.style = Paint.Style.STROKE

        paintBlue.color = NORMAL_LINE_COLOR

        paintThickLineTableBorder.strokeWidth = TABLE_THICK_BORDER_WIDTH

        paintHeaderData.textSize = HEADER_DATA_TEXT_SIZE
        paintHeaderData.typeface = timesBold

        bitmapOptions.inScaled = true


        /*** PDF Creation ***/


        createNewPage(timeSheets, userName.await())
        createAllowanceSummeryTable(timeSheets)

        //Y position for Details Table
        val yPositionDetailsStart = Y_TABLE_TOP + 6 * (textRowHeight)
        var yPositionForCurrentItem = yPositionDetailsStart + textRowHeight

        createAllowanceDetailsHeader(yPositionDetailsStart)


        for (currentItem in timeSheets) {


            if (currentItem == timeSheets.last())
                canvas[currentPageCount].drawLine(
                    X_TABLE_LEFT,
                    yPositionForCurrentItem + paintText.descent(),
                    X_TABLE_LEFT + TABLE_WIDTH,
                    yPositionForCurrentItem + paintText.descent(),
                    paintThickLineTableBorder
                )
            else
                canvas[currentPageCount].drawLine(
                    X_TABLE_LEFT,
                    yPositionForCurrentItem + paintText.descent(),
                    X_TABLE_LEFT + TABLE_WIDTH,
                    yPositionForCurrentItem + paintText.descent(),
                    paintText
                )

            canvas[currentPageCount].drawText(
                currentItem.date.getDisplayName(
                    Calendar.DAY_OF_WEEK,
                    Calendar.SHORT_STANDALONE,
                    Locale.getDefault()
                ) ?: "Error", X_TABLE_LEFT + DAY_SHIFT, yPositionForCurrentItem, paintText
            )

            canvas[currentPageCount].drawText(
                currentItem.date.toStdDate(),
                X_TABLE_LEFT + DETAILS_COLUMN_SHIFT + DATE_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                "X",
                when (currentItem.dailyAllowance) {
                    AllowanceType._24hours -> FULL24H_MARK_SHIFT
                    AllowanceType._8hours -> LESS24H_MARK_SHIFT
                    else -> NO_ALLOWANCE_MARK_SHIFT
                },
                yPositionForCurrentItem,
                paintText
            )

            yPositionForCurrentItem += (textRowHeight)
            if (yPositionForCurrentItem > SIGNATURE_BOTTOM - textRowHeight - userSignHeight || currentItem == timeSheets.last()) {

                // Draw Table Columns
                for (i in 0..5) {
                    when (i) {
                        0, 5 -> {
                            canvas[currentPageCount].drawLine(
                                X_TABLE_LEFT + i * DETAILS_COLUMN_SHIFT,
                                (if (currentPageCount == 0) yPositionDetailsStart else TITLE_Y + 2 * textRowHeight) + paintThickLineTableBorder.ascent(),
                                X_TABLE_LEFT + i * DETAILS_COLUMN_SHIFT,
                                yPositionForCurrentItem - textRowHeight + paintThickLineTableBorder.descent(),
                                paintThickLineTableBorder
                            )
                        }
                        else -> {
                            canvas[currentPageCount].drawLine(
                                X_TABLE_LEFT + i * DETAILS_COLUMN_SHIFT,
                                (if (currentPageCount == 0) yPositionDetailsStart else TITLE_Y + 2 * textRowHeight) + paintThickLineTableBorder.ascent(),
                                X_TABLE_LEFT + i * DETAILS_COLUMN_SHIFT,
                                yPositionForCurrentItem - textRowHeight + paintThickLineTableBorder.descent(),
                                paintText
                            )
                        }
                    }
                }

                // End Current Page if new page will start

                if (yPositionForCurrentItem > SIGNATURE_BOTTOM - textRowHeight - userSignHeight) {
                    pdfDocument.finishPage(page[currentPageCount])
                    yPositionForCurrentItem = TITLE_Y + 3 * textRowHeight
                    currentPageCount++
                }

            }

            if (currentPageCount > lastPageCreated) {
                createNewEmptyPage()
                createAllowanceDetailsHeader(TITLE_Y + 2 * textRowHeight)
            }
        }

        pdfDocument.finishPage(page[currentPageCount])

        val ccExpanses = expanses.filter { !it.personallyPaid }
        if (ccExpanses.isNotEmpty()) {
            currentPageCount++
            createNewPage(timeSheets, userName.await())
            createExpanseTable(ccExpanses, false)

        }

        val cashExpanses = expanses.filter { it.personallyPaid }
        if (cashExpanses.isNotEmpty()) {
            currentPageCount++
            createNewPage(timeSheets, userName.await())
            createExpanseTable(cashExpanses, true)

        }


        // Save PDF

        val directory = File(applicationContext.filesDir.path + "/" + hmeCode.code)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        return withContext(Dispatchers.IO) {
            try {
                if (hmeCode.expanseNumber == 0) {
                    pdfDocument.writeTo(
                        FileOutputStream(
                            File(
                                directory,
                                hmeCode.code + "_expanse.pdf"
                            )
                        )
                    )
                } else {
                    val fileNumber = hmeCode.expanseNumber + 1
                    pdfDocument.writeTo(
                        FileOutputStream(
                            File(
                                directory,
                                hmeCode.code + "_" + fileNumber + "_expanse.pdf"
                            )
                        )
                    )
                }

                pdfDocument.close()

                updateHMECodeUseCase(
                    id = hmeCode.id!!,
                    customerId = hmeCode.customerId,
                    code = hmeCode.code,
                    machineType = hmeCode.machineType,
                    machineNumber = hmeCode.machineNumber,
                    workDescription = hmeCode.workDescription,
                    fileNumber = hmeCode.fileNumber,
                    expanseNumber = hmeCode.expanseNumber + 1,
                    signerName = hmeCode.signerName,
                    signatureDate = Calendar.getInstance(),
                    accommodation = hmeCode.accommodation
                ).collect {}
                return@withContext Result.success()
            } catch (e: IOException) {
                e.printStackTrace()
                pdfDocument.close()
                return@withContext Result.failure()
            }
        }
    }

    private suspend fun createNewEmptyPage() {
        Log.d(TAG, "createNewEmptyPage: Current page $currentPageCount")
        //Create PDF page
        pageInfo.add(
            PdfDocument.PageInfo.Builder(
                PAGE_WIDTH,
                PAGE_HEIGHT,
                currentPageCount
            ).create()
        )
        page.add(pdfDocument.startPage(pageInfo[currentPageCount]))
        canvas.add(page[currentPageCount].canvas)

        // --- Signature ---

        //Engineer Sign
        userSign.await()?.let { userSignature ->
            val ratio = userSignature.height.toFloat() / userSignature.width.toFloat()
            Log.d(
                TAG,
                "doWork: user signature draw ratio is $ratio signature is $userSignature"
            )

            userSignHeight = ENGINEER_SIGN_WIDTH * ratio

            canvas[currentPageCount].drawBitmap(
                userSignature,
                null,
                RectF(
                    ENGINEER_SIGNATURE_IMG_SHIFT,
                    (SIGNATURE_BOTTOM - textRowHeight - userSignHeight),
                    (ENGINEER_SIGNATURE_IMG_SHIFT + ENGINEER_SIGN_WIDTH),
                    (SIGNATURE_BOTTOM - textRowHeight)
                ), null
            )

        }
        canvas[currentPageCount]
            .drawText(
                "Engineer Signature",
                ENGINEER_SIGN_SHIFT,
                SIGNATURE_BOTTOM,
                paintText
            )
        canvas[currentPageCount].drawLine(
            ENGINEER_SIGN_SHIFT + SIGNATURE_LINE_SHIFT,
            SIGNATURE_BOTTOM + paintText.ascent() - paintText.descent(),
            ENGINEER_SIGN_SHIFT + ENGINEER_LINE_LENGTH + SIGNATURE_LINE_SHIFT,
            SIGNATURE_BOTTOM + paintText.ascent() - paintText.descent(),
            paintText
        )
        canvas[currentPageCount].drawText(
            Calendar.getInstance().toStdDate(),
            ENGINEER_SIGNATURE_DATE_SHIFT,
            SIGNATURE_BOTTOM + paintText.ascent() - 2 * paintText.descent(),
            paintText
        )

        //Border
        canvas[currentPageCount].drawRoundRect(
            LEFT_BORDER,
            TOP_BORDER,
            RIGHT_BORDER,
            BOTTOM_BORDER,
            ROUND_X_BORDER,
            ROUND_Y_BORDER,
            paintThickLineTableBoarderLine
        )

        //Update page counter
        lastPageCreated = currentPageCount
    }

    private suspend fun createNewPage(timeSheets: List<TimeSheet>, userName: String) {
        createNewEmptyPage()

        canvas[currentPageCount].drawText(
            "Expanse Sheet",
            TITLE_X,
            TITLE_Y + paintHeader.descent() - paintHeader.ascent(),
            paintHeader
        )


        // Header

        val hbLogo = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.haver_middle_east,
            bitmapOptions
        )

        canvas[currentPageCount].drawBitmap(
            hbLogo,
            null,
            RectF(
                LOGO_X_START,
                LOGO_Y_START,
                LOGO_X_END,
                LOGO_Y_END
            ), null
        )


        canvas[currentPageCount].drawText(
            "HAVER MIDDLE EAST FZE",
            LOGO_X_START,
            DATA_Y + paintHeaderData.descent() - paintHeaderData.ascent(),
            paintHeaderData
        )
        canvas[currentPageCount].drawText(
            "P.O. Box 34098",
            LOGO_X_START + 45,
            DATA_Y + 2 * (paintHeaderData.descent() - paintHeaderData.ascent()),
            paintHeaderData
        )
        canvas[currentPageCount].drawText(
            "Ras Al Khaimah",
            LOGO_X_START + 45,
            DATA_Y + 3 * (paintHeaderData.descent() - paintHeaderData.ascent()),
            paintHeaderData
        )
        canvas[currentPageCount].drawText(
            "United Arab Emirates",
            LOGO_X_START + 35,
            DATA_Y + 4 * (paintHeaderData.descent() - paintHeaderData.ascent()),
            paintHeaderData
        )
        canvas[currentPageCount].drawText(
            "Phone +971 7 20 680 00",
            LOGO_X_START + 30,
            DATA_Y + 5 * (paintHeaderData.descent() - paintHeaderData.ascent()),
            paintHeaderData
        )
        canvas[currentPageCount].drawText(
            "Fax +971 7 20 680 09",
            LOGO_X_START + 35,
            DATA_Y + 6 * (paintHeaderData.descent() - paintHeaderData.ascent()),
            paintHeaderData
        )
        canvas[currentPageCount].drawText(
            "office@havermiddleeast.com",
            LOGO_X_START + 10,
            DATA_Y + 7 * (paintHeaderData.descent() - paintHeaderData.ascent()),
            paintHeaderData
        )


        canvas[currentPageCount].drawText(
            "Customer:",
            LEFT_DATA_START,
            DATA_TOP,
            paintBoldText
        )

        canvas[currentPageCount].drawText(
            "Project City:",
            LEFT_DATA_START,
            DATA_TOP + 2 * (textRowHeight),
            paintBoldText
        )


        canvas[currentPageCount].drawText(
            "Departure:",
            LEFT_DATA_START,
            DATA_TOP + 4 * (textRowHeight),
            paintBoldText
        )

        canvas[currentPageCount].drawText(
            "HME Mission Code:",
            LEFT_DATA_START,
            DATA_TOP + 6 * (textRowHeight),
            paintBoldText
        )


        for (i in 0..3) {
            canvas[currentPageCount].drawLine(
                LEFT_DATA_START,
                DATA_TOP + paintText.descent() + 2 * i * (textRowHeight),
                LEFT_DATA_END,
                DATA_TOP + paintText.descent() + 2 * i * (textRowHeight),
                paintBlue
            )
        }


        canvas[currentPageCount]
            .drawText("Service Engineer:", RIGHT_DATA_START, DATA_TOP, paintBoldText)
        canvas[currentPageCount].drawText(
            "Project Country:",
            RIGHT_DATA_START,
            DATA_TOP + 2 * (textRowHeight),
            paintBoldText
        )
        canvas[currentPageCount]
            .drawText(
                "Arrival:",
                RIGHT_DATA_START,
                DATA_TOP + 4 * (textRowHeight),
                paintBoldText
            )

        canvas[currentPageCount].drawText(
            customer.name,
            LEFT_DATA_START + CUSTOMER_SHIFT,
            DATA_TOP,
            paintText
        )

        canvas[currentPageCount].drawText(
            customer.city,
            LEFT_DATA_START + CITY_SHIFT,
            DATA_TOP + 2 * (textRowHeight),
            paintText
        )

        canvas[currentPageCount].drawText(
            timeSheets.first().date.toStdDate() + "   " + timeSheets.first().travelStart.toTime24(),
            LEFT_DATA_START + DEPARTURE_SHIFT,
            DATA_TOP + 4 * (textRowHeight),
            paintText
        )

        canvas[currentPageCount].drawText(
            hmeCode.code,
            LEFT_DATA_START + HME_CODE_SHIFT,
            DATA_TOP + 6 * (textRowHeight),
            paintText
        )
        canvas[currentPageCount].drawText(
            userName,
            RIGHT_DATA_START + SERVICE_ENGINEER_SHIFT,
            DATA_TOP,
            paintText
        )
        canvas[currentPageCount].drawText(
            customer.country,
            RIGHT_DATA_START + COUNTRY_SHIFT,
            DATA_TOP + 2 * (textRowHeight),
            paintText
        )
        canvas[currentPageCount].drawText(
            timeSheets.last().date.toStdDate() + "   " + timeSheets.last().travelEnd.toTime24(),
            RIGHT_DATA_START + ARRIVAL_SHIFT,
            DATA_TOP + 4 * (textRowHeight),
            paintText
        )
    }

    private suspend fun createAllowanceSummeryTable(timeSheets: List<TimeSheet>) {
        //*** Allowance table overview ***//
        // Draw Lines
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            paintThickLineTableBorder
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            Y_TABLE_TOP + textRowHeight + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            Y_TABLE_TOP + textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            Y_TABLE_TOP + 2 * textRowHeight + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            Y_TABLE_TOP + 2 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            Y_TABLE_TOP + 3 * textRowHeight + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            Y_TABLE_TOP + 3 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintThickLineTableBorder
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintThickLineTableBorder
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + TABLE_WIDTH,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintThickLineTableBorder
        )
        //Vertical lines
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 2 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + 2 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 3 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + 3 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 4 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + 4 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 5 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + 5 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 6 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + 6 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 7 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + 7 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 8 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + 8 * SUMMARY_COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )

        //Particulars
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.particulars),
            X_TABLE_LEFT + PARTICULARS_HEADER_SHIFT,
            Y_TABLE_TOP,
            paintBoldText
        )

        //Category
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.category),
            X_TABLE_LEFT + CATEGORY_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //No. of Days
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.no_of_days),
            X_TABLE_LEFT + SUMMARY_COLUMN_SHIFT + NO_OF_DAYS_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Total daily Allowance
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.total_daily_allowance),
            X_TABLE_LEFT + 2 * SUMMARY_COLUMN_SHIFT + TOTAL_DAILY_ALLOWANCE_HEADER_SHIFT,
            Y_TABLE_TOP,
            paintBoldText
        )


        //Rate
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.rate),
            X_TABLE_LEFT + 2 * SUMMARY_COLUMN_SHIFT + RATE_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Amount
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.amount),
            X_TABLE_LEFT + 3 * SUMMARY_COLUMN_SHIFT + AMOUNT_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )


        //Accrued Saving
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.accrued_saving),
            X_TABLE_LEFT + 4 * SUMMARY_COLUMN_SHIFT + ACCRUED_SAVING_HEADER_SHIFT,
            Y_TABLE_TOP,
            paintBoldText
        )


        //Rate
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.rate),
            X_TABLE_LEFT + 4 * SUMMARY_COLUMN_SHIFT + RATE_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Amount
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.amount),
            X_TABLE_LEFT + 5 * SUMMARY_COLUMN_SHIFT + AMOUNT_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Amount Payable
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.amount_payable),
            X_TABLE_LEFT + 6 * SUMMARY_COLUMN_SHIFT + AMOUNT_PAYABLE_HEADER_SHIFT,
            Y_TABLE_TOP,
            paintBoldText
        )


        //Rate
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.rate),
            X_TABLE_LEFT + 6 * SUMMARY_COLUMN_SHIFT + RATE_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Amount
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.amount),
            X_TABLE_LEFT + 7 * SUMMARY_COLUMN_SHIFT + AMOUNT_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )


        //less than 24H
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.less24h),
            X_TABLE_LEFT + LESS24H_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )

        val less24HDays = timeSheets.count { it.dailyAllowance == AllowanceType._8hours }
        val fullDays = timeSheets.count { it.dailyAllowance == AllowanceType._24hours }
        //less than 24H Count
        canvas[currentPageCount].drawText(
            less24HDays.toString(),
            X_TABLE_LEFT + SUMMARY_COLUMN_SHIFT + DAYS_COUNT_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        val less24HRate = get8HDayAllowanceUseCase()
        val fullRate = getFullDayAllowanceUseCase()

        //less than 24H Rate
        canvas[currentPageCount].drawText(
            less24HRate.toString(),
            X_TABLE_LEFT + 2 * SUMMARY_COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        //less than 24H Amount
        canvas[currentPageCount].drawText(
            (less24HRate * less24HDays).toString(),
            X_TABLE_LEFT + 3 * SUMMARY_COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        //less than 24H Saving Rate
        canvas[currentPageCount].drawText(
            "---",
            X_TABLE_LEFT + 4 * SUMMARY_COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        //less than 24H Saving Amount
        canvas[currentPageCount].drawText(
            "---",
            X_TABLE_LEFT + 5 * SUMMARY_COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        //less than 24H payable Rate
        canvas[currentPageCount].drawText(
            less24HRate.toString(),
            X_TABLE_LEFT + 6 * SUMMARY_COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        //less than 24H payable Amount
        canvas[currentPageCount].drawText(
            (less24HRate * less24HDays).toString(),
            X_TABLE_LEFT + 7 * SUMMARY_COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )

        //Full 24H
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.full24h),
            X_TABLE_LEFT + FULL24H_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Full 24H Count
        canvas[currentPageCount].drawText(
            fullDays.toString(),
            X_TABLE_LEFT + SUMMARY_COLUMN_SHIFT + DAYS_COUNT_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Full24H Rate
        canvas[currentPageCount].drawText(
            fullRate.toString(),
            X_TABLE_LEFT + 2 * SUMMARY_COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Full 24H Amount
        canvas[currentPageCount].drawText(
            (fullRate * fullDays).toString(),
            X_TABLE_LEFT + 3 * SUMMARY_COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        val savingRate = getSavingDeductibleUseCase()
        //Full24H Saving Rate
        canvas[currentPageCount].drawText(
            savingRate.toString(),
            X_TABLE_LEFT + 4 * SUMMARY_COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Full 24H Saving Amount
        canvas[currentPageCount].drawText(
            (savingRate * fullDays).toString(),
            X_TABLE_LEFT + 5 * SUMMARY_COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Full 24H payable Rate
        canvas[currentPageCount].drawText(
            (fullRate - savingRate).toString(),
            X_TABLE_LEFT + 6 * SUMMARY_COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //less than 24H payable Amount
        canvas[currentPageCount].drawText(
            ((fullRate - savingRate) * fullDays).toString(),
            X_TABLE_LEFT + 7 * SUMMARY_COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Sub-totals
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.sub_totals),
            X_TABLE_LEFT + SUB_TOTAL_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight,
            paintBoldText
        )
        //Total Days Count
        canvas[currentPageCount].drawText(
            (less24HDays + fullDays).toString(),
            X_TABLE_LEFT + SUMMARY_COLUMN_SHIFT + DAYS_COUNT_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight,
            paintBoldText
        )
        //Total Amount
        canvas[currentPageCount].drawText(
            ((fullRate * fullDays) + (less24HRate * less24HDays)).toString(),
            X_TABLE_LEFT + 3 * SUMMARY_COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight,
            paintBoldText
        )

        //Total Saving Amount
        canvas[currentPageCount].drawText(
            (savingRate * fullDays).toString(),
            X_TABLE_LEFT + 5 * SUMMARY_COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight,
            paintBoldText
        )
        //Total Payable Amount
        canvas[currentPageCount].drawText(
            calculateAllowance(fullDays, less24HDays).toString(),
            X_TABLE_LEFT + 7 * SUMMARY_COLUMN_SHIFT + TOTAL_PAYABLE_AMOUNT_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight,
            paintBoldText
        )
    }

    private fun createAllowanceDetailsHeader(headerLocationY: Float) {

        //Draw Lines
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            headerLocationY + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            headerLocationY + paintThickLineTableBorder.ascent(),
            paintThickLineTableBorder
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            headerLocationY + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            headerLocationY + paintThickLineTableBorder.descent(),
            paintText
        )
        //Day
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.day),
            X_TABLE_LEFT + DAY_SHIFT,
            headerLocationY,
            paintBoldText
        )
        //Date
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.date),
            X_TABLE_LEFT + DETAILS_COLUMN_SHIFT + DATE_SHIFT,
            headerLocationY,
            paintBoldText
        )
        //Full 24H Day
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.full24h),
            X_TABLE_LEFT + 2 * DETAILS_COLUMN_SHIFT + FULL24H_HEADER_SHIFT,
            headerLocationY,
            paintBoldText
        )

        //less than 24H
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.less24h),
            X_TABLE_LEFT + 3 * DETAILS_COLUMN_SHIFT + LESS24H_HEADER_SHIFT,
            headerLocationY,
            paintBoldText
        )
        //No Allowance
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.no_allowance),
            X_TABLE_LEFT + 4 * DETAILS_COLUMN_SHIFT + NO_ALLOWANCE_HEADER_SHIFT,
            headerLocationY,
            paintBoldText
        )
    }

    private fun createExpanseHeader(headerLocationY: Float) {
        // Draw Lines
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            headerLocationY + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            headerLocationY + paintThickLineTableBorder.ascent(),
            paintThickLineTableBorder
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            headerLocationY + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            headerLocationY + paintThickLineTableBorder.descent(),
            paintText
        )

        //Date
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.date),
            X_TABLE_LEFT + DATE_SHIFT,
            headerLocationY,
            paintBoldText
        )

        //Invoice No
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.invoice_no),
            X_TABLE_LEFT + Expanse_COLUMN_SHIFT + INVOICE_NUMBER_HEADER_SHIFT,
            headerLocationY,
            paintBoldText
        )

        //Description
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.description),
            X_TABLE_LEFT + 2 * Expanse_COLUMN_SHIFT + DESCRIPTION_HEADER_SHIFT,
            headerLocationY,
            paintBoldText
        )

        //Amount
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.amount),
            X_TABLE_LEFT + 4 * Expanse_COLUMN_SHIFT + EXPANSE_AMOUNT_HEADER_SHIFT,
            headerLocationY,
            paintBoldText
        )

        //Currency
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.currency),
            X_TABLE_LEFT + 5 * Expanse_COLUMN_SHIFT + CURRENCY_HEADER_SHIFT,
            headerLocationY,
            paintBoldText
        )
        //Amount AED
        canvas[currentPageCount].drawText(
            applicationContext.getString(R.string.amount_AED),
            X_TABLE_LEFT + 6 * Expanse_COLUMN_SHIFT + AMOUNT_AED_HEADER_SHIFT,
            headerLocationY,
            paintBoldText
        )

    }

    private suspend fun createExpanseTable(expanses: List<Expanse>, personal: Boolean) {

        // Subtitle
        canvas[currentPageCount].drawText(
            if (personal) applicationContext.getString(R.string.cash) else applicationContext.getString(
                R.string.company_cc
            ),
            LEFT_DATA_START + EXPANSE_SUMMERY_HEADER_SHIFT,
            DATA_TOP + 8 * (textRowHeight),
            paintHeaderData
        )
        canvas[currentPageCount].drawText(
            applicationContext.getText(R.string.accomodation)
                .toString() + ": " + hmeCode.accommodation?.name,
            LEFT_DATA_START + EXPANSE_SUMMERY_HEADER_SHIFT,
            DATA_TOP + 9 * (textRowHeight),
            paintHeaderData
        )

        var yPositionForCurrentItem = Y_TABLE_TOP + textRowHeight
        var firstExpansePage = true
        createExpanseHeader(Y_TABLE_TOP)

        var totalInAED = 0f

        for (currentItem in expanses) {

            totalInAED += currentItem.amountAED

            if (currentItem == expanses.last())
                canvas[currentPageCount].drawLine(
                    X_TABLE_LEFT,
                    yPositionForCurrentItem + paintThickLineTableBorder.descent(),
                    X_TABLE_LEFT + TABLE_WIDTH,
                    yPositionForCurrentItem + paintThickLineTableBorder.descent(),
                    paintThickLineTableBorder
                )
            else
                canvas[currentPageCount].drawLine(
                    X_TABLE_LEFT,
                    yPositionForCurrentItem + paintText.descent(),
                    X_TABLE_LEFT + TABLE_WIDTH,
                    yPositionForCurrentItem + paintText.descent(),
                    paintText
                )

            canvas[currentPageCount].drawText(
                currentItem.date.toStdDate(),
                X_TABLE_LEFT + DATE_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                currentItem.invoiceNumber,
                X_TABLE_LEFT + Expanse_COLUMN_SHIFT + INVOICE_NUMBER_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                currentItem.description,
                X_TABLE_LEFT + 2 * Expanse_COLUMN_SHIFT + DESCRIPTION_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                currentItem.amount.toString(),
                X_TABLE_LEFT + 4 * Expanse_COLUMN_SHIFT + EXPANSE_AMOUNT_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            val currency = getCurrencyExchangeByIdUseCase(currentItem.currencyID).let { resource ->
                when (resource) {
                    is Resource.Error -> "Error"
                    is Resource.Success -> resource.data?.currencyName ?: "Error"
                    else -> "Error"
                }
            }

            canvas[currentPageCount].drawText(
                currency,
                X_TABLE_LEFT + 5 * Expanse_COLUMN_SHIFT + CURRENCY_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                currentItem.amountAED.toString(),
                X_TABLE_LEFT + 6 * Expanse_COLUMN_SHIFT + AMOUNT_AED_SHIFT,
                yPositionForCurrentItem,
                paintText
            )

            yPositionForCurrentItem += (textRowHeight)
            if (yPositionForCurrentItem > SIGNATURE_BOTTOM - textRowHeight - userSignHeight || currentItem == expanses.last()) {

                // Draw Table Columns
                for (i in 0..7) {
                    when (i) {
                        0, 7 -> {
                            canvas[currentPageCount].drawLine(
                                X_TABLE_LEFT + i * Expanse_COLUMN_SHIFT,
                                (if (firstExpansePage) Y_TABLE_TOP else TITLE_Y + 2 * textRowHeight) + paintThickLineTableBorder.ascent(),
                                X_TABLE_LEFT + i * Expanse_COLUMN_SHIFT,
                                yPositionForCurrentItem - textRowHeight + paintThickLineTableBorder.descent(),
                                paintThickLineTableBorder
                            )
                        }
                        3 -> Unit
                        else -> {
                            canvas[currentPageCount].drawLine(
                                X_TABLE_LEFT + i * Expanse_COLUMN_SHIFT,
                                (if (firstExpansePage) Y_TABLE_TOP else TITLE_Y + 2 * textRowHeight) + paintThickLineTableBorder.ascent(),
                                X_TABLE_LEFT + i * Expanse_COLUMN_SHIFT,
                                yPositionForCurrentItem - textRowHeight + paintThickLineTableBorder.descent(),
                                paintText
                            )
                        }
                    }
                }

                // End Current Page if new page will start

                if (yPositionForCurrentItem > SIGNATURE_BOTTOM - textRowHeight - userSignHeight) {
                    pdfDocument.finishPage(page[currentPageCount])
                    yPositionForCurrentItem = TITLE_Y + 3 * textRowHeight
                    currentPageCount++
                    firstExpansePage = false
                }

            }

            if (currentPageCount > lastPageCreated) {
                createNewEmptyPage()
                createExpanseHeader(TITLE_Y + 2 * textRowHeight)
            }

        }
        if (personal) {
            canvas[currentPageCount].drawText(
                applicationContext.getString(R.string.totalInAED),
                X_TABLE_LEFT + 5 * Expanse_COLUMN_SHIFT + AMOUNT_AED_SHIFT,
                yPositionForCurrentItem,
                paintBoldText
            )
            canvas[currentPageCount].drawText(
                totalInAED.toString(),
                X_TABLE_LEFT + 6 * Expanse_COLUMN_SHIFT + AMOUNT_AED_SHIFT,
                yPositionForCurrentItem,
                paintText
            )

            canvas[currentPageCount].drawLine(
                X_TABLE_LEFT + 5 * Expanse_COLUMN_SHIFT,
                yPositionForCurrentItem + paintThickLineTableBorder.descent(),
                X_TABLE_LEFT + TABLE_WIDTH,
                yPositionForCurrentItem + paintThickLineTableBorder.descent(),
                paintThickLineTableBorder
            )

            for (i in 5..7)
                canvas[currentPageCount].drawLine(
                    X_TABLE_LEFT + i * Expanse_COLUMN_SHIFT,
                    yPositionForCurrentItem + paintThickLineTableBorder.descent(),
                    X_TABLE_LEFT + i * Expanse_COLUMN_SHIFT,
                    yPositionForCurrentItem - textRowHeight + paintThickLineTableBorder.descent(),
                    paintThickLineTableBorder
                )
        }

        pdfDocument.finishPage(page[currentPageCount])

        for (currentItem in expanses) {
            Log.d(
                TAG,
                "createExpanseTable: Expanse ${currentItem.description} is checked for invoices"
            )
            if (currentItem.invoicesUri.isEmpty()) continue
            for (imageUriAsString in currentItem.invoicesUri) {
                val imageUri = imageUriAsString.toUri()
                val imageFile = imageUri.toFile()
                if (!imageFile.exists()) continue

                val path = imageFile.absolutePath
                val image = BitmapFactory.decodeFile(path)
                val widthRatio =
                    if (image.width > IMAGE_MAX_WIDTH) {
                        IMAGE_MAX_WIDTH / image.width
                    } else 1f
                Log.d(TAG, "createExpanseTable: width ratio $widthRatio")
                val heightRatio =
                    if (image.height > IMAGE_MAX_HEIGHT) {
                        IMAGE_MAX_HEIGHT / image.height
                    } else 1f
                Log.d(TAG, "createExpanseTable: height ratio $heightRatio")

                val ratio = if (widthRatio < heightRatio) widthRatio else heightRatio
                Log.d(TAG, "createExpanseTable: Ratio $ratio")
                currentPageCount++
                createNewEmptyPage()
                //HME Code
                canvas[currentPageCount].drawText(
                    hmeCode.code,
                    HME_CODE_X,
                    HME_CODE_Y,
                    paintHeaderData
                    )

                //Description
                canvas[currentPageCount].drawText(
                    currentItem.description,
                    INVOICE_DESCRIPTION_X,
                    INVOICE_DESCRIPTION_Y,
                    paintHeaderData
                    )

                val currency = getCurrencyExchangeByIdUseCase(currentItem.currencyID)
                //Amount and currency
                canvas[currentPageCount].drawText(
                    currentItem.amount.toString() + if (currency is Resource.Success) currency.data?.currencyName else "",
                    INVOICE_AMOUNT_AND_CURRENCY_X,
                    INVOICE_AMOUNT_AND_CURRENCY_Y,
                    paintHeaderData
                    )
                if (personal && currency.data?.currencyName !="AED"){
                    //Amount in AED
                    canvas[currentPageCount].drawText(
                        currentItem.amountAED.toString() + "AED",
                        INVOICE_AMOUNT_AED_X,
                        INVOICE_AMOUNT_AED_Y,
                        paintHeaderData
                    )
                }

                canvas[currentPageCount].drawBitmap(
                    image,
                    null,
                    RectF(
                        INVOICE_IMAGE_LEFT_POSITION,
                        INVOICE_IMAGE_TOP_POSITION,
                        INVOICE_IMAGE_LEFT_POSITION + (ratio * image.width),
                        INVOICE_IMAGE_TOP_POSITION + (ratio * image.height)
                    ),
                    null
                )
                pdfDocument.finishPage(page[currentPageCount])
            }
        }

    }
}