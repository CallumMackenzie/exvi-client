package com.camackenzie.exvi.client.view

import androidx.compose.runtime.*
import com.camackenzie.exvi.core.util.SelfSerializable
import com.camackenzie.exvi.core.api.NoneResult
import com.camackenzie.exvi.core.util.ExviLogger
import kotlinx.coroutines.*
import kotlin.Unit

fun noArgs(): SelfSerializable = NoneResult()

/**
 * Ensures there is an active account.
 * If there is no active account, app returns to the entry page
 */
fun ensureActiveAccount(appState: AppState) {
    if (!appState.model.accountManager.hasActiveAccount()) {
        ExviLogger.w("No active account, switching to login view", tag = "GUI")
        appState.setView(ExviView.Login)
    }
}

fun listToFormattedString(l: List<*>): String = l.toString().replace(Regex("\\]|\\["), "")
fun List<*>.toFormattedString(): String = listToFormattedString(this)
fun Set<*>.toFormattedString(): String = this.toList().toFormattedString()

fun waitUntilTrue(
    supplier: () -> Boolean,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
    whenTrue: () -> Unit,
) = coroutineScope.launch(coroutineDispatcher) {
    while (!supplier()) delay(100)
    whenTrue()
}

fun <T> delegatedMutableStateOf(
    value: T,
    onGet: () -> Unit = {},
    onSet: (T) -> Unit = {}
): MutableState<T> =
    object : MutableState<T> {
        var mutableState = mutableStateOf(value)

        override var value: T
            get() {
                onGet()
                return mutableState.value
            }
            set(it) {
                onSet(it)
                mutableState.value = it
            }

        override fun component1(): T = mutableState.component1()
        override fun component2(): (T) -> kotlin.Unit = mutableState.component2()
    }