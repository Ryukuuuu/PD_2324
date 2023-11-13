package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class CheckPresences extends ClientStateAdapter{

    public CheckPresences(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public ClientState getState(){return ClientState.CHECK_PRESENCES;}
}
