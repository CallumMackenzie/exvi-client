/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.MainView;
import java.awt.Component;

/**
 *
 * @author callum
 */
public interface View {

    public Component getViewRoot();

    public void onViewClose(MainView mv);

    public void onViewInit(Class<? extends View> sender, MainView mv);

}
