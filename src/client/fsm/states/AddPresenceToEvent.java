package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class AddPresenceToEvent extends ClientStateAdapter{

    public AddPresenceToEvent(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public ClientState getState(){return ClientState.ADD_PRESENCE_TO_EVENT;}
}
