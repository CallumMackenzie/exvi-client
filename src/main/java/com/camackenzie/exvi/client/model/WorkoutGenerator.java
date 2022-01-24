/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import com.camackenzie.exvi.core.model.Exercise;
import com.camackenzie.exvi.core.model.ExerciseSet;
import com.camackenzie.exvi.core.model.Workout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author callum
 */
public class WorkoutGenerator {

    private static final RandomWrapper random = new RandomWrapper();

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
        // Create a new workout
        Workout wkr = new Workout(name, "", new ArrayList<>());
        ArrayList<ExerciseSet> exs = wkr.getExercises();

        // Retrieve the exercises
        ArrayList<Exercise> exercises = new ArrayList(this.exerciseManager.getExercises());

        // Get exercises based on their priority
        int nExercises = random.intInRange(this.params.getMinExercises(), this.params.getMaxExercises());
        // For the number of exercises
        for (int i = 0; i < nExercises; ++i) {
            ArrayList<ExercisePriorityTracker> exercisePriorities = new ArrayList<>();
            // For each exercise find the priority based on the given priority
            // providers
            for (var ex : exercises) {
                // Accumulate priorities from each priority set
                ExercisePriorityProvider[] probs = this.params.getExercisePriorityProviders();
                ArrayList<Double> individualPriorities = new ArrayList<>();
                double exerPriority = 0;
                for (var prob : probs) {
                    double probPriority = prob.getPriority(this, i);
                    exerPriority += probPriority;
                    individualPriorities.add(exerPriority);
                }

                // Add the exercise priority if it has contributed
                if (exerPriority > 0
                        || this.params.getExercisePriorityProviders().length == 0) {
                    exercisePriorities.add(new ExercisePriorityTracker(ex,
                            exerPriority,
                            individualPriorities,
                            probs));
                }
            }
            // Sort the exercise prioritites to find the currentHighestUnitVal ones
            exercisePriorities.sort(new ExercisePriorityComparator());

            // Ensure sufficient exercises exist
            if (exercisePriorities.size() == 0) {
                System.err.println("Insufficient exercises selected.");
                continue;
            }

            // Get exercises with priority similar to the currentHighestUnitVal
            ArrayList<ExercisePriorityTracker> validExers = new ArrayList<>();
            double maxPriority = exercisePriorities.get(exercisePriorities.size() - 1)
                    .getPriority();
            for (int j = exercisePriorities.size() - 1; j >= 0; --j) {
                ExercisePriorityTracker exp = exercisePriorities.get(j);
                if ((exp.getPriority() + this.params.getPriorityRange() > maxPriority
                        && exp.getPriority() - this.params.getPriorityRange() < maxPriority)
                        || j == exercisePriorities.size() - 1) {
                    validExers.add(exp);
                } else {
                    break;
                }
            }

            // Randomize valid exercises
            Collections.shuffle(validExers);

            // Select exercise
            ExercisePriorityTracker selectedExer = validExers.get(0);
            // Generate exercise set based on most presiding probability
            ExerciseSet[] generated = selectedExer.getPriorityProviders()
                    .stream()
                    .map(pr -> pr.generateExerciseSet(this, selectedExer.getExercise()))
                    .toArray(sz -> new ExerciseSet[sz]);

            // Add exercise set to workout & remove the exercise from the exercises array to
            // avoid repeats
            exs.add(this.blendExerciseSets(generated));
            exercises.remove(selectedExer.getExercise());

            // Register a match for each priority provider
            ArrayList<Double> priorities = selectedExer.getIndividualPriorities();
            ArrayList<ExercisePriorityProvider> providers = selectedExer.getPriorityProviders();
            for (int k = 0; k < selectedExer.getNumPriorities(); ++k) {
                providers.get(k).registerMatch(this,
                        wkr,
                        priorities.get(i),
                        selectedExer.getPriority());
            }
        }

        // Return the created workout
        return wkr;
    }

    public ExerciseSet blendExerciseSets(ExerciseSet... exs) {

        // Find the most used unit
        HashMap<String, Integer> unitTypeCount = new HashMap<>();
        for (var ex : exs) {
            Integer unitCount = unitTypeCount.get(ex.getUnit());
            unitTypeCount.put(ex.getUnit(),
                    (unitCount == null ? 0 : unitCount) + 1);
        }

        String currentBestUnit = "";
        int currentHighestUnitVal = 0;
        for (var unitEntry : unitTypeCount.entrySet()) {
            if (unitEntry.getValue() > currentHighestUnitVal) {
                currentHighestUnitVal = unitEntry.getValue();
                currentBestUnit = unitEntry.getKey();
            }
        }
        final String unit = currentBestUnit;

        // Filter out units which do not match most used unit
        exs = Arrays.stream(exs)
                .filter(ex -> ex.getUnit().equals(unit))
                .toArray(sz -> new ExerciseSet[sz]);

        // Retrieve the average set count
        int avgSetCount = 0;
        for (var ex : exs) {
            avgSetCount += ex.getSets().length;
        }
        avgSetCount /= exs.length;

        // Retrieve the average rep counts for each set
        int[] sets = new int[avgSetCount];
        for (int i = 0; i < sets.length; ++i) {
            int nValidSets = exs.length;
            for (int k = 0; k < exs.length; ++k) {
                if (k < exs[k].getSets().length) {
                    sets[i] += exs[k].getSet(i);
                } else {
                    --nValidSets;
                }
            }
            sets[i] /= nValidSets;
        }

        // Retrieve the common exercise
        Exercise ex = exs.length == 0 ? null : exs[0].getExercise();

        // Compose & return data
        return new ExerciseSet(ex, unit, sets);
    }

    private class ExercisePriorityTracker {

        private final Exercise exercise;
        private final ArrayList<ExercisePriorityProvider> probSets;
        private final ArrayList<Double> probSetPriorities;
        private final double priority;

        public ExercisePriorityTracker(Exercise ex,
                double prior,
                ArrayList<Double> psp,
                ExercisePriorityProvider[] ps) {
            this.exercise = ex;
            this.priority = prior;
            this.probSetPriorities = psp;
            this.probSets = new ArrayList<>();
            for (var s : ps) {
                this.probSets.add(s);
            }
        }

        public Exercise getExercise() {
            return this.exercise;
        }

        public ArrayList<ExercisePriorityProvider> getPriorityProviders() {
            return this.probSets;
        }

        public ArrayList<Double> getIndividualPriorities() {
            return this.probSetPriorities;
        }

        public double getPriority() {
            return this.priority;
        }

        public int getNumPriorities() {
            return this.probSets.size();
        }

    }

    private class ExercisePriorityComparator implements Comparator<ExercisePriorityTracker> {

        @Override
        public int compare(ExercisePriorityTracker a, ExercisePriorityTracker b) {
            return Double.compare(a.getPriority(), b.getPriority());
        }
    }

}
