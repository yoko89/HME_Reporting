package com.neklaway.hme_reporting.common.presentation.common.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ListDialog(
    list: List<T>,
    onClick: (T) -> Unit,
    onCancel: () -> Unit,
    onLongClick: (T) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = { onCancel() },
    ) {
        LazyColumn(modifier = modifier.heightIn(max = 500.dp)) {
            items(list) {
                Card(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .heightIn(min = 25.dp)
                        .combinedClickable(enabled = true,
                            onClick = { onClick(it) },
                            onLongClick = { onLongClick(it) })
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        if (it is File)
                            Text(
                                text = it.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        else
                            Text(
                                text = it.toString(),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun ListDialogPreview() {
    ListDialog(list = listOf("123444", "135re44"), onClick = {}, onCancel = { })
}