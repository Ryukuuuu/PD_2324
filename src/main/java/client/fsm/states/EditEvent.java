package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class EditEvent extends ClientStateAdapter{

    protected EditEvent(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public boolean startMenu(){
        changeState(ClientState.START_MENU_ADMIN);
        return true;
    }
    
    @Override
    public ClientState getState(){
        return ClientState.EDIT_EVENT;
    }
}
