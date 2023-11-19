package data;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private MessageTypes type;
    //User who sent the message
    private ClientData clientData;
    private long eventCode;
    private boolean messageReaded = false;

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
    public Message(MessageTypes type, String eventCode){
        this.type = type;
        this.eventCode = Long.parseLong(eventCode);
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

    public long getEventCode() {
        return eventCode;
    }

    public void setEventCode(long eventCode) {
        this.eventCode = eventCode;
    }
    public boolean isMessageReaded() {return messageReaded;}
    public void setMessageReaded(boolean messageReaded) {this.messageReaded = messageReaded;}
}
