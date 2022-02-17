/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.api.APIRequest
import com.camackenzie.exvi.core.api.APIResult
import com.camackenzie.exvi.core.api.GenericDataRequest
import com.camackenzie.exvi.core.api.WorkoutListRequest
import com.camackenzie.exvi.core.api.WorkoutListResult
import com.camackenzie.exvi.core.api.WorkoutPutRequest
import com.camackenzie.exvi.core.model.Workout
import com.camackenzie.exvi.core.model.WorkoutManager
import com.camackenzie.exvi.core.util.cached
import kotlinx.serialization.json.*
import kotlinx.serialization.*

/**
 *
 * @author callum
 */
class ServerWorkoutManager(private val username: String, private val accessKey: String) : WorkoutManager {
    override fun getWorkouts(
        onFail: (APIResult<String>) -> Unit,
        onSuccess: (Array<Workout>) -> Unit,
        onComplete: () -> Unit
    ) {
        val request: GenericDataRequest<WorkoutListRequest> = GenericDataRequest(
            username.cached(), accessKey.cached(),
            WorkoutListRequest(WorkoutListRequest.Type.LIST_ALL)
        )
        APIRequest.requestAsync(
            APIEndpoints.GET_DATA,
            request,
            APIRequest.jsonHeaders()
        ) {
            if (it.failed()) {
                onFail(it)
            } else {
                val workouts = Json.decodeFromString<WorkoutListResult>(it.body)
                onSuccess(workouts.workouts)
            }
            onComplete()
        }
    }

    override fun putWorkouts(
        workoutsToAdd: Array<Workout>,
        onFail: (APIResult<String>) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ) {
        val request: GenericDataRequest<WorkoutPutRequest> = GenericDataRequest(
            username.cached(), accessKey.cached(),
            WorkoutPutRequest(workoutsToAdd)
        )
        APIRequest.requestAsync(
            APIEndpoints.GET_DATA,
            request,
            APIRequest.jsonHeaders()
        ) {
            if (it.failed()) {
                onFail(it)
            } else {
                onSuccess()
            }
            onComplete()
        }
    }
}