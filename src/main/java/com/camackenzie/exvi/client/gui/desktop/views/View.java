/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.views;

import java.awt.Component;

/**
 *
 * @author callum
 */
public interface View {

    public Component getViewRoot();

    public void onViewClose();

    public void onViewInit();

}
