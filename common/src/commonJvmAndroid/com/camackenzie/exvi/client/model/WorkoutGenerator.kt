///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.camackenzie.exvi.client.model
//
//import com.camackenzie.exvi.core.model.Exercise
//import com.camackenzie.exvi.core.model.ExerciseSet
//import com.camackenzie.exvi.core.model.Workout
//import java.util.ArrayList
//import java.util.Arrays
//import java.util.Collections
//import java.util.Comparator
//import java.util.HashMap
//import java.util.HashSet
//import java.util.List
//import java.util.Set
//
///**
// *
// * @author callum
// */
//class WorkoutGenerator(
//    var params: WorkoutGeneratorParams,
//    var exerciseManager: ExerciseManager
//) {
//
//    constructor(exman: ExerciseManager) : this(WorkoutGeneratorParams(), exman) {}
//
//    fun generateWorkout(wkr: Workout, lockedExercises: Set<ExerciseSet>): Workout {
//        // Get the indexes of the locked exercises
//        val lockedExerIndexes = IntArray(lockedExercises.size())
//        var ctr = -1
//        for (exer in lockedExercises) {
//            lockedExerIndexes[++ctr] = wkr.getExercises().indexOf(exer)
//        }
//        // Generate & return the workout
//        return this.generateWorkout(wkr, *lockedExerIndexes)
//    }
//
//    fun generateWorkout(wkr: Workout, vararg lockedIndexes: Int): Workout {
//        // Create a new workout
//        val workoutExers: List<ExerciseSet> = wkr.getExercises()
//
//        // Retrieve the exercises
//        val exercises: HashSet<Exercise> = HashSet(exerciseManager.getExercises())
//
//        // Remove locked exercises from global exercise set
//        for (lockedIndex in lockedIndexes) {
//            if (lockedIndex >= 0 && lockedIndex < workoutExers.size()) {
//                exercises.remove(workoutExers[lockedIndex].getExercise())
//            }
//        }
//
//        // Get the highest index which is locked
//        var highestLocked = -1
//        for (lockedIndex in lockedIndexes) {
//            if (lockedIndex > highestLocked) {
//                highestLocked = lockedIndex
//            }
//        }
//
//        // Get the number of exercises, with at least the highest number locked
//        val nExercises: Int = Math.max(
//            Random.intInRange(
//                params.getMinExercises(),
//                params.getMaxExercises()
//            ),
//            highestLocked + 1
//        )
//        val exs: ArrayList<ExerciseSet> = ArrayList(nExercises)
//
//        // Get exercises based on their priority
//        for (i in 0 until nExercises) {
//            // If the exercise is locked, skip it
//            var indexLocked = false
//            for (index in lockedIndexes) {
//                if (i == index) {
//                    indexLocked = true
//                    break
//                }
//            }
//            if (indexLocked) {
//                exs.add(workoutExers[i])
//                continue
//            }
//
//            // Create exercise priority list to track
//            val exercisePriorities: ArrayList<ExercisePriorityTracker> = ArrayList()
//            // For each exercise find the priority based on the given priority
//            // providers
//            for (ex in exercises) {
//                // Accumulate priorities from each priority set
//                val probs: Array<ExercisePriorityProvider> = params.getExercisePriorityProviders()
//                val individualPriorities: ArrayList<Double> = ArrayList()
//                var exerPriority = 0.0
//                for (prob in probs) {
//                    val probPriority: Double = prob.getPriority(this, i)
//                    exerPriority += probPriority
//                    individualPriorities.add(exerPriority)
//                }
//
//                // Add the exercise priority if it has contributed
//                if (exerPriority > 0
//                    || params.getExercisePriorityProviders().length === 0
//                ) {
//                    exercisePriorities.add(
//                        ExercisePriorityTracker(
//                            ex,
//                            exerPriority,
//                            individualPriorities,
//                            probs
//                        )
//                    )
//                }
//            }
//            // Sort the exercise prioritites to find the currentHighestUnitVal ones
//            exercisePriorities.sort(ExercisePriorityComparator())
//
//            // Ensure sufficient exercises exist
//            if (exercisePriorities.size() === 0) {
//                System.err.println("Insufficient exercises selected.")
//                continue
//            }
//
//            // Get exercises with priority similar to the currentHighestUnitVal
//            val validExers: ArrayList<ExercisePriorityTracker> = ArrayList()
//            val maxPriority: Double = exercisePriorities.get(exercisePriorities.size() - 1)
//                .getPriority()
//            for (j in exercisePriorities.size() - 1 downTo 0) {
//                val exp: ExercisePriorityTracker = exercisePriorities.get(j)
//                if ((exp.getPriority() + params.getPriorityRange() > maxPriority
//                            && exp.getPriority() - params.getPriorityRange() < maxPriority)
//                    || j == exercisePriorities.size() - 1
//                ) {
//                    validExers.add(exp)
//                } else {
//                    break
//                }
//            }
//
//            // Randomize valid exercises
//            Collections.shuffle(validExers)
//
//            // Select exercise
//            val selectedExer: ExercisePriorityTracker = validExers.get(0)
//            // Generate exercise set based on most presiding probability
//            val generated: Array<ExerciseSet> = selectedExer.getPriorityProviders()
//                .stream()
//                .map { pr -> pr.generateExerciseSet(this, selectedExer.getExercise()) }
//                .toArray { sz -> arrayOfNulls<ExerciseSet>(sz) }
//
//            // Add exercise set to workout & remove the exercise from the exercises array to
//            // avoid repeats
//            exs.add(blendExerciseSets(selectedExer.getExercise(), *generated))
//            exercises.remove(selectedExer.getExercise())
//
//            // Register a match for each priority provider
//            val priorities: ArrayList<Double> = selectedExer.getIndividualPriorities()
//            val providers: ArrayList<ExercisePriorityProvider> = selectedExer.getPriorityProviders()
//            for (k in 0 until selectedExer.getNumPriorities()) {
//                providers.get(k).registerMatch(
//                    this,
//                    wkr,
//                    priorities.get(i),
//                    selectedExer.getPriority()
//                )
//            }
//        }
//        workoutExers.clear()
//        workoutExers.addAll(exs)
//
//        // Return the created workout
//        return wkr
//    }
//
//    fun blendExerciseSets(
//        exercise: Exercise?,
//        vararg exs: ExerciseSet?
//    ): ExerciseSet {
//        var exs: Array<out ExerciseSet?> = exs
//        if (exs.size == 0) {
//            return ExerciseSet(exercise, "rep", 8, 8, 8)
//        }
//
//        // Find the most used unit
//        val unitTypeCount: HashMap<String, Integer> = HashMap()
//        for (ex in exs) {
//            val unitCount: Integer = unitTypeCount.get(ex.getUnit())
//            unitTypeCount.put(
//                ex.getUnit(),
//                (if (unitCount == null) 0 else unitCount) + 1
//            )
//        }
//        var currentBestUnit = ""
//        var currentHighestUnitVal = 0
//        for (unitEntry in unitTypeCount.entrySet()) {
//            if (unitEntry.getValue() > currentHighestUnitVal) {
//                currentHighestUnitVal = unitEntry.getValue()
//                currentBestUnit = unitEntry.getKey()
//            }
//        }
//        val unit = currentBestUnit
//
//        // Filter out units which do not match most used unit
//        exs = Arrays.stream(exs)
//            .filter { ex -> ex.getUnit().equals(unit) }
//            .toArray { sz -> arrayOfNulls<ExerciseSet>(sz) }
//
//        // Retrieve the average set count
//        var avgSetCount = 0
//        for (ex in exs) {
//            avgSetCount += ex.getSets().length
//        }
//        avgSetCount /= exs.size
//
//        // Retrieve the average rep counts for each set
//        val sets = IntArray(avgSetCount)
//        for (i in sets.indices) {
//            var nValidSets = exs.size
//            for (k in exs.indices) {
//                if (k < exs[k].getSets().length) {
//                    sets[i] += exs[k].getSet(i)
//                } else {
//                    --nValidSets
//                }
//            }
//            sets[i] /= nValidSets
//        }
//
//        // Compose & return data
//        return ExerciseSet(exercise, unit, sets)
//    }
//
//    private inner class ExercisePriorityTracker(
//        ex: Exercise,
//        prior: Double,
//        psp: ArrayList<Double?>,
//        ps: Array<ExercisePriorityProvider?>
//    ) {
//        private val exercise: Exercise
//        private val probSets: ArrayList<ExercisePriorityProvider>
//        private val probSetPriorities: ArrayList<Double>
//        val priority: Double
//
//        init {
//            exercise = ex
//            priority = prior
//            probSetPriorities = psp
//            probSets = ArrayList()
//            for (s in ps) {
//                probSets.add(s)
//            }
//        }
//
//        fun getExercise(): Exercise {
//            return exercise
//        }
//
//        val priorityProviders: ArrayList<ExercisePriorityProvider>
//            get() = probSets
//        val individualPriorities: ArrayList<Double>
//            get() = probSetPriorities
//        val numPriorities: Int
//            get() = probSets.size()
//    }
//
//    private inner class ExercisePriorityComparator : Comparator<ExercisePriorityTracker> {
//        @Override
//        fun compare(a: ExercisePriorityTracker, b: ExercisePriorityTracker): Int {
//            return Double.compare(a.getPriority(), b.getPriority())
//        }
//    }
//}