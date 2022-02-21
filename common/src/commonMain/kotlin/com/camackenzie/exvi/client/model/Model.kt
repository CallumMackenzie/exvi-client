package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.BodyStats
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Model : SelfSerializable {

    val accountManager: AccountManager = AccountManager()
    val exerciseManager: ExerciseManager = ExerciseManager()

    override fun getUID(): String {
        return uid
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    val activeAccount: Account?
        get() {
            return accountManager.activeAccount
        }

    val bodyStats: BodyStats?
        get() {
            return activeAccount?.bodyStats
        }

    val workoutManager: SyncedWorkoutManager?
        get() {
            return accountManager.activeAccount?.workoutManager
        }

    companion object {
        const val uid = "ExviClientModel"
    }

}