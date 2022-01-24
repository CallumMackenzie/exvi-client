/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import java.awt.EventQueue;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarculaLaf;

/**
 *
 * @author callum
 */
public class AppRunner {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF: " + ex);
        }

        EventQueue.invokeLater(new AppEntry());
    }

}
