package client.Client;

import dependencies.Collection.StudyGroup;
import dependencies.UserAuthorization.*;
import dependencies.CommandManager.*;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.*;

@SuppressWarnings({"FieldCanBeLocal"})
public class Model {
    public InetAddress serverAddress;
    private DatagramSocket socket;
    private String serverHost = "";
    private int serverPort = 0;

    private ResourceBundle bundle;

    public Receiver messageReceiver;
    public Sender messageSender;
    public Session session;

    private ScriptReader scriptReader;
    private CommandObject commandObject;

    private final static Model INSTANCE = new Model();

    private ArrayList<StudyGroup> groups = new ArrayList<>();

    public boolean isAvailable;
    private ExecutorService executorService;

    private Listener listener;

    private Model() {
        session = new Session();
    }

    public void startClient(String host, int port) throws UnknownHostException, SocketException {
        this.serverHost = host;
        this.serverPort = port;
        this.serverAddress = InetAddress.getByName(this.serverHost);
        this.socket = new DatagramSocket();

        this.messageSender = new Sender(this.socket, this.serverPort);
        this.messageReceiver = new Receiver(this.socket, this.serverAddress, this.serverPort);

        this.executorService = Executors.newFixedThreadPool(1);

        this.scriptReader = new ScriptReader(this.messageReceiver, this.messageSender, this.serverAddress);
    }

    public static Model getInstance() {
        return INSTANCE;
    }

    public Listener connectToServer() {
        Listener listener = new Listener(this.socket, this.serverAddress, this.serverPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(listener);
        executorService.shutdown();

        this.messageSender.sendMessage(new CommandObject("connect"), serverAddress);
        return listener;
    }

    public void setGroups(ArrayList<StudyGroup> groups) {
        this.groups.clear();
        this.groups = groups;
    }

    public ArrayList<StudyGroup> getGroups() {
        return this.groups;
    }

    public Listener login(String username, String rawPassword) {
        Listener listener = new Listener(this.socket, this.serverAddress, this.serverPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(listener);
        executorService.shutdown();

        commandObject = new CommandObject("login");
        commandObject.setSender(new User(username, rawPassword));
        session.setUser(new User(username, rawPassword));
        this.messageSender.sendMessage(commandObject, serverAddress);
        return listener;
    }

    public Listener register(String username, String rawPassword) {
        Listener listener = new Listener(this.socket, this.serverAddress, this.serverPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(listener);
        executorService.shutdown();

        commandObject = new CommandObject("register");
        commandObject.setSender(new User(username, rawPassword));

        this.messageSender.sendMessage(commandObject, serverAddress);
        return listener;
    }

    public Listener logout() {
        Listener listener = new Listener(this.socket, this.serverAddress, this.serverPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(listener);
        executorService.shutdown();

        commandObject = new CommandObject("logout");
        commandObject.setSender(session.getUser());
        this.session.clear();
        this.messageSender.sendMessage(commandObject, serverAddress);
        return listener;
    }

    public Listener addGroup(StudyGroup studyGroup) {
        Listener listener = new Listener(this.socket, this.serverAddress, this.serverPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(listener);
        executorService.shutdown();

        commandObject = new CommandObject("add", studyGroup);
        commandObject.setSender(session.getUser());

        this.messageSender.sendMessage(commandObject, serverAddress);
        return listener;
    }

    public Listener editGroup(StudyGroup studyGroup) {
        Listener listener = new Listener(this.socket, this.serverAddress, this.serverPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(listener);
        executorService.shutdown();

        commandObject = new CommandObject("update", studyGroup);
        commandObject.setSender(session.getUser());

        this.messageSender.sendMessage(commandObject, serverAddress);
        return listener;
    }

    public Listener executeScript(File scriptFile) {
        Model model = Model.getInstance();
        Listener listener = new Listener(this.socket, this.serverAddress, this.serverPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(listener);
        executorService.shutdown();
        this.scriptReader.executeScript(scriptFile, model.session.getUser());
        return listener;
    }

    public Listener getListener() {
        return this.listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static class doRemoveGroup implements Callable<Listener> {
        private final Long groupToRemoveID;
        private CommandObject commandObject;

        public doRemoveGroup(Long groupToRemoveID) {
            this.groupToRemoveID = groupToRemoveID;
        }

        @Override
        public Listener call() {
            Model model = Model.getInstance();
            Listener listener = new Listener(model.socket, model.serverAddress, model.serverPort);
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(listener);
            executorService.shutdown();

            commandObject = new CommandObject("remove_by_id", this.groupToRemoveID);
            commandObject.setSender(model.session.getUser());

            model.messageSender.sendMessage(commandObject, model.serverAddress);
            return listener;
        }
    }

    public Callable<Listener> removeAllGroups = () -> {
        Listener listener = new Listener(this.socket, this.serverAddress, this.serverPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(listener);
        executorService.shutdown();

        commandObject = new CommandObject("clear");
        commandObject.setSender(session.getUser());

        this.messageSender.sendMessage(commandObject, serverAddress);
        return listener;
    };

    public Listener retrieveAllGroups() {
        Listener listener = new Listener(this.socket, this.serverAddress, this.serverPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(listener);
        executorService.shutdown();

        commandObject = new CommandObject("show");
        commandObject.setSender(session.getUser());

        this.messageSender.sendMessage(commandObject, serverAddress);
        return listener;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }
}
