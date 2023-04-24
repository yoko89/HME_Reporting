package com.neklaway.hme_reporting.common.presentation.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File

@Composable
fun <T> ListDialog(
    list: List<T>,
    onClick: (T) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = { onCancel() },
    ) {
        LazyColumn(modifier = modifier.heightIn(max= 500.dp)) {
            items(list) {
                Card(modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .heightIn(min = 25.dp)
                    .clickable { onClick(it) }
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