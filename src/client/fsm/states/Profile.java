package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;
import client.ui.controllers.ProfileController;

public class Profile extends ClientStateAdapter{

    public Profile(ClientManager clientManager, ClientContext clientContext){super(clientManager, clientContext);}

    @Override
    public boolean startMenu(){
        changeState(ClientState.START_MENU);
        return true;
    }

    @Override
    public boolean logout(){
        changeState(ClientState.LOGIN);
        return true;
    }

    @Override
    public boolean editUserInfoMenu(){
        changeState(ClientState.EDIT_LOG_INFO);
        return true;
    }

    @Override
    public ClientState getState(){return ClientState.PROFILE;}
}
