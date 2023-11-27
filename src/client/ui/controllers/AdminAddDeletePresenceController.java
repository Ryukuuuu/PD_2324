package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class AdminAddDeletePresenceController {
    private ModelManager modelManager;
    public BorderPane borderPane;
    public TextField tfUserEmail;
    public TextField tfEventName;
    public Button btnAdd;
    public Button btnDelete;
    public Button btnBack;

    public void init(ModelManager modelManager){
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }
    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.ADD_DELETE_PRESENCE_TO_EVENT);
    }

    @FXML
    private void setBtnAdd(){
        if(checkAddPresence()) {
            modelManager.sendAddPresenceMessage(tfUserEmail.getText(), tfEventName.getText());
            tfUserEmail.clear();
            tfEventName.clear();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("All the fields must be filled");
            alert.show();
        }
    }
    @FXML
    private void setBtnDelete(){
        if(checkDeletePresence()) {
            tfUserEmail.clear();
            modelManager.sendDeletePresencesMessage(tfEventName.getText());
            tfEventName.clear();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Event field must be filled");
            alert.show();
        }
    }
    @FXML
    private void setBtnBack(){modelManager.startMenu();}

    private boolean checkAddPresence(){
        if(tfUserEmail.getText().equals(""))
            return false;
        if(tfEventName.getText().equals(""))
            return false;
        return true;
    }
    private boolean checkDeletePresence(){
        if(tfEventName.getText().equals(""))
            return false;
        return true;
    }
}
