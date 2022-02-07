/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon.elements;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BootstrapRow extends Pane {

    private final List<BootstrapColumn> columns = new ArrayList<>();

    public BootstrapRow() {
        this.setVisible(false);
        this.getChildren().addListener((ListChangeListener<Node>) o -> {
            while (o.next()) {
                if (o.wasAdded()) {
                    Iterator<Node> nodeIter = (Iterator<Node>) o.getAddedSubList().iterator();
                    while (nodeIter.hasNext()) {
                        Node node = nodeIter.next();
                        BootstrapColumn column = new BootstrapColumn(node);
                        BiConsumer<Breakpoint, Function<Node, Integer>> setBreakpointColWidth
                                = (breakpoint, fn) -> {
                                    Integer val = fn.apply(node);
                                    if (val != null) {
                                        column.setBreakpointColumnWidth(breakpoint, val);
                                    }
                                };
                        setBreakpointColWidth.accept(Breakpoint.XSMALL, BootstrapPane::getXsmall);
                        setBreakpointColWidth.accept(Breakpoint.SMALL, BootstrapPane::getSmall);
                        setBreakpointColWidth.accept(Breakpoint.MEDIUM, BootstrapPane::getMedium);
                        setBreakpointColWidth.accept(Breakpoint.LARGE, BootstrapPane::getLarge);
                        setBreakpointColWidth.accept(Breakpoint.XLARGE, BootstrapPane::getXlarge);
                        this.columns.add(column);
                        nodeIter.remove();
                    }
                }
            }
        });
    }

    public List<BootstrapColumn> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public int calculateRowPositions(int lastGridPaneRow, Breakpoint currentWindowSize) {
        int inputRow = lastGridPaneRow;
        if (this.getColumns().isEmpty()) {
            return 0;
        }

        int currentGridPaneColumn = 0;
        for (BootstrapColumn column : this.getColumns()) {
            int contentWidth = column.getColumnWidth(currentWindowSize);
            if (currentGridPaneColumn + contentWidth > 12) {
                lastGridPaneRow++;
                currentGridPaneColumn = 0;
            }

            GridPane.setConstraints(
                    column.getContent(),
                    currentGridPaneColumn,
                    lastGridPaneRow,
                    contentWidth,
                    1
            );

            currentGridPaneColumn += contentWidth;

        }
        return lastGridPaneRow - inputRow + 1;
    }

}
