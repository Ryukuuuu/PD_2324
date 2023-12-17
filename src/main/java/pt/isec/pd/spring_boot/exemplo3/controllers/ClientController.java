package pt.isec.pd.spring_boot.exemplo3.controllers;

import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import data.ClientData;
import database.DatabaseConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.spring_boot.exemplo3.models.RequestMessage;

@RestController
public class ClientController {

    @GetMapping("client")
    public ResponseEntity getClientData(Authentication authentication){
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        ClientData clientInfo;

        String subject = authentication.getName();

        clientInfo = dbConnection.getClientWithoutPass(subject);
        if(clientInfo != null){
            return ResponseEntity.status(HttpStatus.OK).body(clientInfo);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }

    @PostMapping("client/edit")
    public ResponseEntity editClientData(Authentication authentication,
                                         @RequestBody ClientData clientData){
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        ClientData clientInfo = dbConnection.getClientWithoutPass(authentication.getName());

        System.out.println("ClientInfo: " + authentication.getName());

        if(clientInfo != null){
            ClientData newClientInfo = dbConnection.editClientInfo(clientData);
            if(newClientInfo != null){
                return ResponseEntity.status(HttpStatus.OK).body(newClientInfo);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User edition failed");
    }
}
