/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing;

import com.camackenzie.exvi.client.gui.desktop.swing.uielements.LoadingIcon;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class WorkoutListView extends ControlledView<WorkoutListViewController> {

    JList list;
    DefaultListModel listModel;
    JScrollPane listScroller;
    LoadingIcon loadingIcon;
    JButton newWorkoutButton;

    public WorkoutListView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.newWorkoutButton = new JButton("Create Workout");
        this.add(this.newWorkoutButton, "growx, wrap");

        this.listModel = new DefaultListModel();

        this.list = new JList(this.listModel);
        this.list.setVisibleRowCount(-1);

        this.listScroller = new JScrollPane(this.list);
        this.add(this.listScroller, "grow, wrap");

        this.loadingIcon = new LoadingIcon();
        this.loadingIcon.setVisible(false);
        this.add(this.loadingIcon, "growx");
    }

    @Override
    public WorkoutListViewController createController(MainView mv) {
        return new WorkoutListViewController(this, mv.getModel());
    }

}
