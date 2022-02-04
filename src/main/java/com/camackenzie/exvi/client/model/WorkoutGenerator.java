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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public WorkoutGenerator(ExerciseManager exman) {
        this(new WorkoutGeneratorParams(), exman);
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

    public Workout generateWorkout(Workout wkr, Set<ExerciseSet> lockedExercises) {
        // Get the indexes of the locked exercises
        int[] lockedExerIndexes = new int[lockedExercises.size()];
        int ctr = -1;
        for (var exer : lockedExercises) {
            lockedExerIndexes[++ctr] = wkr.getExercises().indexOf(exer);
        }
        // Generate & return the workout
        return this.generateWorkout(wkr, lockedExerIndexes);
    }

    public Workout generateWorkout(Workout wkr, int... lockedIndexes) {
        // Create a new workout
        List<ExerciseSet> workoutExers = wkr.getExercises();

        // Retrieve the exercises
        HashSet<Exercise> exercises = new HashSet(this.exerciseManager.getExercises());

        // Remove locked exercises from global exercise set
        for (var lockedIndex : lockedIndexes) {
            if (lockedIndex >= 0 && lockedIndex < workoutExers.size()) {
                exercises.remove(workoutExers.get(lockedIndex).getExercise());
            }
        }

        // Get the highest index which is locked
        int highestLocked = -1;
        for (var lockedIndex : lockedIndexes) {
            if (lockedIndex > highestLocked) {
                highestLocked = lockedIndex;
            }
        }

        // Get the number of exercises, with at least the highest number locked
        int nExercises = Math.max(random.intInRange(this.params.getMinExercises(),
                this.params.getMaxExercises()),
                highestLocked);

        ArrayList<ExerciseSet> exs = new ArrayList<>(nExercises);

        // Get exercises based on their priority
        for (int i = 0; i < nExercises; ++i) {
            // If the exercise is locked, skip it
            boolean indexLocked = false;
            for (var index : lockedIndexes) {
                if (i == index) {
                    indexLocked = true;
                    break;
                }
            }
            if (indexLocked) {
                exs.add(workoutExers.get(i));
                continue;
            }

            // Create exercise priority list to track
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
            exs.add(this.blendExerciseSets(selectedExer.getExercise(), generated));
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

        workoutExers.clear();
        workoutExers.addAll(exs);

        // Return the created workout
        return wkr;
    }

    public ExerciseSet blendExerciseSets(Exercise exercise,
            ExerciseSet... exs) {

        if (exs.length == 0) {
            return new ExerciseSet(exercise, "rep", 8, 8, 8);
        }

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

        // Compose & return data
        return new ExerciseSet(exercise, unit, sets);
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
