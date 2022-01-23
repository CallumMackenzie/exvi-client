/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import java.util.HashSet;
import java.util.ArrayList;
import com.camackenzie.exvi.core.model.Exercise;
import com.camackenzie.exvi.core.model.ExerciseType;
import com.camackenzie.exvi.core.model.Muscle;
import com.camackenzie.exvi.core.model.ExerciseMechanics;
import com.camackenzie.exvi.core.model.ExerciseEquipment;
import com.camackenzie.exvi.core.model.ExerciseExperienceLevel;
import com.camackenzie.exvi.core.model.ExerciseForceType;
import com.google.gson.Gson;
import java.util.function.Function;

/**
 *
 * @author callum
 */
public class ExerciseManager {

    private static final Gson gson = new Gson();

    private final HashSet<Exercise> exercises;

    public ExerciseManager(HashSet<Exercise> exercises) {
        this.exercises = exercises;
    }

    public ExerciseManager(Exercise... exs) {
        this(new HashSet<>() {
            {
                for (var ex : exs) {
                    add(ex);
                }
            }
        });
    }

    public ExerciseManager() {
        this(new HashSet<>());
    }

    public static ExerciseManager fromJSON(String json) {
        Exercise[] exs = gson.fromJson(json, Exercise[].class);
        return new ExerciseManager(exs);
    }

    public String toJSON() {
        return gson.toJson(this.exercises.toArray(sz -> new Exercise[sz]));
    }

    public HashSet<Exercise> getExercises() {
        return this.exercises;
    }

    public ArrayList<Exercise> getExercisesByFunction(Function<Exercise, Boolean> add) {
        ArrayList<Exercise> ret = new ArrayList<>();
        for (var exercise : this.exercises) {
            if (add.apply(exercise)) {
                ret.add(exercise);
            }
        }
        return ret;
    }

    public Exercise getFirstExerciseByFunction(Function<Exercise, Boolean> add) {
        for (var exercise : this.exercises) {
            if (add.apply(exercise)) {
                return exercise;
            }
        }
        return null;
    }

    public Exercise getNamedExercise(String name) {
        return this.getFirstExerciseByFunction(
                exercise -> exercise.getName()
                        .equalsIgnoreCase(name));
    }

    public ArrayList<Exercise> getExercisesOfType(ExerciseType et) {
        return this.getExercisesByFunction(exercise
                -> exercise.getExerciseTypes().contains(et));
    }

    public ArrayList<Exercise> getExercisesWithMajorMuscle(Muscle m) {
        return this.getExercisesByFunction(exercise -> exercise.worksMuscle(m));
    }

    public ArrayList<Exercise> getExercisesNameContaining(String cont) {
        return this.getExercisesByFunction(ex -> ex.getName().contains(cont));
    }

    public ArrayList<Exercise> getExercisesDescriptionContaining(String cont) {
        return this.getExercisesByFunction(ex -> ex.getDescription().contains(cont));
    }

    public ArrayList<Exercise> getExercisesWithForceType(ExerciseForceType ft) {
        return this.getExercisesByFunction(ex -> ex.getForceType() == ft);
    }

    public ArrayList<Exercise> getExercisesWithMechanics(ExerciseMechanics m) {
        return this.getExercisesByFunction(ex -> ex.getMechanics() == m);
    }

    public ArrayList<Exercise> getExercisesWithEquipment(ExerciseEquipment m) {
        return this.getExercisesByFunction(ex -> ex.getEquipment().contains(m));
    }

    public ArrayList<Exercise> getExercisesWithExperienceLevel(ExerciseExperienceLevel exp) {
        return this.getExercisesByFunction(ex -> ex.getExperienceLevel() == exp);
    }
}
