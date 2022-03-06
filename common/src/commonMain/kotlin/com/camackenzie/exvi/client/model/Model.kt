package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.BodyStats
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.russhwolf.settings.Settings

@Serializable
class Model : SelfSerializable {

    val accountManager: AccountManager = AccountManager()
    val exerciseManager: ExerciseManager = ExerciseManager()

    @Transient
    val settings = Settings()

    override fun getUID(): String = uid

    override fun toJson(): String = Json.encodeToString(this)

    val activeAccount: Account?
        get() = accountManager.activeAccount

    val bodyStats: BodyStats?
        get() = activeAccount?.bodyStats

    val workoutManager: SyncedWorkoutManager?
        get() = accountManager.activeAccount?.workoutManager

    fun signOutCurrentAccount() {
        settings.remove("activeUser")
        accountManager.activeAccount = null
    }

    companion object {
        const val uid = "ExviClientModel"
    }

}