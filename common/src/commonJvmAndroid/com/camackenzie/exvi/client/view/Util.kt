package com.camackenzie.exvi.client.view

import androidx.compose.runtime.saveable.mapSaver
import com.camackenzie.exvi.client.model.Model
import kotlinx.serialization.*
import kotlinx.serialization.json.*

val ModelSaver = mapSaver<Model>(
    save = {
        mapOf("json" to it.toJson())
    },
    restore = {
        Json.decodeFromString(it["json"] as String)
    }
)

fun EnsureActiveAccount(model: Model, onViewChange: ViewChangeFun) {
    if (!model.accountManager.hasActiveAccount()) {
        println("No active account, switching to login view")
        onViewChange(ExviView.LOGIN) {}
    }
}