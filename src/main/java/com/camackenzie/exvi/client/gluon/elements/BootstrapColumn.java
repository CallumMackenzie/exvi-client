/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon.elements;

import javafx.scene.Node;

public class BootstrapColumn {

    private final Node content;

    int[] columnWidths = new int[]{1, -1, -1, -1, -1};

    public BootstrapColumn(Node content) {
        this.content = content;
    }

    private static int clamp(int value, int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("Cannot clamp when max is greater than min");
        }
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        } else {
            return value;
        }
    }

    public void setBreakpointColumnWidth(Breakpoint breakPoint, int width) {
        columnWidths[breakPoint.getValue()] = clamp(width, 1, 12);
    }

    public void unsetBreakPoint(Breakpoint breakPoint) {
        columnWidths[breakPoint.getValue()] = -1;
    }

    public void unsetAllBreakPoints() {
        this.columnWidths = new int[]{
            1, // XS
            -1, // S
            -1, // M
            -1, // L
            -1 // XL
        };
    }

    public int getColumnWidth(Breakpoint breakPoint) {
        for (int i = breakPoint.getValue(); i >= 0; i--) {
            if (isValid(columnWidths[i])) {
                return columnWidths[i];
            }
        }
        return 1;
    }

    public Node getContent() {
        return content;
    }

    private boolean isValid(int value) {
        return value > 0 && value <= 12;
    }
}
