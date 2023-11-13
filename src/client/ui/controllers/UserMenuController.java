package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class UserMenuController {

    private ModelManager modelManager;
    public BorderPane borderPane;
    public Button btnProfile;
    public Button btnEventsMenu;
    public Button btnLogout;

    public void init(ModelManager modelManager) throws IOException {
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.START_MENU);
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }

    @FXML
    private void logout(){
        modelManager.logout();
    }
}
