/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing;

import com.camackenzie.exvi.client.gui.desktop.BackendModel;
import com.camackenzie.exvi.client.model.WorkoutGenerator;
import com.camackenzie.exvi.core.model.ExerciseSet;
import com.camackenzie.exvi.core.model.Workout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author callum
 */
public class WorkoutCreationViewController
        extends ViewController<WorkoutCreationView, BackendModel> {

    public Workout workout;

    public WorkoutCreationViewController(WorkoutCreationView v, BackendModel m) {
        super(v, m);
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
        WorkoutGenerator generator = new WorkoutGenerator(getModel().getExerciseManager());
        this.workout = generator.generateNextWorkout("Random Workout");

        this.syncViewWithWorkout();

        getView().generateButton.addActionListener(new GenerateButtonAction());
    }

    private void syncViewWithWorkout() {
        getView().exerciseSetsContainer.removeAll();
        JPanel p = getView().exerciseSetsContainer;

        for (var ex : this.workout.getExercises()) {
            p.add(new ExerciseSetView(ex), "align center, grow, sg exercisesetr");
        }

        getView().getMainView().refresh();
    }

    private class GenerateButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            workout = new WorkoutGenerator(getModel().getExerciseManager())
                    .generateNextWorkout("Random Workout");

            syncViewWithWorkout();
        }

    }

}
