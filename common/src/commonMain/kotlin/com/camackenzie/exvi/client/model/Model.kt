package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.BodyStats
import com.camackenzie.exvi.core.model.ExviSerializer
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.russhwolf.settings.Settings

@Serializable
class Model : SelfSerializable {

    val accountManager: AccountManager = AccountManager()
    val exerciseManager: ExerciseManager = ExerciseManager()

    @Transient
    @OptIn(com.russhwolf.settings.ExperimentalSettingsImplementation::class)
    val settings = Settings()

    override fun getUID(): String = uid

    override fun toJson(): String = ExviSerializer.toJson(this)

    val activeAccount: Account?
        get() = accountManager.activeAccount

    val workoutManager: SyncedWorkoutManager?
        get() = accountManager.activeAccount?.workoutManager

    fun signOutCurrentAccount() {
        settings.remove("activeUser")
        accountManager.activeAccount = null
    }

    fun repair() {
        signOutCurrentAccount()
        exerciseManager.exercises.clear()
    }

    companion object {
        const val uid = "ExviClientModel"
    }

}