package client.Controllers;

import client.Client.Listener;
import client.Client.Model;
import client.Utils.StagesEnum;
import client.Utils.Utils;
import dependencies.CommandManager.CommandObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public ProgressIndicator progressIndicator;
    public Text errorMessageBox;
    public PasswordField passwordField;
    public TextField usernameField;
    public Button backButton;
    public Button loginButton;
    public Button switchToRegisterButton;
    public PasswordField passwordRepeatField;
    public AnchorPane titleBar;
    private double x, y;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.hide();
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.close();
        System.exit(0);
    }


    @FXML
    private void handleLoginAction() {
        Model model = Model.getInstance();
        progressIndicator.setVisible(false);
        Stage stage = (Stage) loginButton.getScene().getWindow();

        if ((!usernameField.getText().equals("")) && (!passwordField.getText().equals(""))) {
            progressIndicator.setVisible(true);
            Listener listener = model.login(usernameField.getText().trim(), passwordField.getText().trim());

            listener.setOnSucceeded(
                    (event) -> {
                        CommandObject response = listener.getReceivedCommandObject();
                        String status = response.getBody().get("status");
                        String message = response.getBody().get("message");

                        try {
                            switch (status) {
                                case "200":
                                    model.session.getUser().setLoggedIn(true);
                                    Utils.switchStage(stage, StagesEnum.MAIN.getStageName()).show();
                                    progressIndicator.setVisible(false);
                                    break;

                                case "504":
                                    model.session.clear();
                                    errorMessageBox.setText("Server is not available");
                                    progressIndicator.setVisible(false);
                                    break;

                                case "401":
                                case "403":
                                    model.session.clear();
                                    errorMessageBox.setText(message);
                                    progressIndicator.setVisible(false);
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            listener.setOnFailed(
                    (event) -> {
                        errorMessageBox.setText("Something went wrong, sorry");
                        progressIndicator.setVisible(false);
                    }
            );
        } else {
            errorMessageBox.setText("Host and port fields cannot be empty");
        }
    }

    @FXML
    private void handleSwitchToRegisterStageAction() throws IOException {
        Model model = Model.getInstance();
        Stage stage = (Stage) switchToRegisterButton.getScene().getWindow();
        Utils.switchStage(stage, StagesEnum.REGISTER.getStageName()).show();
    }

    @FXML
    private void handleBackToLoginAction() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Utils.class.getResource("/views/loginView.fxml"));
        Parent root = fxmlLoader.load();
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleRegisterAction() {
        Model model = Model.getInstance();
        progressIndicator.setVisible(false);
        Stage stage = (Stage) backButton.getScene().getWindow();

        if ((!usernameField.getText().equals("")) && (!passwordField.getText().equals("")) && (!passwordRepeatField.getText().equals(""))) {
            if (passwordField.getText().equals(passwordRepeatField.getText())) {
                progressIndicator.setVisible(true);
                Listener listener = model.getListener();
                model.register(usernameField.getText().trim(), passwordField.getText().trim());

                listener.setOnSucceeded(
                        (event) -> {
                            CommandObject response = listener.getReceivedCommandObject();
                            String status = response.getBody().get("status");
                            String message = response.getBody().get("message");

                            try {
                                switch (status) {
                                    case "200":
                                        progressIndicator.setVisible(false);
                                        model.session.setUser(response.getSender());
                                        Utils.switchStage(stage, StagesEnum.MAIN.getStageName()).show();
                                        break;

                                    case "504":
                                        model.session.clear();
                                        errorMessageBox.setText("Server is not available");
                                        progressIndicator.setVisible(false);
                                        break;

                                    case "400":
                                    case "401":
                                    case "403":
                                        model.session.clear();
                                        errorMessageBox.setText(message);
                                        progressIndicator.setVisible(false);
                                        break;

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                );
                listener.setOnFailed(
                        (event) -> {
                            errorMessageBox.setText("Something went wrong, sorry");
                            progressIndicator.setVisible(false);
                        }
                );
            } else {
                errorMessageBox.setText("Passwords are not equals");
            }
        } else {
            errorMessageBox.setText("Host and port fields cannot be empty");
        }
    }
}
