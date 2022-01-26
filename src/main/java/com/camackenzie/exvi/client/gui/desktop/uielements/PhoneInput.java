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
public class PhoneInput extends PromptedTextField {

    private String phoneError = "Please enter a phone number";

    public PhoneInput() {
        super(new JTextField(), "Phone number");
        this.getTextField().addKeyListener(new PhoneKeyListener());
        this.getTextField().addCaretListener(new PhoneCaretListener());
    }

    public String getPhoneError() {
        return this.phoneError;
    }

    public boolean isPhoneValid() {
        return this.phoneError.equals("");
    }

    public String getPhone() {
        return this.getText();
    }

    public void clear() {
        this.getTextField().setText("");
    }

    private class PhoneKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            String sc = Character.toString(c);
            if (!sc.matches("[+0-9]")
                    || getPhone().length() >= 40
                    || (getPhone().contains("+")
                    && c == '+')) {
                e.consume();
            }
        }

    }

    private class PhoneCaretListener implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent e) {
            if (!getPhone().matches("^(\\+[0-9])?[0-9]+")) {
                phoneError = "Phone number is not valid";
            } else {
                phoneError = "";
            }
        }

    }
}
