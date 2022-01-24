/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.views.SignUpLoginView;
import com.camackenzie.exvi.client.gui.desktop.views.View;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class MainView extends JFrame {

    private final BackendModel model;
    private View currentView = null;

    public MainView(BackendModel model) {
        this.model = model;
        this.addWindowListener(new MainViewWindowListener());
        this.setLayout(new MigLayout());
        this.setView(SignUpLoginView.getInstance());
        this.pack();
    }

    public void setView(View view) {
        if (this.currentView != null) {
            this.currentView.onViewClose();
        }
        this.getContentPane().remove(view.getViewRoot());
        this.getContentPane().add(view.getViewRoot());
        view.onViewInit();
    }

    public BackendModel getModel() {
        return this.model;
    }

    private class MainViewWindowListener implements WindowListener {

        @Override
        public void windowClosed(WindowEvent e) {
            currentView.onViewClose();
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
        }

    }

}
