package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public enum ClientState {

    LOGIN,
    SIGNIN,
    START_MENU,
    PROFILE,
    EDIT_LOG_INFO,

    CHECK_PRESENCES,    // confirmar

    START_MENU_ADMIN,
    EVENT_MENU,
    CREATE_EVENT,
    EDIT_EVENT,
    DELETE_EVENT,
    GENERATE_EVENT_CODE,

    CHECK_PRESENCES_EVENT, // confirmar
    ADD_PRESENCE_TO_EVENT; // confirmar

    public IClientState createState(ClientManager clientManager, ClientContext context){
        return switch (this){
            case LOGIN -> new LogIn(clientManager,context);
            case SIGNIN -> new SignIn(clientManager,context);
            case START_MENU -> new StartMenu(clientManager,context);
            case START_MENU_ADMIN -> new StartMenuAdmin(clientManager,context);
            case PROFILE -> new Profile(clientManager,context);
            case EDIT_LOG_INFO -> new EditLogInfo(clientManager,context);
            case CHECK_PRESENCES -> new CheckPresences(clientManager,context);
            case EVENT_MENU -> new EventMenu(clientManager,context);
            case ADD_PRESENCE_TO_EVENT -> new AddPresenceToEvent(clientManager,context);
            case CHECK_PRESENCES_EVENT -> new CheckPresencesOfEvent(clientManager,context);
            case CREATE_EVENT -> new CreateEvent(clientManager,context);
            case EDIT_EVENT -> new EditEvent(clientManager,context);
            case GENERATE_EVENT_CODE -> new GenerateEventCode(clientManager,context);
            case DELETE_EVENT -> new DeleteEvent(clientManager,context);
        };
    }
}
