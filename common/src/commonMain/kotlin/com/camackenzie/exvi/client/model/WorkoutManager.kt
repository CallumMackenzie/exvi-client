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
import kotlinx.serialization.Serializable

@Suppress("unused")
class ServerWorkoutManager(
    private val username: String,
    private val accessKey: String,
    private val outgoingPutRequests: MutableMap<String, WorkoutPutRequest> = HashMap(),
    private val outgoingDeleteRequests: MutableMap<String, DeleteWorkoutsRequest> = HashMap(),
    private val outgoingActivePutRequests: MutableMap<String, ActiveWorkoutPutRequest> = HashMap()
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
        request.workouts.forEach { outgoingActivePutRequests.remove(it.activeWorkoutId.get()) }

    fun isPuttingActive(): Boolean = outgoingActivePutRequests.isNotEmpty()
    fun isPutting(): Boolean = outgoingPutRequests.isNotEmpty()
    fun isDeletingAny(): Boolean = outgoingDeleteRequests.isNotEmpty()
    fun isUpdatingWorkouts(): Boolean = isPutting() || isDeletingAny()
    fun isUpdatingActiveWorkouts(): Boolean = isPuttingActive() || isDeletingAny()
    fun isUpdatingWorkout(id: String) = outgoingPutRequests.containsKey(id)
    fun isUpdatingWorkout(id: EncodedStringCache) = isUpdatingWorkout(id.get())
    fun isUpdatingActiveWorkout(id: String) = outgoingActivePutRequests.containsKey(id)
    fun isUpdatingActiveWorkout(id: EncodedStringCache) = isUpdatingActiveWorkout(id.get())

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
            APIInfo.ENDPOINT,
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
            APIInfo.ENDPOINT,
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
        type: WorkoutListRequest.Type,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<ActiveWorkout>) -> Unit,
        onComplete: () -> Unit
    ): Job = APIRequest.requestAsync(
        APIInfo.ENDPOINT,
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
        type: WorkoutListRequest.Type,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<Workout>) -> Unit,
        onComplete: () -> Unit
    ): Job = APIRequest.requestAsync(
        APIInfo.ENDPOINT,
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
            APIInfo.ENDPOINT,
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
        ExviLogger.i(
            "Sending request: ${
                request.workouts.map {
                    it.name
                }
            }", tag = "WORKOUT_MANAGER"
        )
        return APIRequest.requestAsync(
            APIInfo.ENDPOINT,
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
        workouts.clear()
        workouts.addAll(workouts.toSet())
        activeWorkouts.clear()
        activeWorkouts.addAll(activeWorkouts.toSet())
    }

    override fun getWorkouts(
        type: WorkoutListRequest.Type,
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
        type: WorkoutListRequest.Type,
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

class SyncedWorkoutManager(
    username: String,
    accessKey: String,
//    private val syncScope: CoroutineScope,
//    private val syncDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : WorkoutManager {
    private val localManager = LocalWorkoutManager()
    private val serverManager = ServerWorkoutManager(username, accessKey)
//    private var syncJob: Job = syncScope.launch(syncDispatcher) {
//        while (true) {
//            joinAll(
//                getWorkouts(WorkoutListRequest.Type.ListAllTemplates,
//                    syncScope, syncDispatcher, onFail = {
//                        ExviLogger.i(tag = "WORKOUT_SYNC") { "Workouts failed to sync: ${it.toJson()}" }
//                    },
//                    onSuccess = {
//                        ExviLogger.i(tag = "WORKOUT_SYNC") { "Workouts synced" }
//                    }),
//                getActiveWorkouts(WorkoutListRequest.Type.ListAllActive, syncScope, syncDispatcher,
//                    onFail = {
//                        ExviLogger.e(tag = "WORKOUT_SYNC") { "Active workouts failed to sync: ${it.toJson()}" }
//                    },
//                    onSuccess = {
//                        ExviLogger.i(tag = "WORKOUT_SYNC") { "Active workouts synced" }
//                    })
//            )
//            delay(30.seconds.toDuration())
//        }
//    }

    override fun deleteWorkouts(
        toDelete: Array<String>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        listOf(
            localManager.deleteWorkouts(toDelete, coroutineScope, dispatcher),
            serverManager.deleteWorkouts(toDelete, coroutineScope, dispatcher, onFail, onSuccess, onComplete)
        ).joinAll()
    }

    override fun getWorkouts(
        type: WorkoutListRequest.Type,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<Workout>) -> Unit,
        onComplete: () -> Unit
    ): Job = if (serverManager.isUpdatingWorkouts()) localManager.getWorkouts(
        type,
        coroutineScope,
        dispatcher,
        onFail = onFail,
        onSuccess = onSuccess,
        onComplete = onComplete
    )
    else serverManager.getWorkouts(
        type,
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
                onFail,
                onSuccess,
                onComplete
            ), localManager.putWorkouts(
                workoutsToAdd,
                coroutineScope,
                dispatcher
            )
        )
        jobs.joinAll()
    }

    override fun deleteActiveWorkouts(
        toDelete: Array<String>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        listOf(
            serverManager.deleteActiveWorkouts(toDelete, coroutineScope, dispatcher, onFail, onSuccess, onComplete),
            localManager.deleteActiveWorkouts(toDelete, coroutineScope, dispatcher)
        ).joinAll()
    }

    override fun getActiveWorkouts(
        type: WorkoutListRequest.Type,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<ActiveWorkout>) -> Unit,
        onComplete: () -> Unit
    ): Job = if (serverManager.isUpdatingActiveWorkouts())
        localManager.getActiveWorkouts(type, coroutineScope, dispatcher, onFail, onSuccess, onComplete)
    else
        serverManager.getActiveWorkouts(type, coroutineScope, dispatcher, onFail, onSuccess, onComplete)

    override fun putActiveWorkouts(
        workoutsToAdd: Array<ActiveWorkout>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        listOf(
            serverManager.putActiveWorkouts(workoutsToAdd, coroutineScope, dispatcher, onFail, onSuccess, onComplete),
            localManager.putActiveWorkouts(workoutsToAdd, coroutineScope, dispatcher)
        ).joinAll()
    }
}