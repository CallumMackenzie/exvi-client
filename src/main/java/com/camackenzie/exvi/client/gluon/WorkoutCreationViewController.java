/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon;

import com.camackenzie.exvi.client.model.WorkoutGenerator;
import com.camackenzie.exvi.client.model.WorkoutGeneratorParams;
import com.camackenzie.exvi.core.model.Exercise;
import com.camackenzie.exvi.core.model.ExerciseSet;
import com.camackenzie.exvi.core.model.Workout;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
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
    Label exerciseNameText;
    @FXML
    Label exerciseDescriptionText;
    @FXML
    Label exerciseOverviewText;
    @FXML
    Label exerciseTipsText;
    @FXML
    Label exerciseTipsHeader;
    @FXML
    Label exerciseOverviewHeader;
    @FXML
    Label exerciseDescriptionHeader;
    @FXML
    TextField workoutNameField;

    WorkoutGenerator currentGenerator;
    Workout workout;
    ExerciseSet selectedExercise;
    HashSet<ExerciseSet> lockedExercises = new HashSet<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.cacheFXML(Views.HOME);
        this.setupControllers();
    }

    private void setupControllers() {
        // Generate button listener
        generateButton.setOnAction(new GenerateWorkoutAction());

        // Updates the view based on the selected exercise
        workoutExercises.getSelectionModel().selectedItemProperty()
                .addListener(new UpdateSelectedExerciseAction());

        // Cancel button listener
        cancelButton.setOnAction(new CancelAction());

        // Setup custom ListView renderer
        workoutExercises.cellFactoryProperty()
                .setValue(new ExerciseSetCellFactory());

        // Add workout name text field content listener
        workoutNameField.textProperty()
                .addListener(new WorkoutNameChangeListener());

        // This listener just sets up the view once the scene has been initialized
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                BackendModel model = (BackendModel) stage.getUserData();
                this.currentGenerator = new WorkoutGenerator(new WorkoutGeneratorParams(),
                        model.getExerciseManager());
                if (this.workout == null) {
                    this.workout = this.currentGenerator
                            .generateWorkout(new Workout("New Workout"));
                }
                new UpdateWorkoutAction().handle(new ActionEvent());
            }
        });
    }

    private class GenerateWorkoutAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            workout = currentGenerator.generateWorkout(workout, lockedExercises);
            new UpdateWorkoutAction().handle(e);
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
                exerciseDescriptionText.setText(ex.getDescription().isBlank()
                        ? "There is no description for this exercise."
                        : ex.getDescription());
                exerciseTipsText.setText(ex.getTips().isBlank()
                        ? "There are no tips for this exercise."
                        : ex.getTips());
                exerciseOverviewText.setText(ex.getOverview().isBlank()
                        ? "There is no overview for this exercise."
                        : ex.getOverview());

                exerciseTipsHeader.setVisible(true);
                exerciseDescriptionHeader.setVisible(true);
                exerciseOverviewHeader.setVisible(true);
            } else {
                exerciseNameText.setText("");
                exerciseDescriptionText.setText("");
                exerciseTipsText.setText("");
                exerciseOverviewText.setText("");

                exerciseTipsHeader.setVisible(false);
                exerciseDescriptionHeader.setVisible(false);
                exerciseOverviewHeader.setVisible(false);
            }

        }

    }

    private class UpdateWorkoutAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            workoutExercises.getItems().clear();
            workoutExercises.getItems().addAll(workout.getExercises());

            workoutNameField.setText(workout.getName());

            ExerciseSet oldVal = selectedExercise;
            selectedExercise = null;
            new UpdateSelectedExerciseAction().changed(null, oldVal, null);
        }

    }

    private class CancelAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent t) {
            setView(Views.HOME, (Node) t.getSource());
        }

    }

    private class ExerciseSetCellFactory
            implements Callback<ListView<ExerciseSet>, ListCell<ExerciseSet>> {

        @Override
        public ListCell<ExerciseSet> call(ListView<ExerciseSet> list) {
            return new ExerciseSetListCell();
        }

        private class ExerciseSetListCell extends ListCell<ExerciseSet> {

            MigPane rootPane;
            Label nameLabel;
            Button deleteButton;
            CheckBox lockCheckBox;
            ExerciseSet set;

            public ExerciseSetListCell() {
                rootPane = new MigPane();
                rootPane.setLayout("fill");

                nameLabel = new Label();
                rootPane.add(nameLabel, "left");

                deleteButton = new Button("X");
                deleteButton.setOnAction(new DeleteItemAction());
                rootPane.add(deleteButton, "east, gap 3pt 3pt");

                lockCheckBox = new CheckBox();
                lockCheckBox.selectedProperty().addListener(new ExerciseLockListener());
                rootPane.add(lockCheckBox, "east, gap 3pt 3pt");
            }

            @Override
            public void updateItem(ExerciseSet set, boolean empty) {
                super.updateItem(set, empty);
                if (empty || set == null) {
                    this.setGraphic(null);
                } else {
                    this.nameLabel.setText(set.getExercise().getName());
                    this.set = set;
                    this.lockCheckBox.setSelected(lockedExercises.contains(this.set));
                    this.setGraphic(rootPane);
                }
            }

            private class ExerciseLockListener implements ChangeListener<Boolean> {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable,
                        Boolean wasChecked, Boolean isChecked) {
                    if (!isChecked.equals(wasChecked)) {
                        if (isChecked) {
                            lockedExercises.add(set);
                        } else {
                            lockedExercises.remove(set);
                        }
                    }
                }

            }

            private class DeleteItemAction implements EventHandler<ActionEvent> {

                @Override
                public void handle(ActionEvent e) {
                    workout.getExercises().remove(set);
                    lockedExercises.remove(set);
                    new UpdateWorkoutAction().handle(e);
                }

            }
        }

    }

    private class WorkoutNameChangeListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable,
                String oldVal, String newVal) {
            if (newVal != null) {
                workout.setName(newVal);
            }
        }

    }

}
