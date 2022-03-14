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
import com.camackenzie.exvi.core.util.Identifiable
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
    override fun deleteActiveWorkouts(
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
            toDelete.map(String::cached).toTypedArray(),
            DeleteWorkoutsRequest.WorkoutType.ActiveWorkout
        ),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher
    ) {
        if (it.failed()) onFail(it) else onSuccess()
        onComplete()
    }

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
            toDelete.map(String::cached).toTypedArray(),
            DeleteWorkoutsRequest.WorkoutType.Workout
        ),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher
    ) {
        if (it.failed()) onFail(it) else onSuccess()
        onComplete()
    }

    override fun getActiveWorkouts(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<ActiveWorkout>) -> Unit,
        onComplete: () -> Unit
    ): Job = APIRequest.requestAsync(
        APIEndpoints.DATA,
        WorkoutListRequest(
            username,
            accessKey,
            WorkoutListRequest.Type.ListAllActive
        ),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher
    ) {
        if (it.failed()) onFail(it) else {
            val response = Json.decodeFromString<ActiveWorkoutListResult>(it.body)
            onSuccess(response.workouts)
        }
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
            val response = Json.decodeFromString<WorkoutListResult>(it.body)
            onSuccess(response.workouts)
        }
        onComplete()
    }

    override fun putActiveWorkouts(
        workoutsToAdd: Array<ActiveWorkout>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = APIRequest.requestAsync(
        APIEndpoints.DATA,
        ActiveWorkoutPutRequest(username, accessKey, workoutsToAdd),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher
    ) {
        if (it.failed()) onFail(it) else onSuccess()
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

    override fun putActiveWorkouts(
        workoutsToAdd: Array<ActiveWorkout>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        Identifiable.intersectIndexed(
            workoutsToAdd.toList(), activeWorkouts,
            onIntersect = { a, _, _, bi -> activeWorkouts[bi] = a },
            onAOnly = { a, _ -> activeWorkouts.add(a) }
        )
        onSuccess()
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
        Identifiable.intersectIndexed(
            workoutsToAdd.toList(), workouts,
            onIntersect = { a, _, _, bi -> workouts[bi] = a },
            onAOnly = { a, _ -> workouts.add(a) }
        )
        onSuccess()
        onComplete()
    }

    override fun deleteActiveWorkouts(
        toDelete: Array<String>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        activeWorkouts.removeAll { toDelete.contains(it.getIdentifier().get()) }
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
        workouts.removeAll { toDelete.contains(it.getIdentifier().get()) }
        onSuccess()
        onComplete()
    }

    override fun getActiveWorkouts(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<ActiveWorkout>) -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        onSuccess(activeWorkouts.toTypedArray())
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

    // TODO: Make this work with the local cache
    override fun deleteActiveWorkouts(
        toDelete: Array<String>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = serverManager.deleteActiveWorkouts(toDelete, coroutineScope, dispatcher, onFail, onSuccess, onComplete)

    // TODO: Make this work with the local cache
    override fun getActiveWorkouts(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<ActiveWorkout>) -> Unit,
        onComplete: () -> Unit
    ): Job = serverManager.getActiveWorkouts(coroutineScope, dispatcher, onFail, onSuccess, onComplete)

    // TODO: Make this work with the local cache
    override fun putActiveWorkouts(
        workoutsToAdd: Array<ActiveWorkout>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = serverManager.putActiveWorkouts(workoutsToAdd, coroutineScope, dispatcher, onFail, onSuccess, onComplete)
}