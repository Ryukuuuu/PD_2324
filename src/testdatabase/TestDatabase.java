package testdatabase;

import client.fsm.states.ClientState;
import data.ClientData;
import data.Event;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TestDatabase {

    private Map<String, ClientData> clients;
    private ArrayList<Event> events;



    public TestDatabase(){
        clients = new HashMap<>();
        events = new ArrayList<>();
        fillClientsMap();
        fillEventsList();
    }

    public void fillClientsMap(){
        clients.put("teste1@isec.pt",new ClientData(
                "name1",
                3894759,
                "teste1@isec.pt",
                "pass1",
                true,
                true
        ));

        clients.put("teste2@isec.pt",new ClientData(
                "name2",
                3,
                "teste2@isec.pt",
                "pass2",
                true,
                false
        ));
        clients.put("teste3@isec.pt",new ClientData(
                "name3",
                384759,
                "email3@isec.pt",
                "pass3",
                true,
                false
        ));
    }

    public void fillEventsList(){
        events.add(new Event("Event1","Local1","11/11/2023",1L,"10","10AM","12AM"));
        events.add(new Event("Event2","Local2","12/11/2023","10AM","12AM"));
        events.add(new Event("Event3","Local3","13/11/2023","10AM","12AM"));
    }

    public ClientData getClient(String email,String password){
        ClientData newClient;
        if(clients.containsKey(email)){
            newClient = clients.get(email);
        }
        else return null;

        if(!Objects.equals(newClient.getPassword(), password)){
            return null;
        }
        return newClient;
    }

    public ClientData getClient(String email){
        return clients.getOrDefault(email, null);
    }

    public boolean addNewEntryToClients(ClientData clientData){
        if(clients.containsKey(clientData.getEmail())){
            return false;
        }
        clients.put(clientData.getEmail(),clientData);
        return true;
    }

    public ClientData editUserInfo(ClientData clientData){
        ClientData client = clients.get(clientData.getEmail());
        if(client == null){
            return null;
        }
        if(!client.getName().equals(clientData.getName()))
            client.setName(clientData.getName());
        if(client.getId() != clientData.getId())
            client.setId(clientData.getId());
        if(!client.getPassword().equals(clientData.getPassword()))
            client.setPassword(clientData.getPassword());

        clients.put(client.getEmail(),client);
        return client;
    }

    public void addNewEvent(Event event){
        events.add(event);
        System.out.println(events);
    }

    public void addCodeToExistingEvent(Event event,long code, String codeValidityEnding){
        for(Event e : events){
            if(e.equals(event)){
                e.setActiveCode(code);
                e.setCodeValidityEnding(codeValidityEnding);
                return;
            }
        }
    }

    public boolean checkIfCodeExists(long code){
        for(Event event : events){
            if(code == event.getActiveCode()){
                return true;
            }
        }
        return false;
    }
}
