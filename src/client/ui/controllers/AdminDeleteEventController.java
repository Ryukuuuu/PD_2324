package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class AdminDeleteEventController {
    public TextField tfName;
    public Button btnSubmit;
    public BorderPane borderPane;
    public Button btnBack;
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
        borderPane.setVisible(modelManager.getState() == ClientState.DELETE_EVENT);
    }

    @FXML
    private void setBtnSubmit(){
        if(tfName.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Event name must be filled");
            alert.show();
        }
        else{
            modelManager.sendDeleteEventMessage(tfName.getText());
        }
    }
    @FXML
    private void setBtnBack(){
        modelManager.startMenu();
    }
}
