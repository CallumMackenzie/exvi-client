/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author callum
 */
public class HomepageViewController extends Controller {

    @FXML
    MenuBar menuBar;

    @FXML
    MenuItem workoutManagerItem;

    @FXML
    MenuItem workoutCreationItem;

    @FXML
    MenuItem signOutItem;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cacheFXML(Views.CREATE_WORKOUT, Views.LOGIN);
        signOutItem.setOnAction(new SignOutAction());
        workoutCreationItem.setOnAction(new ToWorkoutCreationAction());
    }

    private class SignOutAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            BackendModel model = (BackendModel) stage.getUserData();
            model.getUserManager().signOutActiveUser();

            setView(Views.LOGIN, stage);
        }

    }

    private class ToWorkoutCreationAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            setView(Views.CREATE_WORKOUT, menuBar);
        }

    }

}
