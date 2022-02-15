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
import com.camackenzie.exvi.core.util.cached
import kotlinx.serialization.json.*
import kotlinx.serialization.*

/**
 *
 * @author callum
 */
class ServerWorkoutManager(private val username: String, private val accessKey: String) {
    fun getWorkouts(callback: (APIResult<String>) -> Unit) {
        val request: GenericDataRequest<WorkoutListRequest> = GenericDataRequest(
            username.cached(), accessKey.cached(),
            WorkoutListRequest(WorkoutListRequest.Type.LIST_ALL)
        )
        APIRequest.requestAsync(
            APIEndpoints.GET_DATA,
            request,
            APIRequest.jsonHeaders()
        ) {
            val workouts = Json.decodeFromString<WorkoutListResult>(it.body)
        }
    }

    fun addWorkouts(workouts: Array<Workout>, callback: (APIResult<String>) -> Unit) {
        val request: GenericDataRequest<WorkoutPutRequest> = GenericDataRequest(
            username.cached(), accessKey.cached(),
            WorkoutPutRequest(workouts)
        )
    }
}