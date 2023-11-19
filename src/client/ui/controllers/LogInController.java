package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class LogInController {

    public TextField tfEmail;
    public TextField tfPassword;
    public Button btnLogin;
    public Button btnSignin;
    public Button btnExit;
    private ModelManager modelManager;
    @FXML
    private BorderPane borderPane;

    public void init(ModelManager modelManager) throws IOException {
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }

    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.LOGIN);
        //System.out.println("Login visibility " + borderPane.isVisible());
    }
    @FXML
    private void login() {
        modelManager.login(tfEmail.getText(), tfPassword.getText());
    }
    @FXML
    private void signin(){
        modelManager.toSignin();
    }
    @FXML
    private void exit(){
        modelManager.closeConnection();
        Platform.exit();
    }
}
