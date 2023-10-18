package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.neklaway.hme_reporting.common.data.entity.Accommodation
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.common.presentation.common.component.ListDialog
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse.EditExpanseViewModel
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet.component.ExpanseSheetHeader
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet.component.ExpanseSheetItemCard
import com.neklaway.hme_reporting.utils.Constants.EXPENSE_FOLDER
import com.neklaway.hme_reporting.utils.NotificationPermissionRequest
import kotlinx.coroutines.flow.Flow
import java.io.File


@Composable
fun ExpenseSheetScreen(
    navController: NavController,
    state: ExpanseSheetState,
    uiEvents: Flow<ExpanseSheetUiEvents>,
    userEvent: (ExpanseSheetUserEvent) -> Unit,
    getCurrencyExchange: (Long) -> Flow<String>,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var requestPermission by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    LaunchedEffect(
        uiEvents
    ) {
        uiEvents.collect { event ->
            when (event) {
                is ExpanseSheetUiEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)

                is ExpanseSheetUiEvents.NavigateToExpanseSheetUi -> navController.navigate(
                    Screen.EditExpense.route + "?" + EditExpanseViewModel.EXPANSE_ID + "=" + event.id
                )

                is ExpanseSheetUiEvents.ShowFile -> {
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
        targetValue = if (state.fabVisible) 90f else 0f, animationSpec = tween(500), label = ""
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
                        userEvent(ExpanseSheetUserEvent.CreateExpanseSheet)
                    }) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Create ExpanseSheet PDF",
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))
                    FloatingActionButton(onClick = { userEvent(ExpanseSheetUserEvent.OpenExpanseSheets) }) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Open ExpanseSheet",
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                }
            }

            FloatingActionButton(onClick = { userEvent(ExpanseSheetUserEvent.ShowMoreFABClicked) }) {
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
                userEvent(ExpanseSheetUserEvent.CustomerSelected(customer))
            }

            DropDown(
                dropDownList = state.hmeCodes,
                selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                label = "HME Code",
                dropDownContentDescription = "Select HME Code",
                modifier = Modifier.padding(bottom = 5.dp)
            ) { hmeCode ->
                userEvent(ExpanseSheetUserEvent.HmeSelected(hmeCode))
            }

            val infiniteTransition = rememberInfiniteTransition(label = "")

            DropDown(
                dropDownList = Accommodation.values().toList(),
                selectedValue = state.accommodation?.name ?: "Not Selected",
                label = "Accommodation paid by",
                dropDownContentDescription = "Accommodation type",
                modifier = Modifier
                    .padding(bottom = 5.dp),
                warning = state.accommodation == null,
            ) { accommodation ->
                userEvent(ExpanseSheetUserEvent.AccommodationChanged(accommodation))
            }

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
                                ), label = ""
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

                items(items = state.expenseList) { expanse ->
                    ExpanseSheetItemCard(
                        expense = expanse,
                        currencyExchange = getCurrencyExchange(expanse.currencyID)
                            .collectAsState(
                                initial = ""
                            ),
                        cardClicked = { userEvent(ExpanseSheetUserEvent.ExpanseClicked(expanse)) },
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
                File(context.filesDir.path + "/" + state.selectedHMECode?.code + "/" + EXPENSE_FOLDER)
            val listOfFiles =
                expansePdfDirectory.listFiles()?.filter { it.isFile }?.toList() ?: emptyList()

//
            ListDialog(list = listOfFiles, onClick = {
                userEvent(ExpanseSheetUserEvent.FileSelected(it))
            }, onCancel = {
                userEvent(ExpanseSheetUserEvent.FileSelectionCanceled)
            },
                onLongClick = {
                    userEvent(ExpanseSheetUserEvent.FileLongClick(it))
                })
        }

        if (requestPermission) {
            NotificationPermissionRequest(context = context)
            requestPermission = false
        }
    }
}