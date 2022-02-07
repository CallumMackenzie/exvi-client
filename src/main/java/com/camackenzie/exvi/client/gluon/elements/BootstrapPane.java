/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon.elements;

import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;

public class BootstrapPane extends GridPane {

    private final List<BootstrapRow> rows = new ArrayList<>();
    private Breakpoint currentWindowSize = Breakpoint.XSMALL;

    private static final String XSMALL_CONSTRAINT = "xsmall",
            SMALL_CONSTRAINT = "small",
            MEDIUM_CONSTRAINT = "medium",
            LARGE_CONSTRAINT = "large",
            XLARGE_CONSTRAINT = "xlarge";

    private static void setConstraint(Node node, String name, Object val) {
        node.getProperties().put(name, val);
    }

    private static Object getConstraint(Node node, String name) {
        return node.getProperties().get(name);
    }

    private static Integer getIntConstraint(Node node, String name) {
        return (Integer) getConstraint(node, name);
    }

    public static void setXsmall(Node node, Integer value) {
        setConstraint(node, XSMALL_CONSTRAINT, value);
    }

    public static Integer getXsmall(Node node) {
        return getIntConstraint(node, XSMALL_CONSTRAINT);
    }

    public static void setSmall(Node node, Integer value) {
        setConstraint(node, SMALL_CONSTRAINT, value);
    }

    public static Integer getSmall(Node node) {
        return getIntConstraint(node, SMALL_CONSTRAINT);
    }

    public static void setMedium(Node node, Integer value) {
        setConstraint(node, MEDIUM_CONSTRAINT, value);
    }

    public static Integer getMedium(Node node) {
        return getIntConstraint(node, MEDIUM_CONSTRAINT);
    }

    public static void setLarge(Node node, Integer value) {
        setConstraint(node, LARGE_CONSTRAINT, value);
    }

    public static Integer getLarge(Node node) {
        return getIntConstraint(node, LARGE_CONSTRAINT);
    }

    public static void setXlarge(Node node, Integer value) {
        setConstraint(node, XLARGE_CONSTRAINT, value);
    }

    public static Integer getXlarge(Node node) {
        return getIntConstraint(node, XLARGE_CONSTRAINT);
    }

    public BootstrapPane() {
        super();
        setAlignment(Pos.TOP_CENTER);
        setColumnConstraints();
        addListeners();
    }

    private void addListeners() {
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            Breakpoint newBreakpoint = Breakpoint.XSMALL;
            if (newValue.doubleValue() > 576) {

                newBreakpoint = Breakpoint.SMALL;
            }
            if (newValue.doubleValue() > 768) {
                newBreakpoint = Breakpoint.MEDIUM;
            }
            if (newValue.doubleValue() > 992) {
                newBreakpoint = Breakpoint.LARGE;
            }
            if (newValue.doubleValue() > 1200) {
                newBreakpoint = Breakpoint.XLARGE;
            }

            if (newBreakpoint != currentWindowSize) {
                currentWindowSize = newBreakpoint;
                calculateNodePositions();
            }
        });
        this.getChildren().addListener((ListChangeListener<Node>) o -> {
            while (o.next()) {
                if (o.wasAdded()) {
                    ListIterator<Node> nodesIter = (ListIterator<Node>) o.getAddedSubList().listIterator();
                    while (nodesIter.hasNext()) {
                        Node node = nodesIter.next();
                        if (node instanceof BootstrapRow) {
                            BootstrapRow row = (BootstrapRow) node;
                            this.rows.add(row);
                            nodesIter.remove();
                            for (BootstrapColumn col : row.getColumns()) {
                                if (!o.getList().contains(col)) {
                                    nodesIter.add(col.getContent());
                                }
                                GridPane.setFillWidth(col.getContent(), true);
                                GridPane.setFillHeight(col.getContent(), true);
                            }
                        }
                    }
                }
            }
            this.calculateNodePositions();
        });
    }

    private void setColumnConstraints() {
        getColumnConstraints().clear();
        double width = 100.0 / 12.0;
        for (int i = 0; i < 12; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(width);
            getColumnConstraints().add(columnConstraints);
        }
    }

    private void calculateNodePositions() {
        int currentGridPaneRow = 0;
        for (BootstrapRow row : rows) {
            currentGridPaneRow += row.calculateRowPositions(currentGridPaneRow, currentWindowSize);
        }
    }
}
