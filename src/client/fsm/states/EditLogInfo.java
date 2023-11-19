package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;
import data.ClientData;

public class EditLogInfo extends ClientStateAdapter{

    public EditLogInfo(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public boolean editUserInfo(ClientData clientData){
        clientManager.setClientData(clientData);
        changeState(ClientState.PROFILE);
        return true;
    }

    @Override
    public boolean profile(){
        changeState(ClientState.PROFILE);
        return true;
    }

    @Override
    public ClientState getState(){return ClientState.EDIT_LOG_INFO;}
}
