package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Model : SelfSerializable {

    val accountManager: AccountManager = AccountManager()
    val exerciseManager: ExerciseManager = ExerciseManager()

    override fun getUID(): String {
        return "Model"
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

}