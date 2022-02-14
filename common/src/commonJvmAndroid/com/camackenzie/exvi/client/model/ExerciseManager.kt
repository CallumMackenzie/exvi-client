///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.camackenzie.exvi.client.model
//
//import com.camackenzie.exvi.core.model.Exercise
//import com.camackenzie.exvi.core.model.ExerciseType
//import com.camackenzie.exvi.core.model.Muscle
//import com.camackenzie.exvi.core.model.ExerciseMechanics
//import com.camackenzie.exvi.core.model.ExerciseEquipment
//import com.camackenzie.exvi.core.model.ExerciseExperienceLevel
//import com.camackenzie.exvi.core.model.ExerciseForceType
//import kotlin.collections.ArrayList
//import kotlin.collections.HashSet
//import kotlinx.serialization.*
//import kotlinx.serialization.json.*
//
///**
// *
// * @author callum
// */
//class ExerciseManager(var exercises: Set<Exercise>) {
//
//    constructor(json: String) : this() {
//        addAllFromJson(json)
//    }
//
//    constructor(vararg exs: Exercise) : this() {
//        addAll(*exs)
//    }
//
//    constructor() : this(HashSet()) {}
//
//    fun addAll(vararg exs: Exercise) {
//        for (ex in exs) {
//            exercises.add(ex)
//        }
//    }
//
//    fun addAllFromJson(json: String) {
//        addAll(Json.decodeFromString<Array<Exercise>>(json))
//    }
//
//    fun toJson(): String {
//        return Json.encodeToString(exercises.toArray { sz -> arrayOfNulls<Exercise>(sz) })
//    }
//
//    fun getExercisesByFunction(add: (Exercise) -> Boolean): ArrayList<Exercise> {
//        val ret: ArrayList<Exercise> = ArrayList()
//        for (exercise in exercises) {
//            if (add(exercise)) {
//                ret.add(exercise)
//            }
//        }
//        return ret
//    }
//
//    fun getFirstExerciseByFunction(add: (Exercise) -> Boolean): Exercise? {
//        for (exercise in exercises) {
//            if (add(exercise)) {
//                return exercise
//            }
//        }
//        return null
//    }
//
//    fun getNamedExercise(`in`: String): Exercise? {
//        val name: String = `in`.trim()
//        return getFirstExerciseByFunction() { exercise ->
//            exercise.getName().trim()
//                .equalsIgnoreCase(name)
//        }
//    }
//
//    fun getExerciseNameContaining(n: String): Exercise? {
//        return getFirstExerciseByFunction() { ex ->
//            ex.getName()
//                .toLowerCase().contains(n.toLowerCase())
//        }
//    }
//
//    fun getExercisesOfType(et: ExerciseType?): ArrayList<Exercise> {
//        return getExercisesByFunction() { exercise ->
//            exercise.getExerciseTypes().contains(et)
//        }
//    }
//
//    fun getExercisesWithMuscle(m: Muscle): ArrayList<Exercise> {
//        return getExercisesByFunction() { exercise -> exercise.worksMuscle(m) }
//    }
//
//    fun getExercisesNameContaining(cont: String?): ArrayList<Exercise> {
//        return getExercisesByFunction() { ex -> ex.getName().contains(cont) }
//    }
//
//    fun getExercisesDescriptionContaining(cont: String?): ArrayList<Exercise> {
//        return getExercisesByFunction() { ex -> ex.getDescription().contains(cont) }
//    }
//
//    fun getExercisesWithForceType(ft: ExerciseForceType): ArrayList<Exercise> {
//        return getExercisesByFunction() { ex -> ex.getForceType() === ft }
//    }
//
//    fun getExercisesWithMechanics(m: ExerciseMechanics): ArrayList<Exercise> {
//        return getExercisesByFunction() { ex -> ex.getMechanics() === m }
//    }
//
//    fun getExercisesWithEquipment(m: ExerciseEquipment?): ArrayList<Exercise> {
//        return getExercisesByFunction() { ex -> ex.getEquipment().contains(m) }
//    }
//
//    fun getExercisesWithExperienceLevel(exp: ExerciseExperienceLevel): ArrayList<Exercise> {
//        return getExercisesByFunction() { ex -> ex.getExperienceLevel() === exp }
//    }
//}