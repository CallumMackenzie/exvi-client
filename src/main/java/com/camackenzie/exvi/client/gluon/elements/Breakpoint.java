/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon.elements;

/**
 *
 * @author callum
 */
public enum Breakpoint {
    XSMALL(0),
    SMALL(1),
    MEDIUM(2),
    LARGE(3),
    XLARGE(4);

    private int value;

    Breakpoint(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Breakpoint fromString(String s) {
        s = s.trim();
        for (var enumConst : Breakpoint.class.getEnumConstants()) {
            if (s.equalsIgnoreCase(enumConst.toString())) {
                return enumConst;
            }
        }
        return null;
    }
}
