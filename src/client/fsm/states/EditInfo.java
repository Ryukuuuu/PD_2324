package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class EditInfo extends ClientStateAdapter{

    public EditInfo(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public ClientState getState(){return ClientState.EDIT_INFO;}
}
