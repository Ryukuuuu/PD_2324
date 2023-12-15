package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class AdminEventMenu extends ClientStateAdapter{

    protected AdminEventMenu(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public boolean startMenu(){
        changeState(ClientState.START_MENU_ADMIN);
        return true;
    }

    @Override
    public ClientState getState(){
        return ClientState.ADMIN_EVENT_MENU;
    }
}
