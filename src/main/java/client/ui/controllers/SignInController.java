package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import client.model.ModelManagerREST;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SignInController {
    private ModelManagerREST modelManager;
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

    public void init(ModelManagerREST modelManager) throws IOException {
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

    private boolean isValidEmail() {
        return tfEmail.getText().matches("\\S+(@isec\\.pt|esac\\.pt|esec\\.pt|estgoh\\.pt|estesc\\.pt|iscac\\.pt)");
    }

    private boolean checkInfo(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if(tfName.getText().equals("")){
            alert.setHeaderText("Insert a valid name");
            alert.show();
            return false;
        }
        if(!checkId()){
            alert.setHeaderText("Id must be a number");
            alert.show();
            return false;
        }
        if(!isValidEmail()){
            alert.setHeaderText("Invalid email");
            alert.show();
            return false;
        }
        if(tfPassword.getText().equals("")){
            alert.setHeaderText("Insert a password");
            alert.show();
            return false;
        }
        return true;
    }

    @FXML
    private void submitSignIn() {
        if (checkInfo())
            modelManager.submitSigning(tfName.getText(), Long.parseLong(tfId.getText()), tfEmail.getText(), tfPassword.getText());
    }
    @FXML
    private void setBtnBack(){modelManager.back();}
}
