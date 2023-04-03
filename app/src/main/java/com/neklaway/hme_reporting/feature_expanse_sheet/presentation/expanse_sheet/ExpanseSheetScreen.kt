@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.neklaway.hme_reporting.common.data.entity.Accommodation
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.common.presentation.common.component.ListDialog
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse.EditExpanseViewModel
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet.component.ExpanseSheetHeader
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet.component.ExpanseSheetItemCard
import com.neklaway.hme_reporting.utils.Constants.EXPANSE_FOLDER
import com.neklaway.hme_reporting.utils.NotificationPermissionRequest
import java.io.File

private const val TAG = "ExpanseSheetScreen"

@Composable
fun ExpanseSheetScreen(
    navController: NavController,
    viewModel: ExpanseSheetViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var requestPermission by remember {
        mutableStateOf(false)
    }
    val events = viewModel.event

    val context = LocalContext.current

    LaunchedEffect(
        events
    ) {
        events.collect { event ->
            when (event) {
                is ExpanseSheetEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)

                is ExpanseSheetEvents.NavigateToExpanseSheet -> navController.navigate(
                    Screen.EditExpanse.route + "?" + EditExpanseViewModel.EXPANSE_ID + "=" + event.id
                )
                is ExpanseSheetEvents.ShowFile -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val fileUri = FileProvider.getUriForFile(
                        context, "com.neklaway.hme_reporting.provider", event.file
                    )
                    intent.setDataAndType(fileUri, "application/pdf")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.startActivity(intent)
                }

            }

        }
    }


    val fabRotation = animateFloatAsState(
        targetValue = if (state.fabVisible) 90f else 0f, animationSpec = tween(500)
    )


    Scaffold(floatingActionButton = {
        Row {
            AnimatedVisibility(
                visible = state.fabVisible,
                enter = slideInVertically(initialOffsetY = { it }).plus(fadeIn()),
                exit = slideOutVertically(targetOffsetY = { it }).plus(fadeOut())
            ) {
                Row {
                    FloatingActionButton(onClick = {

                        requestPermission = true
                        viewModel.createExpanseSheet()
                    }) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Create ExpanseSheet PDF",
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))
                    FloatingActionButton(onClick = { viewModel.openExpanseSheets() }) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Open ExpanseSheet",
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
    }, snackbarHost = {
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
            DropDown(modifier = Modifier.padding(vertical = 5.dp),
                dropDownList = state.customers,
                selectedValue = state.selectedCustomer?.name ?: "No Customer Selected",
                label = "Customer",
                dropDownContentDescription = "Select Customer",
                onSelect = { customer ->
                    viewModel.customerSelected(customer)
                })

            DropDown(modifier = Modifier.padding(bottom = 5.dp),
                dropDownList = state.hmeCodes,
                selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                label = "HME Code",
                dropDownContentDescription = "Select HME Code",
                onSelect = { hmeCode ->
                    viewModel.hmeSelected(hmeCode)
                })

            val infiniteTransition = rememberInfiniteTransition()

            DropDown(
                modifier = Modifier
                    .padding(bottom = 5.dp),
                dropDownList = Accommodation.values().toList(),
                selectedValue = state.accommodation?.name ?: "Not Selected",
                label = "Accommodation paid by",
                dropDownContentDescription = "Accommodation type",
                onSelect = { accommodation ->
                    viewModel.accommodationChanged(accommodation)
                },
                warning = state.accommodation == null,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (state.missingDailyAllowance) {
                            infiniteTransition.animateColor(
                                initialValue = MaterialTheme.colorScheme.background,
                                targetValue = Color.Yellow,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = 500,
                                        delayMillis = 1000,
                                        easing = LinearEasing
                                    ), repeatMode = RepeatMode.Reverse
                                )
                            ).value
                        } else MaterialTheme.colorScheme.background
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(text = "Less than 24H")
                    Text(
                        text = state.lessThan24hDays.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Column {
                    Text(text = "Full 24H")
                    Text(
                        text = state.fullDays.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Column {
                    Text(text = "No Allowance")
                    Text(
                        text = state.noAllowanceDays.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {

                item {
                    AnimatedVisibility(
                        visible = state.loading, enter = fadeIn(), exit = fadeOut()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                item {
                    ExpanseSheetHeader(modifier = Modifier.fillMaxWidth())
                }

                items(items = state.expanseList) { expanse ->
                    ExpanseSheetItemCard(
                        expanse = expanse,
                        currencyExchange = viewModel.getCurrencyExchangeName(expanse)
                            .collectAsState(initial = ""),
                        cardClicked = { viewModel.expanseClicked(expanse) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "Total Payable = ${state.totalPaidAmount}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = state.showFileList) {
            val expansePdfDirectory =
                File(context.filesDir.path + "/" + state.selectedHMECode?.code + "/" + EXPANSE_FOLDER)
            val listOfFiles = expansePdfDirectory.listFiles()?.toList() ?: emptyList()

            ListDialog<File>(list = listOfFiles,
                modifier = Modifier.fillMaxHeight(0.8f),
                onClick = { file ->
                    viewModel.fileSelected(file)
                },
                onCancel = {
                    viewModel.fileSelectionCanceled()
                })
        }

        if (requestPermission) {
            NotificationPermissionRequest(context = context)
            requestPermission = false
        }
    }
}