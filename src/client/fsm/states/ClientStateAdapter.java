package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class ClientStateAdapter implements IClientState{

    protected ClientContext clientContext;
    protected ClientManager clientManager;

    protected ClientStateAdapter(ClientManager clientManager,ClientContext clientContext){
        this.clientManager = clientManager;
        this.clientContext = clientContext;
    }

    protected void changeState(ClientState state){clientContext.changeState(state.createState(clientManager, clientContext));}


    @Override
    public boolean toStartMenu(){return false;}
    @Override
    public boolean toStartMenu(String email,String password){return false;}
    @Override
    public boolean toEditInfo(){return false;}
    @Override
    public boolean toCheckPresences(){return false;}
    @Override
    public boolean toEvent(){return false;}
    @Override
    public boolean toSignIn(){return false;}
    @Override
    public boolean toAddPresences(){return false;}
    @Override
    public boolean toCheckPresencesOfEvent(){return false;}
    @Override
    public boolean submitSignIn(String name,String id,String email,String password){return false;}
    @Override
    public boolean login(String email,String password){return false;}
    @Override
    public boolean logout(){return false;}

    @Override
    public ClientState getState(){
        return null;
    }
}
