package client.model;


import client.thread.ListenToServerThread;
import data.ClientData;
import data.Message;
import data.MessageTypes;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientManager {

    private ClientData clientData;
    private Socket clientSocket = null;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Thread listenToServer;
    private final String serverIp;
    private final int serverPort;

    private static final int TIMEOUT_LOGIN = 10000;

    public ClientManager(String ipServer,String portServer){
        this.clientData = new ClientData();
        this.serverPort = Integer.parseInt(portServer);
        this.serverIp = ipServer;
    }

    //Connects to server and creates ObjectStreams
    //Returns true if the connection is created
    //False if there's a problem
    private boolean connectSocket(){
        try{
            clientSocket = new Socket(InetAddress.getByName(serverIp),serverPort);
            getSocketStreams();
            initializeListenningThread();
        }catch (IOException e){
            System.out.println("Error creating socket");
        }
        return clientSocket == null;
    }

    private boolean getSocketStreams(){
        if(clientSocket == null){
            return false;
        }
        try {
            ois = new ObjectInputStream(clientSocket.getInputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void initializeListenningThread(){
        listenToServer = new Thread(new ListenToServerThread(ois));
        listenToServer.start();
    }

    public boolean connectAUser(MessageTypes messageType,String... data){
        Message connectUserMessage;

        if(data.length < 2){
            throw new IllegalArgumentException("Not enough data");
        }

        if(!connectSocket()){
            return false;
        }

        switch (messageType){
            case LOGIN -> connectUserMessage = new Message(messageType,new ClientData(data[0],data[1]));
            case SIGNIN -> connectUserMessage = new Message(messageType,new ClientData(data[0], data[1],data[2],data[3]));
            default -> {return false;}
        }

        try {
            oos.writeObject(connectUserMessage);
            oos.flush();
            System.out.println("Object sent");
        }catch (IOException e){
            System.out.println("IOException[ClientManager]");
            return false;
        }
        return true;
    }


    //Returns true if logIn was successfull
    //False otherwise
    public boolean login(String email,String password){

        connectAUser(MessageTypes.LOGIN,email,password);

        //TO DO

        return true;
    }

    public boolean createNewClient(String name,String id,String email,String password){
        try{
            Message createNewClientMessage = new Message(MessageTypes.SIGNIN,new ClientData(name,id,email,password));
            oos.writeObject(createNewClientMessage);
            oos.flush();
        }catch (SocketTimeoutException e){
            System.out.println("SocketTimeoutException[ClientManager.logInClient]");
        }catch (IOException e){
            System.out.println("IOException[ClientManager.logInClient]");
        }
        return true;
    }


    public ClientData getClientData() {
        return clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public boolean isClientAdmin(){return clientData.isAdmin();}
}
