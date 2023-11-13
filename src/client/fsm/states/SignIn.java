package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class SignIn extends ClientStateAdapter{

    public SignIn(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public boolean submitSignIn(String name,String id,String email,String password){
        if(!clientManager.createNewClient(name, id, email, password)){
            return false;
        }
        changeState(ClientState.START_MENU);
        return true;
    }

    @Override
    public ClientState getState(){
        return ClientState.SIGNIN;
    }
}
