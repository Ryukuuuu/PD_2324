package server.thread;

import data.ClientData;
import data.Message;
import data.MessageTypes;
import testdatabase.TestDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NewUserConnection implements Runnable{
    Socket toClientSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private TestDatabase testDatabase;

    public NewUserConnection(Socket toClientSocket, TestDatabase testDatabase) {
        this.toClientSocket = toClientSocket;
        this.testDatabase = testDatabase;
        try{
            ois = new ObjectInputStream(toClientSocket.getInputStream());
            oos = new ObjectOutputStream(toClientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("<ERRO> Não foi possível obter as streams associadas a um Socket conectado ao cliente.");
        }
    }

    private Message handleRequestMessage(Message messageReceived){
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

    @Override
    public void run() {
        Message requestMessage, responseMessage;
        while(true){
            try {
                requestMessage = (Message)ois.readObject();
                System.out.println("<PEDIDO> " + requestMessage.getType().name() + "de <" + requestMessage.getClientData().getEmail() + ">" );

                responseMessage = handleRequestMessage(requestMessage);
                System.out.println("<RESPOSTA> " + requestMessage.getType().name() + "de <" + requestMessage.getClientData().getEmail() + ">>> " + responseMessage.getType().name());

                oos.writeObject(responseMessage);
                oos.flush();

            }catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
