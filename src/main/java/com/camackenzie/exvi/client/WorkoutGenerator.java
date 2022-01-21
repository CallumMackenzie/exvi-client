/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client;

import com.camackenzie.exvi.core.model.Workout;

/**
 *
 * @author callum
 */
public class WorkoutGenerator {

    private WorkoutGeneratorParams params;
    private ExerciseManager exerciseManager;

    public WorkoutGenerator(WorkoutGeneratorParams params,
            ExerciseManager exman) {
        this.params = params;
        this.exerciseManager = exman;
    }

    public WorkoutGeneratorParams getWorkoutGeneratorParams() {
        return this.params;
    }

    public void setWorkoutGeneratorParams(WorkoutGeneratorParams wgp) {
        this.params = wgp;
    }

    public ExerciseManager getExerciseManager() {
        return this.exerciseManager;
    }

    public void setExerciseManager(ExerciseManager exm) {
        this.exerciseManager = exm;
    }

    public Workout generateNextWorkout(String name) {
        throw new UnsupportedOperationException();
    }

}
