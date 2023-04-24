@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.common.presentation.common.component.ListDialog
import com.neklaway.hme_reporting.feature_signature.presentation.signature.SignatureScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet.EditTimeSheetViewModel
import com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.component.TimeSheetHeader
import com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.component.TimeSheetItemCard
import com.neklaway.hme_reporting.utils.NotificationPermissionRequest
import java.io.File
import java.io.FilenameFilter

private const val TAG = "TimeSheetScreen"


@Composable
fun TimeSheetScreen(
    navController: NavController,
    viewModel: TimeSheetViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var requestPermission by remember {
        mutableStateOf(false)
    }
    val events = viewModel.event

    val context = LocalContext.current

    LaunchedEffect(
        key1 = events, key2 = state
    ) {

        events.collect { event ->
            when (event) {
                is TimeSheetEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)

                is TimeSheetEvents.NavigateToTimeSheet ->
                    navController.navigate(
                        Screen.EditTimeSheet.route
                                + "?" + EditTimeSheetViewModel.TIME_SHEET_ID
                                + "=" + event.id
                    )
                is TimeSheetEvents.ShowFile -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        "com.neklaway.hme_reporting.provider",
                        event.file
                    )
                    intent.setDataAndType(fileUri, "application/pdf")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.startActivity(intent)
                }

            }

        }
    }


    val fabRotation = animateFloatAsState(
        targetValue = if (state.fabVisible) 90f else 0f,
        animationSpec = tween(500)
    )


    Scaffold(
        floatingActionButton = {
            Row {
                AnimatedVisibility(
                    visible = state.fabVisible,
                    enter = slideInVertically(initialOffsetY = { it }).plus(fadeIn()),
                    exit = slideOutVertically(targetOffsetY = { it }).plus(fadeOut())
                ) {
                    Row {
                        FloatingActionButton(
                            onClick = { viewModel.sign() },
                            contentColor = if (state.signatureAvailable) Color.Green else MaterialTheme.colorScheme.tertiary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Draw,
                                contentDescription = "Sign TimeSheet",
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        AnimatedVisibility(visible = state.timeSheets.any { it.selected }) {
                            FloatingActionButton(onClick = {

                                requestPermission = true
                                viewModel.createTimeSheet()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "Create TimeSheet PDF",
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        FloatingActionButton(onClick = { viewModel.openTimeSheets() }) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Open TimeSheet",
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }

                FloatingActionButton(onClick = { viewModel.showMoreFABClicked() }) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Show More Floating action buttons",
                        modifier = Modifier.rotate(fabRotation.value)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
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
                viewModel.customerSelected(customer)
            }

            DropDown(
                dropDownList = state.hmeCodes,
                selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                label = "HME Code",
                dropDownContentDescription = "Select HME Code",
                modifier = Modifier.padding(bottom = 5.dp)
            ) { hmeCode ->
                viewModel.hmeSelected(hmeCode)
            }


            AnimatedVisibility(
                visible = state.isIbau,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                DropDown(
                    dropDownList = state.ibauCodes,
                    selectedValue = state.selectedIBAUCode?.code ?: "No IBAU Code Selected",
                    label = "IBAU Code",
                    dropDownContentDescription = "Select IBAU Code",
                    modifier = Modifier.padding(bottom = 5.dp)
                ) { ibauCode ->
                    viewModel.ibauSelected(ibauCode)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {

                item {
                    AnimatedVisibility(
                        visible = state.loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                item {
                    TimeSheetHeader(
                        selectAll = state.selectAll,
                        onSelectAllChecked = { checked ->
                            viewModel.selectAll(checked)
                        })
                }

                items(items = state.timeSheets) { timeSheet ->
                    TimeSheetItemCard(timeSheet = timeSheet,
                        cardClicked = { viewModel.timesheetClicked(timeSheet) },
                        onCheckedChanged = { checked ->
                            viewModel.sheetSelectedChanged(timeSheet, checked)
                        })
                }
            }

            state.selectedHMECode?.id?.let {

                AnimatedVisibility(visible = state.showSignaturePad) {
                    SignatureScreen(
                        signatureFileName = it.toString(),
                        signatureUpdatedAtExit = { signedSuccessfully, signerName ->
                            if (signedSuccessfully) {
                                viewModel.signatureDone(signerName)
                            } else {
                                viewModel.signatureCanceled()
                            }

                        },
                        requireSignerName = true
                    )
                }
            }
        }

        AnimatedVisibility(visible = state.showFileList) {
            val pdfDirectory = File(context.filesDir.path + "/" + state.selectedHMECode?.code)
            val listOfFiles = pdfDirectory.listFiles()?.filter { it.isFile }?.toList() ?: emptyList()

            ListDialog(list = listOfFiles,
                onClick = viewModel::fileSelected,
                onCancel = viewModel::fileSelectionCanceled)
        }

        if (requestPermission) {
            NotificationPermissionRequest(context = context)
            requestPermission = false
        }
    }
}



