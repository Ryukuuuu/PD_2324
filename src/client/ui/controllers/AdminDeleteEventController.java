package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class AdminDeleteEventController {
    public TextField tfName;
    public Button btnSubmit;
    public Button btnCancel;
    public BorderPane borderPane;
    private ModelManager modelManager;

    public void init(ModelManager modelManager){
        this.modelManager = modelManager;
        registerHandlers();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }

    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.DELETE_EVENT);
    }

    @FXML
    private void setBtnSubmit(){

    }
    @FXML
    private void setBtnCancel(){
        modelManager.startMenu();
    }
}
