package client.model;


import data.ClientData;
import data.Message;
import data.MessageTypes;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientManager {
    private ClientData clientData;

    public ClientManager(){this.clientData = new ClientData();}
    public ClientData getClientData() {return clientData;}
    public void setClientData(ClientData clientData) {this.clientData = clientData;}
    public boolean isClientAdmin(){return clientData.isAdmin();}
}
