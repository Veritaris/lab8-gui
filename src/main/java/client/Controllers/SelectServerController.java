package client.Controllers;

import client.Client.Listener;
import client.Client.Model;
import client.Utils.I18N;
import client.Utils.StagesEnum;
import client.Utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

public class SelectServerController implements Initializable {

    public ProgressIndicator progressIndicator;
    public ChoiceBox<String> languageSelector;
    public TextField serverPortField;
    public TextField serverHostField;
    public Text errorMessageBox;
    public Label mainLabel;
    public Button loginButton;
    private double x, y;
    private AnchorPane titleBar;
    private Stage mainStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mainLabel.textProperty().bind(I18N.createStringBinding("selectServerStage.mainLabel"));
        serverHostField.promptTextProperty().bind(I18N.createStringBinding("selectServerStage.addressPlaceholder"));
        serverPortField.promptTextProperty().bind(I18N.createStringBinding("selectServerStage.portPlaceholder"));
        loginButton.textProperty().bind(I18N.createStringBinding("selectServerStage.connectButton"));

        languageSelector.getItems().clear();
        languageSelector.getItems().addAll(
                "ru",
                "en",
                "es",
                "pt",
                "se"
        );
        languageSelector.setValue("en");
        languageSelector.setOnAction(
                (e) -> {
                    Model model = Model.getInstance();
                    I18N.setLocale(
                            new Locale(languageSelector.getValue())
                    );
                    model.setBundle(ResourceBundle.getBundle("bundles.locale", I18N.getLocale()));
                }
        );
        mainLabel.sceneProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        newValue.windowProperty().addListener(
                                (observable1, oldValue1, newValue1) -> {
                                    mainStage = (Stage) newValue.getWindow();
                                }
                        );
                    }
                }
        );
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
    public void minimizeWindow() {
        Stage stage = (Stage) mainStage.getScene().getWindow();
        stage.hide();
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) mainStage.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    @FXML
    private void handleConnectToServerAction() throws IOException {
        Model model = Model.getInstance();
        progressIndicator.setVisible(false);
        errorMessageBox.setText(null);
        Stage stage = (Stage) serverHostField.getScene().getWindow();

        if ((!serverPortField.getText().equals("")) && (!serverHostField.getText().equals(""))) {
            try {
                model.startClient(serverHostField.getText(), Integer.parseInt(serverPortField.getText()));

                Listener listener = model.connectToServer();
                progressIndicator.setVisible(true);
                listener.setOnSucceeded(
                        (event) -> {
                            model.isAvailable = listener.getReceivedCommandObject().getName().equals("connected");
                            if (model.isAvailable) {
                                try {
                                    Utils.switchStage(stage, StagesEnum.LOGIN.getStageName()).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                errorMessageBox.setText("Server is not available");
                            }
                            progressIndicator.setVisible(false);
                        }
                );
                listener.setOnFailed(
                        (event) -> {
                            progressIndicator.setVisible(false);
                        }
                );
            } catch (NumberFormatException e) {
                errorMessageBox.setText("Only numbers are allowed for port");
            }
        } else {
            errorMessageBox.setText("Host and port fields cannot be empty");
        }
    }
}
