package client.fsm.states;

import client.fsm.ClientContext;
import client.model.ClientManager;

public class GenerateEventCode extends ClientStateAdapter{

    protected GenerateEventCode(ClientManager clientManager, ClientContext clientContext) {
        super(clientManager, clientContext);
    }
}
