/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class MainView extends JFrame implements View {

    private final BackendModel model;
    private View currentView = null;

    public MainView(BackendModel model) {
        super("Exvi Fitness");
        this.model = model;
        this.addWindowListener(new MainViewWindowListener());
        this.setLayout(new MigLayout(new LC().fill()));
        this.setView(MainView.class, SignUpLoginView.getInstance());
        this.pack();
    }

    public void setView(Class<? extends View> senderClass, View view) {
        System.out.print(senderClass.getSimpleName()
                + " requests switch to view "
                + view.getClass().getSimpleName()
                + ": ");

        if (this.currentView != null) {
            System.out.print("Closing " + this.currentView.getClass().getSimpleName()
                    + ", ");
            this.currentView.onViewClose(this);
            this.getContentPane().remove(this.currentView.getViewRoot());
        }
        this.getContentPane().add(view.getViewRoot(), new CC().grow());
        System.out.print("Opening " + view.getClass().getSimpleName() + ", ");
        view.onViewInit(senderClass, this);
        this.currentView = view;

        this.revalidate();
        this.repaint();

        System.out.println("Completed");
    }

    public BackendModel getModel() {
        return this.model;
    }

    @Override
    public Component getViewRoot() {
        return this.getContentPane();
    }

    @Override
    public void onViewClose(MainView mv) {
    }

    @Override
    public void onViewInit(Class<? extends View> sender, MainView mv) {
    }

    private class MainViewWindowListener implements WindowListener {

        @Override
        public void windowClosed(WindowEvent e) {
            if (MainView.this.currentView != null) {
                MainView.this.currentView.onViewClose(MainView.this);
            }
        }

        @Override
        public void windowClosing(WindowEvent e) {
            MainView.this.dispose();
            MainView.this.onViewClose(MainView.this);
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
            MainView.this.onViewInit(MainView.class, MainView.this);
        }

    }

}
