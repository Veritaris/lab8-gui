package client.Utils;

import client.Client.Model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Utils {

    public static Stage switchStage(Stage stage, String stageName) throws IOException {
        String viewPath = "loginView";
        String title = "Login";

        switch (stageName) {
            case "selectServer":
                viewPath = "selectServerView";
                title = "Select server";
                break;

            case "login":
                viewPath = "loginView";
                title = "Login";
                break;

            case "register":
                viewPath = "registerView";
                title = "Register";
                break;

            case "main":
                viewPath = "mainView";
                title = "Lab client by Liza";
                break;
        }
        stage.close();

        Model model = Model.getInstance();
        FXMLLoader fxmlLoader = new FXMLLoader(Utils.class.getResource(String.format("/views/%s.fxml", viewPath)), model.getBundle());

        Parent root = fxmlLoader.load();

        Stage newStage = new Stage();
        newStage.initStyle(StageStyle.UNDECORATED);
        newStage.setTitle(title);
        newStage.setScene(new Scene(root));
        return newStage;
    }
}




