package com.camackenzie.exvi.client.view

import androidx.compose.runtime.saveable.mapSaver
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.model.ActiveWorkout
import com.camackenzie.exvi.core.model.BodyStats
import com.camackenzie.exvi.core.model.Exercise
import com.camackenzie.exvi.core.model.Workout
import com.camackenzie.exvi.core.util.None
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

val SelfSerializableSaver = mapSaver<SelfSerializable>(save = {
    mapOf("json" to it.toJson(), "uid" to it.getUID())
}, restore = {
    val json = it["json"] as String
    when (val uid = it["uid"] as String) {
        Workout.uid -> Json.decodeFromString<Workout>(json)
        Model.uid -> Json.decodeFromString<Model>(json)
        ActiveWorkout.uid -> Json.decodeFromString<ActiveWorkout>(json)
        BodyStats.uid -> Json.decodeFromString<BodyStats>(json)
        None.uid -> Json.decodeFromString<None>(json)
        Exercise.uid -> Json.decodeFromString<Exercise>(json)
        else -> throw Exception("Could not restore type \"$uid\" from: $it")
    }
})

fun noArgs(): SelfSerializable {
    return None
}

fun ensureActiveAccount(model: Model, onViewChange: ViewChangeFun) {
    if (!model.accountManager.hasActiveAccount()) {
        println("No active account, switching to login view")
        onViewChange(ExviView.Login, ::noArgs)
    }
}