package client.Client;

import client.Controllers.Controller;
import dependencies.CommandManager.CommandObject;
import dependencies.CommandManager.CommandObjectCreator;
import javafx.concurrent.Task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class Listener extends Task<CommandObject> {

    private final byte[] message = new byte[8 * 1024];
    private Controller controller;
    private CommandObject receivedCommandObject;
    private InetAddress serverAddress;
    private ByteArrayInputStream bais;
    private ObjectInputStream ois;
    public DatagramPacket packet;
    public DatagramSocket socket;
    private boolean isDaemon;
    private int serverPort;

    public Listener(DatagramSocket datagramSocket, InetAddress serverAddress, int serverPort) {
        this.socket = datagramSocket;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public Listener(Controller controller) {
        this.controller = controller;
    }

    public void startListen() {

    }

    public CommandObject receiveMessage() {
        try {
            this.socket.setSoTimeout(15000);
            packet = new DatagramPacket(message, message.length, this.serverAddress, this.serverPort);
            this.socket.receive(packet);
            bais = new ByteArrayInputStream(message);
            ois = new ObjectInputStream(bais);
            return (CommandObject) ois.readObject();
        } catch (SocketTimeoutException | SocketException ignored) {
            return CommandObjectCreator.createErrorObject("504", "Server not responding");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return CommandObjectCreator.createErrorObject("400", "Something went wrong with received object");
        }
    }

    @Override
    protected CommandObject call() {
        this.receivedCommandObject = receiveMessage();
        updateMessage(this.receivedCommandObject.getName());
        return this.receivedCommandObject;
    }

    public CommandObject getReceivedCommandObject() {
        return this.receivedCommandObject;
    }

    public boolean isDaemon() {
        return this.isDaemon;
    }

    public void setDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
    }
}
