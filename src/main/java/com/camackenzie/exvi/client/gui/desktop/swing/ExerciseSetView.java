/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing;

import com.camackenzie.exvi.core.model.ExerciseSet;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class ExerciseSetView extends ControlledView<ExerciseSetViewController> {

    ExerciseSet exercise;

    JLabel name, description;

    public ExerciseSetView(ExerciseSet ex) {
        this.exercise = ex;
        this.setupComponents();
    }

    @Override
    public ExerciseSetViewController createController(MainView mv) {
        return new ExerciseSetViewController(this, mv.getModel());
    }

    private void setupComponents() {
        this.setLayout(new MigLayout());

        this.name = new JLabel("<html><h2 style='text-align:center;'>"
                + this.exercise.getExercise().getName()
                + "</h2></html>");
        this.add(this.name, "alignx center, wrap");

        this.description = new JLabel("<html>"
                + this.exercise.getExercise().getDescription().replaceAll("\n", " ")
                + "</html>");
        this.add(this.description, "alignx center, wrap");
    }

}
