package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;
import data.ClientData;
import data.MessageTypes;

public class LogIn extends ClientStateAdapter{

    public LogIn(ClientManager clientManager, ClientContext clientContext){
        super(clientManager,clientContext);
    }

    @Override
    public boolean login(ClientData clientData){
        //Completes the data from the client which was sent from the server
        clientManager.setClientData(clientData);

        if(clientManager.isClientAdmin()){changeState(ClientState.START_MENU_ADMIN);}
        else {changeState(ClientState.START_MENU);}

        return true;
    }

    @Override
    public boolean toSignIn(){
        changeState(ClientState.SIGNIN);
        return true;
    }

    @Override
    public ClientState getState(){return ClientState.LOGIN;}
}