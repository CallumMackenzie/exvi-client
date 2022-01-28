/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.model.ExerciseManager;
import com.camackenzie.exvi.client.model.UserManager;
import com.camackenzie.exvi.core.model.ExerciseSet;
import com.camackenzie.exvi.core.model.Workout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 *
 * @author callum
 */
public class WorkoutCreationView extends ControlledView<WorkoutCreationViewController> {

    public static void main(String[] args) throws IOException {
        UserManager um = new UserManager();
        ExerciseManager exman = new ExerciseManager();
        exman.addAllFromJson(Files.readString(Path.of("./exercises.json")));

        um.checkForLoggedInUsers();
        System.out.println(um.getActiveUser().getWorkoutManager()
                .addWorkouts(new Workout("Test Workout",
                        "A workout created to test Exvi Fitness.",
                        new ArrayList<ExerciseSet>() {
                    {
                        add(new ExerciseSet(exman.getNamedExercise("push up"), "rep", new int[]{10, 10, 10}));
                        add(new ExerciseSet(exman.getNamedExercise("pull up"), "rep", new int[]{7, 7, 7}));
                    }
                })).getFailOnError().getStatusCode());
    }

    public WorkoutCreationView() {
        this.setupComponents();
    }

    private void setupComponents() {
    }

    @Override
    public WorkoutCreationViewController createController(MainView mv) {
        return new WorkoutCreationViewController(this, mv.getModel());
    }

}
