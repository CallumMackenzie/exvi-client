package com.camackenzie.exvi.client.components

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable

@Composable
expect fun AlertDialog(
    onDismissRequest: (() -> Unit),
    buttons: (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
)