/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.api.*
import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.EncodedStringCache
import com.camackenzie.exvi.core.util.ExviLogger
import com.camackenzie.exvi.core.util.Identifiable
import com.camackenzie.exvi.core.util.cached
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import kotlinx.serialization.*
import kotlinx.datetime.Clock

@Suppress("unused")
class ServerWorkoutManager(
    private val username: String,
    private val accessKey: String,
    val outgoingPutRequests: MutableMap<String, WorkoutPutRequest> = HashMap(),
    val outgoingDeleteRequests: MutableMap<String, DeleteWorkoutsRequest> = HashMap(),
    val outgoingActivePutRequests: MutableMap<String, ActiveWorkoutPutRequest> = HashMap()
) : WorkoutManager {

    private fun registerDeleting(request: DeleteWorkoutsRequest) = request.workoutIds.forEach {
        outgoingDeleteRequests[it.get()] = request
    }

    private fun registerDeleted(request: DeleteWorkoutsRequest) = request.workoutIds.forEach {
        outgoingDeleteRequests.remove(it.get())
    }

    private fun registerAdding(request: WorkoutPutRequest) =
        request.workouts.forEach { outgoingPutRequests[it.id.get()] = request }

    private fun registerAdding(request: ActiveWorkoutPutRequest) =
        request.workouts.forEach { outgoingActivePutRequests[it.activeWorkoutId.get()] = request }

    private fun registerAdded(request: WorkoutPutRequest) =
        request.workouts.forEach { outgoingPutRequests.remove(it.id.get()) }

    private fun registerAdded(request: ActiveWorkoutPutRequest) =
        request.workouts.forEach { outgoingPutRequests.remove(it.activeWorkoutId.get()) }

    fun isPuttingActive(): Boolean = outgoingActivePutRequests.isNotEmpty()
    fun isPutting(): Boolean = outgoingPutRequests.isNotEmpty()
    fun isDeletingAny(): Boolean = outgoingDeleteRequests.isNotEmpty()
    fun isUpdatingWorkouts(): Boolean = isPutting() || isDeletingAny()
    fun isUpdatingActiveWorkouts(): Boolean = isPuttingActive() || isDeletingAny()
    fun isUpdatingWorkout(id: String) = outgoingPutRequests.containsKey(id)
    fun isUpdatingWorkout(id: EncodedStringCache) = isUpdatingWorkout(id.get())
    fun isUpdatingActiveWorkout(id: String) = outgoingActivePutRequests.containsKey(id)
    fun isUpdatingActiveWorkout(id: EncodedStringCache) = isUpdatingWorkout(id.get())

    override fun deleteActiveWorkouts(
        toDelete: Array<String>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job {
        val request = DeleteWorkoutsRequest(
            username.cached(),
            accessKey.cached(),
            toDelete.map(String::cached).toTypedArray(),
            DeleteWorkoutsRequest.WorkoutType.ActiveWorkout
        )
        registerDeleting(request)
        return APIRequest.requestAsync(
            APIEndpoints.DATA,
            request,
            coroutineScope = coroutineScope,
            coroutineDispatcher = dispatcher
        ) {
            registerDeleted(request)
            if (it.failed()) onFail(it) else onSuccess()
            onComplete()
        }
    }

    override fun deleteWorkouts(
        toDelete: Array<String>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job {
        val request = DeleteWorkoutsRequest(
            username.cached(),
            accessKey.cached(),
            toDelete.map(String::cached).toTypedArray(),
            DeleteWorkoutsRequest.WorkoutType.Workout
        )
        registerDeleting(request)
        return APIRequest.requestAsync(
            APIEndpoints.DATA,
            request,
            coroutineScope = coroutineScope,
            coroutineDispatcher = dispatcher
        ) {
            registerDeleted(request)
            if (it.failed()) onFail(it) else onSuccess()
            onComplete()
        }
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
            val response = ExviSerializer.fromJson<ActiveWorkoutListResult>(it.body)
            onSuccess(response.workouts as Array<ActiveWorkout>)
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
            val response = ExviSerializer.fromJson<WorkoutListResult>(it.body)
            onSuccess(response.workouts as Array<Workout>)
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
    ): Job {
        val request = ActiveWorkoutPutRequest(username, accessKey, workoutsToAdd.map {
            it.toActual()
        }.toTypedArray())
        registerAdding(request)
        return APIRequest.requestAsync(
            APIEndpoints.DATA,
            request,
            coroutineScope = coroutineScope,
            coroutineDispatcher = dispatcher
        ) {
            registerAdded(request)
            if (it.failed()) onFail(it) else onSuccess()
            onComplete()
        }
    }

    override fun putWorkouts(
        workoutsToAdd: Array<Workout>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job {
        val request = WorkoutPutRequest(username, accessKey, workoutsToAdd.map { it.toActual() }.toTypedArray())
        registerAdding(request)
        ExviLogger.i("Sending request: ${request.toJson()}", tag = "WORKOUT_MANAGER")
        return APIRequest.requestAsync(
            APIEndpoints.DATA,
            request,
            coroutineScope = coroutineScope,
            coroutineDispatcher = dispatcher
        ) {
            registerAdded(request)
            if (it.failed()) onFail(it) else onSuccess()
            onComplete()
        }
    }

}

@Serializable
data class LocalWorkoutManager constructor(
    val workouts: ArrayList<Workout> = ArrayList(),
    val activeWorkouts: ArrayList<ActiveWorkout> = ArrayList()
) : WorkoutManager {

    companion object {
        // Used to delay local manager calls to give compose time to react
        private const val STATIC_DELAY = 50L
    }

    fun ensureNoDuplicates() {
        val newWorkouts = arrayListOf(*workouts.toSet().toTypedArray())
        workouts.clear()
        workouts.addAll(newWorkouts)
    }

    override fun getWorkouts(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<Workout>) -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        delay(STATIC_DELAY)
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
        delay(STATIC_DELAY)
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
        delay(STATIC_DELAY)
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
        delay(STATIC_DELAY)
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
        delay(STATIC_DELAY)
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
        delay(STATIC_DELAY)
        onSuccess(activeWorkouts.toTypedArray())
        onComplete()
    }
}


class SyncedWorkoutManager(username: String, accessKey: String) : WorkoutManager {
    private val localManager = LocalWorkoutManager()
    val serverManager = ServerWorkoutManager(username, accessKey)

    // TODO: Add coroutine to sync with server periodically

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
        jobs.joinAll()
    }

    override fun getWorkouts(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<Workout>) -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        if (!serverManager.isUpdatingWorkouts()) {
            serverManager.getWorkouts(
                coroutineScope,
                dispatcher,
                onFail = onFail,
                onSuccess = {
                    localManager.workouts.clear()
                    localManager.workouts.addAll(it)
                    localManager.ensureNoDuplicates()
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
        val jobs = listOf(
            serverManager.putWorkouts(
                workoutsToAdd,
                coroutineScope,
                dispatcher,
                onFail = onFail,
                onSuccess = onSuccess,
                onComplete = onComplete
            ), localManager.putWorkouts(
                workoutsToAdd,
                coroutineScope,
                dispatcher
            )
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