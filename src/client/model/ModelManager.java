package client.model;

import client.fsm.ClientContext;
import client.fsm.states.ClientState;
import data.ClientData;
import data.Message;
import data.MessageTypes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ModelManager {

    public static final String PROP_UPDATE = "_update_";
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
    public void startMenu(){
        fsm.toStartMenu();
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

    /*---------------------MESSAGES FROM SERVER---------------------*/
    public Message getUpdatedInfo(){return connectionManager.getLastMessageFromServer();}
    public ClientData getClientInfo(){return connectionManager.getLastMessageFromServer().getClientData();}
    public void fireUpdate(){pcs.firePropertyChange(PROP_UPDATE,null,null);}
    //Method called by the ui to check if the last operation was a success
    public boolean checkLastMessageFromServer(){
        return connectionManager.getLastMessageFromServer().getType() != MessageTypes.FAILED;
    }

    /*---------------------END EXECUTION---------------------*/
    public void closeConnection(){
        connectionManager.sendMessageToServer(createMessage(MessageTypes.QUIT,fsm.getClientData()));
        connectionManager.closeConnection();
    }

    /*---------------------MESSAGES---------------------*/
    private Message createMessage(MessageTypes type){return new Message(type);}
    private Message createMessage(MessageTypes type,long code){return new Message(type,code);}
    private Message createMessage(MessageTypes type,ClientData clientData){return new Message(type,clientData);}
    private Message createMessage(MessageTypes type,String name,long id,String email,String password){return new Message(type,new ClientData(name,id,email,password));}

    /*---------------------STATE---------------------*/
    public ClientState getState(){return fsm.getState();}
    public ClientData getClientData(){return fsm.getClientData();}
}
