/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import com.camackenzie.exvi.core.api.APIRequest;
import com.camackenzie.exvi.core.api.APIResult;
import com.camackenzie.exvi.core.api.GenericDataRequest;
import com.camackenzie.exvi.core.api.GenericDataResult;
import com.camackenzie.exvi.core.api.WorkoutListRequest;
import com.camackenzie.exvi.core.api.WorkoutListResult;
import com.camackenzie.exvi.core.api.WorkoutPutRequest;
import com.camackenzie.exvi.core.async.FutureWrapper;
import com.camackenzie.exvi.core.async.SharedMethodFuture;
import com.camackenzie.exvi.core.model.Workout;

/**
 *
 * @author callum
 */
public class ServerWorkoutManager {

    private String username,
            accessKey;

    public ServerWorkoutManager(String username, String dataKey) {
        this.username = username;
        this.accessKey = dataKey;
    }

    public FutureWrapper<Workout[]> getWorkouts() {
        GenericDataRequest<WorkoutListRequest> request
                = new GenericDataRequest(this.username, this.accessKey,
                        new WorkoutListRequest(WorkoutListRequest.Type.LIST_ALL));
        FutureWrapper<APIResult<GenericDataResult>> apiFuture
                = APIRequest.sendJson(APIEndpoints.GET_DATA,
                        request,
                        GenericDataResult.class);
        return new SharedMethodFuture(apiFuture,
                () -> {
                    APIResult<GenericDataResult> res = apiFuture.getFailOnError();
                    System.out.println(new com.google.gson.Gson().toJson(res));
                    if (res.getStatusCode() != 200
                    || res.getBody().errorOccured()) {
                        System.err.println("Status code: " + res.getStatusCode());
                        return null;
                    }
                    return ((WorkoutListResult) res.getBody().getResult())
                            .getWorkouts();
                }).wrapped();
    }

    public FutureWrapper<APIResult> addWorkouts(Workout... workouts) {
        GenericDataRequest request = this.createGenericRequest(
                new WorkoutPutRequest(workouts));
        FutureWrapper<APIResult<Void>> result
                = APIRequest.sendJson(APIEndpoints.GET_DATA,
                        request,
                        Void.class);
        return new SharedMethodFuture(result, () -> result.getFailOnError())
                .wrapped();
    }

    private <T> GenericDataRequest<T> createGenericRequest(T data) {
        return new GenericDataRequest(username, accessKey, data);
    }

}