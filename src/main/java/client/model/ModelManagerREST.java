package client.model;

import client.fsm.ClientContext;
import client.fsm.states.ClientState;
import com.nimbusds.jose.shaded.gson.Gson;
import data.ClientData;
import data.Event;
import org.ietf.jgss.GSSContext;
import pt.isec.pd.spring_boot.exemplo3.models.RequestMessage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

public class ModelManagerREST {

    //URI'S
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String DELETE = "DELETE";

    private static final String loginURI = "http://localhost:8080/login";
    private static final String signingURI = "http://localhost:8080/signing";

    private static final String getClientInfoAfterLoginURI = "http://localhost:8080/client";
    private static final String editUserURI = "http://localhost:8080/client/edit";

    private static final String submitEventCodeURI = "http://localhost:8080/events/submitCode";
    private static final String generateEventCodeURI = "http://localhost:8080/events/generateCode";
    private static final String checkPresencesURI = "http://localhost:8080/events/checkPresences";
    private static final String addPresenceURI = "http://localhost:8080/events/addPresence";
    private static final String deletePresenceURI = "http://localhost:8080/events/deletePresence";
    private static final String createEventURI = "http://localhost:8080/events/create";
    private static final String deleteEventURI = "http://localhost:8080/events/delete";
    private static final String editEventURI = "http://localhost:8080/events/edit";
    private static final String getEventsURI = "http://localhost:8080/events/getEvents";
    private static final String getPresencesByEventURI = "http://localhost:8080/events/presencesByEvent";
    private static final String getPresencesByUserURI = "http://localhost:8080/events/presencesByUser";


    /*PCS*/
    private static final String PROP_STATE = "_state_";

    private PropertyChangeSupport pcs;
    private ClientContext fsm;
    private ConnectionManagerREST connectionManager;
    private String token;

    public ModelManagerREST(){
        this.fsm = new ClientContext();
        this.pcs = new PropertyChangeSupport(this);
        this.connectionManager = new ConnectionManagerREST(this);
    }

    /*---------------------PROPERTY CHANGE---------------------*/
    public void addClient(String property, PropertyChangeListener listener){pcs.addPropertyChangeListener(property,listener);}

    /*---------------------LOGIN---------------------*/

    public void login(String email,String password) {
        String credentials = Base64.getEncoder().encodeToString((email+":"+password).getBytes());
        try {
            token = connectionManager.sendRequestAndShowResponse(loginURI, POST, "basic " + credentials, null);
            if (token != null) {
                String clientInfoJSON = connectionManager.sendRequestAndShowResponse(getClientInfoAfterLoginURI, GET, "bearer " + token, null);
                Gson gson = new Gson();  // new GsonBuilder().create(); ?
                loginSuccess(gson.fromJson(clientInfoJSON, ClientData.class));
            }
        }catch (IOException ignored){}
    }

    public void loginSuccess(ClientData clientData){
        fsm.login(clientData);
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void back(){
        fsm.back();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    /*---------------------SIGN IN---------------------*/
    public void toSigning(){
        fsm.toSignin();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void submitSigning(String name, long id, String email, String password){
        ClientData signingClientInfo = new ClientData(name, id, email, password);
        try {
            connectionManager.sendRequestAndShowResponse(signingURI, POST, null, convertObjectToJSON(signingClientInfo));
        }catch (IOException e){
            System.out.println("<MMREST>Erro submiting sign in");
        }
    }

    /*---------------------PROFILE---------------------*/

    public void profile(){
        fsm.profile();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void editUserInformation(){
        fsm.editUserInfo();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void sendEditUserInformationMessage(String name,String password,long id,String email){
        ClientData clientInfo = new ClientData(name, id, email, password);
        System.out.println(clientInfo);
        try{
            connectionManager.sendRequestAndShowResponse(editUserURI,POST,"bearer " + token,convertObjectToJSON(clientInfo));
        }catch (IOException e){
            System.out.println("<MMREST>Error editing credencials");
        }
    }

    /*---------------------START MENU---------------------*/
    public void startMenu(){
        fsm.toStartMenu();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void events(){
        try {
            connectionManager.sendRequestAndShowResponse(checkPresencesURI, GET, "bearer " + token, null);
        }catch (IOException e){
            System.out.println("<MMREST>Error getting events");
        }
        fsm.events();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void sendEventsMessageWithFilters(Event event){
        try{
            connectionManager.sendRequestAndShowResponse(getEventsURI,GET,"bearer " + token,convertObjectToJSON(event));
        }catch (IOException e){
            System.out.println("<MMREST> error getting events with filters");
        }
    }

    public void presencesInEvent(){
        fsm.toCheckPresencesOfEvent();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void createEventMenu(){
        fsm.createEvent();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void editEventMenu(){
        fsm.editEvent();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void deleteEventMenu(){
        fsm.deleteEvent();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void generateEventCode(){
        fsm.generateEventCode();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public void addDeletePresence(){
        fsm.addDeletePresence();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    /*---------------------LOG OUT---------------------*/
    public void logout(){
        fsm.logout();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    /*---------------------SUBMIT CODE---------------------*/
    public void submitEventCode(long eventCode){
        RequestMessage requestMessage = new RequestMessage(getClientData(),eventCode);

        try {
            connectionManager.sendRequestAndShowResponse(submitEventCodeURI, POST, "bearer " + token, convertObjectToJSON(requestMessage));
        }catch (IOException e){
            System.out.println("<MMREST>Error submiting code");
        }
    }

    /*----------------------EVENTS---------------------*/

    public void createEvent(String name,String local,String date,String startingTime,String endingTime){
        RequestMessage requestMessage = new RequestMessage(new Event(name, local, date, startingTime, endingTime));
        try{
            connectionManager.sendRequestAndShowResponse(createEventURI,POST,"bearer " + token,convertObjectToJSON(requestMessage));
        }catch (IOException e){
            System.out.println("<MMREST>Error creating event");
        }
    }

    public void sendDeleteEventMessage(String eventName){
        RequestMessage requestMessage = new RequestMessage(new Event(eventName));
        try{
            connectionManager.sendRequestAndShowResponse(deleteEventURI,DELETE,"bearer " + token,convertObjectToJSON(requestMessage));
        }catch (IOException e){
            System.out.println("<MMREST>Error deleting event");
        }
    }

    public void sendEditEventMessage(String name,String local,String date,String startingTime,String endingTime){
        RequestMessage requestMessage = new RequestMessage(new Event(name, local, date, startingTime, endingTime));
        try{
            connectionManager.sendRequestAndShowResponse(editEventURI,POST,"bearer " + token,convertObjectToJSON(requestMessage));
        }catch (IOException e){
            System.out.println("<MMREST>Error editing message");
        }
    }


    /*---------------------ADD/DELETE PRESENCES---------------------*/
    public void sendAddPresenceMessage(String email,String eventName){
        RequestMessage requestMessage = new RequestMessage(new ClientData(email),new Event(eventName));
        try {
            connectionManager.sendRequestAndShowResponse(addPresenceURI, POST, "bearer " + token, convertObjectToJSON(requestMessage));
        }catch (IOException e){
            System.out.println("<MMREST>Error adding presence");
        }
    }

    public void sendDeletePresencesMessage(String eventName){
        RequestMessage requestMessage = new RequestMessage(new Event(eventName));
        try{
            connectionManager.sendRequestAndShowResponse(deletePresenceURI,DELETE,"bearer " + token, convertObjectToJSON(requestMessage));
        }catch (IOException e){
            System.out.println("<MMREST>Error deleting presence");
        }

    }

    public void getPresencesByEvent(String eventName){
        try{
            connectionManager.sendRequestAndShowResponse(getPresencesByEventURI,GET,"bearer " + token,convertObjectToJSON(eventName));
        }catch (IOException e){
            System.out.println("<MMREST>Error getting presences from event");
        }
    }

    public void getEventsByUser(String email){
        try{
            connectionManager.sendRequestAndShowResponse(getPresencesByUserURI,GET,"bearer "+token,convertObjectToJSON(email));
        }catch (IOException e){
            System.out.println("<MMREST>Error getting presences of user");
        }
    }

    /*---------------------EVENT CODE---------------------*/

    public void sendGenerateCodeMessage(String eventName,String duration){
        RequestMessage requestMessage = new RequestMessage(new Event(eventName,duration));
        try{
            connectionManager.sendRequestAndShowResponse(generateEventCodeURI,POST,"bearer"+token,convertObjectToJSON(requestMessage));
        }catch (IOException e){}
    }

    public long getGeneratedCode(){
        /*TODO*/
        return 0;
    }

    /*---------------------CSV---------------------*/

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

    /*---------------------ENCODING---------------------*/
    private <T> String getBase64EncodedObject(T obj) {

        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {

            oos.writeObject(obj);
            byte[] objByteArray = baos.toByteArray();
            return Base64.getEncoder().encodeToString(objByteArray);
        } catch (IOException e) {
            System.out.println("Error encoding");
        }
        return null;
    }

    private <T> String convertObjectToJSON(T obj){
        return new Gson().toJson(obj);
    }

    /*---------------------STATE---------------------*/
    public ClientState getState(){return fsm.getState();}
    public ClientData getClientData(){return fsm.getClientData();}
}
