package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class AdminCreateEventController {
    private ModelManager modelManager;
    public BorderPane borderPane;
    public TextField tfName;
    public TextField tfLocal;
    public TextField tfDate;
    public TextField tfStartingTime;
    public TextField tfEndingTime;
    public Button btnCreate;
    public Button btnCancel;

    public void init(ModelManager modelManager){
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }

    private void update(){
        borderPane.setVisible(modelManager.getState()== ClientState.CREATE_EVENT);
    }

    @FXML
    private void submitEventInfo(){
        if(checkIfTextFieldIsEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing information");
            alert.show();
        }
        else{
            modelManager.createEvent(tfName.getText(),
                    tfLocal.getText(),
                    tfDate.getText(),
                    tfStartingTime.getText(),
                    tfEndingTime.getText());
        }
    }

    @FXML
    private void setBtnCancel(){modelManager.startMenu();}

    private boolean checkIfTextFieldIsEmpty(){
        if(tfName.getText().equals(""))
            return true;
        if(tfLocal.getText().equals(""))
            return true;
        if(tfDate.getText().equals(""))
            return true;
        if(tfStartingTime.getText().equals(""))
            return true;
        if(tfEndingTime.getText().equals(""))
            return true;
        return false;
    }
}
