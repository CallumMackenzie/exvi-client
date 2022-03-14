/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.api.*
import com.camackenzie.exvi.core.model.ActiveWorkout
import com.camackenzie.exvi.core.model.Workout
import com.camackenzie.exvi.core.model.WorkoutManager
import com.camackenzie.exvi.core.util.cached
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import kotlinx.serialization.*
import kotlinx.datetime.Clock

/**
 *
 * @author callum
 */
data class ServerWorkoutManager(private val username: String, private val accessKey: String) : WorkoutManager {

    override fun deleteWorkouts(
        toDelete: Array<String>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = APIRequest.requestAsync(
        APIEndpoints.DATA,
        DeleteWorkoutsRequest(
            username.cached(),
            accessKey.cached(),
            toDelete.map {
                it.cached()
            }.toTypedArray(),
            DeleteWorkoutsRequest.WorkoutType.Workout
        ),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher
    ) {
        if (it.failed()) onFail(it) else onSuccess()
        onComplete()
    }

    override fun getWorkouts(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<Workout>) -> Unit,
        onComplete: () -> Unit
    ): Job = APIRequest.requestAsync(
        APIEndpoints.DATA,
        WorkoutListRequest(
            username,
            accessKey,
            WorkoutListRequest.Type.ListAllTemplates
        ),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher
    ) {
        if (it.failed()) onFail(it) else {
            val workouts = Json.decodeFromString<WorkoutListResult>(it.body)
            onSuccess(workouts.workouts)
        }
        onComplete()
    }

    override fun putWorkouts(
        workoutsToAdd: Array<Workout>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = APIRequest.requestAsync(
        APIEndpoints.DATA,
        WorkoutPutRequest(username, accessKey, workoutsToAdd),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher
    ) {
        if (it.failed()) onFail(it) else onSuccess()
        onComplete()
    }

}

@Serializable
data class LocalWorkoutManager constructor(
    val workouts: ArrayList<Workout> = ArrayList(),
    val activeWorkouts: ArrayList<ActiveWorkout> = ArrayList()
) : WorkoutManager {

    override fun getWorkouts(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<Workout>) -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        onSuccess(workouts.toTypedArray())
        onComplete()
    }

    override fun putWorkouts(
        workoutsToAdd: Array<Workout>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        val workoutIds = workouts.map { it.id.get() }
        for (workout in workoutsToAdd) {
            val i = workoutIds.indexOf(workout.id.get())
            if (i != -1) {
                workouts[i] = workout
            } else {
                workouts.add(workout)
            }
        }
        onSuccess()
        onComplete()
    }

    override fun deleteWorkouts(
        toDelete: Array<String>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        workouts.removeAll {
            toDelete.contains(it.id.get())
        }
        onSuccess()
        onComplete()
    }
}


class SyncedWorkoutManager(username: String, accessKey: String) : WorkoutManager {
    private var lastPullUTC: Long = 0
    private var pullOverride: Boolean = false

    private val localManager = LocalWorkoutManager()
    private val serverManager = ServerWorkoutManager(username, accessKey)
    private val pullTimeUTC = 120

    private fun shouldPull(): Boolean {
        val pullDiff = Clock.System.now().epochSeconds - lastPullUTC
        return pullDiff > pullTimeUTC || pullOverride
    }

    private fun resetPull() {
        lastPullUTC = Clock.System.now().epochSeconds
        pullOverride = false
    }

    fun validateLocalCache() = resetPull()

    fun invalidateLocalCache() {
        pullOverride = true
    }

    override fun deleteWorkouts(
        toDelete: Array<String>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        val jobs = listOf(
            localManager.deleteWorkouts(toDelete, coroutineScope, dispatcher, {}, {}, {}),
            serverManager.deleteWorkouts(toDelete, coroutineScope, dispatcher, onFail, onSuccess, onComplete)
        )
        invalidateLocalCache()
        jobs.joinAll()
    }

    override fun getWorkouts(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<Workout>) -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        if (shouldPull()) {
            resetPull()
            serverManager.getWorkouts(
                coroutineScope,
                dispatcher,
                onFail = onFail,
                onSuccess = {
                    localManager.workouts.clear()
                    localManager.workouts.addAll(it)
                    onSuccess(it)
                },
                onComplete = onComplete
            )
        } else {
            localManager.getWorkouts(
                coroutineScope,
                dispatcher,
                onFail = onFail,
                onSuccess = onSuccess,
                onComplete = onComplete
            )
        }.join()
    }

    override fun putWorkouts(
        workoutsToAdd: Array<Workout>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        val jobs = listOf(serverManager.putWorkouts(
            workoutsToAdd,
            coroutineScope,
            dispatcher,
            onFail = {
                invalidateLocalCache()
                onFail(it)
            },
            onSuccess = {
                invalidateLocalCache()
                onSuccess()
            },
            onComplete = onComplete
        ), localManager.putWorkouts(workoutsToAdd,
            coroutineScope,
            dispatcher,
            onFail = {
                invalidateLocalCache()
            }, onSuccess = {
                validateLocalCache()
            })
        )
        jobs.joinAll()
    }
}