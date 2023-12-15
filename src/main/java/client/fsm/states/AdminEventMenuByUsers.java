package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class AdminEventMenuByUsers extends ClientStateAdapter{

    public AdminEventMenuByUsers(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public boolean startMenu(){
        changeState(ClientState.START_MENU_ADMIN);
        return true;
    }

    @Override
    public ClientState getState(){
        return ClientState.ADMIN_EVENT_MENU_BY_USERS;
    }
}
