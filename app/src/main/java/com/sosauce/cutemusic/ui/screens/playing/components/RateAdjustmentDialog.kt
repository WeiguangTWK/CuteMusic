package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun RateAdjustmentDialog(
    rate: Float,
    onSetNewRate: (Float) -> Unit,
    titleText: String,
    onDismissRequest: () -> Unit
) {

    var newRate by remember { mutableStateOf("%.2f".format(rate)) }
    val focusRequest = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequest.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                CuteText(stringResource(id = R.string.cancel))
            }
        },
        title = { CuteText(titleText) },
        text = {
            Column {
                CuteText(stringResource(id = R.string.new_rate))
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = newRate.toString(),
                        onValueChange = { newRate = it },
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (newRate.toFloat() > 2.0f) {
                                    onSetNewRate(2.0f)
                                } else if (newRate.toFloat() < 0.5f) {
                                    onSetNewRate(0.5f)
                                } else {
                                    onSetNewRate(newRate.toFloat())
                                }
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .focusRequester(focusRequest),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center
                        ),
                    )
                }
            }
        }
    )
}