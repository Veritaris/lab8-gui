package client.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class ConfirmModalController<T> implements Initializable {

    private Callable<T> action;

    public ConfirmModalController(Callable<T> action) {
        this.action = action;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void handleConfirmAction(ActionEvent event) throws Exception {
        this.action.call();
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void handleCancelAction(ActionEvent event) {
        Button source = (Button) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
