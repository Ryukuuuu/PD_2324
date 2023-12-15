package client.ui.controllers;

import client.fsm.states.ClientState;
import client.model.ModelManager;
import client.model.ModelManagerREST;
import data.ClientData;
import data.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class AdminEventMenuByEventController {
    public BorderPane borderPane;
    public Button btnBack;
    public Button btnCsv;
    public TextField tfFileName;
    public TextField tfEvent;
    public Button btnSearch;

    public TableView<ClientData> table;
    public TableColumn<ClientData,ClientData> name;
    public TableColumn<ClientData,ClientData> clientID;
    public TableColumn<ClientData,ClientData> email;
    private ArrayList<ClientData> clientData = new ArrayList<>();

    private ModelManagerREST modelManager;
    private static final String CSV_PATH = "src/client/csvFiles/";

    public void init(ModelManagerREST modelManager){
        this.modelManager = modelManager;
        registerHandlers();
        update();
    }

    private void registerHandlers(){
        modelManager.addClient(ModelManager.PROP_STATE,evt -> Platform.runLater(this::update));
        modelManager.addClient(ModelManager.PROP_UPDATE_EVENT,evt -> Platform.runLater(this::updateData));
    }
    private void update(){
        borderPane.setVisible(modelManager.getState() == ClientState.ADMIN_EVENT_MENU_BY_EVENTS);
    }

    private boolean updateData(){
        table.getItems().clear();
        //clientData = modelManager.checkLastMessageFromServer().getClients();
        //System.out.println(events);
        if(clientData == null) return false;
        if(clientData.isEmpty()) return false;
        table.getItems().addAll(clientData);
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
        else{
            //Message message = modelManager.checkLastMessageFromServer();
            //modelManager.createEventsPresencesCSVFile(message.getEvent(),message.getClients(),CSV_PATH + tfFileName.getText() + ".csv");
        }
    }

    @FXML
    private void setBtnSearch(){
        modelManager.getPresencesByEvent(tfEvent.getText());
    }
}
