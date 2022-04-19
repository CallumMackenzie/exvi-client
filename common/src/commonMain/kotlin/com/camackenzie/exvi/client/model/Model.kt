package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.util.SelfSerializable
import com.russhwolf.settings.Settings
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Suppress("UNCHECKED_CAST")
@Serializable
class Model : SelfSerializable {

    val accountManager: AccountManager = AccountManager()
    val exerciseManager: ExerciseManager = ExerciseManager()

    @Transient
    @OptIn(com.russhwolf.settings.ExperimentalSettingsImplementation::class)
    val settings = Settings()

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

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

}