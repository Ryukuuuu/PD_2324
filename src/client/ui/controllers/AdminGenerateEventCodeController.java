package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class AdminGenerateEventCodeController {
    private ModelManager modelManager;

    public BorderPane borderPane;
    public Text tCode;
    public TextField tfName;
    public TextField tfDuration;
    public Button btnGenerate;
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
        System.out.println(modelManager.getState());
        borderPane.setVisible(modelManager.getState() == ClientState.GENERATE_EVENT_CODE);
    }

    @FXML
    private void setBtnGenerate(){
        if(checkForEmptyFields())
            modelManager.sendGenerateCodeMessage(tfName.getText(),tfDuration.getText());
    }
    @FXML
    private void setBtnBack(){
        modelManager.startMenu();
    }

    private boolean checkForEmptyFields(){
        if(tfName.getText().equals("") || tfDuration.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("All TextFields must be filled");
            alert.show();
            return false;
        }
        return true;
    }

}
