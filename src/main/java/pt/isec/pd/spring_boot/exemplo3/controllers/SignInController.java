package pt.isec.pd.spring_boot.exemplo3.controllers;

import data.ClientData;
import database.DatabaseConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.spring_boot.exemplo3.models.RequestMessage;
import pt.isec.pd.spring_boot.exemplo3.security.TokenService;

@RestController
public class SignInController {
    @PostMapping("/signing")
    public ResponseEntity signing(@RequestBody ClientData clientData){
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        if(clientData != null){
            if(dbConnection.addNewEntryToClients(clientData)){
                return ResponseEntity.ok("Account created");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signing failed, invalid email");
    }
}