/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

/**
 *
 * @author callum
 */
public class WorkoutCreationView extends ControlledView<WorkoutCreationViewController> {

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
