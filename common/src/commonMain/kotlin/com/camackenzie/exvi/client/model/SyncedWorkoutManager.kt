package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.WorkoutManager
import com.camackenzie.exvi.core.model.LocalWorkoutManager
import com.camackenzie.exvi.core.model.Workout
import com.camackenzie.exvi.core.api.APIResult
import kotlinx.datetime.Clock

class SyncedWorkoutManager(username: String, accessKey: String) : WorkoutManager {
    private val localManager: WorkoutManager = LocalWorkoutManager()
    private val serverManager: WorkoutManager = ServerWorkoutManager(username, accessKey)
    private var lastPullUTC: Long = 0
    private val pullTimeUTC = 5 * 60 * 1000

    private fun shouldPull(): Boolean {
        return Clock.System.now().epochSeconds - lastPullUTC > pullTimeUTC
    }

    fun invalidateLocalCache() {
        lastPullUTC = 0
    }

    override fun deleteWorkouts(
        toDelete: Array<String>,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ) {
        serverManager.deleteWorkouts(toDelete, onFail, onSuccess, onComplete)
        invalidateLocalCache()
    }

    override fun getWorkouts(
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<Workout>) -> Unit,
        onComplete: () -> Unit
    ) {
        if (shouldPull()) {
            serverManager.getWorkouts(
                onFail = onFail,
                onSuccess = onSuccess,
                onComplete = onComplete
            )
            lastPullUTC = Clock.System.now().epochSeconds
        } else {
            localManager.getWorkouts(
                onFail = onFail,
                onSuccess = onSuccess,
                onComplete = onComplete
            )
        }
    }

    override fun putWorkouts(
        workoutsToAdd: Array<Workout>,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ) {
        serverManager.putWorkouts(
            workoutsToAdd,
            onFail = onFail,
            onSuccess = onSuccess,
            onComplete = onComplete
        )
        invalidateLocalCache()
    }
}