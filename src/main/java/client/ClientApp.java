package client;

import client.Client.Model;
import client.Controllers.MainController;
import client.Controllers.SelectServerController;
import client.Utils.I18N;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Locale;
import java.util.ResourceBundle;


public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale locale = new Locale("en", "");
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.locale", locale);

        Model model = Model.getInstance();
        model.setBundle(bundle);
        primaryStage.titleProperty().bind(I18N.createStringBinding("selectServerStage.title"));
        primaryStage.initStyle(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/selectServerView.fxml"), bundle);
        SelectServerController controller = new SelectServerController();
        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/appIcon.png")));

        Platform.setImplicitExit(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
