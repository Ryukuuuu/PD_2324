package pt.isec.pd.spring_boot.exemplo3.controllers;

import com.sun.net.httpserver.HttpsServer;
import data.ClientData;
import data.Event;
import database.DatabaseConnection;
import org.apache.coyote.Response;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.spring_boot.exemplo3.models.RequestMessage;

import javax.xml.crypto.Data;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RestController
public class EventsController {

    @PostMapping("events/submitCode")
    public ResponseEntity submitCode(@RequestBody RequestMessage requestMessage){
        if(requestMessage.getEventCode() != 0 && requestMessage.getClientData() != null) {
            String formattedTimeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            if(DatabaseConnection.getInstance().checkCodeToAssignPresence(requestMessage.getEventCode(),requestMessage.getClientData().getEmail(),formattedTimeNow)){
                return ResponseEntity.ok("Code submitted");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid code");
    }


    @GetMapping("events/checkPresences")
    public ResponseEntity checkPresences(Authentication authentication,
                                         @RequestBody RequestMessage requestMessage){
        String clientEmail = authentication.getName();
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        ArrayList<Event> events = dbConnection.getEvents(requestMessage.getEvent(),requestMessage.getClientData().getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @PostMapping("events/addPresence")
    public ResponseEntity addPresences(@RequestBody RequestMessage requestMessage){
        String formattedTimeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        if(dbConnection.addPresence(
                requestMessage.getClientData().getEmail(),
                requestMessage.getEvent().getName(),
                formattedTimeNow)){
            return ResponseEntity.status(HttpStatus.CREATED).body("Presence added");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding presence");
    }

    @DeleteMapping("events/deletePresence")
    public ResponseEntity deletePresence(@RequestBody RequestMessage requestMessage){
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        if(dbConnection.removePresencesFromEvent(requestMessage.getEvent().getName()) != null){
            return ResponseEntity.status(HttpStatus.OK).body("Event presences removed");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error removing presences from event");
    }

    @PostMapping("events/create")
    public ResponseEntity createEvent(@RequestBody RequestMessage requestMessage){
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        if(dbConnection.addNewEntryToEvents(requestMessage.getEvent())){
            return ResponseEntity.status(HttpStatus.OK).body("Event Created");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating event");
    }

    @DeleteMapping("events/delete")
    public ResponseEntity deleteEvent(@RequestBody RequestMessage requestMessage){
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        if(dbConnection.removeEvent(requestMessage.getEvent().getName())){
            return ResponseEntity.status(HttpStatus.OK).body("Event removed");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error removing event");
    }

    @GetMapping("events/presencesByEvent")
    public ResponseEntity getPresencesByEvent(@RequestParam String eventName){
        ArrayList<ClientData> clients = DatabaseConnection.getInstance().getPresences(eventName);
        return ResponseEntity.status(HttpStatus.OK).body(clients);
    }

    @GetMapping("events/presencesByUser")
    public ResponseEntity getPresencesByUser(@RequestBody RequestMessage requestMessage){
        ArrayList<Event> events = DatabaseConnection.getInstance().getEvents(requestMessage.getEvent(),requestMessage.getClientData().getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    // passar isto para /events ?
    @GetMapping("events/getEvents")
    public ResponseEntity getEvents(@RequestBody Event event){
        ArrayList<Event> events = DatabaseConnection.getInstance().getEvents(event,null);
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }
}
