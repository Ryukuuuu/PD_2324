package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
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
    public Button btnEventsMenu;
    public Button btnLogout;

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
        modelManager.addClient(ModelManager.PROP_UPDATE,evt -> Platform.runLater(this::showResultOfCodeSubmitted));
    }

    @FXML
    private void logout(){modelManager.sendLogoutMessage();}

    @FXML
    private void submitCode(){
        if(checkCode()){
            modelManager.sendSubmitCodeMessage(Long.parseLong(tfCode.getText()));
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Code must be a number");
            alert.show();
        }
    }

    @FXML
    private void events(){
        /*TODO*/
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(modelManager.checkLastMessageFromServer()){
            alert.setHeaderText("Code submitted");
        }
        else
            alert.setHeaderText("Error submiting code");
        alert.show();
        tfCode.clear();
    }
}
