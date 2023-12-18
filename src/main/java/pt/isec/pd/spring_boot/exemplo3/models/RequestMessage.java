package pt.isec.pd.spring_boot.exemplo3.models;

import data.ClientData;
import data.Event;

public class RequestMessage {
    private ClientData clientData;
    private Event event;
    private long eventCode;

    public RequestMessage(){}

    public RequestMessage(ClientData clientData){
        this.clientData = clientData;
    }
    public RequestMessage(ClientData clientData,long eventCode){
        this.clientData = clientData;
        this.eventCode = eventCode;
    }
    public RequestMessage(ClientData clientData,Event event){
        this.clientData = clientData;
        this.event = event;
    }



    public RequestMessage(Event event){
        this.event = event;
    }


    public ClientData getClientData() {
        return clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public Event getEvent() {return event;}

    public void setEvent(Event event) {this.event = event;}

    public long getEventCode() {
        return eventCode;
    }

    public void setEventCode(long eventCode) {
        this.eventCode = eventCode;
    }
}
