package data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private MessageTypes type;
    //Ver se pomos assim ou se adicionamos o evento ao arrayList e usamos so o primeiro index no server
    private Event event = null;
    //User who sent the message
    private ClientData clientData = null;
    private ArrayList<ClientData> clients;
    private ArrayList<Event> events;
    private long eventCode = 0L;

    public Message(MessageTypes type, ClientData clientData){
        this.type = type;
        this.clientData = clientData;
    }
    public Message(MessageTypes type){
        this.type = type;
    }
    public Message(MessageTypes type, ClientData clientData,long eventCode){
        this.type = type;
        this.clientData = clientData;
        this.eventCode = eventCode;
    }
    public Message(MessageTypes type, long eventCode){
        this.type = type;
        this.eventCode = eventCode;
    }
    public Message(MessageTypes type,Event event){
        this.type = type;
        this.event = event;
    }

    public Message(MessageTypes type, ArrayList<Event> events){
        this.type = type;
        this.events = events;
    }

    public Message(ArrayList<ClientData> clients, MessageTypes type){
        this.type = type;
        this.clients = clients;
    }

    public Message(MessageTypes type, ClientData clientData, Event event) {
        this.type = type;
        this.clientData = clientData;
        this.event = event;
    }

    public MessageTypes getType() {
        return type;
    }
    public void setType(MessageTypes type) {
        this.type = type;
    }
    public ClientData getClientData() {
        return clientData;
    }
    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }
    public long getEventCode() {return eventCode;}
    public void setEventCode(long eventCode) {
        this.eventCode = eventCode;
    }
    public Event getEvent(){return event;}
    public void setEvent(Event event){this.event = event;}
    public ArrayList<Event> getEvents() {
        return events;
    }
    public ArrayList<ClientData> getClients(){
        return clients;
    }
}
