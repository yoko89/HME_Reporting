@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.CustomDatePicker
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.utils.toDate
import java.util.*


@Composable
fun EditExpanseScreen(
    navController: NavController,
    viewModel: EditExpanseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val event = viewModel.event

    val dateInteractionSource = remember { MutableInteractionSource() }

    val imageCapture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = viewModel::photoTaken
    )

    LaunchedEffect(key1 = event) {
        event.collect { event ->
            when (event) {
                EditExpanseEvents.PopBackStack -> navController.popBackStack(
                    Screen.ExpanseSheet.route,
                    false
                )
                is EditExpanseEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)
                is EditExpanseEvents.TakePicture -> {
                    imageCapture.launch(event.uri)
                }
            }
        }
    }

    LaunchedEffect(key1 = dateInteractionSource) {
        dateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> viewModel.dateClicked()
            }
        }
    }

    LaunchedEffect(key1 = true) {
        dateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> viewModel.dateClicked()
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
                viewModel.datePicked(year, month, day)
            },
            canceled = {
                viewModel.dateShown()
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = { viewModel.deleteExpanse() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Expanse"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))

                val context = LocalContext.current
                FloatingActionButton(onClick = { viewModel.takePicture(context) }) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = "Add Invoice Image"
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { viewModel.updateExpanse() }) {
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
                onValueChange = viewModel::invoiceNumberChanged,
                label = { Text(text = "Invoice Number") },
                modifier = Modifier
                    .fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::descriptionChanged,
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
                    onCheckedChange = viewModel::cashCheckChanged
                )

            }

            OutlinedTextField(
                value = state.amount,
                onValueChange = viewModel::amountChanged,
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
                onSelect = viewModel::currencySelected
            )

            OutlinedTextField(
                value = state.amountAED,
                onValueChange = viewModel::amountAEDChanged,
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
                    Box {
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.padding(5.dp),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = {
                                viewModel.deleteImage(uri)
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




