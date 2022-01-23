/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import com.camackenzie.exvi.core.model.Exercise;
import com.camackenzie.exvi.core.model.ExerciseSet;
import com.camackenzie.exvi.core.model.Workout;

/**
 *
 * @author callum
 */
public interface ExercisePriorityProvider<T> {

    public double getPriority(WorkoutGenerator g, int exerciseIndex);

    public T getType();

    public ExerciseSet generateExerciseSet(WorkoutGenerator g, Exercise ex);

    public void registerMatch(WorkoutGenerator g, Workout c);

}
