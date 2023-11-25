package server.thread;

import data.*;
import database.DatabaseConnection;
import server.thread.multicast.SendHeartBeats;

import java.io.*;
import java.net.Socket;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private static final String FILENAME_FROM_TEMPLATE = "/ficheiros_csv/presences_in_events_from_%s";
    private static final String FILENAME_AT_TEMPLATE = "/ficheiros_csv/presences_at_%s";

    public NewUserConnection(Socket toClientSocket, DatabaseConnection dbConnection, UserConnectionsThread userConnectionsThread, SendHeartBeats sendHeartBeats) {
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

    private Message handleRequestMessage(Message messageReceived){
        switch (messageReceived.getType()){
            case LOGIN -> {
                ClientData clientData = dbConnection.getClient(messageReceived.getClientData().getEmail(),messageReceived.getClientData().getPassword());
                if(clientData != null){
                    this.clientData = clientData;
                    return new Message(MessageTypes.LOGGED_IN,clientData);
                }
            }
            case SIGNING -> {
                if(dbConnection.addNewEntryToClients(messageReceived.getClientData())){
                    this.clientData = messageReceived.getClientData();
                    userConnectionsThread.notifyAllClientsUpdate();
                    sendHeartBeats.setDataBaseVersion(dbConnection.getDBVersion());
                    return new Message(MessageTypes.ACC_CREATED,messageReceived.getClientData());
                }
            }
            case EDIT_LOG_INFO -> {
                ClientData data = dbConnection.editClientInfo(messageReceived.getClientData());
                if(data != null){
                    this.clientData = data;
                    userConnectionsThread.notifyAllClientsUpdate();
                    return new Message(MessageTypes.EDIT_LOG_INFO,clientData);
                }
            }
            case SUBMIT_CODE -> {
                String formattedTimeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                if(dbConnection.checkCodeToAssignPresence(messageReceived.getEventCode(), clientData.getEmail(), formattedTimeNow))
                    return new Message(MessageTypes.SUBMIT_CODE);
            }
            case CREATE_EVENT -> {
                if(dbConnection.addNewEntryToEvents(messageReceived.getEvent())){
                    userConnectionsThread.notifyAllClientsEventsUpdate();
                    return new Message(MessageTypes.CREATE_EVENT);
                }
            }
            case EDIT_EVENT -> {
                if(dbConnection.editEventInfo(messageReceived.getEvent())!=null){
                    userConnectionsThread.notifyAllClientsEventsUpdate();
                    return new Message(MessageTypes.EDIT_EVENT);
                }
            }
            case REMOVE_EVENT -> {
                if(dbConnection.removeEvent(messageReceived.getEvent().getName())){
                    userConnectionsThread.notifyAllClientsEventsUpdate();
                    return new Message(MessageTypes.REMOVE_EVENT);
                }
            }
            case CHECK_PRESENCES -> {
                userConnectionsThread.notifyAllClientsEventsUpdate();
                return new Message(MessageTypes.CHECK_PRESENCES, dbConnection.getEvents(null, clientData.getEmail()));
            }
            case GET_PRESENCES_CSV -> {
                ArrayList<Event> eventsList= dbConnection.getEvents(null, clientData.getEmail());
                String filename = String.format(FILENAME_FROM_TEMPLATE, clientData.getEmail());
                createClientsPresencesCSVFile(clientData, eventsList, filename);
            }
            case CHECK_CREATED_EVENTS -> {
                userConnectionsThread.notifyAllClientsEventsUpdate();
                return new Message(MessageTypes.CHECK_CREATED_EVENTS, dbConnection.getEvents(messageReceived.getEvent(), null));
            }
            case GENERATE_EVENT_CODE -> {
                Event editedEvent;
                do{
                    long newCode = generateCode();
                    editedEvent = dbConnection.editActiveCode(messageReceived.getEvent().getName(), newCode, messageReceived.getEvent().getCodeValidityEnding());
                }while (editedEvent == null);
                return new Message(MessageTypes.GENERATE_EVENT_CODE, editedEvent);
            }
            case CHECK_REGISTERED_PRESENCES -> {
                return new Message(dbConnection.getPresences(messageReceived.getEvent().getName()), MessageTypes.CHECK_REGISTERED_PRESENCES);
            }
            case GET_REGISTERED_PRESENCES_CSV -> {
                ArrayList<ClientData> clientDataList = dbConnection.getPresences(messageReceived.getEvent().getName());
                String filename = String.format(FILENAME_AT_TEMPLATE, messageReceived.getEvent().getName());
                createEventsPresencesCSVFile(messageReceived.getEvent(), clientDataList, filename);
            }
            case CHECK_USER_REGISTERED_PRESENCES -> {
                userConnectionsThread.notifyAllClientsEventsUpdate();
                return new Message(MessageTypes.CHECK_PRESENCES, dbConnection.getEvents(null, messageReceived.getClientData().getEmail()));
            }
            case GET_USER_REGISTERED_PRESENCES_CSV -> {
                ArrayList<Event> eventsList = dbConnection.getEvents(null, messageReceived.getClientData().getEmail());
                String filename = String.format(FILENAME_FROM_TEMPLATE, messageReceived.getClientData().getEmail());
                createClientsPresencesCSVFile(clientData, eventsList, filename);
            }
            case REMOVE_PRESENCE -> {
                return new Message(dbConnection.removePresencesFromEvent(messageReceived.getEvent().getName()), MessageTypes.REMOVE_PRESENCE);
            }
            case ADD_PRESENCE -> {
                String formattedTimeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                if(dbConnection.addPresence(messageReceived.getClientData().getEmail(),
                                            messageReceived.getEvent().getName(),
                                            formattedTimeNow))
                    return new Message(MessageTypes.ADD_PRESENCE);
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

    private void createEventsPresencesCSVFile(Event event, ArrayList<ClientData> clientDataList, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write event header
            writer.write("\"Designação\";\"Local\";\"Data\";\"Horainício\";\"Hora fim\"");
            writer.newLine();

            // Write event data
            writer.write('"' + event.getName() + "\";\"" + event.getLocal() + "\";\"" +
                    event.getDate() + "\";\"" + event.getStartingTime() + "\";\"" + event.getEndingTime() + "\"");
            writer.newLine();
            writer.newLine();

            // Write clients header
            writer.write("\"Nome\";\"Número identificação\";\"Email\"");
            writer.newLine();

            // Write each client
            for (ClientData client : clientDataList) {
                writer.write('"' + client.getName() + "\";\"" + client.getId() + "\";\"" + client.getEmail() + "\"");
                writer.newLine();
            }

            System.out.println("Dados das presenças no evento '" + event.getName() + "' enviadas para o ficheiro '" + filename + "'!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createClientsPresencesCSVFile(ClientData clientData, ArrayList<Event> eventsList, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write header
            writer.write("\"Nome\";\"Número identificação\";\"Email\"");
            writer.newLine();

            // Write client data
            writer.write('"' + clientData.getName() + "\";\"" + clientData.getId() + "\";\"" + clientData.getEmail() + "\"");
            writer.newLine();
            writer.newLine();

            // Write events header
            writer.write("\"Designação\";\"Local\";\"Data\";\"Horainício\"");
            writer.newLine();

            // Write each event
            for (Event event : eventsList) {
                writer.write('"' + event.getName() + "\";\"" + event.getLocal() + "\";\"" + event.getDate() + "\";\"" + event.getStartingTime() + "\"");
                writer.newLine();
            }

            System.out.println("Dados das presenças de '" + clientData.getEmail() + "' enviados para o ficheiro '" + filename + "'!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long generateCode() {
        Random random = new Random();

        // Generate a random number between 1000 and 9999
        int randomInt = random.nextInt(9000) + 1000;

        // Convert the int to long and return
        return (long) randomInt;
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

            } catch (ClassNotFoundException | IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}
