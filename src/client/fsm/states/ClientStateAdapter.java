package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;
import data.ClientData;

public class ClientStateAdapter implements IClientState{

    protected ClientContext clientContext;
    protected ClientManager clientManager;

    protected ClientStateAdapter(ClientManager clientManager,ClientContext clientContext){
        this.clientManager = clientManager;
        this.clientContext = clientContext;
    }

    protected void changeState(ClientState state){clientContext.changeState(state.createState(clientManager, clientContext));}


    @Override
    public boolean startMenu(){return false;}
    @Override
    public boolean profile(){return false;}
    @Override
    public boolean editUserInfo(ClientData clientData){return false;}
    @Override
    public boolean editUserInfoMenu(){return false;}
    @Override
    public boolean toCheckPresences(){return false;}
    @Override
    public boolean toEvent(){return false;}
    @Override
    public boolean createEvent(){return false;}
    @Override
    public boolean toSignIn(){return false;}
    @Override
    public boolean toAddPresences(){return false;}
    @Override
    public boolean toCheckPresencesOfEvent(){return false;}
    @Override
    public boolean submitSignIn(ClientData clientData){return false;}
    @Override
    public boolean login(ClientData clientData){return false;}
    @Override
    public boolean logout(){return false;}

    @Override
    public ClientState getState(){
        return null;
    }
}
