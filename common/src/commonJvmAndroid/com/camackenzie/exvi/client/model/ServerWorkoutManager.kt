///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.camackenzie.exvi.client.model
//
//import com.camackenzie.exvi.core.api.APIRequest
//import com.camackenzie.exvi.core.api.APIResult
//import com.camackenzie.exvi.core.api.GenericDataRequest
//import com.camackenzie.exvi.core.api.GenericDataResult
//import com.camackenzie.exvi.core.api.WorkoutListRequest
//import com.camackenzie.exvi.core.api.WorkoutListResult
//import com.camackenzie.exvi.core.api.WorkoutPutRequest
//import com.camackenzie.exvi.core.async.FutureWrapper
//import com.camackenzie.exvi.core.async.SharedMethodFuture
//import com.camackenzie.exvi.core.model.Workout
//
///**
// *
// * @author callum
// */
//class ServerWorkoutManager(private val username: String, private val accessKey: String) {
//    val workouts: FutureWrapper<Array<Workout>>
//        get() {
//            val request: GenericDataRequest<WorkoutListRequest> = GenericDataRequest(
//                username, accessKey,
//                WorkoutListRequest(WorkoutListRequest.Type.LIST_ALL)
//            )
//            val apiFuture: FutureWrapper<APIResult<GenericDataResult>> = APIRequest.sendJson(
//                APIEndpoints.GET_DATA,
//                request,
//                GenericDataResult::class.java
//            )
//            return SharedMethodFuture(apiFuture) label@
//            {
//                val res: APIResult<GenericDataResult> = apiFuture.getFailOnError()
//                System.out.println(Gson().toJson(res))
//                if (res.getStatusCode() !== 200
//                    || res.getBody().errorOccured()
//                ) {
//                    System.err.println("Status code: " + res.getStatusCode())
//                    return@label null
//                }
//                (res.getBody().getResult() as WorkoutListResult)
//                    .getWorkouts()
//            }.wrapped()
//        }
//
//    fun addWorkouts(vararg workouts: Workout, callback: (APIResult) -> ()) {
//        val request: GenericDataRequest = GenericDataRequest(
//            WorkoutPutRequest(workouts)
//        )
//        val result: FutureWrapper<APIResult<Void>> = APIRequest.sendJson(
//            APIEndpoints.GET_DATA,
//            request
//        )
//    }
//
//    private fun <T> createGenericRequest(data: T): GenericDataRequest<T> {
//        return GenericDataRequest(username, accessKey, data)
//    }
//}