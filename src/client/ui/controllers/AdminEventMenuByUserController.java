package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import data.Event;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class AdminEventMenuByUserController {
    public BorderPane borderPane;
    public Button btnBack;
    public Button btnCsv;
    public TextField tfFileName;
    public TextField tfUserEmail;
    public Button btnSearch;
    public TableView<Event> table;
    public TableColumn<Event,Event> name;
    public TableColumn<Event,Event> local;
    public TableColumn<Event,Event> date;
    public TableColumn<Event,Event> start;
    public TableColumn<Event,Event> end;

    private ArrayList<Event> events = new ArrayList<>();
    private ModelManager modelManager;
    private static final String CSV_PATH = "src/client/csvFiles/";

    public void init(ModelManager modelManager){
        this.modelManager = modelManager;
        registerHandles();
        update();
    }

    private void registerHandles(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
        modelManager.addClient(ModelManager.PROP_UPDATE_EVENT,evt -> Platform.runLater(this::updateEvents));
    }
    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.ADMIN_EVENT_MENU_BY_USERS);
    }

    private boolean updateEvents(){
        table.getItems().clear();
        events = modelManager.checkLastMessageFromServer().getEvents();
        //System.out.println(events);
        if(events == null) return false;
        if(events.isEmpty()) return false;
        table.getItems().addAll(events);
        return true;
    }

    @FXML
    private void setBtnBack(){
        modelManager.startMenu();
    }
    @FXML
    private void setBtnCsv(){
        if(tfFileName.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("File name must be filled");
        }
        else
            modelManager.createClientsPresencesCSVFile(events,tfFileName.getText());
    }

    @FXML
    private void setBtnSearch(){
        modelManager.getEventsByUser(tfUserEmail.getText());
    }
}
