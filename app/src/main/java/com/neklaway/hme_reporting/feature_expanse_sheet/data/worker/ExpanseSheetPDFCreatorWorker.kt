package com.neklaway.hme_reporting.feature_expanse_sheet.data.worker

import android.app.Notification
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.core.app.NotificationCompat
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
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.Get8HDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetFullDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetSavingDeductibleUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name.GetUserNameUseCase
import com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case.LoadBitmapUseCase
import com.neklaway.hme_reporting.utils.*
import com.neklaway.hmereporting.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
private const val COLUMN_SHIFT = TABLE_WIDTH / 8
private const val COLUMN_SHIFT_TOTAL_DAILY_ALLOWANCE = COLUMN_SHIFT - 12f


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
private const val LESS24H_SHIFT = 5
private const val FULL24H_SHIFT = 5
private const val SUB_TOTAL_SHIFT = 5
private const val DAYS_COUNT_SHIFT = 25
private const val NO_OF_DAYS_HEADER_SHIFT = 5
private const val DAY_SHIFT = 5
private const val TOTAL_DAILY_ALLOWANCE_HEADER_SHIFT = 25
private const val RATE_HEADER_SHIFT = 25
private const val RATE_SHIFT = 25
private const val AMOUNT_HEADER_SHIFT = 15
private const val AMOUNT_SHIFT = 25
private const val DATE_SHIFT = 2
private const val ACCRUED_SAVING_HEADER_SHIFT = 25
private const val AMOUNT_PAYABLE_HEADER_SHIFT = 25


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
    private val calculateExpanse: CalculateExpanse
) : CoroutineWorker(appContext, workerParameters) {


    lateinit var hmeCode: HMECode
    lateinit var customer: Customer


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

        val userSign = withContext(Dispatchers.IO) {
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

        /***  PDF SETUP  ***/
        //Font
        val timesFontFamily: Typeface = applicationContext.resources.getFont(R.font.times)
        val timesBold = Typeface.create(timesFontFamily, Typeface.BOLD)

        //Expanse Sheet Header
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
        var lastPageCreated = 0

        fun createNewEmptyPage() {
            //Create PDF page
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
        }

        suspend fun createNewPage() {
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
                timeSheets.last().date.toStdDate() + "   " + timeSheets.last().travelEnd.toTime24(),
                RIGHT_DATA_START + ARRIVAL_SHIFT,
                DATA_TOP + 4 * (textRowHeight),
                paintText
            )
        }

        createNewPage()

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
            paintThickLineTableBorder
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT,
            Y_TABLE_TOP + textRowHeight + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + TABLE_WIDTH,
            Y_TABLE_TOP + textRowHeight + paintThickLineTableBorder.descent(),
            paintThickLineTableBorder
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
            X_TABLE_LEFT + COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 2 * COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + 2 * COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 3 * COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + 3 * COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 4 * COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + 4 * COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 5 * COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + 5 * COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 6 * COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + 6 * COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 7 * COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.descent(),
            X_TABLE_LEFT + 7 * COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )
        canvas[currentPageCount].drawLine(
            X_TABLE_LEFT + 8 * COLUMN_SHIFT,
            Y_TABLE_TOP + paintThickLineTableBorder.ascent(),
            X_TABLE_LEFT + 8 * COLUMN_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight + paintThickLineTableBorder.descent(),
            paintText
        )

        //Particulars
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.particulars),
            X_TABLE_LEFT + PARTICULARS_HEADER_SHIFT,
            Y_TABLE_TOP,
            paintBoldText
        )

        //Category
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.category),
            X_TABLE_LEFT + CATEGORY_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //No. of Days
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.no_of_days),
            X_TABLE_LEFT + COLUMN_SHIFT + NO_OF_DAYS_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Total daily Allowance
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.total_daily_allowance),
            X_TABLE_LEFT + 2 * COLUMN_SHIFT + TOTAL_DAILY_ALLOWANCE_HEADER_SHIFT,
            Y_TABLE_TOP,
            paintBoldText
        )


        //Rate
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.rate),
            X_TABLE_LEFT + 2 * COLUMN_SHIFT + RATE_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Amount
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.amount),
            X_TABLE_LEFT + 3 * COLUMN_SHIFT + AMOUNT_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )


        //Accrued Saving
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.accrued_saving),
            X_TABLE_LEFT + 4 * COLUMN_SHIFT + ACCRUED_SAVING_HEADER_SHIFT,
            Y_TABLE_TOP,
            paintBoldText
        )


        //Rate
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.rate),
            X_TABLE_LEFT + 4 * COLUMN_SHIFT + RATE_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Amount
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.amount),
            X_TABLE_LEFT + 5 * COLUMN_SHIFT + AMOUNT_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Amount Payable
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.amount_payable),
            X_TABLE_LEFT + 6 * COLUMN_SHIFT + AMOUNT_PAYABLE_HEADER_SHIFT,
            Y_TABLE_TOP,
            paintBoldText
        )


        //Rate
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.rate),
            X_TABLE_LEFT + 6 * COLUMN_SHIFT + RATE_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )

        //Amount
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.amount),
            X_TABLE_LEFT + 7 * COLUMN_SHIFT + AMOUNT_HEADER_SHIFT,
            Y_TABLE_TOP + textRowHeight,
            paintBoldText
        )


        //less than 24H
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.less24h),
            X_TABLE_LEFT + LESS24H_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )

        val less24HDays = timeSheets.count { it.dailyAllowance == AllowanceType._8hours }
        val fullDays = timeSheets.count { it.dailyAllowance == AllowanceType._24hours }
        //less than 24H Count
        canvas[currentPageCount].drawText(
            less24HDays.toString(),
            X_TABLE_LEFT + COLUMN_SHIFT + DAYS_COUNT_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        val less24HRate = get8HDayAllowanceUseCase()
        val fullRate = getFullDayAllowanceUseCase()

        //less than 24H Rate
        canvas[currentPageCount].drawText(
            less24HRate.toString(),
            X_TABLE_LEFT + 2 * COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        //less than 24H Amount
        canvas[currentPageCount].drawText(
            (less24HRate * less24HDays).toString(),
            X_TABLE_LEFT + 3 * COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        //less than 24H Saving Rate
        canvas[currentPageCount].drawText(
            "---",
            X_TABLE_LEFT + 4 * COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )
        //less than 24H Saving Amount
        canvas[currentPageCount].drawText(
            "---",
            X_TABLE_LEFT + 5 * COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 2 * textRowHeight,
            paintText
        )

        //Full 24H
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.full24h),
            X_TABLE_LEFT + FULL24H_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Full 24H Count
        canvas[currentPageCount].drawText(
            fullDays.toString(),
            X_TABLE_LEFT + COLUMN_SHIFT + DAYS_COUNT_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Full24H Rate
        canvas[currentPageCount].drawText(
            fullRate.toString(),
            X_TABLE_LEFT + 2 * COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Full 24H Amount
        canvas[currentPageCount].drawText(
            (fullRate * fullDays).toString(),
            X_TABLE_LEFT + 3 * COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        val savingRate = getSavingDeductibleUseCase()
        //Full24H Saving Rate
        canvas[currentPageCount].drawText(
            savingRate.toString(),
            X_TABLE_LEFT + 4 * COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )
        //Full 24H Saving Amount
        canvas[currentPageCount].drawText(
            (savingRate * fullDays).toString(),
            X_TABLE_LEFT + 5 * COLUMN_SHIFT + AMOUNT_SHIFT,
            Y_TABLE_TOP + 3 * textRowHeight,
            paintText
        )

        //Sub-totals
        canvas[currentPageCount].drawText(
            applicationContext.resources.getString(R.string.sub_totals),
            X_TABLE_LEFT + SUB_TOTAL_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight,
            paintBoldText
        )
        //Total Days Count
        canvas[currentPageCount].drawText(
            (less24HDays + fullDays).toString(),
            X_TABLE_LEFT + COLUMN_SHIFT + DAYS_COUNT_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight,
            paintText
        )
        //Total Amount
        canvas[currentPageCount].drawText(
            ((fullRate * fullDays) + (less24HRate * less24HDays)).toString(),
            X_TABLE_LEFT + 3 * COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight,
            paintText
        )

        //Total Saving Amount
        canvas[currentPageCount].drawText(
            ((savingRate * fullDays) + (less24HRate * less24HDays)).toString(),
            X_TABLE_LEFT + 5 * COLUMN_SHIFT + RATE_SHIFT,
            Y_TABLE_TOP + 4 * textRowHeight,
            paintText
        )




        for (currentItem in timeSheets) {

            if (currentPageCount > lastPageCreated) {
                createNewPage()


                //TODO: Create allowance total and calculation

                // --- Create Table Header ---


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

                // --- Signature ---

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
                    Calendar.getInstance().toStdDate(),
                    ENGINEER_SIGNATURE_DATE_SHIFT,
                    SIGNATURE_BOTTOM + paintText.ascent() - 2 * paintText.descent(),
                    paintText
                )


                //wait till new page created
                lastPageCreated = currentPageCount
            }
        }

//
//            if (currentItem == timeSheets.last())
//                canvas[currentPageCount].drawLine(
//                    X_TABLE_LEFT,
//                    yPositionForCurrentItem + paintText.descent(),
//                    X_TABLE_LEFT + TABLE_WIDTH,
//                    yPositionForCurrentItem + paintText.descent(),
//                    paintThickLineTableBorder
//                )
//            else
//                canvas[currentPageCount].drawLine(
//                    X_TABLE_LEFT,
//                    yPositionForCurrentItem + paintText.descent(),
//                    X_TABLE_LEFT + TABLE_WIDTH,
//                    yPositionForCurrentItem + paintText.descent(),
//                    paintText
//                )
//
//
//            canvas[currentPageCount].drawText(
//                currentItem.date.getDisplayName(
//                    Calendar.DAY_OF_WEEK,
//                    Calendar.SHORT_STANDALONE,
//                    Locale.getDefault()
//                ) ?: "Error", X_TABLE_LEFT + DAY_SHIFT, yPositionForCurrentItem, paintText
//            )
//
//            canvas[currentPageCount].drawText(
//                currentItem.date.toStdDate(),
//                X_TABLE_LEFT + COLUMN_SHIFT_TOTAL_DAILY_ALLOWANCE + DATE_SHIFT,
//                yPositionForCurrentItem,
//                paintText
//            )
//            canvas[currentPageCount].drawText(
//                currentItem.travelStart.toTime24(),
//                X_TABLE_LEFT + 2 * COLUMN_SHIFT + TRAVEL_START_SHIFT,
//                yPositionForCurrentItem,
//                paintText
//            )
//            canvas[currentPageCount].drawText(
//                currentItem.workStart.toTime24(),
//                X_TABLE_LEFT + 3 * COLUMN_SHIFT + WORK_START_SHIFT,
//                yPositionForCurrentItem,
//                paintText
//            )
//            canvas[currentPageCount].drawText(
//                currentItem.workEnd.toTime24(),
//                X_TABLE_LEFT + 4 * COLUMN_SHIFT + WORK_END_SHIFT,
//                yPositionForCurrentItem,
//                paintText
//            )
//            canvas[currentPageCount].drawText(
//                currentItem.travelEnd.toTime24(),
//                X_TABLE_LEFT + 5 * COLUMN_SHIFT + TRAVEL_END_SHIFT,
//                yPositionForCurrentItem,
//                paintText
//            )
//            canvas[currentPageCount].drawText(
//                currentItem.breakTimeString,
//                X_TABLE_LEFT + 6 * COLUMN_SHIFT + BREAK_SHIFT,
//                yPositionForCurrentItem,
//                paintText
//            )
//
//
//            // Calculate work duration and travel
//            if (currentItem.noWorkDay) {
//                canvas[currentPageCount].drawText(
//                    "---",
//                    X_TABLE_LEFT + 7 * COLUMN_SHIFT + WORKING_HOURS_SHIFT,
//                    yPositionForCurrentItem,
//                    paintText
//                )
//                canvas[currentPageCount].drawText(
//                    "---",
//                    X_TABLE_LEFT + 9 * COLUMN_SHIFT + TRAVEL_HOURS_SHIFT,
//                    yPositionForCurrentItem,
//                    paintText
//                )
//            } else {
//                canvas[currentPageCount].drawText(
//                    currentItem.workTimeString,
//                    X_TABLE_LEFT + 7 * COLUMN_SHIFT + WORKING_HOURS_SHIFT,
//                    yPositionForCurrentItem,
//                    paintText
//                )
//                canvas[currentPageCount].drawText(
//                    currentItem.overTimeString,
//                    X_TABLE_LEFT + 8 * COLUMN_SHIFT + WORKING_HOURS_SHIFT,
//                    yPositionForCurrentItem,
//                    paintText
//                )
//                canvas[currentPageCount].drawText(
//                    currentItem.travelTimeString,
//                    X_TABLE_LEFT + 9 * COLUMN_SHIFT + TRAVEL_HOURS_SHIFT,
//                    yPositionForCurrentItem,
//                    paintText
//                )
//            }
//
//            canvas[currentPageCount].drawText(
//                currentItem.traveledDistance.toString(),
//                X_TABLE_LEFT + 10 * COLUMN_SHIFT + TRAVEL_DISTANCE_SHIFT,
//                yPositionForCurrentItem,
//                paintText
//            )
//
//            yPositionForCurrentItem += (textRowHeight)
//            if (yPositionForCurrentItem > SIGNATURE_BOTTOM - textRowHeight - userSignHeight || currentItem == timeSheets.last()) {
//
//                // Draw Table Columns
//                for (i in 0..11) {
//                    when (i) {
//                        0, 11 -> {
//                            canvas[currentPageCount].drawLine(
//                                X_TABLE_LEFT + i * COLUMN_SHIFT,
//                                Y_TABLE_TOP + paintText.ascent(),
//                                X_TABLE_LEFT + i * COLUMN_SHIFT,
//                                yPositionForCurrentItem - textRowHeight + paintThickLineTableBorder.descent(),
//                                paintThickLineTableBorder
//                            )
//                        }
//                        1 -> {
//                            canvas[currentPageCount].drawLine(
//                                X_TABLE_LEFT + COLUMN_SHIFT_TOTAL_DAILY_ALLOWANCE,
//                                Y_TABLE_TOP + paintText.ascent(),
//                                X_TABLE_LEFT + COLUMN_SHIFT_TOTAL_DAILY_ALLOWANCE,
//                                yPositionForCurrentItem - textRowHeight + paintText.descent(),
//                                paintText
//                            )
//                        }
//                        else -> {
//                            canvas[currentPageCount].drawLine(
//                                X_TABLE_LEFT + i * COLUMN_SHIFT,
//                                Y_TABLE_TOP + paintText.ascent(),
//                                X_TABLE_LEFT + i * COLUMN_SHIFT,
//                                yPositionForCurrentItem - textRowHeight + paintText.descent(),
//                                paintText
//                            )
//                        }
//                    }
//                }
//
//                // End Current Page if new page will start
//
//                if (yPositionForCurrentItem > SIGNATURE_BOTTOM - textRowHeight - userSignHeight) {
//                    pdfDocument.finishPage(page[currentPageCount])
//                    yPositionForCurrentItem = yPositionStart
//                    currentPageCount++
//                }
//            }
//        }

//        // Create totals
//        canvas[currentPageCount].drawText(
//            "Total",
//            X_TABLE_LEFT + 6 * COLUMN_SHIFT + TOTAL_SHIFT,
//            yPositionForCurrentItem,
//            paintBoldText
//        )


//        for (i in 7..11) {
//            canvas[currentPageCount].drawLine(
//                X_TABLE_LEFT + i * COLUMN_SHIFT,
//                yPositionForCurrentItem + paintText.ascent(),
//                X_TABLE_LEFT + i * COLUMN_SHIFT,
//                yPositionForCurrentItem + paintText.descent(),
//                paintThickLineTableBorder
//            )
//        }
//
//        canvas[currentPageCount].drawLine(
//            X_TABLE_LEFT + 7 * COLUMN_SHIFT,
//            yPositionForCurrentItem + paintText.descent(),
//            X_TABLE_LEFT + 11 * COLUMN_SHIFT,
//            yPositionForCurrentItem + paintText.descent(),
//            paintThickLineTableBorder
//        )


        pdfDocument.finishPage(page[currentPageCount])


        // Save PDF

        val directory = File(applicationContext.filesDir.path + "/" + hmeCode.code)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        return withContext(Dispatchers.IO) {
            try {
                if (hmeCode.fileNumber == 0) {
                    pdfDocument.writeTo(
                        FileOutputStream(
                            File(
                                directory,
                                hmeCode.code + "expanse.pdf"
                            )
                        )
                    )
                } else {
                    val fileNumber = hmeCode.fileNumber + 1
                    pdfDocument.writeTo(
                        FileOutputStream(
                            File(
                                directory,
                                hmeCode.code + "_" + fileNumber + "expanse.pdf"
                            )
                        )
                    )
                }

                pdfDocument.close()

//                updateHMECodeUseCase(
//                    hmeCode.id!!,
//                    hmeCode.customerId,
//                    hmeCode.code,
//                    hmeCode.machineType,
//                    hmeCode.machineNumber,
//                    hmeCode.workDescription,
//                    hmeCode.fileNumber + 1,
//                    hmeCode.signerName,
//                    Calendar.getInstance()
//                ).collect()
//
//                markCreatedTimeSheetUseCase(timeSheets).collect()
                return@withContext Result.success()
            } catch (e: IOException) {
                e.printStackTrace()
                pdfDocument.close()
                return@withContext Result.failure()
            }
        }
    }
}