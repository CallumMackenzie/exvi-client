/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing.uielements;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class CollapsibleView extends JPanel {

    private JPanel content,
            template;
    private boolean isCollapsed;

    public CollapsibleView(JPanel content, JPanel template) {
        this.content = content;
        this.template = template;
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout());
    }

    public void setCollapsed(boolean collapsed) {
        this.isCollapsed = collapsed;
    }

    public boolean isCollapsed() {
        return this.isCollapsed;
    }

}
