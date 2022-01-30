/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx;

import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author callum
 */
public class AppRunner extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(this.getClass().getResource("/fxml/LoginSignupView.fxml"));
        stage.setTitle("Exvi Fitness");
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/image/Logo.png")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
