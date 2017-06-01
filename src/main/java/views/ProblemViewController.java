package main.java.views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import main.java.Bogatyrs;
import main.java.model.Solver;
import main.java.util.ModalAlerts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProblemViewController implements Initializable {

    private ResourceBundle langBundle;

    @FXML
    private ChoiceBox<String> problemPicker;

    @FXML
    private Label solutionLabel;

    @FXML
    private TextField solutionField;

    @FXML
    private Button backBtn;

    @FXML
    private Button saveFileBtn;

    @FXML
    private Button computeBtn;

    @FXML
    private TextField firstTimeField;

    @FXML
    private TextField secondTimeField;

    @FXML
    void loadWelcomeView(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("WelcomeView.fxml"), langBundle);
        AnchorPane pane;
        Scene scene;

        try {
            pane = (AnchorPane) loader.load();
            scene = new Scene(pane, 600, 400);
            Bogatyrs.getMainStage().setScene(scene);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    boolean compute(ActionEvent event) {

        double firstTime;
        double secondTime;

        try {
            firstTime = Double.valueOf(firstTimeField.getText().split("\\s+")[0]);
            secondTime = Double.valueOf(secondTimeField.getText().split("\\s+")[0]);
        } catch (NumberFormatException e) {
            ModalAlerts.displayError(
                    langBundle.getString("problem.numexeption.title"),
                    langBundle.getString("problem.numexeption.header"),
                    langBundle.getString("problem.numexeption.msg")
            );
            return false;
        }


        if (!checkRange(firstTime, 1, 365) || !checkRange(secondTime, 1, 365)) {
            ModalAlerts.displayError(
                    langBundle.getString("problem.intervalerror.title"),
                    langBundle.getString("problem.intervalerror.header"),
                    langBundle.getString("problem.intervalerror.msg")
            );
            return false;
        }

        solutionField.setText("≈ " + Solver.solve(firstTime, secondTime) + " " + langBundle.getString("problem.solution.units"));

        return true;
    }

    @FXML
    void saveToFile(ActionEvent event) {
        if (!compute(null)) {
            return;
        }

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(langBundle.getString("problem.chooser.title"));
        fileChooser.setInitialFileName("solution.txt");

        File file = fileChooser.showSaveDialog(Bogatyrs.getMainStage());

        if (file == null) {
            return;
        }

        StringBuilder sb = new StringBuilder("");
        String units = langBundle.getString("problem.solution.timeunits");
        String firstTime = firstTimeField.getText().split("\\s+")[0];
        String secondTime = secondTimeField.getText().split("\\s+")[0];

        sb.append(langBundle.getString("problem.file.firstbogatyr"))
                .append(" ").append(firstTime)
                .append(" ").append(units).append("\n");

        sb.append(langBundle.getString("problem.file.secondbogatyr"))
                .append(" ").append(secondTime)
                .append(" ").append(units).append("\n");

        sb.append(langBundle.getString("problem.solution.label"))
                .append(" ").append(solutionField.getText());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(sb.toString());
            writer.close(); // force flush and save the file
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void setOriginalSolution() {
        solutionField.setText(langBundle.getString("problem.solution.answer"));
        firstTimeField.setText(langBundle.getString("problem.solution.firstoriginal"));
        secondTimeField.setText(langBundle.getString("problem.solution.secondoriginal"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.langBundle = resources;

        ObservableList<String> items = FXCollections.observableArrayList();

        String originalSolution = langBundle.getString("problem.selector.original");
        String anyValue = langBundle.getString("problem.selector.any");

        items.addAll(originalSolution, anyValue);

        problemPicker.setItems(items);

        problemPicker.getSelectionModel().selectFirst();

        setOriginalSolution();

        disableEditing();

        problemPicker.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                String chosenPlanet = problemPicker.getItems().get(newValue.intValue());
                if (chosenPlanet.equals(originalSolution)) {
                    disableEditing();
                    setOriginalSolution();
                } else if (chosenPlanet.equals(anyValue)) {
                    enableEditing();
                    clearVariableInputs();
                }
            }
        });
    }

    private void enableEditing() {
        firstTimeField.setEditable(true);
        secondTimeField.setEditable(true);
    }

    private void disableEditing() {
        firstTimeField.setEditable(false);
        secondTimeField.setEditable(false);
    }

    private void clearVariableInputs() {
        solutionField.setText("");
        firstTimeField.setText("");
        secondTimeField.setText("");
    }

    private boolean checkRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
