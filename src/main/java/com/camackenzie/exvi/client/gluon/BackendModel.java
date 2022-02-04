/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon;

import com.camackenzie.exvi.client.model.ExerciseManager;
import com.camackenzie.exvi.client.model.UserManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 *
 * @author callum
 */
public class BackendModel {

    private UserManager accountManager;
    private ExerciseManager exerciseManager;

    public BackendModel() {
        this.accountManager = new UserManager();
        this.exerciseManager = new ExerciseManager();

        this.setupExerciseManager();
    }

    private void setupExerciseManager() {
        try (InputStream in = getClass().getResourceAsStream("/exercises.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            this.exerciseManager
                    .addAllFromJson(reader.lines().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public UserManager getUserManager() {
        return this.accountManager;
    }

    public ExerciseManager getExerciseManager() {
        return this.exerciseManager;
    }

}
