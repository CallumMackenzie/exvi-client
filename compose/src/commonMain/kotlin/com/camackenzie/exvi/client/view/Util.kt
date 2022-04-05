package com.camackenzie.exvi.client.view

import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.util.None
import com.camackenzie.exvi.core.util.SelfSerializable
import com.camackenzie.exvi.client.model.WorkoutGeneratorParams
import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.ExviLogger
import kotlinx.coroutines.*
import kotlin.Unit

fun selfSerializableFromMap(map: Map<String, Any?>): SelfSerializable =
    selfSerializableFromJson(map["json"] as String, map["uid"] as String)

fun selfSerializableToMap(ss: SelfSerializable): Map<String, Any?> =
    mapOf("json" to ss.toJson(), "uid" to ss.getUID())

fun selfSerializableFromJson(json: String, uid: String): SelfSerializable =
    when (uid) {
        ActualWorkout.uid -> ExviSerializer.fromJson<Workout>(json)
        Model.uid -> ExviSerializer.fromJson<Model>(json)
        ActualActiveWorkout.uid -> ExviSerializer.fromJson<ActiveWorkout>(json)
        ActualBodyStats.uid -> ExviSerializer.fromJson<BodyStats>(json)
        None.uid -> ExviSerializer.fromJson<None>(json)
        ActualExercise.uid -> ExviSerializer.fromJson<Exercise>(json)
        WorkoutGeneratorParams.uid -> ExviSerializer.fromJson<WorkoutGeneratorParams>(json)
        ExviView.uid -> ExviSerializer.fromJson<ExviView>(json)
        else -> throw Exception("Could not restore type \"$uid\"")
    }

val SelfSerializableSaver = mapSaver<SelfSerializable>(save = {
    selfSerializableToMap(it)
}, restore = {
    selfSerializableFromMap(it)
})

fun noArgs(): SelfSerializable = None

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
    onGet: () -> kotlin.Unit = {},
    onSet: (T) -> kotlin.Unit = {}
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