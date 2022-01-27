/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class WorkoutManagerView extends ControlledView<WorkoutManagerViewController> {

    WorkoutListView workoutListView;

    public WorkoutManagerView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout());

        this.workoutListView = new WorkoutListView();
        this.add(this.workoutListView);
    }

    @Override
    public WorkoutManagerViewController createController(MainView mv) {
        return new WorkoutManagerViewController(this, mv.getModel());
    }
}
