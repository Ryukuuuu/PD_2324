package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import data.Event;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class EventController {

    public Text tName;
    public Text tLocal;
    public Text tDate;
    public Text tStart;
    public Text tEnd;
    public AnchorPane anchorPane;
    private Event event;
    private ModelManager modelManager;
    public void init(ModelManager modelManager, Event event){
        this.modelManager = modelManager;
        this.event = event;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
    }

    private void update(){
        if(modelManager.getState() == ClientState.EVENT_MENU){
            tName.setText(event.getName());
            tLocal.setText(event.getLocal());
            tDate.setText(event.getDate());
            tStart.setText(event.getStartingTime());
            tEnd.setText(event.getEndingTime());
            anchorPane.setVisible(true);
        }
        else{
            anchorPane.setVisible(false);
        }
    }
}
