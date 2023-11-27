package client.fsm.states;

import data.ClientData;

public interface IClientState {
    boolean startMenu();
    boolean profile();
    boolean editUserInfo(ClientData clientData);
    boolean editUserInfoMenu();
    boolean toCheckPresencesOfEvents();
    boolean toEvent();
    boolean toEventsByUser();
    boolean createEvent();
    boolean editEvent();
    boolean deleteEvent();
    boolean generateEventCode();
    boolean toSignIn();
    boolean addDeletePresences();
    boolean toCheckPresencesOfEvent();
    boolean submitSignIn(ClientData clientData);
    boolean login(ClientData clientData);
    boolean back();
    boolean logout();


    ClientState getState();
}
