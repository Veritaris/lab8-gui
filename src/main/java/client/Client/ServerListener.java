package client.Client;

import client.Controllers.Controller;
import client.Controllers.MainController;
import dependencies.Collection.StudyGroup;
import dependencies.CommandManager.CommandObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.codec.digest.DigestUtils;

public class ServerListener extends Thread {
    private MainController controller;
    private final Short SLEEP = 5000;
    private Model model = Model.getInstance();
    private CommandObject response;


    public ServerListener(MainController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        while (true) {
            CommandObject commandObject = new CommandObject("show");
            commandObject.setSender(model.session.getUser());
            model.messageSender.sendMessage(commandObject, model.serverAddress);
            response = model.messageReceiver.receiveMessage();
            System.out.println(model.getGroups());
            System.out.println(response.getStudyGroups());
            System.out.println(response.getStudyGroups().equals(model.getGroups()));
            if (response.getStudyGroups() != null) {
                model.setGroups(response.getStudyGroups());
                ObservableList<StudyGroup> studyGroups = FXCollections.observableArrayList(response.getStudyGroups());
                this.controller.groupsTable.setItems(studyGroups);
                this.controller.groupsTable.refresh();
            }
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
