package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class EditUserInfoController {
    private ModelManager modelManager;
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

    private boolean checkId(){
        try{
            Long.parseLong(tfId.getText());
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    @FXML
    private void submitNewUserInformation(){
        if(checkId()) {
            modelManager.sendEditUserInformationMessage(
                    tfName.getText(),
                    tfPassword.getText(),
                    Long.parseLong(tfId.getText()),
                    modelManager.getClientData().getEmail()
            );
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Id must be a number");
            alert.show();
        }
    }
    @FXML
    private void cancel(){
        modelManager.profile();
        clearTextFields();
    }
    private void clearTextFields(){
        tfName.clear();
        tfPassword.clear();
        tfId.clear();
    }
}
