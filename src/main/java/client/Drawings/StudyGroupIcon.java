package client.Drawings;

import client.Client.Model;
import client.Controllers.Controller;
import client.Controllers.EditGroupModalController;
import client.Controllers.MainController;
import dependencies.Collection.StudyGroup;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.stream.Collectors;

public class StudyGroupIcon extends Circle {
    private final StudyGroup group;
    private final int x;
    private final int y;
    private final int r;
    private final String colorHex;

    private final Controller parentController;
    private final Stage parentStage;

    public StudyGroupIcon(StudyGroup studyGroup, Controller parentController, Stage parentStage) {
        super();
        this.parentController = parentController;
        this.parentStage = parentStage;

        this.x = (int) studyGroup.getCoordinatesObject().getX();
        this.y = (int) studyGroup.getCoordinatesObject().getY();
        this.r = (int) studyGroup.getStudentsCount();

        this.colorHex = studyGroup.getColorHex();
        this.group = studyGroup;

        this.setCenterX(this.x);
        this.setCenterY(this.y);
        this.setRadius(this.r);

        this.setFill(Paint.valueOf(this.colorHex));

        setEventListeners();
    }

    public StudyGroupIcon(Integer x, Integer y, Integer r, Controller parentController, Stage parentStage) {
        super(x, y, r);
        this.parentController = parentController;
        this.parentStage = parentStage;
        this.group = null;
        this.x = x;
        this.y = y;
        this.r = r;
        this.colorHex = String.format("#%s", (int) hashCode() % 1e6);
        this.setFill(Paint.valueOf(this.colorHex));
        setEventListeners();
    }

    private void setEventListeners() {
        this.addEventHandler(
                MouseEvent.MOUSE_PRESSED,
                (event) -> {
                    openEditGroupModal();

                    ((Group) ((Circle) event.getSource()).getParent()).getChildren().stream()
                            .map(node -> {
                                node.opacityProperty().setValue(0.4);
                                return null;
                            })
                            .collect(Collectors.toList());

                    this.opacityProperty().setValue(1.0);
                }
        );
    }

    private <T> void openEditGroupModal() {
        final Stage addGroupModal = new Stage();
        Model model = Model.getInstance();
        FXMLLoader modalLoader = new FXMLLoader(getClass().getResource("/views/editGroupView.fxml"), model.getBundle());
        try {
            EditGroupModalController controller = new EditGroupModalController(this.group, (MainController) this.parentController);
            modalLoader.setController(controller);
            Parent root = modalLoader.load();
            addGroupModal.setScene(new Scene(root));
            addGroupModal.initOwner(parentStage);
            addGroupModal.initModality(Modality.WINDOW_MODAL);
            addGroupModal.initStyle(StageStyle.UNDECORATED);
            addGroupModal.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
