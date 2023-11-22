package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import data.Event;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class EventsMenuController {

    public TableView<Event> table;

    public TableColumn<Event,Event> name;
    public TableColumn<Event,Event> local;
    public TableColumn<Event,Event> date;
    public TableColumn<Event,Event> start;
    public TableColumn<Event,Event> end;
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
    }

    private void updateEvents(){
        table.getItems().clear();
        var res = modelManager.checkLastMessageFromServer().getEvents();
        System.out.println(res);
        if(res == null) return;
        if(res.isEmpty()) return;
        table.getItems().addAll(res);
    }

    @FXML
    private void back(){
        modelManager.startMenu();
    }
}
