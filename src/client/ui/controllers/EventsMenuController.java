package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import data.Event;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class EventsMenuController {

    private ModelManager modelManager;
    public BorderPane borderPane;
    public Button btnBack;
    public ScrollPane scrollView;

    private ArrayList<EventController> eventControllers=new ArrayList<>();
    private ArrayList<Event> events;


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
        System.out.println("Updating events");
    }

    @FXML
    private void back(){
        modelManager.startMenu();
    }
}
