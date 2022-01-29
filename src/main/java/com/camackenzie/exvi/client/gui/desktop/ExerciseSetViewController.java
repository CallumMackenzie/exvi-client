/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 *
 * @author Alexx
 */
public class ExerciseSetViewController extends ViewController<ExerciseSetView, BackendModel> {

    public ExerciseSetViewController(ExerciseSetView view, BackendModel model) {
        super(view, model);
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
        getView().addMouseListener(new HighlightOuterPanelListener());
    }

    private class HighlightOuterPanelListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            System.out.println("ENTER");
            getView().setBackground(Color.RED);
            getView().getMainView().refresh();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            System.out.println("EXIT");
            getView().setBackground(null);
            getView().getMainView().refresh();
        }

    }

}
