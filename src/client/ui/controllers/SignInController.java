package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SignInController {

    public Button btnSignIn;
    private ModelManager modelManager;
    public BorderPane borderPane;
    public Button btnBack;
    public VBox vbox;
    public TextField tfName;
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

    private void clearTexts(){
        tfEmail.clear();
        tfId.clear();
        tfName.clear();
        tfPassword.clear();
    }

    @FXML
    private void submitSignIn(){
        modelManager.submitSignIn(tfName.getText(),tfId.getText(),tfEmail.getText(),tfPassword.getText());
        System.out.println("Submitted:" + modelManager.getState());
    }
}
