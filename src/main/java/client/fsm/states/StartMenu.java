package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;
import javafx.fxml.FXML;

public class StartMenu extends ClientStateAdapter{


    public StartMenu(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public boolean toSignIn(){
        changeState(ClientState.SIGNIN);
        return true;
    }

    @Override
    public boolean profile(){
        changeState(ClientState.PROFILE);
        return true;
    }

    @Override
    public boolean toEvent(){
        changeState(ClientState.EVENT_MENU);
        return true;
    }

    @Override
    public boolean logout() {
        changeState(ClientState.LOGIN);
        return true;
    }

    @Override
    public ClientState getState(){return ClientState.START_MENU;}
}
