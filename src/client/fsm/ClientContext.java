package client.fsm;

import client.fsm.states.ClientState;
import client.fsm.states.IClientState;
import client.model.ClientManager;

public class ClientContext {

    private ClientManager clientManager;

    private IClientState state;

    public ClientContext(String ipServer, String portServer) {
        clientManager = new ClientManager(ipServer, portServer);
        state = ClientState.LOGIN.createState(clientManager, this);
    }

    public ClientManager getClientManager() {return this.clientManager;}

    public ClientState getState(){return state.getState();}

    public void changeState(IClientState state){this.state = state;}

    public boolean toStartMenu(){return state.toStartMenu();}

    public boolean login(String email,String password){return state.login(email,password);}
    public boolean toSignin(){return state.toSignIn();}

    public boolean submitSignIn(String name,String id,String email,String password){return state.submitSignIn(name, id, email, password);}
    public boolean logout(){return state.logout();}

}
