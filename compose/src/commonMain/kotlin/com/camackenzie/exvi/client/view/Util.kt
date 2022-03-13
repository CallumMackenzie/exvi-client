package com.camackenzie.exvi.client.view

import androidx.compose.foundation.background
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.runtime.*
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.util.None
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.camackenzie.exvi.client.model.WorkoutGenerator
import com.camackenzie.exvi.client.model.WorkoutGeneratorParams
import com.camackenzie.exvi.core.model.*
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

fun selfSerializableFromMap(map: Map<String, Any?>): SelfSerializable =
    selfSerializableFromJson(map["json"] as String, map["uid"] as String)

fun selfSerializableToMap(ss: SelfSerializable): Map<String, Any?> =
    mapOf("json" to ss.toJson(), "uid" to ss.getUID())

fun selfSerializableFromJson(json: String, uid: String): SelfSerializable =
    when (uid) {
        Workout.uid -> Json.decodeFromString<Workout>(json)
        Model.uid -> Json.decodeFromString<Model>(json)
        ActiveWorkout.uid -> Json.decodeFromString<ActiveWorkout>(json)
        BodyStats.uid -> Json.decodeFromString<BodyStats>(json)
        None.uid -> Json.decodeFromString<None>(json)
        Exercise.uid -> Json.decodeFromString<Exercise>(json)
        WorkoutGeneratorParams.uid -> Json.decodeFromString<WorkoutGeneratorParams>(json)
        ExviView.uid -> Json.decodeFromString<ExviView>(json)
        else -> throw Exception("Could not restore type \"$uid\"")
    }

val SelfSerializableSaver = mapSaver<SelfSerializable>(save = {
    selfSerializableToMap(it)
}, restore = {
    selfSerializableFromMap(it)
})

fun noArgs(): SelfSerializable = None

fun ensureActiveAccount(appState: AppState) {
    if (!appState.model.accountManager.hasActiveAccount()) {
        println("No active account, switching to login view")
        appState.setView(ExviView.Login)
    }
}