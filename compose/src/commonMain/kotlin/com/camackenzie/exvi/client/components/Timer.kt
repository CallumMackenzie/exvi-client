package com.camackenzie.exvi.client.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.camackenzie.exvi.core.model.Time
import com.camackenzie.exvi.core.model.TimeUnit
import com.camackenzie.exvi.core.model.formatToElapsedTime
import com.camackenzie.exvi.core.model.milliseconds
import com.camackenzie.exvi.core.model.toDuration
import kotlinx.coroutines.*

@Composable
fun Timer(
    remainingTime: Time,
    onRemainingTimeChanged: (Time) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
    modifier: Modifier = Modifier,
    timeGranularity: Time = 100.milliseconds,
) {
    coroutineScope.launch(coroutineDispatcher) {
        delay(timeGranularity.toDuration())
        onRemainingTimeChanged(remainingTime - timeGranularity)
    }
    Text(remainingTime.formatToElapsedTime(setOf(TimeUnit.Second)))
}