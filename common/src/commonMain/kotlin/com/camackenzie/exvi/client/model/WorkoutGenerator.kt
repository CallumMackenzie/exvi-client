/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.ExviLogger
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.KSerializer
import kotlin.math.max
import kotlin.math.round

/**
 *
 * @author callum
 */
@Suppress("UNCHECKED_CAST")
@kotlinx.serialization.Serializable
class WorkoutGenerator(
    var exerciseManager: ExerciseManager,
    var params: WorkoutGeneratorParams = WorkoutGeneratorParams()
) : SelfSerializable {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    fun generateWorkout(
        wkr: Workout,
        lockedExercises: Set<ExerciseSet>
    ): Workout = this.generateWorkout(wkr, List(lockedExercises.size) { index -> index }.toTypedArray())

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
                ExviLogger.w("Insufficient exercises.", tag = "CLIENT")
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

    private fun blendExerciseSets(
        exercise: Exercise,
        exercises: List<WeightedExerciseSet>
    ): ExerciseSet {
        var exs = exercises.toMutableList()
        if (exs.size == 0) {
            return ExerciseSet(exercise, "", emptyList())
        }

        // Find the most used unit
        val unitTypeCount: HashMap<String, Int> = HashMap()
        for (ex in exs) {
            val unitCount: Int = unitTypeCount[ex.exerciseSet.unit] ?: 0
            unitTypeCount[ex.exerciseSet.unit] = unitCount + 1
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
        exs.retainAll { ex -> ex.exerciseSet.unit == unit }

        // Function to iterate over all valid sets and return the count
        fun forEachValidSet(valid: (WeightedExerciseSet) -> kotlin.Unit, invalid: () -> kotlin.Unit = {}) {
            for (k in exs.indices)
                if (k < exs[k].exerciseSet.sets.size) valid(exs[k]) else invalid()
        }

        // Retrieve the average set count
        var avgSetCount = 0
        for (ex in exs) {
            avgSetCount += ex.exerciseSet.sets.size
        }
        avgSetCount /= exs.size

        // Retrieve the average single weighted rep sets
        val sets = Array(avgSetCount) { SingleExerciseSet(0) }
        for (i in sets.indices) {
            var probWeightSum = 0.0
            var totalReps = 0.0
            var totalWeight = Mass(MassUnit.Kilogram, 0.0)
            forEachValidSet({
                val singleSet = it.exerciseSet.sets[i]

                probWeightSum += it.weight
                totalReps += singleSet.reps * it.weight
                totalWeight += singleSet.weight * it.weight
            })
            sets[i].reps = round(totalReps / probWeightSum).toInt()
            sets[i].weight = totalWeight / probWeightSum
            sets[i].timing = emptyArray()
        }

        // Compose & return data
        return ExerciseSet(exercise, unit, listOf(*sets))
    }

    private inner class ExercisePriorityTracker(
        val exercise: Exercise,
        val providers: Array<Pair<ExercisePriorityProvider, Double>>,
        val priority: Double
    )

    companion object {

        fun fromPriorities(
            exerciseManager: ExerciseManager,
            bodyStats: BodyStats = ActualBodyStats.average(),
            providers: Array<ExercisePriorityProvider>
        ): WorkoutGenerator = WorkoutGenerator(
            exerciseManager,
            WorkoutGeneratorParams(
                bodyStats = bodyStats,
                providers = providers
            )
        )

        fun random(
            exerciseManager: ExerciseManager,
            bodyStats: BodyStats = ActualBodyStats.average()
        ): WorkoutGenerator = fromPriorities(exerciseManager, bodyStats, emptyArray())

        fun arms(
            exerciseManager: ExerciseManager,
            bodyStats: BodyStats = ActualBodyStats.average()
        ): WorkoutGenerator = fromPriorities(exerciseManager, bodyStats, armPriorities())

        fun armPriorities(): Array<ExercisePriorityProvider> = arrayOf(
            ExerciseMusclePriority(Muscle.Arms.workData(1.0), 1.75),
            ExerciseExperiencePriority(ExerciseExperienceLevel.Beginner),
            ExerciseExperiencePriority(ExerciseExperienceLevel.Intermediate, 0.3),
            ExerciseEquipmentPriority(ExerciseEquipment("bodyweight"), 0.7),
            ExerciseEquipmentPriority(ExerciseEquipment("dumbbell"), 0.5, start = 2),
            ExerciseEquipmentPriority(ExerciseEquipment("kettle bells"), 0.2, start = 2),
            *warmupPriorities()
        )

        fun legs(
            exerciseManager: ExerciseManager,
            bodyStats: BodyStats = ActualBodyStats.average()
        ): WorkoutGenerator = fromPriorities(exerciseManager, bodyStats, legPriorities())

        fun warmupPriorities(end: Int = 2): Array<ExercisePriorityProvider> = arrayOf(
            ExerciseTypePriority(
                ExerciseType.Strength,
                start = end
            ),
            ExerciseTypePriority(
                ExerciseType.Warmup,
                end = end
            ),
            ExerciseForceTypePriority(
                ExerciseForceType.DynamicStretching, 1.25,
                end = end
            ),
        )

        fun legPriorities(): Array<ExercisePriorityProvider> = arrayOf(
            ExerciseMusclePriority(Muscle.Legs.workData(1.0)),
            ExerciseExperiencePriority(ExerciseExperienceLevel.Beginner),
            ExerciseExperiencePriority(ExerciseExperienceLevel.Intermediate),
            *warmupPriorities()
        )

        fun corePriorities(): Array<ExercisePriorityProvider> = arrayOf(
            ExerciseMusclePriority(Muscle.Abs.workData(1.0), 1.5),
            ExerciseTypePriority(ExerciseType.Strength),
            ExerciseExperiencePriority(ExerciseExperienceLevel.Beginner),
            ExerciseMusclePriority(Muscle.Back.workData(2.0), 0.1),
            ExerciseMusclePriority(Muscle.Back.workData(1.0), 0.2)
        )

        fun backPriorities(): Array<ExercisePriorityProvider> = arrayOf(
            ExerciseMusclePriority(Muscle.Back.workData(1.0), 1.5),
            ExerciseExperiencePriority(ExerciseExperienceLevel.Beginner),
            *warmupPriorities()
        )
    }
}