package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class StartMenuAdmin extends ClientStateAdapter{

    public StartMenuAdmin(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public ClientState getState(){return ClientState.START_MENU_ADMIN;}
}
