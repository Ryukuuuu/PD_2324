package client.fsm;

import client.fsm.states.ClientState;
import client.fsm.states.IClientState;
import client.model.ClientManager;
import data.ClientData;

public class ClientContext {

    private ClientManager clientManager;

    private IClientState state;

    public ClientContext() {
        clientManager = new ClientManager();
        state = ClientState.LOGIN.createState(clientManager, this);
    }

    public ClientManager getClientManager() {return this.clientManager;}

    public ClientState getState(){return state.getState();}

    public void changeState(IClientState state){this.state = state;}

    public boolean toStartMenu(){return state.startMenu();}

    public boolean login(ClientData clientData){return state.login(clientData);}
    public boolean createEvent(){return state.createEvent();}
    public boolean toSignin(){return state.toSignIn();}

    public boolean submitSignIn(ClientData clientData){return state.submitSignIn(clientData);}
    public boolean profile(){return state.profile();}
    public boolean events(){return state.toEvent();}
    public boolean editUserInfo(ClientData clientData){return state.editUserInfo(clientData);}
    public boolean editUserInfo(){return state.editUserInfoMenu();}
    public boolean logout(){return state.logout();}

    public ClientData getClientData(){return clientManager.getClientData();}
}
