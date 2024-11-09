package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expense

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import com.neklaway.hme_reporting.common.presentation.common.component.CustomDatePicker
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.utils.BitmapOrientationCorrector
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.toDate
import kotlinx.coroutines.flow.Flow
import java.util.*

@Composable
fun NewExpanseScreen(
    state: NewExpenseState,
    uiEvent: Flow<NewExpenseUiEvents>,
    userEvent: (NewExpenseUserEvent) -> Unit,
) {
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val dateInteractionSource = remember { MutableInteractionSource() }

    val imageCapture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            userEvent(NewExpenseUserEvent.PhotoTaken)
        }
    )

    val imageSelection = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            userEvent(NewExpenseUserEvent.PhotoPicked(context, uri))
        }
    )

    val focusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = uiEvent) {
        uiEvent.collect { event ->
            when (event) {
                is NewExpenseUiEvents.TakePicture -> {
                    imageCapture.launch(event.uri)
                }

                is NewExpenseUiEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)

                NewExpenseUiEvents.PickPicture -> imageSelection.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }


    LaunchedEffect(key1 = dateInteractionSource) {
        dateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> userEvent(NewExpenseUserEvent.DateClicked)
            }
        }
    }

    if (state.showDatePicker) {
        CustomDatePicker(
            year = state.date?.get(Calendar.YEAR),
            month = state.date?.get(Calendar.MONTH),
            day = state.date?.get(Calendar.DAY_OF_MONTH),
            dateSet =
            { year, month, day ->
                userEvent(NewExpenseUserEvent.DatePicked(year, month, day))
            },
            canceled = {
                userEvent(NewExpenseUserEvent.DatePickedCanceled)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = { userEvent(NewExpenseUserEvent.TakePicture(context)) }) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Add Invoice Image from camera"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { userEvent(NewExpenseUserEvent.PickPicture) }) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Add Invoice Image from gallery"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { userEvent(NewExpenseUserEvent.InsertExpense) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Expanse"
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            DropDown(
                dropDownList = state.customers,
                selectedValue = state.selectedCustomer?.name ?: "No Customer Selected",
                label = "Customer",
                dropDownContentDescription = "Select Customer",
                modifier = Modifier.padding(vertical = 5.dp)
                ) { customer ->
                userEvent(NewExpenseUserEvent.CustomerSelected(customer))
            }

            DropDown(
                dropDownList = state.hmeCodes,
                selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                label = "HME Code",
                dropDownContentDescription = "Select HME Code",
                modifier = Modifier.padding(bottom = 5.dp)
            ) { hmeCode ->
                userEvent(NewExpenseUserEvent.HmeSelected(hmeCode))
            }

            AnimatedVisibility(
                visible = state.loading,
                enter = slideInHorizontally(initialOffsetX = {
                    -it
                }),
                exit = slideOutHorizontally(targetOffsetX = { -it })
            ) {
                CircularProgressIndicator()
            }
            OutlinedTextField(
                value = state.date.toDate(),
                onValueChange = {},
                label = { Text(text = "Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                readOnly = true,
                interactionSource = dateInteractionSource,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )

            OutlinedTextField(
                value = state.invoiceNumber,
                onValueChange = {
                    userEvent(NewExpenseUserEvent.InvoiceNumberChanged(it))
                },
                label = { Text(text = "Invoice Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = {
                    userEvent(NewExpenseUserEvent.DescriptionChanged(it))
                },
                label = { Text(text = "Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
                )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Paid in Cash")
                Checkbox(
                    checked = state.personallyPaid,
                    onCheckedChange = {
                        userEvent(NewExpenseUserEvent.CashCheckChanged(it))
                    }
                )

            }

            OutlinedTextField(
                value = state.amount.string?:"",
                onValueChange = {
                    userEvent(NewExpenseUserEvent.AmountChanged(it))
                },
                label = { Text(text = "Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                supportingText = {
                    Text(text = state.amount.message?:"")
                },
                isError = state.amount is ResourceWithString.Error
            )

            DropDown(
                dropDownList = state.currencyList,
                selectedValue = state.selectedCurrency?.currencyName ?: "",
                label = "Currency",
                dropDownContentDescription = "Currency",
                onSelect = {
                    userEvent(NewExpenseUserEvent.CurrencySelected(it))
                },
                modifier = Modifier.focusable(false)
            )

            OutlinedTextField(
                value = state.amountAED.string?:"",
                onValueChange = {
                    userEvent(NewExpenseUserEvent.AmountAEDChanged(it))
                },
                label = { Text(text = "Amount in AED") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                supportingText = {
                    Text(text = state.amountAED.message?:"")
                },
                isError = state.amountAED is ResourceWithString.Error
            )
            state.invoicesUris.forEach { uri ->
                val imageFile = uri.toFile()
                if (imageFile.exists()) {
                    val path = imageFile.absolutePath
                    val image = BitmapFactory.decodeFile(path)
                    val bitmapOrientationCorrector = BitmapOrientationCorrector()
                    val imageCorrected = bitmapOrientationCorrector(path, image)

                    Box {
                        Image(
                            bitmap = imageCorrected.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(5.dp),
                            contentScale = ContentScale.Fit,
                        )
                        IconButton(
                            onClick = {
                                userEvent(NewExpenseUserEvent.DeleteImage(uri))
                            },
                            modifier = Modifier.align(
                                Alignment.TopEnd
                            )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                            )
                        }
                    }
                }

            }
        }

    }
}