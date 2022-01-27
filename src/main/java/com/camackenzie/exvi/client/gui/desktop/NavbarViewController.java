/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author callum
 */
public class NavbarViewController extends ViewController<NavbarView, BackendModel> {

    public NavbarViewController(NavbarView view, BackendModel model) {
        super(view, model);
        this.setupControllers();
    }

    private void setupControllers() {
        this.getView().homeViewButton.addActionListener(new ToHomeViewAction());
        this.getView().signOutButton.addActionListener(new SignOutAction());
        this.getView().switchAccountButton.addActionListener(new ToUserSelectorAction());
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
    }

    @Override
    public void onViewClose() {
    }

    private class ToUserSelectorAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            getView().getMainView().setView(NavbarView.class, new UserSelectorView());
        }
    }

    private class ToHomeViewAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            getView().getMainView().setView(NavbarView.class, new HomepageView());
        }
    }

    private class SignOutAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            getModel().getUserManager().signOutActiveUser();
            getView().getMainView().setView(NavbarView.class, new SignUpLoginView());
        }
    }

}
