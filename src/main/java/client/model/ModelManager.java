package client.model;

import client.fsm.ClientContext;
import client.fsm.states.ClientState;
import data.ClientData;
import data.Event;
import data.Message;
import data.MessageTypes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;

public class ModelManager {

    public static final String PROP_UPDATE = "_update_";
    public static final String PROP_UPDATE_CODE = "_updateCode_";
    public static final String PROP_UPDATE_EVENT = "_updateEvent_";
    public static final String PROP_UPDATE_REFRESH_EVENT = "_refreshEvent_";
    public static final String PROP_ADD_PRESENCE_UPDATE = "_addPresenceUpdate_";
    public static final String PROP_UPDATE_DELETE_PRESENCE = "_deletePrecenseUpdate_";
    public static final String PROP_UPDATE_QUIT = "_quit_";

    public static final String PROP_STATE = "_state_";


    private PropertyChangeSupport pcs;
    private ClientContext fsm;
    private ConnectionManager connectionManager;


    public ModelManager(String[] args){
        fsm = new ClientContext();
        pcs = new PropertyChangeSupport(this);
        connectionManager = new ConnectionManager(this,args);
    }

    /*---------------------PROPERTY CHANGE---------------------*/
    public void addClient(String property, PropertyChangeListener listener){pcs.addPropertyChangeListener(property,listener);}

    /*---------------------LOGIN---------------------*/

    //Sends login message to the server
    public void login(String email,String password){
        connectionManager.sendMessageToServer(new Message(MessageTypes.LOGIN,new ClientData(email,password)));
    }
    //ConnectionManager will call this method when the login is successful
    public void loginSuccess(ClientData clientData){
        fsm.login(clientData);
        pcs.firePropertyChange(PROP_STATE,null,null);
    }


    /*---------------------SIGN IN---------------------*/

    //Changes state to signin and fires the property change to the view
    public void toSignin(){
        fsm.toSignin();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    //Sends the SignIn message to the server
    public void submitSignIn(String name, long id, String email, String password){
        connectionManager.sendMessageToServer(
                createMessage(
                        MessageTypes.SIGNING,
                        name,
                        id,
                        email,
                        password));
    }
    //ConnectionManager will call this method when the signin is successful
    public void signinSuccess(ClientData clientData){
        fsm.submitSignIn(clientData);
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    /*---------------------LOGOUT--------------------*/
    public void sendLogoutMessage(){connectionManager.sendMessageToServer(createMessage(MessageTypes.LOGOUT,fsm.getClientData()));}
    public void logout(){
        fsm.logout();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    /*---------------------START MENU---------------------*/
    public void sendSubmitCodeMessage(long eventCode){connectionManager.sendMessageToServer(createMessage(MessageTypes.SUBMIT_CODE,eventCode));}
    public void sendGenerateCodeMessage(String eventName,String codeDuration){connectionManager.sendMessageToServer(createMessage(MessageTypes.GENERATE_EVENT_CODE,new Event(eventName,codeDuration)));}
    public long getGeneratedCode(){return connectionManager.getLastMessageFromServer().getEvent().getActiveCode();}
    public void generateEventCode(){
        fsm.generateEventCode();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void startMenu(){
        fsm.toStartMenu();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void back(){
        fsm.back();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    /*---------------------PROFILE---------------------*/

    public void profile(){
        fsm.profile();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void sendEditUserInformationMessage(String name,String password,long id,String email){
        connectionManager.sendMessageToServer(createMessage(
                MessageTypes.EDIT_LOG_INFO,
                name,
                id,
                email,
                password
                ));
    }
    public void editUserInformation(ClientData clientData){
        fsm.editUserInfo(clientData);
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void editUserInformation(){
        fsm.editUserInfo();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    /*----------------------ADD/DELETE PRESENCES---------------------*/
    public void addDeletePresence(){
        fsm.addDeletePresence();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void sendAddPresenceMessage(String email,String eventName){
        connectionManager.sendMessageToServer(createMessage(MessageTypes.ADD_PRESENCE,new ClientData(email),new Event(eventName)));
    }
    public void sendDeletePresencesMessage(String eventName){connectionManager.sendMessageToServer(createMessage(MessageTypes.REMOVE_PRESENCE,new Event(eventName)));}

    /*---------------------MESSAGES FROM SERVER---------------------*/
    public Message getUpdatedInfo(){return connectionManager.getLastMessageFromServer();}
    public ClientData getClientInfo(){return connectionManager.getLastMessageFromServer().getClientData();}
    public void fireCodeUpdate(){
        pcs.firePropertyChange(PROP_UPDATE_CODE,null,null);
    }
    public void fireEventUpdate(){pcs.firePropertyChange(PROP_UPDATE_EVENT,null,null);}
    public void fireAddPresenceUpdate(){
        pcs.firePropertyChange(PROP_ADD_PRESENCE_UPDATE,null,null);
    }
    public void fireDeletePresenceUpdate(){pcs.firePropertyChange(PROP_UPDATE_DELETE_PRESENCE,null,null);}
    public void fireEventRefreshUpdate(){pcs.firePropertyChange(PROP_UPDATE_REFRESH_EVENT,null,null);}

    public void fireUpdate(){
        pcs.firePropertyChange(PROP_UPDATE,null,null);
    }
    //Method called by the ui to check if the last operation was a success
    public Message checkLastMessageFromServer(){return connectionManager.getLastMessageFromServer();}
    public void resendLastMessageToServer(){connectionManager.resendLastMessage();}

    /*-------------------------Events------------------------*/
    public void sendEventsMessage(MessageTypes type){
        System.out.println("Sended: " + type);
        connectionManager.sendMessageToServer(createMessage(type));
    }

    public void events(){
        fsm.events();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void sendEventsMessageWithFilters(Event eventFilter,MessageTypes type){connectionManager.sendMessageToServer(createMessage(MessageTypes.CHECK_PRESENCES,eventFilter));}

    public void toEvents(){
        fsm.adminEvents();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void presencesInEvent(){
        fsm.toCheckPresencesOfEvent();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void createEvent(String name,String local,String date,String startingTime,String endingTime){
        connectionManager.sendMessageToServer(createMessage(MessageTypes.CREATE_EVENT,new Event(name,local,date,startingTime,endingTime)));
        fsm.createEvent();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void createEventMenu(){
        fsm.createEvent();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void sendEditEventMessage(String name,String local,String date,String startingTime,String endingTime){connectionManager.sendMessageToServer(createMessage(MessageTypes.EDIT_EVENT,new Event(name,local,date,startingTime,endingTime)));}
    public void editEventMenu(){
        fsm.editEvent();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void deleteEvent(){
        fsm.deleteEvent();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void sendDeleteEventMessage(String name){connectionManager.sendMessageToServer(createMessage(MessageTypes.REMOVE_EVENT,new Event(name)));}

    public void getEventsByUser(String email){connectionManager.sendMessageToServer(createMessage(MessageTypes.CHECK_USER_REGISTERED_PRESENCES,new ClientData(email)));}
    public void getPresencesByEvent(String name){connectionManager.sendMessageToServer(createMessage(MessageTypes.CHECK_REGISTERED_PRESENCES,new Event(name)));}
    /*---------------------END EXECUTION---------------------*/
    public void closeConnection(){
        connectionManager.sendMessageToServer(createMessage(MessageTypes.QUIT,fsm.getClientData()));
        connectionManager.closeConnection();
    }

    /*---------------------MESSAGES---------------------*/
    private Message createMessage(MessageTypes type){return new Message(type);}
    private Message createMessage(MessageTypes type,long code){return new Message(type,code);}
    private Message createMessage(MessageTypes type,ClientData clientData){return new Message(type,clientData);}
    private Message createMessage(MessageTypes type,ClientData clientData,Event event){return new Message(type,clientData,event);}
    private Message createMessage(MessageTypes type,String name,long id,String email,String password){return new Message(type,new ClientData(name,id,email,password));}
    private Message createMessage(MessageTypes type,Event event){return new Message(type, event);}
    /*----------------------CSV----------------------*/
    public void createEventsPresencesCSVFile(Event event, ArrayList<ClientData> clientDataList, String filename) {
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

    public void createClientsPresencesCSVFile(ArrayList<Event> eventsList, String filename) {
        ClientData clientData = getClientData();

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

    /*---------------------STATE---------------------*/
    public ClientState getState(){return fsm.getState();}
    public ClientData getClientData(){return fsm.getClientData();}
}