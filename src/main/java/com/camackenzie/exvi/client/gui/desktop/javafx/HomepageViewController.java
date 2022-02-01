/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author callum
 */
public class HomepageViewController implements Initializable {

    @FXML
    MenuBar menuBar;

    @FXML
    MenuItem workoutManagerItem;

    @FXML
    MenuItem signOutItem;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        signOutItem.setOnAction(new SignOutAction());
    }

    private class SignOutAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            try {
                Stage stage = (Stage) menuBar.getScene().getWindow();
                BackendModel model = (BackendModel) stage.getUserData();
                model.getUserManager().signOutActiveUser();
                Parent signinPage = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
                stage.getScene().setRoot(signinPage);
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }

    }

}
