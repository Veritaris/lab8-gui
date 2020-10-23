package client.Client;


import client.ClientApp;
import dependencies.CommandManager.CommandObject;
import dependencies.CommandManager.CommandObjectCreator;
import javafx.concurrent.Task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.concurrent.Callable;

@SuppressWarnings("FieldCanBeLocal")
public class Receiver {
    private final byte[] message = new byte[8 * 1024];
    private CommandObject receivedCommandObject;
    private InetAddress serverAddress;
    private ByteArrayInputStream bais;
    private ObjectInputStream ois;
    public DatagramPacket packet;
    public DatagramSocket socket;
    private int serverPort;

    public Receiver(DatagramSocket datagramSocket, InetAddress serverAddress, int serverPort) {
        this.socket = datagramSocket;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public CommandObject receiveMessage() {
        try {
            this.socket.setSoTimeout(15000);
            packet = new DatagramPacket(message, message.length, this.serverAddress, this.serverPort);
            this.socket.receive(packet);
            bais = new ByteArrayInputStream(message);
            ois = new ObjectInputStream(bais);
            receivedCommandObject = (CommandObject) ois.readObject();
            return receivedCommandObject;
        } catch (SocketTimeoutException | SocketException ignored) {
            return CommandObjectCreator.createErrorObject("ServerNotResponding", "Server not responding");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return CommandObjectCreator.createErrorObject("ServerNotResponding", "Server not responding");
        }
    }

    Callable<CommandObject> handleMessage = () -> {
        try {
            this.socket.setSoTimeout(15000);
            packet = new DatagramPacket(message, message.length, serverAddress, serverPort);
            this.socket.receive(packet);
            bais = new ByteArrayInputStream(message);
            ois = new ObjectInputStream(bais);
            receivedCommandObject = (CommandObject) ois.readObject();
            return receivedCommandObject;
        } catch (SocketTimeoutException ignored) {
            return CommandObjectCreator.createErrorObject("504", "Server not responding");
        }
    };

//    Task<CommandObject> receiveMessage = new Task<CommandObject>() {
//        @Override
//        protected CommandObject call() throws Exception {
//            try {
//                socket.setSoTimeout(15000);
//                packet = new DatagramPacket(message, message.length, serverAddress, serverPort);
//                socket.receive(packet);
//                bais = new ByteArrayInputStream(message);
//                ois = new ObjectInputStream(bais);
//                receivedCommandObject = (CommandObject) ois.readObject();
//                updateMessage(receivedCommandObject.getMessage().get(0));
//                return receivedCommandObject;
//            } catch (SocketTimeoutException ignored) {
//                return CommandObjectCreator.createErrorObject("ServerNotResponding", "Server not responding");
//            }
//        }
//    };
}
