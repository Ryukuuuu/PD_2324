package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SignInController {
    private ModelManager modelManager;
    @FXML
    private Button btnSignIn;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Button btnBack;
    @FXML
    private VBox vbox;
    @FXML
    private TextField tfName;
    public TextField tfId;
    public TextField tfEmail;
    public TextField tfPassword;

    public void init(ModelManager modelManager) throws IOException {
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }
    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.SIGNIN);
        clearTexts();
    }

    private boolean checkId(){
        try{
            Long.parseLong(tfId.getText());
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private void clearTexts(){
        tfEmail.clear();
        tfId.clear();
        tfName.clear();
        tfPassword.clear();
    }

    @FXML
    private void submitSignIn(){
        if(checkId()) {
            modelManager.submitSignIn(tfName.getText(), Long.parseLong(tfId.getText()), tfEmail.getText(), tfPassword.getText());
            //System.out.println("Submitted:" + modelManager.getState());
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Id must be a number");
            alert.show();
        }
    }

    @FXML
    private void setBtnBack(){modelManager.back();}
}
