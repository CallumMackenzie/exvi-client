/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client;

import com.camackenzie.exvi.core.model.BodyStats;

/**
 *
 * @author callum
 */
public class WorkoutGeneratorParams {

    private int minExercises = 6,
            maxExercises = 10;
    private double priorityRange = 0.1;
    private ExercisePriorityProvider[] providers;
    private BodyStats bodyStats;

    public WorkoutGeneratorParams(int minExercises,
            int maxExercises,
            double priorityRange,
            BodyStats bodyStats,
            ExercisePriorityProvider[] providers) {
        this.minExercises = minExercises;
        this.maxExercises = maxExercises;
        this.priorityRange = priorityRange;
        this.providers = providers;
        this.bodyStats = bodyStats;
    }

    public WorkoutGeneratorParams() {
        this(6, 10, 0.1, BodyStats.average(),
                new ExercisePriorityProvider[0]);
    }

    public BodyStats getBodyStats() {
        return this.bodyStats;
    }

    public void setBodyStats(BodyStats bs) {
        this.bodyStats = bs;
    }

    public WorkoutGeneratorParams withBodyStats(BodyStats bs) {
        this.setBodyStats(bs);
        return this;
    }

    public int getMinExercises() {
        return this.minExercises;
    }

    public void setMinExercises(int n) {
        this.minExercises = n;
    }

    public WorkoutGeneratorParams withMinExercises(int n) {
        this.setMinExercises(n);
        return this;
    }

    public int getMaxExercises() {
        return this.maxExercises;
    }

    public void setMaxExercises(int n) {
        this.maxExercises = n;
    }

    public WorkoutGeneratorParams withMaxExercises(int n) {
        this.setMaxExercises(n);
        return this;
    }

    public double getPriorityRange() {
        return this.priorityRange;
    }

    public void setPriorityRange(double d) {
        this.priorityRange = d;
    }

    public WorkoutGeneratorParams withPriorityRange(double d) {
        this.setPriorityRange(d);
        return this;
    }

    public ExercisePriorityProvider[] getExercisePriorityProviders() {
        return this.providers;
    }

    public void setExercisePriorityProviders(ExercisePriorityProvider[] exp) {
        this.providers = exp;
    }

    public WorkoutGeneratorParams withExercisePriorityProviders(
            ExercisePriorityProvider[] providers) {
        this.setExercisePriorityProviders(providers);
        return this;
    }

    public void setExerciseCount(int n) {
        this.setMaxExercises(n);
        this.setMinExercises(n);
    }

    public WorkoutGeneratorParams withExerciseCount(int n) {
        this.setExerciseCount(n);
        return this;
    }

}
