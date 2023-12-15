package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import client.model.ModelManagerREST;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class LogInController {

    private ModelManagerREST modelManager;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnSignin;
    @FXML
    private Button btnExit;
    @FXML
    private BorderPane borderPane;

    public void init(ModelManagerREST modelManager){
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }

    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.LOGIN);
        clearTextFields();
        //System.out.println("Login visibility " + borderPane.isVisible());
    }
    @FXML
    private void login() {
        modelManager.login(tfEmail.getText(), tfPassword.getText());
    }
    @FXML
    private void signin(){
        modelManager.toSigning();
    }
    @FXML
    private void exit() {
        //modelManager.closeConnection();
        Platform.exit();
    }

    private void clearTextFields(){
        tfEmail.clear();
        tfPassword.clear();
    }
}
