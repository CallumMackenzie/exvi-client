/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.uielements;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPasswordField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author callum
 */
public class PasswordInput extends PromptedTextField {

    private String errorMessage;

    public PasswordInput() {
        super(new JPasswordField(), "Password");
        this.getTextField().addCaretListener(new PasswordCaretListener());
        this.getTextField().addKeyListener(new PasswordKeyListener());
    }

    public boolean isPasswordValid() {
        return this.errorMessage.equals("");
    }

    public String getPasswordError() {
        return this.errorMessage;
    }
    
    public void clear() {
        this.getTextField().setText("");
    }

    public String getPassword() {
        return this.getText();
    }

    private class PasswordCaretListener implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent e) {
            errorMessage = this.getPasswordError(getText());
        }

        private String getPasswordError(String p) {
            if (p.length() < 8) {
                return "Password must be 8 characters or longer";
            }
            return "";
        }

    }

    private class PasswordKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.toString(c)
                    .matches("[0-9a-zA-Z]|[*.!@#$%^&(){}\\[\\]:;<>,.?/~_+-=|]")
                    || getText().length() >= 24) {
                e.consume();
            }
        }

    }

}
