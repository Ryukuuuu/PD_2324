package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class AdminEditEventController {
    private ModelManager modelManager;
    public BorderPane borderPane;
    public TextField tfName;
    public TextField tfLocal;
    public TextField tfDate;
    public TextField tfStartingTime;
    public TextField tfEndingTime;
    public Button btnSubmit;
    public Button btnCancel;

    public void init(ModelManager modelManager) {
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers() {
        modelManager.addClient(ModelManager.PROP_STATE, evt -> Platform.runLater(this::update));
    }

    private void update() {
        borderPane.setVisible(modelManager.getState() == ClientState.EDIT_EVENT);
    }

    @FXML
    private void setBtnCancel() {
        modelManager.startMenu();
    }

    @FXML
    private void setBtnSubmit() {
        if(!tfName.getText().equals("")){
            modelManager.sendEditEventMessage(tfName.getText(),
                    tfLocal.getText(),
                    tfDate.getText(),
                    tfStartingTime.getText(),
                    tfEndingTime.getText());
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing the name of the event");
            alert.show();
        }
    }
}
