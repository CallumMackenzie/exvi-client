/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.uielements;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author callum
 */
public class UsernameInput extends PromptedTextField {

    private String usernameError = "Please enter a username";

    public UsernameInput() {
        super(new JTextField(), "Username");
        this.getTextField().addKeyListener(new UsernameKeyListener());
        this.getTextField().addCaretListener(new UsernameCaretListener());
    }

    public boolean isUsernameValid() {
        return this.usernameError.equals("");
    }

    public String getUsername() {
        return this.getText();
    }

    public String getUsernameError() {
        return this.usernameError;
    }

    public void clear() {
        this.getTextField().setText("");
    }

    private class UsernameKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            String sc = Character.toString(c);
            if (sc.matches("[A-Z]")) {
                sc = sc.toLowerCase();
                c = sc.charAt(0);
                e.setKeyChar(c);
            }
            if (!sc.matches("[0-9a-z]|[._-]")
                    || getText().length() >= 24) {
                e.consume();
            }

        }

    }

    private class UsernameCaretListener implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent arg0) {
            usernameError = "";
        }

    }

}
