package com.neklaway.hme_reporting.feature_time_sheet.data.worker

import android.app.Notification
import android.content.Context
import android.graphics.*
import android.graphics.Typeface.BOLD
import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetCustomerByIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.UpdateHMECodeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.GetIBAUCodeByIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.MarkCreatedTimeSheetUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name.GetUserNameUseCase
import com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case.LoadBitmapUseCase
import com.neklaway.hme_reporting.utils.Constants
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.toDate
import com.neklaway.hme_reporting.utils.toTime
import com.neklaway.hmereporting.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


private const val TAG = "PDFCreatorWorker"
//Times new roman font

private const val HEADER_TEXT_SIZE = 20f
private const val HEADER_DATA_TEXT_SIZE = 12f
private const val NORMAL_TEXT_SIZE = 10f

// Page Size
private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842

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
private const val COLUMN_SHIFT = 51f
private const val COLUMN_SHIFT_DATE = COLUMN_SHIFT - 12f


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
private const val MACHINE_TYPE_SHIFT = 70
private const val DEPARTURE_SHIFT = 55
private const val HME_CODE_SHIFT = 95
private const val WORK_DESCRIPTION_SHIFT = 95
private const val MACHINE_NUMBER_SHIFT = 80
private const val ARRIVAL_SHIFT = 45
private const val CITY_SHIFT = 60
private const val COUNTRY_SHIFT = 80
private const val IBAU_SO_SHIFT = 100

// PDF Tables Shifts
private const val DAY_HEADER_SHIFT = 5
private const val DAY_SHIFT = 5
private const val DATE_HEADER_SHIFT = 5
private const val DATE_SHIFT = 2
private const val TRAVEL_START_HEADER_SHIFT = 5
private const val TRAVEL_START_SHIFT = 5
private const val WORK_START_HEADER_SHIFT = 5
private const val WORK_START_SHIFT = 5
private const val WORK_END_HEADER_SHIFT = 5
private const val WORK_END_SHIFT = 5
private const val TRAVEL_END_HEADER_SHIFT = 5
private const val TRAVEL_END_SHIFT = 5
private const val BREAK_HEADER_SHIFT = 5
private const val BREAK_SHIFT = 5
private const val WORKING_HOURS_HEADER_SHIFT = 5
private const val WORKING_HOURS_SHIFT = 5
private const val OVER_TIME_HEADER_SHIFT = 2
private const val OVER_TIME_SHIFT = 2
private const val TRAVEL_HOURS_HEADER_SHIFT = 5
private const val TRAVEL_HOURS_SHIFT = 5
private const val TRAVEL_DISTANCE_HEADER_SHIFT = 5
private const val TRAVEL_DISTANCE_SHIFT = 5
private const val TOTAL_SHIFT = 10


//Create Signature and date
private const val CHECKED_SHIFT = 35f
private const val CUSTOMER_SIGN_SHIFT = 250f
private const val CUSTOMER_SIGNATURE_IMG_SHIFT = CUSTOMER_SIGN_SHIFT - 50f
private const val CUSTOMER_SIGNATURE_DATE_SHIFT = CUSTOMER_SIGN_SHIFT
private const val ENGINEER_SIGN_SHIFT = 480f
private const val ENGINEER_SIGNATURE_IMG_SHIFT = ENGINEER_SIGN_SHIFT - 20f
private const val ENGINEER_SIGNATURE_DATE_SHIFT = ENGINEER_SIGN_SHIFT + 30f
private const val ENGINEER_SIGN_WIDTH = 150f
private const val CUSTOMER_SIGN_WIDTH = 100f
private const val SIGNATURE_BOTTOM = 815f
private const val SIGNATURE_LINE_SHIFT = -5f
private const val CHECKED_LINE_LENGTH = 80f
private const val CUSTOMER_LINE_LENGTH = 90f
private const val ENGINEER_LINE_LENGTH = 90f

@HiltWorker
class PDFCreatorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    val getHMECodeByIdUseCase: GetHMECodeByIdUseCase,
    val updateHMECodeUseCase: UpdateHMECodeUseCase,
    val markCreatedTimeSheetUseCase: MarkCreatedTimeSheetUseCase,
    val getCustomerByIdUseCase: GetCustomerByIdUseCase,
    val isIbauUseCase: GetIsIbauUseCase,
    val getIBAUCodeByIdUseCase: GetIBAUCodeByIdUseCase,
    val getUserNameUseCase: GetUserNameUseCase,
    val loadBitmapUseCase: LoadBitmapUseCase,
) : CoroutineWorker(appContext, workerParameters) {


    lateinit var hmeCode: HMECode
    lateinit var customer: Customer
    lateinit var ibau: IBAUCode


    companion object {
        const val TIME_SHEET_LIST_KEY = "time_sheet_list"
    }


    // Global variables
    private var userSignHeight = 100f
    private var customerSignHeight = 100f

    //Counters
    private var totalWork = 0f
    private var totalTravel = 0f
    private var totalOverTime = 0f
    private var totalTravelDistance = 0

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(Constants.PDF_NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, Constants.PDF_CHANNEL_ID)
            .setSmallIcon(R.drawable.hb_logo)
            .setContentTitle("PDF Creation on going")
            .setContentText("PDF is under preparation")
            .build()
    }

    override suspend fun doWork(
    ): Result {

        // Get passed Data
        val timeSheetsSerialized = inputData.getString(TIME_SHEET_LIST_KEY) ?: return Result.failure()
        val timeSheets = Json.decodeFromString(TimeSheet.listSerializer, timeSheetsSerialized)

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

        val isIbau = withContext(Dispatchers.Default) {
            async {
                isIbauUseCase()
            }
        }

        if (isIbau.await()) {
            timeSheets.firstOrNull()?.IBAUId?.let { id ->
                getIBAUCodeByIdUseCase(id).collect { resource ->
                    when (resource) {
                        is Resource.Success -> ibau = resource.data ?: return@collect
                        else -> Unit
                    }
                }
            }
            if (!this::ibau.isInitialized) return Result.failure()
        }

        val userName = withContext(Dispatchers.Default) {
            async {
                getUserNameUseCase()
            }
        }


        val userSign = withContext(Dispatchers.IO) {
            async {
                val signature =
                    loadBitmapUseCase(Constants.SIGNATURES_FOLDER, Constants.USER_SIGNATURE)
                if (signature is Resource.Success) {
                    Log.d(TAG, "PDF worker doWork: userSignature ${signature.data}")
                    signature.data
                } else {
                    Log.d(TAG, "PDF worker doWork: userSignature $signature")
                    null
                }
            }
        }


        val customerSign = withContext(Dispatchers.IO) {
            async {
                return@async hmeCode.id?.let { hmeCodeId ->
                    val signature =
                        loadBitmapUseCase(Constants.SIGNATURES_FOLDER, hmeCodeId.toString())

                    if (signature is Resource.Success) {
                        signature.data
                    } else {
                        null
                    }
                }
            }
        }


        /***  PDF SETUP  ***/
        //Font
        val timesFontFamily: Typeface = applicationContext.resources.getFont(R.font.times)
        val timesBold = Typeface.create(timesFontFamily, BOLD)

        //Time Sheet Header
        val paintHeader = Paint()
        paintHeader.textSize = HEADER_TEXT_SIZE
        paintHeader.typeface = timesBold

        // Normal Text
        val paintText = Paint()
        paintText.textSize = NORMAL_TEXT_SIZE
        paintText.typeface = timesFontFamily

        //Row Height
        val textRowHeight = paintText.descent() - paintText.ascent()
        val yPositionStart = Y_TABLE_TOP + 2 * (textRowHeight)
        var yPositionForCurrentItem = yPositionStart


        // Bold Text
        val paintBoldText = Paint()
        paintBoldText.textSize = NORMAL_TEXT_SIZE
        paintBoldText.typeface = timesBold

        // Thick Line for border
        val paintThickLineTableBoarderLine = Paint()
        paintThickLineTableBoarderLine.strokeWidth = THICK_LINE_STROKE_WIDTH
        paintThickLineTableBoarderLine.color = THICK_LINE_COLOR
        paintThickLineTableBoarderLine.style = Paint.Style.STROKE

        val paintBlue = Paint()
        paintBlue.color = NORMAL_LINE_COLOR

        // Thick blue for Table outline
        val paintThickLineTableBorder = Paint()
        paintThickLineTableBorder.strokeWidth = TABLE_THICK_BORDER_WIDTH

        // Paint to Fill pdf Header Data
        val paintHeaderData = Paint()
        paintHeaderData.textSize = HEADER_DATA_TEXT_SIZE
        paintHeaderData.typeface = timesBold

        // Bitmap for Signatures
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inScaled = true

        /*** PDF Setup End ***/


        //create PDF
        val pdfDocument = PdfDocument()

        //Create page description List
        val pageInfo = mutableListOf<PdfDocument.PageInfo>()

        // Page List
        val page = mutableListOf<PdfDocument.Page>()

        // Canvas List
        val canvas = mutableListOf<Canvas>()


        /*** PDF Creation ***/

        //Page counter
        var currentPageCount = 0
        var lastPageCreated = -1

        for (currentItem in timeSheets) {

            if (currentPageCount > lastPageCreated) {
                pageInfo.add(
                    PdfDocument.PageInfo.Builder(
                        PAGE_WIDTH,
                        PAGE_HEIGHT,
                        currentPageCount + 1
                    ).create()
                )
                page.add(pdfDocument.startPage(pageInfo[currentPageCount]))
                canvas.add(page[currentPageCount].canvas)


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


                canvas[currentPageCount].drawText(
                    "Time Sheet",
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
                    "Machine Type:",
                    LEFT_DATA_START,
                    DATA_TOP + 4 * (textRowHeight),
                    paintBoldText
                )

                canvas[currentPageCount].drawText(
                    "Departure:",
                    LEFT_DATA_START,
                    DATA_TOP + 6 * (textRowHeight),
                    paintBoldText
                )

                canvas[currentPageCount].drawText(
                    "HME Mission Code:",
                    LEFT_DATA_START,
                    DATA_TOP + 8 * (textRowHeight),
                    paintBoldText
                )

                canvas[currentPageCount].drawText(
                    "Work Description:",
                    LEFT_DATA_START,
                    DATA_TOP + 10 * (textRowHeight),
                    paintBoldText
                )

                for (i in 0..5) {
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
                canvas[currentPageCount].drawText(
                    "Machine Serial:",
                    RIGHT_DATA_START,
                    DATA_TOP + 4 * (textRowHeight),
                    paintBoldText
                )
                canvas[currentPageCount]
                    .drawText(
                        "Arrival:",
                        RIGHT_DATA_START,
                        DATA_TOP + 6 * (textRowHeight),
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
                    if (isIbau.await()) ibau.machineType else hmeCode.machineType ?: "Error",
                    LEFT_DATA_START + MACHINE_TYPE_SHIFT,
                    DATA_TOP + 4 * (textRowHeight),
                    paintText
                )

                canvas[currentPageCount].drawText(
                    timeSheets.first().date.toDate() + "   " + timeSheets.first().travelStart.toTime(),
                    LEFT_DATA_START + DEPARTURE_SHIFT,
                    DATA_TOP + 6 * (textRowHeight),
                    paintText
                )

                canvas[currentPageCount].drawText(
                    hmeCode.code,
                    LEFT_DATA_START + HME_CODE_SHIFT,
                    DATA_TOP + 8 * (textRowHeight),
                    paintText
                )

                canvas[currentPageCount].drawText(
                    if (isIbau.await()) ibau.workDescription else hmeCode.workDescription
                        ?: "Error",
                    LEFT_DATA_START + WORK_DESCRIPTION_SHIFT,
                    DATA_TOP + 10 * (textRowHeight),
                    paintText
                )
                canvas[currentPageCount].drawText(
                    userName.await(),
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
                    if (isIbau.await()) ibau.machineNumber else hmeCode.machineNumber
                        ?: "Error",
                    RIGHT_DATA_START + MACHINE_NUMBER_SHIFT,
                    DATA_TOP + 4 * (textRowHeight),
                    paintText
                )
                canvas[currentPageCount].drawText(
                    timeSheets.last().date.toDate() + "   " + timeSheets.last().travelEnd.toTime(),
                    RIGHT_DATA_START + ARRIVAL_SHIFT,
                    DATA_TOP + 6 * (textRowHeight),
                    paintText
                )

                if (isIbau.await()
                ) {
                    canvas[currentPageCount].drawText(
                        "IBAU Service Order:",
                        RIGHT_DATA_START,
                        DATA_TOP + 8 * (textRowHeight),
                        paintBoldText
                    )
                    canvas[currentPageCount].drawText(
                        ibau.code,
                        RIGHT_DATA_START + IBAU_SO_SHIFT,
                        DATA_TOP + 8 * (textRowHeight),
                        paintText
                    )
                }

                // --- Create Table Header ---
                // Draw Lines
                canvas[currentPageCount].drawLine(
                    X_TABLE_LEFT,
                    Y_TABLE_TOP + paintText.ascent(),
                    X_TABLE_LEFT + TABLE_WIDTH,
                    Y_TABLE_TOP + paintText.ascent(),
                    paintThickLineTableBorder
                )
                canvas[currentPageCount].drawLine(
                    X_TABLE_LEFT,
                    Y_TABLE_TOP + textRowHeight + paintThickLineTableBorder.descent(),
                    X_TABLE_LEFT + TABLE_WIDTH,
                    Y_TABLE_TOP + textRowHeight + paintThickLineTableBorder.descent(),
                    paintThickLineTableBorder
                )

                //Day
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.day),
                    X_TABLE_LEFT + DAY_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )

                //Date
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.date),
                    X_TABLE_LEFT + COLUMN_SHIFT_DATE + DATE_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )

                //Travel Start
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.travel),
                    X_TABLE_LEFT + 2 * COLUMN_SHIFT + TRAVEL_START_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.start),
                    X_TABLE_LEFT + 2 * COLUMN_SHIFT + TRAVEL_START_HEADER_SHIFT,
                    Y_TABLE_TOP + textRowHeight,
                    paintBoldText
                )

                //Work Start
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.work),
                    X_TABLE_LEFT + 3 * COLUMN_SHIFT + WORK_START_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.start),
                    X_TABLE_LEFT + 3 * COLUMN_SHIFT + WORK_START_HEADER_SHIFT,
                    Y_TABLE_TOP + textRowHeight,
                    paintBoldText
                )

                //Work End
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.work),
                    X_TABLE_LEFT + 4 * COLUMN_SHIFT + WORK_END_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.end),
                    X_TABLE_LEFT + 4 * COLUMN_SHIFT + WORK_END_HEADER_SHIFT,
                    Y_TABLE_TOP + textRowHeight,
                    paintBoldText
                )

                //Travel End
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.travel),
                    X_TABLE_LEFT + 5 * COLUMN_SHIFT + TRAVEL_END_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.end),
                    X_TABLE_LEFT + 5 * COLUMN_SHIFT + TRAVEL_END_HEADER_SHIFT,
                    Y_TABLE_TOP + textRowHeight,
                    paintBoldText
                )

                //Break Duration
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.break__),
                    X_TABLE_LEFT + 6 * COLUMN_SHIFT + BREAK_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.duration),
                    X_TABLE_LEFT + 6 * COLUMN_SHIFT + BREAK_HEADER_SHIFT,
                    Y_TABLE_TOP + textRowHeight,
                    paintBoldText
                )

                //working Hours
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.work),
                    X_TABLE_LEFT + 7 * COLUMN_SHIFT + WORKING_HOURS_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.hours),
                    X_TABLE_LEFT + 7 * COLUMN_SHIFT + WORKING_HOURS_HEADER_SHIFT,
                    Y_TABLE_TOP + textRowHeight,
                    paintBoldText
                )

                //OverTime Hours
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.overtime),
                    X_TABLE_LEFT + 8 * COLUMN_SHIFT + OVER_TIME_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.hours),
                    X_TABLE_LEFT + 8 * COLUMN_SHIFT + OVER_TIME_HEADER_SHIFT,
                    Y_TABLE_TOP + textRowHeight,
                    paintBoldText
                )

                //Travel Hours
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.travel),
                    X_TABLE_LEFT + 9 * COLUMN_SHIFT + TRAVEL_HOURS_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.hours),
                    X_TABLE_LEFT + 9 * COLUMN_SHIFT + TRAVEL_HOURS_HEADER_SHIFT,
                    Y_TABLE_TOP + textRowHeight,
                    paintBoldText
                )

                //Travel Distance
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.travel),
                    X_TABLE_LEFT + 10 * COLUMN_SHIFT + TRAVEL_DISTANCE_HEADER_SHIFT,
                    Y_TABLE_TOP,
                    paintBoldText
                )
                canvas[currentPageCount].drawText(
                    applicationContext.resources.getString(R.string.distance),
                    X_TABLE_LEFT + 10 * COLUMN_SHIFT + TRAVEL_DISTANCE_HEADER_SHIFT,
                    Y_TABLE_TOP + textRowHeight,
                    paintBoldText
                )

                // User signature


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


                // Customer signature
                Log.d(TAG, "doWork: customer signature draw ${customerSign.await()}")

                customerSign.await()?.let { customerSignature ->
                    val ratio =
                        customerSignature.height.toFloat() / customerSignature.width.toFloat()
                    customerSignHeight = CUSTOMER_SIGN_WIDTH * ratio

                    Log.d(
                        TAG,
                        "doWork: user signature draw ratio is $ratio signature is $customerSignature, height ${customerSignature.height}, width ${customerSignature.width}"
                    )

                    canvas[currentPageCount].drawBitmap(
                        customerSignature,
                        null,
                        RectF(
                            CUSTOMER_SIGNATURE_IMG_SHIFT,
                            (SIGNATURE_BOTTOM - textRowHeight - customerSignHeight - (paintText.descent() - paintText.ascent())),
                            (CUSTOMER_SIGN_SHIFT + CUSTOMER_SIGN_WIDTH + CUSTOMER_SIGN_WIDTH),
                            (SIGNATURE_BOTTOM - textRowHeight - (paintText.descent() - paintText.ascent()))
                        ), null
                    )
                    Log.d(TAG, "doWork: customer signature drawn")
                    canvas[currentPageCount].drawText(
                        hmeCode.signerName ?: "",
                        CUSTOMER_SIGNATURE_DATE_SHIFT,
                        SIGNATURE_BOTTOM + paintText.ascent() - 2 * paintText.descent() - (paintText.descent() - paintText.ascent()),
                        paintText
                    )
                    canvas[currentPageCount].drawText(
                        hmeCode.signatureDate.toTime() + "  " + hmeCode.signatureDate.toDate(),
                        CUSTOMER_SIGNATURE_DATE_SHIFT,
                        SIGNATURE_BOTTOM + paintText.ascent() - 2 * paintText.descent(),
                        paintText
                    )

                }

                if (customerSign.await() == null) {
                    hmeCode.signerName?.let { signerName ->
                        for (i in 0..currentPageCount) {
                            canvas[currentPageCount].drawText(
                                signerName,
                                CUSTOMER_SIGNATURE_DATE_SHIFT,
                                SIGNATURE_BOTTOM + paintText.ascent() - 2 * paintText.descent() - (paintText.descent() - paintText.ascent()),
                                paintText
                            )
                        }

                    }
                }

                // --- Signature ---
                //Checked by HME
                canvas[currentPageCount]
                    .drawText("Check by HME", CHECKED_SHIFT, SIGNATURE_BOTTOM, paintText)
                canvas[currentPageCount].drawLine(
                    CHECKED_SHIFT + SIGNATURE_LINE_SHIFT,
                    SIGNATURE_BOTTOM + paintText.ascent() - paintText.descent(),
                    CHECKED_SHIFT + CHECKED_LINE_LENGTH + SIGNATURE_LINE_SHIFT,
                    SIGNATURE_BOTTOM + paintText.ascent() - paintText.descent(),
                    paintText
                )

                // Customer name and Sign
                canvas[currentPageCount]
                    .drawText(
                        "Customer Signature",
                        CUSTOMER_SIGN_SHIFT,
                        SIGNATURE_BOTTOM,
                        paintText
                    )
                canvas[currentPageCount].drawLine(
                    CUSTOMER_SIGN_SHIFT + SIGNATURE_LINE_SHIFT,
                    SIGNATURE_BOTTOM + paintText.ascent() - paintText.descent(),
                    CUSTOMER_SIGN_SHIFT + CUSTOMER_LINE_LENGTH + SIGNATURE_LINE_SHIFT,
                    SIGNATURE_BOTTOM + paintText.ascent() - paintText.descent(),
                    paintText
                )

                //Engineer Sign
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
                    Calendar.getInstance().toDate(),
                    ENGINEER_SIGNATURE_DATE_SHIFT,
                    SIGNATURE_BOTTOM + paintText.ascent() - 2 * paintText.descent(),
                    paintText
                )


                //wait till new page created
                lastPageCreated = currentPageCount
            }


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
                currentItem.date.toDate(),
                X_TABLE_LEFT + COLUMN_SHIFT_DATE + DATE_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                currentItem.travelStart.toTime(),
                X_TABLE_LEFT + 2 * COLUMN_SHIFT + TRAVEL_START_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                currentItem.workStart.toTime(),
                X_TABLE_LEFT + 3 * COLUMN_SHIFT + WORK_START_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                currentItem.workEnd.toTime(),
                X_TABLE_LEFT + 4 * COLUMN_SHIFT + WORK_END_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                currentItem.travelEnd.toTime(),
                X_TABLE_LEFT + 5 * COLUMN_SHIFT + TRAVEL_END_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            canvas[currentPageCount].drawText(
                currentItem.breakTimeString,
                X_TABLE_LEFT + 6 * COLUMN_SHIFT + BREAK_SHIFT,
                yPositionForCurrentItem,
                paintText
            )


            // Calculate work duration and travel
            if (currentItem.noWorkDay) {
                canvas[currentPageCount].drawText(
                    "---",
                    X_TABLE_LEFT + 7 * COLUMN_SHIFT + WORKING_HOURS_SHIFT,
                    yPositionForCurrentItem,
                    paintText
                )
                canvas[currentPageCount].drawText(
                    "---",
                    X_TABLE_LEFT + 9 * COLUMN_SHIFT + TRAVEL_HOURS_SHIFT,
                    yPositionForCurrentItem,
                    paintText
                )
            } else {
                canvas[currentPageCount].drawText(
                    currentItem.workTimeString,
                    X_TABLE_LEFT + 7 * COLUMN_SHIFT + WORKING_HOURS_SHIFT,
                    yPositionForCurrentItem,
                    paintText
                )
                canvas[currentPageCount].drawText(
                    currentItem.overTimeString,
                    X_TABLE_LEFT + 8 * COLUMN_SHIFT + WORKING_HOURS_SHIFT,
                    yPositionForCurrentItem,
                    paintText
                )
                canvas[currentPageCount].drawText(
                    currentItem.travelTimeString,
                    X_TABLE_LEFT + 9 * COLUMN_SHIFT + TRAVEL_HOURS_SHIFT,
                    yPositionForCurrentItem,
                    paintText
                )
            }

            canvas[currentPageCount].drawText(
                currentItem.traveledDistance.toString(),
                X_TABLE_LEFT + 10 * COLUMN_SHIFT + TRAVEL_DISTANCE_SHIFT,
                yPositionForCurrentItem,
                paintText
            )
            totalTravelDistance += currentItem.traveledDistance
            totalTravel += currentItem.travelTime
            totalWork += currentItem.workTime
            totalOverTime += currentItem.overTime

            yPositionForCurrentItem += (textRowHeight)
            if (yPositionForCurrentItem > SIGNATURE_BOTTOM - textRowHeight - userSignHeight || currentItem == timeSheets.last()) {

                // Draw Table Columns
                for (i in 0..11) {
                    when (i) {
                        0, 11 -> {
                            canvas[currentPageCount].drawLine(
                                X_TABLE_LEFT + i * COLUMN_SHIFT,
                                Y_TABLE_TOP + paintText.ascent(),
                                X_TABLE_LEFT + i * COLUMN_SHIFT,
                                yPositionForCurrentItem - textRowHeight + paintThickLineTableBorder.descent(),
                                paintThickLineTableBorder
                            )
                        }
                        1 -> {
                            canvas[currentPageCount].drawLine(
                                X_TABLE_LEFT + COLUMN_SHIFT_DATE,
                                Y_TABLE_TOP + paintText.ascent(),
                                X_TABLE_LEFT + COLUMN_SHIFT_DATE,
                                yPositionForCurrentItem - textRowHeight + paintText.descent(),
                                paintText
                            )
                        }
                        else -> {
                            canvas[currentPageCount].drawLine(
                                X_TABLE_LEFT + i * COLUMN_SHIFT,
                                Y_TABLE_TOP + paintText.ascent(),
                                X_TABLE_LEFT + i * COLUMN_SHIFT,
                                yPositionForCurrentItem - textRowHeight + paintText.descent(),
                                paintText
                            )
                        }
                    }
                }

                // End Current Page if new page will start

                if (yPositionForCurrentItem > SIGNATURE_BOTTOM - textRowHeight - userSignHeight) {
                    pdfDocument.finishPage(page[currentPageCount])
                    yPositionForCurrentItem = yPositionStart
                    currentPageCount++
                }
            }
        }

        // Create totals
        canvas[currentPageCount].drawText(
            "Total",
            X_TABLE_LEFT + 6 * COLUMN_SHIFT + TOTAL_SHIFT,
            yPositionForCurrentItem,
            paintBoldText
        )
        canvas[currentPageCount].drawText(
            String.format("%.2fH", totalWork),
            X_TABLE_LEFT + 7 * COLUMN_SHIFT + WORKING_HOURS_SHIFT,
            yPositionForCurrentItem,
            paintText
        )
        canvas[currentPageCount].drawText(
            String.format("%.2fH", totalOverTime),
            X_TABLE_LEFT + 8 * COLUMN_SHIFT + OVER_TIME_SHIFT,
            yPositionForCurrentItem,
            paintText
        )
        canvas[currentPageCount].drawText(
            String.format("%.2fH", totalTravel),
            X_TABLE_LEFT + 9 * COLUMN_SHIFT + TRAVEL_HOURS_SHIFT,
            yPositionForCurrentItem,
            paintText
        )
        canvas[currentPageCount].drawText(
            totalTravelDistance.toString(),
            X_TABLE_LEFT + 10 * COLUMN_SHIFT + TRAVEL_DISTANCE_SHIFT,
            yPositionForCurrentItem,
            paintText
        )

        for (i in 7..11) {
            canvas[currentPageCount].drawLine(
                X_TABLE_LEFT + i * COLUMN_SHIFT,
                yPositionForCurrentItem + paintText.ascent(),
                X_TABLE_LEFT + i * COLUMN_SHIFT,
                yPositionForCurrentItem + paintText.descent(),
                paintThickLineTableBorder
            )
        }

        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 7 * COLUMN_SHIFT,
            yPositionForCurrentItem + paintText.descent(),
            X_TABLE_LEFT + 11 * COLUMN_SHIFT,
            yPositionForCurrentItem + paintText.descent(),
            paintThickLineTableBorder
        )


        pdfDocument.finishPage(page[currentPageCount])


        // Save PDF

        val directory = File(applicationContext.filesDir.path + "/" + hmeCode.code)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        return withContext(Dispatchers.IO) {
            try {
                if (hmeCode.fileNumber == 0) {
                    pdfDocument.writeTo(FileOutputStream(File(directory, hmeCode.code + ".pdf")))
                } else {
                    val fileNumber = hmeCode.fileNumber + 1
                    pdfDocument.writeTo(
                        FileOutputStream(
                            File(
                                directory,
                                hmeCode.code + "_" + fileNumber + ".pdf"
                            )
                        )
                    )
                }

                pdfDocument.close()

                updateHMECodeUseCase(
                    hmeCode.id!!,
                    hmeCode.customerId,
                    hmeCode.code,
                    hmeCode.machineType,
                    hmeCode.machineNumber,
                    hmeCode.workDescription,
                    hmeCode.fileNumber + 1,
                    hmeCode.signerName,
                    Calendar.getInstance()
                ).collect()

                markCreatedTimeSheetUseCase(timeSheets).collect()
                return@withContext Result.success()
            } catch (e: IOException) {
                e.printStackTrace()
                pdfDocument.close()
                return@withContext Result.failure()
            }
        }
    }
}

