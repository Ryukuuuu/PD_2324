package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class EditUserInfoController {
    private ModelManager modelManager;
    public TextField tfEmail;
    public TextField tfPassword;
    public TextField tfId;
    public Button btnCancel;
    public Button btnConfirm;
    public BorderPane borderPane;
    public TextField tfName;

    public void init(ModelManager modelManager){
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }


    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }

    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.EDIT_LOG_INFO);
    }

    @FXML
    private void submitNewUserInformation(){
        modelManager.sendEditUserInformationMessage(
                tfName.getText(),
                tfEmail.getText(),
                tfPassword.getText(),
                tfId.getText()
        );
        clearTextFields();
    }
    @FXML
    private void cancel(){
        modelManager.profile();
        clearTextFields();
    }
    private void clearTextFields(){
        tfName.clear();
        tfEmail.clear();
        tfPassword.clear();
        tfId.clear();
    }
}
