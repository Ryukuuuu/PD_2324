package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class AddDeletePresenceToEvent extends ClientStateAdapter{

    public AddDeletePresenceToEvent(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public boolean startMenu(){
        changeState(ClientState.START_MENU_ADMIN);
        return true;
    }

    @Override
    public ClientState getState(){return ClientState.ADD_DELETE_PRESENCE_TO_EVENT;}
}
