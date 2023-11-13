package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class CheckPresencesOfEvent extends ClientStateAdapter{

    public CheckPresencesOfEvent(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public ClientState getState(){return ClientState.CHECK_PRESENCES_EVENT;}
}
