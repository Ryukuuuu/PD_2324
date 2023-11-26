package server.thread;

import data.*;
import database.DatabaseConnection;
import server.MainServer;
import server.thread.multicast.SendHeartBeats;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class NewUserConnection implements Runnable{
    Socket toClientSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private DatabaseConnection dbConnection;
    private ClientData clientData;
    private UserConnectionsThread userConnectionsThread;
    private SendHeartBeats sendHeartBeats;
    private Boolean keepRunning;

    private MainServer mainDBService;

    public NewUserConnection(Socket toClientSocket, DatabaseConnection dbConnection, UserConnectionsThread userConnectionsThread, SendHeartBeats sendHeartBeats, MainServer mainDBService) {
        this.toClientSocket = toClientSocket;
        this.dbConnection = dbConnection;
        this.userConnectionsThread = userConnectionsThread;
        this.sendHeartBeats = sendHeartBeats;
        keepRunning = true;
        try{
            oos = new ObjectOutputStream(toClientSocket.getOutputStream());
            ois = new ObjectInputStream(toClientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("<ERRO> Nao foi possivel obter as streams associadas a um Socket conectado ao cliente.");
        }
        // ref
        this.mainDBService = mainDBService;
    }

    public synchronized void notifyEventUpdate(){
        try{
            oos.writeObject(new Message(MessageTypes.EVENT_UPDATE));
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void notifyClientUpdate(){
        try{
            oos.writeObject(new Message(MessageTypes.CLIENT_UPDATE));
            oos.flush();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public synchronized void notifyAddPresenceToClient(MessageTypes type){
        try{
            oos.writeObject(new Message(type));
            oos.flush();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public synchronized void notifyDeleteEventPresences(MessageTypes type,String eventName){
        try{
            oos.writeObject(new Message(type,new Event(eventName)));
            oos.flush();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private Message handleRequestMessage(Message messageReceived){
        switch (messageReceived.getType()){
            case LOGIN -> {
                ClientData clientData = dbConnection.getClient(messageReceived.getClientData().getEmail(),messageReceived.getClientData().getPassword());
                if(clientData != null){
                    try {
                        toClientSocket.setSoTimeout(0);
                    } catch (SocketException e) {
                        System.out.println("<ClientConnection|ERRO> Retirar o timeout ao socket apos login.");;
                    }
                    this.clientData = clientData;
                    return new Message(MessageTypes.LOGGED_IN,clientData);
                }
            }
            case SIGNING -> {
                if(dbConnection.addNewEntryToClients(messageReceived.getClientData())){
                    try {
                        toClientSocket.setSoTimeout(0);
                    } catch (SocketException e) {
                        System.out.println("<ClientConnection|ERRO> Retirar o timeout ao socket apos login.");;
                    }
                    this.clientData = messageReceived.getClientData();
                    userConnectionsThread.notifyAllClientsUpdate();
                    mainDBService.notifyObservers();
                    sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                    return new Message(MessageTypes.ACC_CREATED,messageReceived.getClientData());
                }
            }
            case EDIT_LOG_INFO -> {
                ClientData data = dbConnection.editClientInfo(messageReceived.getClientData());
                if(data != null){
                    this.clientData = data;
                    userConnectionsThread.notifyAllClientsUpdate();
                    mainDBService.notifyObservers();
                    sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                    return new Message(MessageTypes.EDIT_LOG_INFO,clientData);
                }
            }
            case SUBMIT_CODE -> {
                String formattedTimeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                if(dbConnection.checkCodeToAssignPresence(messageReceived.getEventCode(), clientData.getEmail(), formattedTimeNow)) {
                    mainDBService.notifyObservers();
                    sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                    return new Message(MessageTypes.SUBMIT_CODE);
                }
            }
            case CREATE_EVENT -> {
                if(dbConnection.addNewEntryToEvents(messageReceived.getEvent())){
                    userConnectionsThread.notifyAllClientsEventsUpdate();
                    mainDBService.notifyObservers();
                    sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                    return new Message(MessageTypes.CREATE_EVENT);
                }
            }
            case EDIT_EVENT -> {
                if(dbConnection.editEventInfo(messageReceived.getEvent())!=null){
                    userConnectionsThread.notifyAllClientsEventsUpdate();
                    mainDBService.notifyObservers();
                    sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                    return new Message(MessageTypes.EDIT_EVENT);
                }
            }
            case REMOVE_EVENT -> {
                if(dbConnection.removeEvent(messageReceived.getEvent().getName())){
                    userConnectionsThread.notifyAllClientsEventsUpdate();
                    mainDBService.notifyObservers();
                    sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                    return new Message(MessageTypes.REMOVE_EVENT);
                }
            }
            case CHECK_PRESENCES -> {
                return new Message(MessageTypes.CHECK_PRESENCES, dbConnection.getEvents(messageReceived.getEvent(), clientData.getEmail()));
            }
            case CHECK_CREATED_EVENTS -> {
                return new Message(MessageTypes.CHECK_CREATED_EVENTS, dbConnection.getEvents(messageReceived.getEvent(), null));
            }
            case GENERATE_EVENT_CODE -> {
                Event editedEvent;
                Calendar currentTime = Calendar.getInstance();
                currentTime.add(Calendar.MINUTE,Integer.parseInt(messageReceived.getEvent().getCodeValidityEnding()));
                messageReceived.getEvent().setCodeValidityEnding(currentTime.get(Calendar.HOUR_OF_DAY)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND));
                do{
                    long newCode = generateCode();
                    editedEvent = dbConnection.editActiveCode(messageReceived.getEvent().getName(), newCode, messageReceived.getEvent().getCodeValidityEnding());
                }while (editedEvent == null);
                mainDBService.notifyObservers();
                sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                return new Message(MessageTypes.GENERATE_EVENT_CODE, editedEvent);
            }
            case CHECK_REGISTERED_PRESENCES -> {
                return new Message(dbConnection.getPresences(messageReceived.getEvent().getName()), MessageTypes.CHECK_REGISTERED_PRESENCES);
            }

            case CHECK_USER_REGISTERED_PRESENCES -> {
                return new Message(MessageTypes.CHECK_USER_REGISTERED_PRESENCES, dbConnection.getEvents(null, messageReceived.getClientData().getEmail()));
            }
            case REMOVE_PRESENCE -> {
                ArrayList<ClientData> data = dbConnection.removePresencesFromEvent(messageReceived.getEvent().getName());
                if(data != null) {
                    mainDBService.notifyObservers();
                    sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                    userConnectionsThread.notifyDeletePresenceToClients(MessageTypes.REMOVE_PRESENCE,messageReceived.getEvent().getName());
                    //return new Message(data, MessageTypes.REMOVE_PRESENCE);
                    return new Message(MessageTypes.REMOVE_PRESENCE,messageReceived.getEvent());
                }
            }
            case ADD_PRESENCE -> {
                String formattedTimeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                if(dbConnection.addPresence(messageReceived.getClientData().getEmail(),
                                            messageReceived.getEvent().getName(),
                                            formattedTimeNow))
                {
                    mainDBService.notifyObservers();
                    sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                    userConnectionsThread.notifyAddPresenceToClients(MessageTypes.ADD_PRESENCE,messageReceived.getClientData().getEmail());
                    return new Message(MessageTypes.ADD_PRESENCE);
                }
            }
            case LOGOUT -> {return new Message(MessageTypes.LOGOUT);}

            case QUIT -> {
                keepRunning = false;
                return new Message(MessageTypes.QUIT);
            }
            default -> {return new Message(MessageTypes.FAILED);}
        }
        return new Message(MessageTypes.FAILED);
    }

    private long generateCode() {
        Random random = new Random();

        // Generate a random number between 1000 and 9999
        int randomInt = random.nextInt(9000) + 1000;

        // Convert the int to long and return
        return (long) randomInt;
    }

    public ClientData getClientData(){return clientData;}

    private void endClientConnection(){
        try {
            oos.writeObject(new Message(MessageTypes.QUIT));
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    @Override
    public void run() {
        Message requestMessage, responseMessage;
        while(keepRunning){
            try {
                requestMessage = (Message) ois.readObject();
                try {
                    System.out.println("<PEDIDO> " + requestMessage.getType().name() + " de <" + requestMessage.getClientData().getEmail() + ">");
                } catch (NullPointerException e) {
                    System.out.println("<PEDIDO> " + requestMessage.getType().name() + " de <" + clientData.getEmail() + ">");
                }

                responseMessage = handleRequestMessage(requestMessage);
                try {
                    System.out.println("<RESPOSTA> " + requestMessage.getType().name() + " de <" + requestMessage.getClientData().getEmail() + ">");
                } catch (NullPointerException e) {
                    System.out.println("<RESPOSTA> " + requestMessage.getType().name() + " de <" + clientData.getEmail() + ">>> " + responseMessage.getType().name());
                }

                oos.writeObject(responseMessage);
                oos.flush();

            } catch (SocketTimeoutException se){
                System.out.println("<ClientConnection|ERRO> Client não fez login ou registo a tempo.");
                endClientConnection();
                keepRunning = false;
            } catch (ClassNotFoundException | IOException e){
                System.out.println("<ClientConnection|ERRO> Leitura/Escrita do socket comprometida");
                keepRunning = false;
            }
        }

        try {
            if (!toClientSocket.isClosed()){
                oos.close();
                ois.close();
                toClientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("<ClientConnection|ERRO> Erro a fechar o socket.");
        }
    }
}
