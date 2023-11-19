package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;
import data.ClientData;

public class SignIn extends ClientStateAdapter{

    public SignIn(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public boolean submitSignIn(ClientData clientData){
        clientManager.setClientData(clientData);
        changeState(ClientState.START_MENU);
        return true;
    }

    @Override
    public ClientState getState(){
        return ClientState.SIGNIN;
    }
}
