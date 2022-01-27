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
public class WorkoutManagerViewController extends ViewController<WorkoutManagerView, BackendModel> {

    public WorkoutManagerViewController(WorkoutManagerView v, BackendModel m) {
        super(v, m);
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
        getView().workoutListView.onViewInit(sender, this);
    }

    @Override
    public void onViewClose() {
        getView().workoutListView.onViewClose(this);
    }

}
