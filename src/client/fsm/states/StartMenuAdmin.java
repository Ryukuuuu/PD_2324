package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class StartMenuAdmin extends ClientStateAdapter{

    public StartMenuAdmin(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }

    @Override
    public boolean createEvent(){
        changeState(ClientState.CREATE_EVENT);
        return true;
    }
    @Override
    public boolean deleteEvent(){
        changeState(ClientState.DELETE_EVENT);
        return true;
    }

    @Override
    public boolean editEvent(){
        changeState(ClientState.EDIT_EVENT);
        return true;
    }

    @Override
    public boolean generateEventCode(){
        changeState(ClientState.GENERATE_EVENT_CODE);
        return true;
    }

    @Override
    public boolean toEvent(){
        changeState(ClientState.ADMIN_EVENT_MENU_BY_EVENTS);
        return true;
    }

    @Override
    public boolean toEventsByUser(){
        changeState(ClientState.ADMIN_EVENT_MENU_BY_USERS);
        return true;
    }

    @Override
    public boolean addDeletePresences(){
        changeState(ClientState.ADD_DELETE_PRESENCE_TO_EVENT);
        return true;
    }

    @Override
    public boolean logout(){
        changeState(ClientState.LOGIN);
        return true;
    }

    @Override
    public ClientState getState(){return ClientState.START_MENU_ADMIN;}
}
