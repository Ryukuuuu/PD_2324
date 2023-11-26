package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import com.sun.webkit.Timer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class AdminMenuController {
    public BorderPane borderPane;
    public Button btnCreateEvent;
    public Button btnEditEvent;
    public Button btnLogout;
    public Button btnDeleteEvent;
    public Button btnGenerateCode;
    private ModelManager modelManager;

    public void init(ModelManager modelManager){
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }

    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.START_MENU_ADMIN);
    }

    @FXML
    private void setBtnCreateEvent(){
        modelManager.createEventMenu();
    }
    @FXML
    private void setBtnEditEvent(){
        modelManager.editEventMenu();
    }
    @FXML
    private void setBtnDeleteEvent(){modelManager.deleteEvent();}
    @FXML
    private void setBtnGenerateCode(){modelManager.generateEventCode();}
    @FXML
    private void setBtnLogout(){modelManager.sendLogoutMessage();}
}
