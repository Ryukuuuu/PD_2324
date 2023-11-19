package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class EventMenu extends ClientStateAdapter{

    public EventMenu(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public ClientState getState(){return ClientState.EVENT_MENU;}
}