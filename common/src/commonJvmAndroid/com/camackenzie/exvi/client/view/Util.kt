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