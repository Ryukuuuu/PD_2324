package server;

import data.ClientData;
import data.Message;
import data.MessageTypes;
import testdatabase.TestDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class clientHandlingThread implements Runnable{

    private Socket clientSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private TestDatabase testDatabase;
    private boolean keepRunning = true;

    public clientHandlingThread(Socket clientSocket,TestDatabase testDatabase){
        this.clientSocket = clientSocket;
        this.testDatabase = testDatabase;
        try{
            ois = new ObjectInputStream(clientSocket.getInputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("IOE exception [clientHandlingThread]");
        }
    }



    @Override
    public void run(){
        Message message;
        while(keepRunning){
            try {
                message = (Message)ois.readObject();
            }catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Message handleMessage(Message messageReceived){

        switch (messageReceived.getType()){
            case LOGIN -> {
                ClientData clientData = testDatabase.getClient(messageReceived.getClientData().getEmail(),messageReceived.getClientData().getPassword());
                if(clientData != null){
                    return new Message(MessageTypes.LOGGED_IN,clientData);
                }
            }
            case SIGNING -> {
                if(testDatabase.addNewEntryToClients(messageReceived.getClientData())){
                    return new Message(MessageTypes.ACC_CREATED,messageReceived.getClientData());
                }
            }
            case EDIT_LOG_INFO -> {}
            default -> {
                return new Message(MessageTypes.FAILED);
            }
        }
        return new Message(MessageTypes.FAILED);
    }
}
