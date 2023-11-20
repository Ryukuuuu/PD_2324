package server.thread;

import data.ClientData;
import data.Message;
import data.MessageTypes;
import testdatabase.TestDatabase;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NewUserConnection implements Runnable{
    Socket toClientSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private TestDatabase testDatabase;
    private ClientData clientData;

    public NewUserConnection(Socket toClientSocket, TestDatabase testDatabase) {
        this.toClientSocket = toClientSocket;
        this.testDatabase = testDatabase;
        try{
            oos = new ObjectOutputStream(toClientSocket.getOutputStream());
            ois = new ObjectInputStream(toClientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("<ERRO> Nao foi possivel obter as streams associadas a um Socket conectado ao cliente.");
        }
    }

    private Message handleRequestMessage(Message messageReceived){
        switch (messageReceived.getType()){
            case LOGIN -> {
                ClientData clientData = testDatabase.getClient(messageReceived.getClientData().getEmail(),messageReceived.getClientData().getPassword());
                if(clientData != null){
                    this.clientData = clientData;
                    return new Message(MessageTypes.LOGGED_IN,clientData);
                }
            }
            case SIGNING -> {
                if(testDatabase.addNewEntryToClients(messageReceived.getClientData())){
                    this.clientData = messageReceived.getClientData();
                    return new Message(MessageTypes.ACC_CREATED,messageReceived.getClientData());
                }
            }
            case EDIT_LOG_INFO -> {
                ClientData data = testDatabase.editUserInfo(messageReceived.getClientData());
                if(data != null){
                    this.clientData = data;
                    return new Message(MessageTypes.EDIT_LOG_INFO,clientData);
                }
            }
            case SUBMIT_CODE -> {
                if(testDatabase.checkIfCodeExists(messageReceived.getEventCode()))
                    return new Message(MessageTypes.SUBMIT_CODE);
            }
            case LOGOUT -> {return new Message(MessageTypes.LOGOUT);}
            default -> {
                return new Message(MessageTypes.FAILED);
            }
        }
        return new Message(MessageTypes.FAILED);
    }

    @Override
    public void run() {
        Message requestMessage, responseMessage;
        while(true){
            try {
                requestMessage = (Message) ois.readObject();
                try {
                    System.out.println("<PEDIDO> " + requestMessage.getType().name() + " de <" + requestMessage.getClientData().getEmail() + ">");
                } catch (NullPointerException e) {
                    System.out.println("<PEDIDO> " + requestMessage.getType().name() + " de <" + clientData.getEmail() + ">");
                }


                responseMessage = handleRequestMessage(requestMessage);
                System.out.println("<RESPOSTA> " + requestMessage.getType().name() + " de <" + clientData.getEmail() + ">>> " + responseMessage.getType().name());
                oos.writeObject(responseMessage);
                oos.flush();
            } catch (ClassNotFoundException | IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}
