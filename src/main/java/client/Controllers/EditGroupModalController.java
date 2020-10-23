package client.Controllers;

import client.Client.Listener;
import client.Client.Model;
import dependencies.Collection.Country;
import dependencies.Collection.Semester;
import dependencies.Collection.StudyGroup;
import dependencies.CommandManager.CommandObject;
import dependencies.CommandManager.StudyGroupCreator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class EditGroupModalController implements Initializable {
    @FXML public Button cancelActionButton;
    @FXML public Button confirmActionButton;

    @FXML public ChoiceBox<String> semesterSelector;
    @FXML public ChoiceBox<String> adminCountrySelector;
    @FXML public TextField adminNameField;
    @FXML public TextField groupNameField;
    @FXML public TextField adminHeightField;
    @FXML public TextField adminWeightField;
    @FXML public TextField xCoordinateField;
    @FXML public TextField yCoordinateField;
    @FXML public TextField studentsAmountField;
    @FXML public TextField studentsToExpelAmountField;
    @FXML public TextField expelledStudentsAmountField;

    private final StudyGroup groupToEdit;
    private MainController parentController;

    public EditGroupModalController(StudyGroup group, MainController controller) {
        super();
        this.parentController = controller;
        this.groupToEdit = group;
    }

    private final Callable<Integer> blank = () -> 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminCountrySelector.getItems().clear();
        adminCountrySelector.getItems().addAll(
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

        adminNameField.setText(this.groupToEdit.getGroupAdminObject().getName());
        adminHeightField.setText(String.format("%s", this.groupToEdit.getGroupAdminObject().getHeight()));
        adminWeightField.setText(String.format("%s", this.groupToEdit.getGroupAdminObject().getWeight()));
        adminCountrySelector.setValue(this.groupToEdit.getGroupAdminObject().getNationality().toString());

        groupNameField.setText(this.groupToEdit.getName());
        semesterSelector.setValue(this.groupToEdit.getSemesterObject().toString());
        xCoordinateField.setText(String.format("%s", this.groupToEdit.getCoordinatesObject().getX()));
        yCoordinateField.setText(String.format("%s", this.groupToEdit.getCoordinatesObject().getY()));
        studentsAmountField.setText(String.format("%s", this.groupToEdit.getStudentsCount()));
        expelledStudentsAmountField.setText(String.format("%s", this.groupToEdit.getExpelledStudentsAmount()));
        studentsToExpelAmountField.setText(String.format("%s", this.groupToEdit.getToExpelAmount()));
    }

    public void handleCancelAction(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }

    public void handleConfirmEditGroupAction(ActionEvent event) {
        Model model = Model.getInstance();

        StudyGroup group = StudyGroupCreator.constructor(
                this.groupToEdit.getId(), groupNameField.getText(), semesterSelector.getValue(), xCoordinateField.getText(),
                yCoordinateField.getText(), studentsAmountField.getText(), adminNameField.getText(), adminHeightField.getText(),
                adminWeightField.getText(), adminCountrySelector.getValue(), studentsToExpelAmountField.getText(), expelledStudentsAmountField.getText()
        );

        Listener listener = model.editGroup(group);

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
                            this.parentController.updateTableView(response.getStudyGroups());
                            ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
                            break;
                        case "403":
                            ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
                            openDenyDialog((Stage) ((Button) event.getSource()).getScene().getWindow());
                            break;
                        case "404":
                            break;
                    }
                }
        );
    }

    private void openDenyDialog(Stage parent) {
        Model model = Model.getInstance();
        final Stage confirmModal = new Stage();

        FXMLLoader modalLoader = new FXMLLoader(getClass().getResource("/views/deniedModal.fxml"), model.getBundle());
        try {
            DenyModalController controller = new DenyModalController();
            modalLoader.setController(controller);
            Parent root = modalLoader.load();
            confirmModal.setScene(new Scene(root));
            confirmModal.initOwner(parent);
            confirmModal.initModality(Modality.WINDOW_MODAL);
            confirmModal.initStyle(StageStyle.UNDECORATED);
            confirmModal.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
