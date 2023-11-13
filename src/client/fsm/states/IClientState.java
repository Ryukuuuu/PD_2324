package client.fsm.states;

public interface IClientState {
    boolean toStartMenu();
    boolean toStartMenu(String email,String password);
    boolean toEditInfo();
    boolean toCheckPresences();
    boolean toEvent();
    boolean toSignIn();
    boolean toAddPresences();
    boolean toCheckPresencesOfEvent();
    boolean submitSignIn(String name,String id,String email,String password);
    boolean login(String email, String password);
    boolean logout();


    ClientState getState();
}
