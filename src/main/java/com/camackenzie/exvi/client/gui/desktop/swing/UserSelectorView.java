/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing;

/**
 *
 * @author callum
 */
public class UserSelectorView extends ControlledView<UserSelectorViewController> {

    public UserSelectorView() {
        this.setupComponents();
    }

    private void setupComponents() {
    }

    @Override
    public UserSelectorViewController createController(MainView mv) {
        return new UserSelectorViewController(this, mv.getModel());
    }

}
