package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import client.model.ModelManagerREST;
import com.sun.webkit.Timer;
import data.MessageTypes;
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
    public Button btnCheckEvent;
    public Button btnAddDeletePresence;
    public Button btnCheckUserPresences;
    public Button btnCheckPresencesInEvent;
    public Button btnCheckCreatedEvents;
    private ModelManagerREST modelManager;

    public void init(ModelManagerREST modelManager){
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
    private void setBtnCheckUserPresences(){modelManager.toMenuEvents();}
    @FXML
    private void setBtnCheckPresencesInEvent(){modelManager.presencesInEvent();}
    @FXML
    private void setBtnCheckCreatedEvents(){modelManager.toMenuEvents();}
    @FXML
    private void setBtnCreateEvent(){
        modelManager.createEventMenu();
    }
    @FXML
    private void setBtnEditEvent(){
        modelManager.editEventMenu();
    }
    @FXML
    private void setBtnDeleteEvent(){modelManager.deleteEventMenu();}
    @FXML
    private void setBtnGenerateCode(){modelManager.generateEventCode();}
    @FXML
    private void setBtnAddDeletePresence(){modelManager.addDeletePresence();}
    @FXML
    private void setBtnLogout(){modelManager.logout();}
}
