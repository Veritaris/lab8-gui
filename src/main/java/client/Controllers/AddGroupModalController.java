package client.Controllers;

import client.Client.Listener;
import client.Client.Model;
import dependencies.Collection.Coordinates;
import dependencies.Collection.Country;
import dependencies.Collection.Semester;
import dependencies.Collection.StudyGroup;
import dependencies.CommandManager.CommandObject;
import dependencies.CommandManager.StudyGroupCreator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class AddGroupModalController<T> extends Controller implements Initializable {

    @FXML public Button cancelActionButton;
    @FXML public Button confirmActionButton;

    @FXML public TextField adminNameField;
    @FXML public TextField adminHeightField;
    @FXML public TextField adminWeightField;
    @FXML public TextField groupNameField;
    @FXML public TextField xCoordinateField;
    @FXML public TextField yCoordinateField;
    @FXML public TextField studentsAmountField;
    @FXML public TextField expelledStudentsAmountField;
    @FXML public ChoiceBox<String> country;
    @FXML public ChoiceBox<String> semesterSelector;
    @FXML public TextField studentsToExpelAmountField;

    private final Callable<T> action;
    private MainController mainController;

    public AddGroupModalController(Callable<T> action, Stage mainStage, MainController controller) {
        this.action = action;
        this.mainController = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        country.getItems().clear();
        country.getItems().addAll(
                Country.GERMANY.toString(),
                Country.SOUTH_KOREA.toString(),
                Country.FRANCE.toString(),
                Country.VATICAN.toString(),
                Country.INDIA.toString()
        );
        semesterSelector.getItems().clear();
        semesterSelector.getItems().addAll(
                Semester.FIRST.toString(),
                Semester.SECOND.toString(),
                Semester.FOURTH.toString(),
                Semester.FIFTH.toString(),
                Semester.EIGHTH.toString()
        );
    }

    @FXML
    public void handleConfirmCreateGroupAction(ActionEvent event) throws Exception {

        Model model = Model.getInstance();

        StudyGroup group = StudyGroupCreator.constructor(
                0L, groupNameField.getText(), semesterSelector.getValue(), xCoordinateField.getText(),
                yCoordinateField.getText(), studentsAmountField.getText(), adminNameField.getText(), adminHeightField.getText(),
                adminWeightField.getText(), country.getValue(), studentsToExpelAmountField.getText(), expelledStudentsAmountField.getText()
        );

        Listener listener = model.addGroup(group);
//        model.startListen();
//        model.addGroup(group);

        listener.setOnSucceeded(
                (e) -> {
                    CommandObject response = listener.getReceivedCommandObject();
                    String status = response.getBody().get("status");
                    String message = response.getBody().get("message");
                    System.out.println(status);
                    switch (status) {
                        case "200":
                        case "201":
                            model.setGroups(response.getStudyGroups());
                            this.mainController.updateTableView(response.getStudyGroups());
                            ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
//                            model.startListen();
                            break;
                        case "404":
//                            model.startListen();
                            break;
                    }

                }
        );
    }

    @FXML
    public void handleCancelAction(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }
}
