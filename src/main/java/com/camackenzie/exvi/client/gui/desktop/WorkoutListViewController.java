/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.core.async.FutureWrapper;
import com.camackenzie.exvi.core.async.RunnableFuture;
import com.camackenzie.exvi.core.model.Workout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

/**
 *
 * @author callum
 */
public class WorkoutListViewController extends ViewController<WorkoutListView, BackendModel> {

    private RunnableFuture workoutSyncFuture;

    public WorkoutListViewController(WorkoutListView w, BackendModel m) {
        super(w, m);
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
        new SyncWorkoutsAction()
                .actionPerformed(new ActionEvent(this, 0, ""));

        getView().newWorkoutButton
                .addActionListener(new CreateWorkoutAction());
    }

    @Override
    public void onViewClose() {
        if (this.workoutSyncFuture != null) {
            this.workoutSyncFuture.cancel(true);
        }
    }

    private class SyncWorkoutsAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            getView().loadingIcon.setVisible(true);

            workoutSyncFuture = new RunnableFuture(new Runnable() {
                @Override
                public void run() {
                    FutureWrapper<Workout[]> workoutFuture
                            = getModel().getUserManager()
                                    .getActiveUser()
                                    .getWorkoutManager()
                                    .getWorkouts();
                    try {
                        Workout[] workouts = workoutFuture.get();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (workouts != null) {
                                    for (var workout : workouts) {
                                        getView().listModel.addElement(workout);
                                    }
                                }
                                getView().loadingIcon.setVisible(false);
                            }
                        });
                    } catch (InterruptedException ex) {
                        return;
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                }
            });
            workoutSyncFuture.start();
        }
    }

    private class CreateWorkoutAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            getView().getMainView().setView(WorkoutListView.class, new WorkoutCreationView());
        }

    }

}
