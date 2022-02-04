/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx;

import com.camackenzie.exvi.client.model.WorkoutGeneratorParams;
import com.camackenzie.exvi.client.model.WorkoutGenerator;
import com.camackenzie.exvi.core.model.Exercise;
import com.camackenzie.exvi.core.model.ExerciseSet;
import com.camackenzie.exvi.core.model.Workout;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.fxml.MigPane;

/**
 * FXML Controller class
 *
 * @author callum
 */
public class WorkoutCreationViewController extends Controller {

    @FXML
    MigPane rootPane;
    @FXML
    Button generateButton;
    @FXML
    Button cancelButton;
    @FXML
    Button finishButton;
    @FXML
    ListView<ExerciseSet> workoutExercises;
    @FXML
    Text exerciseNameText;
    @FXML
    Label exerciseDescriptionText;
    @FXML
    Label exerciseOverviewText;
    @FXML
    Label exerciseTipsText;
    @FXML
    Text exerciseTipsHeader;
    @FXML
    Text exerciseOverviewHeader;
    @FXML
    Text exerciseDescriptionHeader;

    WorkoutGenerator currentGenerator;
    Workout currentWorkout;
    ExerciseSet selectedExercise;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cacheFXML(Views.HOME);
        
        this.setupLocalModel();
        this.setupControllers();
    }

    public void setWorkout(Workout w) {
        this.currentWorkout = w;
    }

    private void setupControllers() {
        generateButton.setOnAction(new GenerateWorkoutAction());
        workoutExercises.getSelectionModel().selectedItemProperty()
                .addListener(new UpdateSelectedExerciseAction());
        cancelButton.setOnAction(new CancelAction());
    }

    private void setupLocalModel() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                BackendModel model = (BackendModel) stage.getUserData();
                this.currentGenerator = new WorkoutGenerator(new WorkoutGeneratorParams(),
                        model.getExerciseManager());
                if (this.currentWorkout == null) {
                    this.currentWorkout = this.currentGenerator.generateNextWorkout("New Workout");
                }
                new UpdateWorkoutAction().handle(new ActionEvent());
            }
        });
    }

    private class GenerateWorkoutAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            currentWorkout = currentGenerator.regenerateWorkout(currentWorkout);
            new UpdateWorkoutAction()
                    .handle(e);
        }

    }

    private class UpdateSelectedExerciseAction implements ChangeListener<ExerciseSet> {

        @Override
        public void changed(ObservableValue<? extends ExerciseSet> e,
                ExerciseSet oldVal,
                ExerciseSet newVal) {
            selectedExercise = newVal;
            if (newVal != null) {
                Exercise ex = selectedExercise.getExercise();
                exerciseNameText.setText(ex.getName());
                exerciseDescriptionText.setText(ex.getDescription());
                exerciseTipsText.setText(ex.getTips());
                exerciseOverviewText.setText(ex.getOverview());
                exerciseTipsHeader.setVisible(!ex.getTips().isBlank());
                exerciseDescriptionHeader.setVisible(!ex.getDescription().isBlank());
                exerciseOverviewHeader.setVisible(!ex.getOverview().isBlank());
            }

        }

    }

    private class UpdateWorkoutAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            workoutExercises.getItems().clear();
            workoutExercises.getItems().addAll(currentWorkout.getExercises());
        }

    }

    private class CancelAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent t) {
            setView(Views.HOME, (Node) t.getSource());
        }

    }

}
