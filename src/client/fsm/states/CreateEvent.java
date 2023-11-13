package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class CreateEvent extends ClientStateAdapter{

    public CreateEvent(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

     @Override
    public ClientState getState(){return ClientState.CREATE_EVENT;}
}
