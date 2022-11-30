@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.common.presentation.common.component.ListDialog
import com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet.EditTimeSheetViewModel
import com.neklaway.hme_reporting.feature_signature.presentation.signature.SignatureScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.component.TimeSheetHeader
import com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.component.TimeSheetItemCard
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import java.io.File

private const val TAG = "TimeSheetScreen"

@Composable
fun TimeSheetScreen(
    navController: NavController,
    viewModel: TimeSheetViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val events = viewModel.event

    val context = LocalContext.current


    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )

    LaunchedEffect(
        key1 = events, key2 = state
    ) {

        events.collect { event ->
            when (event) {
                is TimeSheetEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)

                is TimeSheetEvents.NavigateToTimeSheet ->
                    navController.navigate(
                        Screen.EditTimeSheetScreen.route
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


    HMEReportingTheme {
        Scaffold(
            floatingActionButton = {
                Row {
                    AnimatedVisibility(
                        visible = state.fabVisible,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
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
                            FloatingActionButton(onClick = {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (!hasNotificationPermission) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }

                                viewModel.createTimeSheet()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "Create TimeSheet PDF",
                                )
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

        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = it)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                DropDown(
                    modifier = Modifier.padding(vertical = 5.dp),
                    dropDownList = state.customers,
                    selectedValue = state.selectedCustomer?.name ?: "No Customer Selected",
                    label = "Customer",
                    dropDownContentDescription = "Select Customer",
                    onSelect = { customer ->
                        viewModel.customerSelected(customer)
                    }
                )

                DropDown(
                    modifier = Modifier.padding(bottom = 5.dp),
                    dropDownList = state.hmeCodes,
                    selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                    label = "HME Code",
                    dropDownContentDescription = "Select HME Code",
                    onSelect = { hmeCode ->
                        viewModel.hmeSelected(hmeCode)
                    }
                )


                AnimatedVisibility(
                    visible = state.isIbau,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    DropDown(
                        modifier = Modifier.padding(bottom = 5.dp),
                        dropDownList = state.ibauCodes,
                        selectedValue = state.selectedIBAUCode?.code ?: "No IBAU Code Selected",
                        label = "IBAU Code",
                        dropDownContentDescription = "Select IBAU Code",
                        onSelect = { ibauCode ->
                            viewModel.ibauSelected(ibauCode)
                        }
                    )
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
                        SignatureScreen(signatureFileName = it.toString(), requireSignerName = true,
                            signatureUpdatedAtExit = { signedSuccessfully, signerName ->
                                if (signedSuccessfully) {
                                    viewModel.signatureDone(signerName)
                                } else {
                                    viewModel.signatureCanceled()
                                }

                            })
                    }
                }
            }

            AnimatedVisibility(visible = state.showFileList) {
                val pdfDirectory = File(context.filesDir.path + "/" + state.selectedHMECode?.code)
                val listOfFiles = pdfDirectory.listFiles()?.toList() ?: emptyList()

                ListDialog<File>(list = listOfFiles,
                    modifier = Modifier.fillMaxHeight(0.8f),
                    onClick = { file ->
                        viewModel.fileSelected(file)
                    }, onCancel = {
                        viewModel.fileSelectionCanceled()
                    })
            }
        }
    }
}



