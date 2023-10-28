package Data;

import Client.Client;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private MessageTypes type;

    //User who sent the message
    private ClientData clientData;

    public Message(MessageTypes type, ClientData clientData){
        this.type = type;
        this.clientData = clientData;
    }


}
