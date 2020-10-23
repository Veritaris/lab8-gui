package client.Controllers;

import client.Client.Listener;
import client.Client.Model;
import client.Client.ServerListener;
import client.Utils.I18N;
import client.Utils.Utils;
import dependencies.Collection.Person;
import dependencies.Collection.Semester;
import dependencies.Collection.StudyGroup;
import dependencies.CommandManager.CommandObject;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@SuppressWarnings("DuplicatedCode")
public class MainController extends Controller implements Initializable {
    @FXML public AnchorPane userInfoBlock;
    public AnchorPane collectionActionsBlock;
    public ToggleButton visualizationSwitch;
    public AnchorPane titleBar;

    public ChoiceBox<String> groupSemesterFilter;
    public TextField studyGroupNameLookupField;

    private File selectedFile;
    private Model model;
    public ChoiceBox<String> languageSelector;
    private Stage visualisationModal;

    private Listener serverListener;

    public Button changePasswordButton;
    public Button selectScriptButton;
    public Button addGroupButton;

    public Label collectionActionsLabel;
    public Text currentLanguageLabel;
    public Text currentUsernameLabel;
    public Label currentUserLabel;
    public Button executeScriptButton;
    public Text currentUserName;
    public Text scriptFileName;
    public Button logoutButton;
    public Button deleteSelectedGroupButton;
    public Button deleteAllGroupsButton;
    public Button editGroupButton;

    private double x, y;

    public ObservableList<StudyGroup> studyGroups = FXCollections.observableArrayList();

    public TableView<StudyGroup> groupsTable;
    public TableColumn<StudyGroup, Long> groupID;
    public TableColumn<StudyGroup, String> groupName;
    public TableColumn<StudyGroup, String> groupSemester;
    public TableColumn<StudyGroup, String> groupCoordinates;
    public TableColumn<StudyGroup, String> groupStudentsAmount;
    public TableColumn<StudyGroup, Person> groupAdminName;
    public TableColumn<StudyGroup, Integer> groupStudentsAmountToExpel;
    public TableColumn<StudyGroup, Integer> groupStudentsExpelledAmount;
    public TableColumn<StudyGroup, LocalDate> groupCreationDate;
    public TableColumn<StudyGroup, Boolean> actionColumn = new TableColumn<>("");


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = Model.getInstance();

        collectionActionsLabel.textProperty().bind(I18N.createStringBinding("mainStage.collectionsActionsLabel"));
        currentUsernameLabel.textProperty().bind(I18N.createStringBinding("mainStage.currentUsernameLabel"));
        currentLanguageLabel.textProperty().bind(I18N.createStringBinding("mainStage.currentLocale"));

        executeScriptButton.textProperty().bind(I18N.createStringBinding("mainStage.executeScriptButton"));
        currentUserLabel.textProperty().bind(I18N.createStringBinding("mainStage.currentUserLabel"));
        logoutButton.textProperty().bind(I18N.createStringBinding("mainStage.logoutButton"));
        addGroupButton.textProperty().bind(I18N.createStringBinding("mainStage.addGroupButton"));
        changePasswordButton.textProperty().bind(I18N.createStringBinding("mainStage.changePasswordButton"));
        deleteSelectedGroupButton.textProperty().bind(I18N.createStringBinding("mainStage.deleteSelectedGroupsButton"));
        deleteAllGroupsButton.textProperty().bind(I18N.createStringBinding("mainStage.deleteAllGroupsButton"));
        editGroupButton.textProperty().bind(I18N.createStringBinding("mainStage.editGroupButton"));
        selectScriptButton.textProperty().bind(I18N.createStringBinding("mainStage.chooseScriptButton"));

        groupName.textProperty().bind(I18N.createStringBinding("mainStage.table.groupName"));
        groupSemester.textProperty().bind(I18N.createStringBinding("mainStage.table.semester"));
        groupCoordinates.textProperty().bind(I18N.createStringBinding("mainStage.table.coordinates"));
        groupStudentsAmount.textProperty().bind(I18N.createStringBinding("mainStage.table.studentsAmount"));
        groupAdminName.textProperty().bind(I18N.createStringBinding("mainStage.table.groupAdminName"));
        groupStudentsAmountToExpel.textProperty().bind(I18N.createStringBinding("mainStage.table.studentsToExpelAmount"));
        groupStudentsExpelledAmount.textProperty().bind(I18N.createStringBinding("mainStage.table.expelledStudentsAmount"));
        groupCreationDate.textProperty().bind(I18N.createStringBinding("mainStage.table.creationDate"));

        languageSelector.getItems().clear();
        languageSelector.getItems().addAll(
                "ru",
                "en",
                "es",
                "pt",
                "se"
        );
        groupSemesterFilter.getItems().clear();
        groupSemesterFilter.getItems().addAll(
                Semester.FIRST.getTittle(),
                Semester.SECOND.getTittle(),
                Semester.FOURTH.getTittle(),
                Semester.FIFTH.getTittle(),
                Semester.EIGHTH.getTittle(),
                ""
        );

        languageSelector.setValue(I18N.getLocale().toLanguageTag());
        languageSelector.setOnAction(
                (e) -> {
                    Model model = Model.getInstance();
                    I18N.setLocale(
                            new Locale(languageSelector.getValue())
                    );
                    model.setBundle(ResourceBundle.getBundle("bundles.locale", I18N.getLocale()));
                }
        );

        groupSemesterFilter.setOnAction(
                (e) -> {
                    String filter = groupSemesterFilter.getValue();
                    ArrayList<StudyGroup> filteredGroups;
                    if (!filter.equals("")) {
                        filteredGroups = (ArrayList<StudyGroup>) model.getGroups().stream()
                                .filter(g -> filter.equals(g.getSemesterObject().getTittle()))
                                .collect(Collectors.toList());
                    } else {
                        filteredGroups = model.getGroups();
                    }
                    updateTableView(filteredGroups);
                }
        );

        studyGroupNameLookupField.textProperty().addListener(
                (observable, oldValue, filter) -> {
                    ArrayList<StudyGroup> filteredGroups = (ArrayList<StudyGroup>) model.getGroups().stream()
                            .filter(g -> g.getName().startsWith(filter))
                            .collect(Collectors.toList());
                    updateTableView(filteredGroups);
                }
        );

        currentUserName.setText(model.session.getUser().getUsername());
        model.retrieveAllGroups();
        actionColumn.setSortable(false);

        groupsTable.setRowFactory( table -> {
                    TableRow<StudyGroup> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        editGroupButton.setDisable(false);
                    });
                    return row;
                }

        );

        actionColumn.setCellValueFactory(
                param -> new SimpleBooleanProperty(param.getValue() != null)
        );
        actionColumn.setCellFactory(
                param -> new AddRemoveGroupButtonCell((Stage) currentUserName.getScene().getWindow(), groupsTable)
        );

        groupsTable.getColumns().add(actionColumn);

        Listener listener = model.retrieveAllGroups();
        listener.setOnSucceeded(
                (e) -> {
                    CommandObject response = listener.getReceivedCommandObject();
                    String status = response.getBody().get("status");
                    String message = response.getBody().get("message");

                    switch (status) {
                        case "200":
                            model.setGroups(response.getStudyGroups());
                            studyGroups.addAll(response.getStudyGroups());
                            displayStudyGroups(studyGroups);
                            break;
                        case "404":
                            break;
                    }
                }
        );

        ServerListener serverListener = new ServerListener(this);
        serverListener.start();
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
    public void displayStudyGroups(ObservableList<StudyGroup> studyGroupObservableValue) {
        groupID.setCellValueFactory(new PropertyValueFactory<>("id"));
        groupName.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        groupCoordinates.setCellValueFactory(new PropertyValueFactory<>("coordinates"));
        groupStudentsAmount.setCellValueFactory(new PropertyValueFactory<>("studentsCount"));
        groupAdminName.setCellValueFactory(new PropertyValueFactory<>("groupAdmin"));
        groupStudentsAmountToExpel.setCellValueFactory(new PropertyValueFactory<>("toExpelAmount"));
        groupStudentsExpelledAmount.setCellValueFactory(new PropertyValueFactory<>("expelledStudentsAmount"));
        groupCreationDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

        for (StudyGroup group: studyGroupObservableValue) {
            groupsTable.getItems().add(group);
        }
    }

    @FXML
    private void handleSelectScriptAction() {
        Stage stage = (Stage) scriptFileName.getScene().getWindow();
        scriptFileName.setText("");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt")
        );
        selectedFile = fileChooser.showOpenDialog(stage);
        try {
            scriptFileName.setText(selectedFile.getName());
            executeScriptButton.setDisable(false);
        } catch (NullPointerException ignored) {

        }
    }

    @FXML
    private void handleExecuteScriptAction(ActionEvent event) {
        Listener serverListener = model.executeScript(selectedFile);
        serverListener.setOnSucceeded(
                (e) -> {
                    CommandObject response = serverListener.getReceivedCommandObject();
                    String status = response.getBody().get("status");
                    String message = response.getBody().get("message");
                    switch (status) {
                        case "200":
                        case "201":
                            model.setGroups(response.getStudyGroups());
                            this.updateTableView(response.getStudyGroups());
                            break;
                        case "403":
                            openDenyDialog();
                            break;
                        case "404":
                            break;
                    }
                }
        );
    }

    @FXML
    private void handleLogoutAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) scriptFileName.getScene().getWindow();
        model.logout();
        Utils.switchStage(stage, "login").show();
    }

    @FXML
    private void handleAddGroupAction(ActionEvent event) {
        Button eventSourceButton = (Button) event.getSource();
        Stage stage = (Stage) eventSourceButton.getScene().getWindow();
        openAddGroupModal(stage, blank);
    }

    @FXML
    private void handleEditGroupAction(ActionEvent event) {
        Button eventSourceButton = (Button) event.getSource();
        Stage stage = (Stage) eventSourceButton.getScene().getWindow();
        openEditGroupModal(stage);
    }

    @FXML
    private void handleDeleteAllGroupsAction(ActionEvent event) throws Exception {
        Button eventSourceButton = (Button) event.getSource();
        Stage stage = (Stage) eventSourceButton.getScene().getWindow();
        openConfirmDeleteModal(stage, submitDeleteAllGroups);
    }

    @FXML
    public void handleToggleVisualizeSwitchAction(ActionEvent event) {
        ToggleButton eventSourceButton = (ToggleButton) event.getSource();
        Stage stage = (Stage) eventSourceButton.getScene().getWindow();

        boolean state = visualizationSwitch.isSelected();

        if (state) {
            visualisationModal = openVisualizationModal(stage);
            assert visualisationModal != null;
            visualisationModal.show();
        } else {
            visualisationModal.close();
        }
    }

    @FXML
    private void handleSubmitDeleteGroupsAction(ActionEvent event) {

    }

    @FXML
    public void handleDeleteSelectedGroupsAction(ActionEvent event) {}

    private final Callable<Integer> blank = () -> 0;

    private final Callable<Model.doRemoveGroup> submitDeleteGroup = () -> {
        Long groupID = this.groupsTable.getSelectionModel().getSelectedItem().getId();
        this.groupsTable.getSelectionModel().clearSelection();

        Listener serverListener = (new Model.doRemoveGroup(groupID)).call();
        serverListener.setOnSucceeded(
                (e) -> {
                    CommandObject response = serverListener.getReceivedCommandObject();
                    String status = response.getBody().get("status");
                    String message = response.getBody().get("message");
                    switch (status) {
                        case "200":
                        case "201":
                            model.setGroups(response.getStudyGroups());
                            this.updateTableView(response.getStudyGroups());
                            break;
                        case "403":
                            openDenyDialog();
                            break;
                        case "404":
                            break;
                    }
                }
        );
        return null;
    };

    private final Callable<Listener> submitDeleteAllGroups = () -> {
        Listener listener = model.removeAllGroups.call();
        listener.setOnSucceeded(
                (e) -> {
                    CommandObject response = listener.getReceivedCommandObject();
                    String status = response.getBody().get("status");
                    String message = response.getBody().get("message");
                    switch (status) {
                        case "200":
                        case "201":
                            model.setGroups(response.getStudyGroups());
                            this.updateTableView(response.getStudyGroups());
                            break;
                        case "403":
                            openDenyDialog();
                            break;
                        case "404":
                            break;
                    }

                }
        );
        return null;
    };

    @Override
    public void updateTableView(ArrayList<StudyGroup> groups) {
        groupsTable.setItems(FXCollections.observableArrayList(groups));
        groupsTable.refresh();
    }

    private class AddRemoveGroupButtonCell extends TableCell<StudyGroup, Boolean> {
        final Button removeGroupButton = new Button("Delete");
        final StackPane paddedButton = new StackPane();
        final DoubleProperty buttonY = new SimpleDoubleProperty();

        AddRemoveGroupButtonCell(final Stage stage, final TableView<StudyGroup> table) {
            paddedButton.setPadding(new Insets(3));
            removeGroupButton.textProperty().bind(I18N.createStringBinding("mainStage.table.deleteSpecificGroup"));
            paddedButton.getChildren().add(removeGroupButton);
            removeGroupButton.setOnMousePressed(mouseEvent -> buttonY.set(mouseEvent.getScreenY()));
            removeGroupButton.setOnAction(actionEvent -> {
                try {
                    openConfirmDeleteModal(stage, submitDeleteGroup);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                table.getSelectionModel().select(getTableRow().getIndex());
            });
        }
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }

    private <T> void openEditGroupModal(Stage parent) {
        final Stage addGroupModal = new Stage();

        FXMLLoader modalLoader = new FXMLLoader(getClass().getResource("/views/editGroupView.fxml"), model.getBundle());
        try {
            EditGroupModalController controller = new EditGroupModalController(this.groupsTable.getSelectionModel().getSelectedItem(), this);
            modalLoader.setController(controller);
            Parent root = modalLoader.load();
            addGroupModal.setScene(new Scene(root));
            addGroupModal.initOwner(parent);
            addGroupModal.initModality(Modality.WINDOW_MODAL);
            addGroupModal.initStyle(StageStyle.UNDECORATED);
            addGroupModal.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> void openAddGroupModal(Stage parent, Callable<T> action) {
        final Stage addGroupModal = new Stage();

        FXMLLoader modalLoader = new FXMLLoader(getClass().getResource("/views/addGroupView.fxml"), model.getBundle());
        try {
            AddGroupModalController controller = new AddGroupModalController(action, parent, this);
            modalLoader.setController(controller);
            Parent root = modalLoader.load();
            addGroupModal.setScene(new Scene(root));
            addGroupModal.initOwner(parent);
            addGroupModal.initModality(Modality.WINDOW_MODAL);
            addGroupModal.initStyle(StageStyle.UNDECORATED);
            addGroupModal.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> void openConfirmDeleteModal(Stage parent, Callable<T> action) {
        final Stage confirmModal = new Stage();

        FXMLLoader modalLoader = new FXMLLoader(getClass().getResource("/views/confirmModal.fxml"), model.getBundle());
        try {
            ConfirmModalController controller = new ConfirmModalController(action);
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

    private <T> Stage openVisualizationModal(Stage parent) {
        final Stage visualisationModal = new Stage();

        FXMLLoader modalLoader = new FXMLLoader(getClass().getResource("/views/visualizationView.fxml"), model.getBundle());
        try {
            GroupsVisualizationController controller = new GroupsVisualizationController(parent ,this);
            modalLoader.setController(controller);
            Parent root = modalLoader.load();
            visualisationModal.initStyle(StageStyle.UNDECORATED);

            visualisationModal.setX(parent.getX() + parent.getWidth() - parent.getWidth() / 2);
            visualisationModal.setY(parent.getY() + parent.getHeight() / 4);

            visualisationModal.setScene(new Scene(root));
            controller.setMainStage(visualisationModal);
            return visualisationModal;
        } catch (IOException e) {
            e.printStackTrace();
            return (Stage) null;
        }
    }

    private void openDenyDialog() {
        Model model = Model.getInstance();
        final Stage confirmModal = new Stage();

        FXMLLoader modalLoader = new FXMLLoader(getClass().getResource("/views/deniedModal.fxml"), model.getBundle());
        try {
            DenyModalController controller = new DenyModalController();
            modalLoader.setController(controller);
            Parent root = modalLoader.load();
            confirmModal.setScene(new Scene(root));
            confirmModal.initModality(Modality.WINDOW_MODAL);
            confirmModal.initStyle(StageStyle.UNDECORATED);
            confirmModal.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
