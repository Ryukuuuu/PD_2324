package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import client.model.ModelManagerREST;
import data.Event;
import data.MessageTypes;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class EventsMenuController {

    public TableView<Event> table;

    public TableColumn<Event,Event> name;
    public TableColumn<Event,Event> local;
    public TableColumn<Event,Event> date;
    public TableColumn<Event,Event> start;
    public TableColumn<Event,Event> end;

    public ArrayList<Event> events = new ArrayList<>();
    public Button btnCsv;
    public TextField tfFileName;
    public TextField tfName;
    public TextField tfLocal;
    public TextField tfDate;
    public TextField tfStartingTime;
    public TextField tfEndingTime;
    public Button btnSearch;

    public BorderPane borderPane;

    public Button btnBack;
    public ScrollPane scrollView;

    private ModelManagerREST modelManager;

    private static final String CSV_PATH = "src/client/csvFiles/";


    public void init(ModelManagerREST modelManager){
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
        //modelManager.addClient(ModelManager.PROP_UPDATE_EVENT,evt -> Platform.runLater(this::updateEvents));
        modelManager.addClient(ModelManager.PROP_UPDATE_REFRESH_EVENT,evt -> Platform.runLater(this::getEventsUpdated));
        modelManager.addClient(ModelManager.PROP_ADD_PRESENCE_UPDATE,evt -> Platform.runLater(this::getEventsUpdated));
        modelManager.addClient(ModelManager.PROP_UPDATE_DELETE_PRESENCE,evt -> Platform.runLater(this::getEventsUpdated));
    }

    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.EVENT_MENU || modelManager.getState() == ClientState.ADMIN_EVENT_MENU);
        if(modelManager.getState() == ClientState.EVENT_MENU || modelManager.getState() == ClientState.ADMIN_EVENT_MENU) {
            if(modelManager.getState() == ClientState.EVENT_MENU)
                events = modelManager.events();
            if(modelManager.getState() == ClientState.ADMIN_EVENT_MENU)
                events = modelManager.getEvents();
            updateEvents();
        }
    }

    private boolean updateEvents(){
        table.getItems().clear();
        //events = modelManager.checkLastMessageFromServer().getEvents();
        //System.out.println(events);
        if(events == null) return false;
        if(events.isEmpty()) return false;
        table.getItems().addAll(events);
        return true;
    }

    private void getEventsUpdated(){
        /*modelManager.resendLastMessageToServer();*/
    }

    private boolean checkInfoForCSV(){
        if(tfFileName.getText().equals(""))
            return true;
        if(table.getItems().isEmpty())
            return true;
        return false;
    }
    @FXML
    private void back(){modelManager.startMenu();}
    @FXML
    private void csv(){
        if(checkInfoForCSV()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("File name not filled or presences table is empty");
            alert.show();
        }
        else{
            modelManager.createClientsPresencesCSVFile(events,CSV_PATH + tfFileName.getText() + ".csv");
        }
    }

    @FXML
    private void setBtnSearch(){
        modelManager.getEvents();
        /*Event eventFilter = new Event(tfName.getText(),tfLocal.getText(),tfDate.getText(),tfStartingTime.getText(),tfEndingTime.getText());
        if(modelManager.getState() == ClientState.EVENT_MENU)
            modelManager.sendEventsMessageWithFilters(eventFilter);
        else
            modelManager.sendEventsMessageWithFilters(eventFilter);*/
    }
}