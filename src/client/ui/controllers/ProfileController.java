package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import data.ClientData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class ProfileController {
    public Text tId;
    private ModelManager modelManager;
    public Button btnBack;
    public Button btnLogOut;
    public Button btnEdit;
    public Text tEmail;
    public Text tName;

    public BorderPane borderPane;

    public void init(ModelManager modelManager) {
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }

    private void update(){
        if(modelManager.getState() == ClientState.PROFILE){
            borderPane.setVisible(true);
            fillTextsWithClientData();
        }
        else{
            borderPane.setVisible(false);

        }
    }

    private void fillTextsWithClientData(){
        ClientData clientData = modelManager.getClientInfo();
        if(clientData.hasInformationToDisplay()){
            tName.setText(clientData.getName());
            tEmail.setText(clientData.getEmail());
            tId.setText(clientData.getIdString());
        }
    }

    @FXML
    private void back(){modelManager.startMenu();}
    @FXML
    private void editUserInfo(){
        modelManager.editUserInformation();
    }
    @FXML
    private void logOut(){
        modelManager.logout();
    }

}