package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.navigation.NavController
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.CustomDatePicker
import com.neklaway.hme_reporting.common.presentation.common.component.DeleteDialog
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.utils.BitmapOrientationCorrector
import com.neklaway.hme_reporting.utils.toDate
import kotlinx.coroutines.flow.Flow
import java.util.Calendar


@Composable
fun EditExpanseScreen(
    navController: NavController,
    state: EditExpanseState,
    uiEvents: Flow<EditExpanseUiEvents>,
    userEvent: (EditExpanseUserEvent) -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val dateInteractionSource = remember { MutableInteractionSource() }
    var deleteDialogVisible by remember {
        mutableStateOf(false)
    }
    AnimatedVisibility(visible = deleteDialogVisible) {
        DeleteDialog(item = state.date,
            onConfirm = {
                userEvent(EditExpanseUserEvent.DeleteExpanse)
                deleteDialogVisible = false
            },
            onDismiss = { deleteDialogVisible = false }
        )
    }

    val imageCapture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            userEvent(EditExpanseUserEvent.PhotoTaken)
        }
    )
    val imageSelection = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            userEvent(EditExpanseUserEvent.PhotoPicked(context, uri))
        }
    )

    LaunchedEffect(key1 = uiEvents) {
        uiEvents.collect { event ->
            when (event) {
                EditExpanseUiEvents.PopBackStack -> navController.popBackStack(
                    Screen.ExpanseSheet.route,
                    false
                )

                is EditExpanseUiEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)
                is EditExpanseUiEvents.TakePicture -> {
                    imageCapture.launch(event.uri)
                }

                EditExpanseUiEvents.PickPicture -> imageSelection.launch(
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
                is PressInteraction.Release -> userEvent(EditExpanseUserEvent.DateClicked)
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
                userEvent(EditExpanseUserEvent.DatePicked(year, month, day))
            },
            canceled = {
                userEvent(EditExpanseUserEvent.DateShown)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = { deleteDialogVisible = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Expanse"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { userEvent(EditExpanseUserEvent.TakePicture(context)) }) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Add Invoice Image"
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { userEvent(EditExpanseUserEvent.PickPicture) }) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Add Invoice Image from gallery"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { userEvent(EditExpanseUserEvent.UpdateExpanse) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Update Expanse"
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
                    userEvent(EditExpanseUserEvent.InvoiceNumberChanged(it))
                },
                label = { Text(text = "Invoice Number") },
                modifier = Modifier
                    .fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = {
                    userEvent(EditExpanseUserEvent.DescriptionChanged(it))
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
                        userEvent(EditExpanseUserEvent.CashCheckChanged(it))
                    }
                )

            }

            OutlinedTextField(
                value = state.amount,
                onValueChange = {
                    userEvent(EditExpanseUserEvent.AmountChanged(it))
                },
                label = { Text(text = "Amount") },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            DropDown(
                dropDownList = state.currencyList,
                selectedValue = state.selectedCurrency?.currencyName ?: "",
                label = "Currency",
                dropDownContentDescription = "Currency",
                onSelect = {
                    userEvent(EditExpanseUserEvent.CurrencySelected(it))
                }
            )

            OutlinedTextField(
                value = state.amountAED,
                onValueChange = {
                    userEvent(EditExpanseUserEvent.AmountAEDChanged(it))
                },
                label = { Text(text = "Amount in AED") },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                            modifier = Modifier.padding(5.dp),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = {
                                userEvent(EditExpanseUserEvent.DeleteImage(uri))
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




