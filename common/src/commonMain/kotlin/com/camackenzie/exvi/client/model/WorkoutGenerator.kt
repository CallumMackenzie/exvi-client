/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.HashMap
import kotlin.math.max
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 *
 * @author callum
 */
@kotlinx.serialization.Serializable
class WorkoutGenerator(
    var exerciseManager: ExerciseManager,
    var params: WorkoutGeneratorParams = WorkoutGeneratorParams()
) : SelfSerializable {

    fun generateWorkout(
        wkr: Workout,
        lockedExercises: Set<ExerciseSet>
    ): Workout {
        return this.generateWorkout(wkr, List(lockedExercises.size) { index -> index }.toTypedArray())
    }

    fun generateWorkout(
        workout: Workout = Workout("New Workout", "", arrayListOf()),
        lockedIndexes: Array<Int> = emptyArray()
    ): Workout {
        // Create a new workout
        val wkr = Workout(workout)
        val workoutExers: ArrayList<ExerciseSet> = wkr.exercises

        // Retrieve the exercises
        val exercises: HashSet<Exercise> = HashSet(exerciseManager.exercises)

        // Remove locked exercises from global exercise set
        for (lockedIndex in lockedIndexes) {
            if (lockedIndex >= 0 && lockedIndex < workoutExers.size) {
                exercises.remove(workoutExers[lockedIndex].exercise)
            }
        }

        // Get the highest index which is locked
        var highestLocked = -1
        for (lockedIndex in lockedIndexes) {
            if (lockedIndex > highestLocked) {
                highestLocked = lockedIndex
            }
        }

        // Get the number of exercises, with at least the highest number locked
        val nExercises: Int = max(
            Random.intInRange(
                params.minExercises,
                params.maxExercises
            ),
            highestLocked + 1
        )
        val exs: ArrayList<ExerciseSet> = ArrayList(nExercises)

        // Get exercises based on their priority
        for (i in 0 until nExercises) {
            // If the exercise is locked, skip it
            if (lockedIndexes.contains(i)) {
                exs.add(workoutExers[i])
                continue
            }

            // Create exercise priority list to track
            val exercisePriorities: ArrayList<ExercisePriorityTracker> = ArrayList()
            // For each exercise find the priority based on the given priority providers
            for (ex in exercises) {
                // Accumulate priorities from each priority set
                val probs = params.providers
                val individualPriorities = ArrayList<Double>()
                var exerPriority = 0.0
                for (prob in probs) {
                    val probPriority = prob.getPriority(this, ex, i)
                    exerPriority += probPriority
                    individualPriorities.add(exerPriority)
                }

                // Add the exercise priority if it has contributed
                if (exerPriority > 0
                    || params.providers.isEmpty()
                ) {
                    exercisePriorities.add(
                        ExercisePriorityTracker(
                            ex,
                            probs.mapIndexed { index, v ->
                                v to individualPriorities[index]
                            }.toTypedArray(),
                            exerPriority
                        )
                    )
                }
            }
            // Sort the exercise prioritites to find the currentHighestUnitVal ones
            exercisePriorities.sortWith { a, b ->
                a.priority.compareTo(b.priority)
            }

            // Ensure sufficient exercises exist
            if (exercisePriorities.size == 0) {
                println("Insufficient exercises.")
                continue
            }

            // Get exercises with priority similar to the currentHighestUnitVal
            val validExers: ArrayList<ExercisePriorityTracker> = ArrayList()
            val maxPriority: Double = exercisePriorities[exercisePriorities.size - 1]
                .priority
            for (j in exercisePriorities.size - 1 downTo 0) {
                val exp: ExercisePriorityTracker = exercisePriorities[j]
                if ((exp.priority + params.priorityRange > maxPriority
                            && exp.priority - params.priorityRange < maxPriority)
                    || j == exercisePriorities.size - 1
                ) {
                    validExers.add(exp)
                } else {
                    break
                }
            }

            // Randomize valid exercises
            validExers.shuffle()

            // Select exercise
            val selectedExer: ExercisePriorityTracker = validExers[0]
            // Generate exercise set based on most presiding probability
            val generated = selectedExer.providers
                .map { pr -> pr.first.generateExerciseSet(this, selectedExer.exercise) }

            // Add exercise set to workout & remove the exercise from the exercises array to
            // avoid repeats
            exs.add(blendExerciseSets(selectedExer.exercise, generated))
            exercises.remove(selectedExer.exercise)

            // Register a match for each priority provider
            for (k in 0 until selectedExer.providers.size) {
                val provider = selectedExer.providers[k]
                provider.first.registerMatch(
                    this,
                    wkr,
                    provider.second,
                    selectedExer.priority
                )
            }
        }
        workoutExers.clear()
        workoutExers.addAll(exs)

        // Return the created workout
        return wkr
    }

    fun blendExerciseSets(
        exercise: Exercise,
        exercises: List<ExerciseSet>
    ): ExerciseSet {
        var exs = exercises.toMutableList()
        if (exs.size == 0) {
            return ExerciseSet(exercise, "rep", arrayOf(8, 8, 8))
        }

        // Find the most used unit
        val unitTypeCount: HashMap<String, Int> = HashMap()
        for (ex in exs) {
            val unitCount: Int = unitTypeCount[ex.unit] ?: 0
            unitTypeCount[ex.unit] = unitCount + 1
        }
        var currentBestUnit = ""
        var currentHighestUnitVal = 0
        for ((key, value) in unitTypeCount) {
            if (value > currentHighestUnitVal) {
                currentHighestUnitVal = value
                currentBestUnit = key
            }
        }
        val unit = currentBestUnit

        // Filter out units which do not match most used unit
        exs.retainAll { ex -> ex.unit == unit }

        // Retrieve the average set count
        var avgSetCount = 0
        for (ex in exs) {
            avgSetCount += ex.sets.size
        }
        avgSetCount /= exs.size

        // Retrieve the average rep counts for each set
        val sets = IntArray(avgSetCount)
        for (i in sets.indices) {
            var nValidSets = exs.size
            for (k in exs.indices) {
                if (k < exs[k].sets.size) {
                    sets[i] += exs[k].sets[i]
                } else {
                    --nValidSets
                }
            }
            sets[i] /= nValidSets
        }

        // Compose & return data
        return ExerciseSet(exercise, unit, sets.toTypedArray())
    }

    private inner class ExercisePriorityTracker(
        val exercise: Exercise,
        val providers: Array<Pair<ExercisePriorityProvider, Double>>,
        val priority: Double
    )

    companion object {

        const val uid = "WorkoutGenerator"

        fun fromPriorities(
            exerciseManager: ExerciseManager,
            bodyStats: BodyStats = BodyStats.average(),
            providers: Array<ExercisePriorityProvider>
        ): WorkoutGenerator {
            return WorkoutGenerator(
                exerciseManager,
                WorkoutGeneratorParams(
                    bodyStats = bodyStats,
                    providers = providers
                )
            )
        }

        fun random(
            exerciseManager: ExerciseManager,
            bodyStats: BodyStats = BodyStats.average()
        ): WorkoutGenerator {
            return fromPriorities(exerciseManager, bodyStats, emptyArray())
        }

        fun arms(
            exerciseManager: ExerciseManager,
            bodyStats: BodyStats = BodyStats.average()
        ): WorkoutGenerator {
            return fromPriorities(exerciseManager, bodyStats, armPriorities())
        }

        fun armPriorities(): Array<ExercisePriorityProvider> {
            return arrayOf(
                ExerciseMusclePriority(Muscle.ARMS.workData(1.0), 1.75),
                ExerciseExperiencePriority(ExerciseExperienceLevel.BEGINNER),
                ExerciseTypePriority(
                    ExerciseType.STRENGTH,
                    start = 2
                ),
                ExerciseTypePriority(
                    ExerciseType.WARMUP, 0.5,
                    end = 2
                ),
                ExerciseForceTypePriority(
                    ExerciseForceType.DYNAMIC_STRETCHING, 1.25,
                    end = 2
                ),
                ExerciseExperiencePriority(ExerciseExperienceLevel.INTERMEDIATE, 0.3),
                ExerciseEquipmentPriority(ExerciseEquipment("bodyweight"), 0.7),
                ExerciseEquipmentPriority(ExerciseEquipment("dumbbell"), 0.5, start = 2),
                ExerciseEquipmentPriority(ExerciseEquipment("kettle bells"), 0.2, start = 2)
            )
        }

        fun legs(
            exerciseManager: ExerciseManager,
            bodyStats: BodyStats = BodyStats.average()
        ): WorkoutGenerator {
            return fromPriorities(exerciseManager, bodyStats, legPriorities())
        }

        fun legPriorities(): Array<ExercisePriorityProvider> {
            return arrayOf(
                ExerciseMusclePriority(Muscle.LEGS.workData(1.0)),
                ExerciseTypePriority(
                    ExerciseType.STRENGTH,
                    start = 2
                ),
                ExerciseTypePriority(
                    ExerciseType.WARMUP,
                    end = 2
                ),
                ExerciseExperiencePriority(ExerciseExperienceLevel.BEGINNER),
                ExerciseExperiencePriority(ExerciseExperienceLevel.INTERMEDIATE)
            )
        }
    }

    override fun getUID(): String {
        return uid
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }
}