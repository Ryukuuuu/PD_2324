package client.model;

import client.fsm.ClientContext;
import client.fsm.states.ClientState;
import testdatabase.TestDatabase;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ModelManager {



    public static final String PROP_UPDATE = "_update_";
    public static final String PROP_STATE = "_state_";

    private PropertyChangeSupport pcs;
    private ClientContext fsm;

    public ModelManager(String[] args){
        fsm = new ClientContext(args[0],args[1]);
        pcs = new PropertyChangeSupport(this);
    }

    public void addClient(String property, PropertyChangeListener listener){pcs.addPropertyChangeListener(property,listener);}

    public void login(String email,String password){
        fsm.login(email,password);
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void toSignin(){
        fsm.toSignin();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void submitSignIn(String name,String id,String email,String password){
        fsm.submitSignIn(name,id,email,password);
        pcs.firePropertyChange(PROP_STATE,null,null);
    }
    public void logout(){
        fsm.logout();
        pcs.firePropertyChange(PROP_STATE,null,null);
    }

    public ClientState getState(){return fsm.getState();}
}
