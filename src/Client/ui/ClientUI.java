package Client.ui;

import Client.model.ClientManager;
import Data.ClientData;

import java.util.Scanner;

public class ClientUI {
    private boolean quit = false;
    private ClientManager clientManager;
    private Scanner scanner = new Scanner(System.in);

    public ClientUI(String ipServer,String portServer){
        this.clientManager = new ClientManager(ipServer,portServer);
    }

    private boolean askForCredentials(){
        ClientData loginClientData = new ClientData();
        System.out.println("Insert email: ");
        loginClientData.setEmail(scanner.nextLine());
        System.out.println("Insert password: ");
        loginClientData.setPassword(scanner.nextLine());

        return clientManager.logInClient(loginClientData);
    }

    private boolean showMainMenu(){
        int option;


        System.out.println("1- Submit code\n"+
                "2- Check presences\n"+
                "3- Edit info\n"+
                "4- LogOut");


        return true;
    }


    public void start(){
        if(!askForCredentials()){
            return;
        }
        while(!quit){
            System.out.println("Waiting for input");
            scanner.next();
        }
    }
}
