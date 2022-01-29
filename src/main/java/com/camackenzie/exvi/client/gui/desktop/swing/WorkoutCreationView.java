/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing;

import com.camackenzie.exvi.client.gui.desktop.swing.uielements.LoadingIcon;
import com.camackenzie.exvi.core.model.ExerciseSet;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class WorkoutCreationView extends ControlledView<WorkoutCreationViewController> {

    JPanel exerciseSetsContainer;
    JButton generateButton;

    public WorkoutCreationView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.generateButton = new JButton("Generate");
        this.add(this.generateButton, "dock north");

        this.exerciseSetsContainer = new JPanel();
        this.exerciseSetsContainer.setLayout(new MigLayout("fill"));
        this.add(this.exerciseSetsContainer, "grow, wrap");

    }

    @Override
    public WorkoutCreationViewController createController(MainView mv) {
        return new WorkoutCreationViewController(this, mv.getModel());
    }

}
