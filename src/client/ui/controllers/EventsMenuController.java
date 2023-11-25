package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import data.Event;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class EventsMenuController {

    public TableView<Event> table;

    public TableColumn<Event,Event> name;
    public TableColumn<Event,Event> local;
    public TableColumn<Event,Event> date;
    public TableColumn<Event,Event> start;
    public TableColumn<Event,Event> end;
    public Button btnCsv;
    public TextField tfFileName;
    private ModelManager modelManager;
    public BorderPane borderPane;
    public Button btnBack;
    public ScrollPane scrollView;




    public void init(ModelManager modelManager){
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
        modelManager.addClient(ModelManager.PROP_UPDATE_EVENT,evt -> Platform.runLater(this::updateEvents));
    }

    private void update(){
            borderPane.setVisible(modelManager.getState() == ClientState.EVENT_MENU);
        if(modelManager.getState() == ClientState.EVENT_MENU) {
            updateEvents();
            checkFileName();
        }
    }

    private void updateEvents(){
        table.getItems().clear();
        var res = modelManager.checkLastMessageFromServer().getEvents();
        System.out.println(res);
        if(res == null) return;
        if(res.isEmpty()) return;
        table.getItems().addAll(res);
    }

    private void checkFileName(){
        System.out.println(tfFileName.getText());
        btnCsv.setDisable(tfFileName.getText().equals("") && table.getItems().isEmpty());
    }
    @FXML
    private void back(){
        modelManager.startMenu();
    }
    @FXML
    private void csv(){

    }
}
