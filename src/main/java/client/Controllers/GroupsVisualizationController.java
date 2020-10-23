package client.Controllers;

import client.Client.Model;
import client.Drawings.StudyGroupIcon;
import dependencies.Collection.StudyGroup;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GroupsVisualizationController implements Initializable {

    @FXML private Pane titleBar;
    @FXML private Canvas studentsField;
    @FXML private Button minimizeButton;
    @FXML private Button closeButton;
    @FXML private AnchorPane root;
    @FXML private Group studentsFieldGroup;
    private Stage mainStage;
    private final Controller parentController;
    private final Stage parentStage;
    private Model model = Model.getInstance();

    private double x, y;

    public GroupsVisualizationController(Stage parentStage, Controller parentController) {
        this.parentController = parentController;
        this.parentStage = parentStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.sceneProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        newValue.windowProperty().addListener(
                                (observable1, oldValue1, newValue1) -> {
                                    mainStage = (Stage) newValue.getWindow();
                                    studentsFieldGroup.getChildren().addAll(
                                            dispatchGroupIcons()
                                    );
                                }
                        );
                    }
                }
        );
    }

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    private ArrayList<StudyGroupIcon> dispatchGroupIcons() {
        ArrayList<StudyGroupIcon> groupIcons = new ArrayList<>();
        for (StudyGroup group: model.getGroups()) {
            System.out.println(group.getColorHex());
            StudyGroupIcon groupIcon = new StudyGroupIcon(group, parentController, parentStage);
            System.out.println(groupIcon);
            groupIcons.add(
                    groupIcon
            );
        }
        return groupIcons;
    }

    @FXML
    private void titleBarMouseDraggedEvent(MouseEvent event) {
        Stage stage = (Stage) ((Pane) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    private void titleBarMousePressedEvent(MouseEvent event) {
        Stage stage = (Stage) ((Pane) event.getSource()).getScene().getWindow();
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    private void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Button )event.getSource()).getScene().getWindow();
        stage.hide();
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Button )event.getSource()).getScene().getWindow();
        stage.close();
    }
}
