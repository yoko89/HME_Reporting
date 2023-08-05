package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expanse

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

private const val TAG = "NewExpanseScreen"

@Composable
fun NewExpanseScreen(
    state: NewExpanseState,
    uiEvent: Flow<NewExpanseUiEvents>,
    userEvent: (NewExpanseUserEvent) -> Unit,
) {
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val dateInteractionSource = remember { MutableInteractionSource() }

    val imageCapture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            userEvent(NewExpanseUserEvent.PhotoTaken)
        }
    )

    val imageSelection = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            userEvent(NewExpanseUserEvent.PhotoPicked(context, uri))
        }
    )

    LaunchedEffect(key1 = uiEvent) {
        uiEvent.collect { event ->
            when (event) {
                is NewExpanseUiEvents.TakePicture -> {
                    imageCapture.launch(event.uri)
                }

                is NewExpanseUiEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)

                NewExpanseUiEvents.PickPicture -> imageSelection.launch(
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
                is PressInteraction.Release -> userEvent(NewExpanseUserEvent.DateClicked)
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
                userEvent(NewExpanseUserEvent.DatePicked(year, month, day))
            },
            canceled = {
                userEvent(NewExpanseUserEvent.DatePickedCanceled)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = { userEvent(NewExpanseUserEvent.TakePicture(context)) }) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Add Invoice Image from camera"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { userEvent(NewExpanseUserEvent.PickPicture) }) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Add Invoice Image from gallery"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { userEvent(NewExpanseUserEvent.InsertExpanse) }) {
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
                userEvent(NewExpanseUserEvent.CustomerSelected(customer))
            }

            DropDown(
                dropDownList = state.hmeCodes,
                selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                label = "HME Code",
                dropDownContentDescription = "Select HME Code",
                modifier = Modifier.padding(bottom = 5.dp)
            ) { hmeCode ->
                userEvent(NewExpanseUserEvent.HmeSelected(hmeCode))
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
                    .fillMaxWidth(),
                readOnly = true,
                interactionSource = dateInteractionSource
            )

            OutlinedTextField(
                value = state.invoiceNumber,
                onValueChange = {
                    userEvent(NewExpanseUserEvent.InvoiceNumberChanged(it))
                },
                label = { Text(text = "Invoice Number") },
                modifier = Modifier
                    .fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = {
                    userEvent(NewExpanseUserEvent.DescriptionChanged(it))
                },
                label = { Text(text = "Description") },
                modifier = Modifier
                    .fillMaxWidth(),
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
                        userEvent(NewExpanseUserEvent.CashCheckChanged(it))
                    }
                )

            }

            OutlinedTextField(
                value = state.amount.string?:"",
                onValueChange = {
                    userEvent(NewExpanseUserEvent.AmountChanged(it))
                },
                label = { Text(text = "Amount") },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    userEvent(NewExpanseUserEvent.CurrencySelected(it))
                }
            )

            OutlinedTextField(
                value = state.amountAED.string?:"",
                onValueChange = {
                    userEvent(NewExpanseUserEvent.AmountAEDChanged(it))
                },
                label = { Text(text = "Amount in AED") },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                userEvent(NewExpanseUserEvent.DeleteImage(uri))
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