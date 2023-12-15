package client.model;


import data.ClientData;


public class ClientManager {
    private ClientData clientData;

    public ClientManager(){this.clientData = new ClientData();}
    public ClientData getClientData() {return clientData;}
    public void setClientData(ClientData clientData) {this.clientData = clientData;}
    public boolean isClientAdmin(){return clientData.isAdmin();}
}
