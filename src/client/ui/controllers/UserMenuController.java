package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import data.Message;
import data.MessageTypes;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class UserMenuController {

    public Button btnLogOut;
    public Button btnEvents;
    public TextField tfCode;
    public Button btnSubmit;
    private ModelManager modelManager;
    public BorderPane borderPane;
    public Button btnProfile;

    public void init(ModelManager modelManager) {
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.START_MENU);
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
        modelManager.addClient(ModelManager.PROP_UPDATE_CODE,evt -> Platform.runLater(this::showResultOfCodeSubmitted));
        modelManager.addClient(ModelManager.PROP_ADD_PRESENCE_UPDATE,evt -> Platform.runLater(this::showUpdate));
        modelManager.addClient(ModelManager.PROP_UPDATE_DELETE_PRESENCE,evt -> Platform.runLater(this::notifyDeletedPresence));
    }

    @FXML
    private void logout(){modelManager.sendLogoutMessage();}

    @FXML
    private void submitCode(){
        if(checkCode()){
            modelManager.sendSubmitCodeMessage(Long.parseLong(tfCode.getText()));
            tfCode.clear();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Code must be a number");
            alert.show();
        }
    }

    @FXML
    private void events(){
        if(modelManager.getState() == ClientState.EVENT_MENU)
            modelManager.sendEventsMessage(MessageTypes.CHECK_PRESENCES);
        else
            modelManager.sendEventsMessage(MessageTypes.CHECK_CREATED_EVENTS);
    }

    @FXML
    private void profile(){modelManager.profile();}

    private boolean checkCode(){
        try{
            Long.parseLong(tfCode.getText());
            return true;
        }catch (NumberFormatException ignored){
            return false;
        }
    }

    private void showResultOfCodeSubmitted(){
        if(modelManager.getState() == ClientState.START_MENU) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (modelManager.checkLastMessageFromServer().getType() != MessageTypes.FAILED) {
                alert.setHeaderText("Code submitted");
            } else
                alert.setHeaderText("Error submiting code");
            alert.show();
            tfCode.clear();
        }
    }

    private void showUpdate(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(modelManager.checkLastMessageFromServer().getType() == MessageTypes.ADD_PRESENCE)
            alert.setHeaderText("Presence added by an administrator");
        else
            alert.setHeaderText("Presence deleted by an administrator");
        alert.show();
    }

    private void notifyDeletedPresence(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Presences from the event");
        alert.show();
    }
}
