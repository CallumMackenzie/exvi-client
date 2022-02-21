package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.WorkoutManager
import com.camackenzie.exvi.core.model.LocalWorkoutManager
import com.camackenzie.exvi.core.model.Workout
import com.camackenzie.exvi.core.api.APIResult
import kotlinx.datetime.Clock

class SyncedWorkoutManager(username: String, accessKey: String) : WorkoutManager {
    private var lastPullUTC: Long = 0
    private var pullOverride: Boolean = false

    private val localManager = LocalWorkoutManager()
    private val serverManager = ServerWorkoutManager(username, accessKey)
    private val pullTimeUTC = 120
    private val noRefreshPullUTC = 5

    private fun shouldPull(): Boolean {
        val pullDiff = Clock.System.now().epochSeconds - lastPullUTC
        if (pullDiff <= noRefreshPullUTC) {
            return false
        }
        return pullDiff > pullTimeUTC || pullOverride
    }

    private fun resetPull() {
        lastPullUTC = Clock.System.now().epochSeconds
        pullOverride = false
    }

    fun validateLocalCache() {
        resetPull()
    }

    fun invalidateLocalCache() {
        pullOverride = true
    }

    override fun deleteWorkouts(
        toDelete: Array<String>,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ) {
        localManager.deleteWorkouts(toDelete, {}, {}, {})
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
                onSuccess = {
                    localManager.workouts.clear()
                    localManager.workouts.addAll(it)
                    onSuccess(it)
                },
                onComplete = onComplete
            )
            resetPull()
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
        localManager.putWorkouts(workoutsToAdd, {}, {}, {})
        serverManager.putWorkouts(
            workoutsToAdd,
            onFail = onFail,
            onSuccess = onSuccess,
            onComplete = onComplete
        )
        invalidateLocalCache()
    }
}