package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import com.sun.webkit.Timer;
import data.Event;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class AdminEventMenuController {


    public ScrollPane scrollView;
    public TableColumn<Event,Event> name;
    public TableColumn<Event,Event> local;
    public TableColumn<Event,Event> date;
    public TableColumn<Event,Event> start;
    public TableColumn<Event,Event> end;
    public Button btnSearch;

    public TextField tfEvent;

    public BorderPane borderPane;
    public Button btnBack;
    public Button btnCsv;
    public TextField tfFileName;

    public TableView<Event> table;
    private ModelManager modelManager;
    private ArrayList<Event> events = new ArrayList<>();


    public void init(ModelManager modelManager){
        this.modelManager = modelManager;
        registerHandles();
        update();
    }

    private void registerHandles(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }
    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.ADMIN_EVENT_MENU_BY_USERS);
    }

    private boolean updateEvents(){
        table.getItems().clear();
        events = modelManager.checkLastMessageFromServer().getEvents();
        if(events == null) return false;
        if(events.isEmpty()) return false;
        table.getItems().addAll(events);
        return false;
    }


    @FXML
    private void setBtnBack(){modelManager.startMenu();}
    @FXML
    private void setBtnSearch(){
        modelManager.sendEventsMessageWithFilters(new Event(tfEvent.getText()));
    }
}
